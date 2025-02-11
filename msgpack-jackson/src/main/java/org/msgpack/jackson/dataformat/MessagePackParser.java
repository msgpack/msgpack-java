//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.core.json.DupDetector;
import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferInput;
import org.msgpack.core.buffer.InputStreamBufferInput;
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.value.ValueType;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.msgpack.jackson.dataformat.JavaInfo.STRING_VALUE_FIELD_IS_CHARS;

public class MessagePackParser
        extends ParserMinimalBase
{
    private static final ThreadLocal<Tuple<Object, MessageUnpacker>> messageUnpackerHolder = new ThreadLocal<>();
    private final MessageUnpacker messageUnpacker;

    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    private ObjectCodec codec;
    private MessagePackReadContext streamReadContext;

    private boolean isClosed;
    private long tokenPosition;
    private long currentPosition;
    private final IOContext ioContext;
    private ExtensionTypeCustomDeserializers extTypeCustomDesers;
    private final byte[] tempBytes = new byte[64];
    private final char[] tempChars = new char[64];

    private enum Type
    {
        INT, LONG, DOUBLE, STRING, BYTES, BIG_INT, EXT
    }
    private Type type;
    private int intValue;
    private long longValue;
    private double doubleValue;
    private byte[] bytesValue;
    private String stringValue;
    private BigInteger biValue;
    private MessagePackExtensionType extensionTypeValue;

    public MessagePackParser(
            IOContext ctxt,
            int features,
            ObjectCodec objectCodec,
            InputStream in,
            boolean reuseResourceInParser)
            throws IOException
    {
        this(ctxt, features, new InputStreamBufferInput(in), objectCodec, in, reuseResourceInParser);
    }

    public MessagePackParser(
            IOContext ctxt,
            int features,
            ObjectCodec objectCodec,
            byte[] bytes,
            boolean reuseResourceInParser)
            throws IOException
    {
        this(ctxt, features, new ArrayBufferInput(bytes), objectCodec, bytes, reuseResourceInParser);
    }

    private MessagePackParser(IOContext ctxt,
            int features,
            MessageBufferInput input,
            ObjectCodec objectCodec,
            Object src,
            boolean reuseResourceInParser)
            throws IOException
    {
        super(features);

        this.codec = objectCodec;
        ioContext = ctxt;
        DupDetector dups = Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features)
                ? DupDetector.rootDetector(this) : null;
        streamReadContext = MessagePackReadContext.createRootContext(dups);
        if (!reuseResourceInParser) {
            messageUnpacker = MessagePack.newDefaultUnpacker(input);
            return;
        }

        Tuple<Object, MessageUnpacker> messageUnpackerTuple = messageUnpackerHolder.get();
        if (messageUnpackerTuple == null) {
            messageUnpacker = MessagePack.newDefaultUnpacker(input);
        }
        else {
            // Considering to reuse InputStream with JsonParser.Feature.AUTO_CLOSE_SOURCE,
            // MessagePackParser needs to use the MessageUnpacker that has the same InputStream
            // since it has buffer which has loaded the InputStream data ahead.
            // However, it needs to call MessageUnpacker#reset when the source is different from the previous one.
            if (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE) || messageUnpackerTuple.first() != src) {
                messageUnpackerTuple.second().reset(input);
            }
            messageUnpacker = messageUnpackerTuple.second();
        }
        messageUnpackerHolder.set(new Tuple<>(src, messageUnpacker));
    }

    public void setExtensionTypeCustomDeserializers(ExtensionTypeCustomDeserializers extTypeCustomDesers)
    {
        this.extTypeCustomDesers = extTypeCustomDesers;
    }

    @Override
    public ObjectCodec getCodec()
    {
        return codec;
    }

    @Override
    public void setCodec(ObjectCodec c)
    {
        codec = c;
    }

    @Override
    public Version version()
    {
        return null;
    }

    private String unpackString(MessageUnpacker messageUnpacker) throws IOException
    {
        int strLen = messageUnpacker.unpackRawStringHeader();
        if (strLen <= tempBytes.length) {
            messageUnpacker.readPayload(tempBytes, 0, strLen);
            if (STRING_VALUE_FIELD_IS_CHARS.get()) {
                for (int i = 0; i < strLen; i++) {
                    byte b = tempBytes[i];
                    if ((0x80 & b) != 0) {
                        return new String(tempBytes, 0, strLen, StandardCharsets.UTF_8);
                    }
                    tempChars[i] = (char) b;
                }
                return new String(tempChars, 0, strLen);
            }
            else {
                return new String(tempBytes, 0, strLen);
            }
        }
        else {
            byte[] bytes = messageUnpacker.readPayload(strLen);
            return new String(bytes, 0, strLen, StandardCharsets.UTF_8);
        }
    }

    @Override
    public JsonToken nextToken() throws IOException
    {
        tokenPosition = messageUnpacker.getTotalReadBytes();

        boolean isObjectValueSet = streamReadContext.inObject() && _currToken != JsonToken.FIELD_NAME;
        if (isObjectValueSet) {
            if (!streamReadContext.expectMoreValues()) {
                streamReadContext = streamReadContext.getParent();
                return _updateToken(JsonToken.END_OBJECT);
            }
        }
        else if (streamReadContext.inArray()) {
            if (!streamReadContext.expectMoreValues()) {
                streamReadContext = streamReadContext.getParent();
                return _updateToken(JsonToken.END_ARRAY);
            }
        }

        if (!messageUnpacker.hasNext()) {
            throw new JsonEOFException(this, null, "Unexpected EOF");
        }

        MessageFormat format = messageUnpacker.getNextFormat();
        ValueType valueType = format.getValueType();

        JsonToken nextToken;
        switch (valueType) {
            case STRING:
                type = Type.STRING;
                stringValue = unpackString(messageUnpacker);
                if (isObjectValueSet) {
                    streamReadContext.setCurrentName(stringValue);
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_STRING;
                }
                break;
            case INTEGER:
                Object v;
                switch (format) {
                    case UINT64:
                        BigInteger bi = messageUnpacker.unpackBigInteger();
                        if (0 <= bi.compareTo(LONG_MIN) && bi.compareTo(LONG_MAX) <= 0) {
                            type = Type.LONG;
                            longValue = bi.longValue();
                            v = longValue;
                        }
                        else {
                            type = Type.BIG_INT;
                            biValue = bi;
                            v = biValue;
                        }
                        break;
                    default:
                        long l = messageUnpacker.unpackLong();
                        if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE) {
                            type = Type.INT;
                            intValue = (int) l;
                            v = intValue;
                        }
                        else {
                            type = Type.LONG;
                            longValue = l;
                            v = longValue;
                        }
                        break;
                }

                if (isObjectValueSet) {
                    streamReadContext.setCurrentName(String.valueOf(v));
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_NUMBER_INT;
                }
                break;
            case NIL:
                messageUnpacker.unpackNil();
                nextToken = JsonToken.VALUE_NULL;
                break;
            case BOOLEAN:
                boolean b = messageUnpacker.unpackBoolean();
                if (isObjectValueSet) {
                    streamReadContext.setCurrentName(Boolean.toString(b));
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = b ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
                }
                break;
            case FLOAT:
                type = Type.DOUBLE;
                doubleValue = messageUnpacker.unpackDouble();
                if (isObjectValueSet) {
                    streamReadContext.setCurrentName(String.valueOf(doubleValue));
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_NUMBER_FLOAT;
                }
                break;
            case BINARY:
                type = Type.BYTES;
                int len = messageUnpacker.unpackBinaryHeader();
                bytesValue = messageUnpacker.readPayload(len);
                if (isObjectValueSet) {
                    streamReadContext.setCurrentName(new String(bytesValue, MessagePack.UTF8));
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_EMBEDDED_OBJECT;
                }
                break;
            case ARRAY:
                nextToken = JsonToken.START_ARRAY;
                streamReadContext = streamReadContext.createChildArrayContext(messageUnpacker.unpackArrayHeader());
                break;
            case MAP:
                nextToken = JsonToken.START_OBJECT;
                streamReadContext = streamReadContext.createChildObjectContext(messageUnpacker.unpackMapHeader());
                break;
            case EXTENSION:
                type = Type.EXT;
                ExtensionTypeHeader header = messageUnpacker.unpackExtensionTypeHeader();
                extensionTypeValue = new MessagePackExtensionType(header.getType(), messageUnpacker.readPayload(header.getLength()));
                if (isObjectValueSet) {
                    streamReadContext.setCurrentName(deserializedExtensionTypeValue().toString());
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_EMBEDDED_OBJECT;
                }
                break;
            default:
                throw new IllegalStateException("Shouldn't reach here");
        }
        currentPosition = messageUnpacker.getTotalReadBytes();

        _updateToken(nextToken);

        return nextToken;
    }

    @Override
    protected void _handleEOF()
    {
    }

    @Override
    public String getText() throws IOException
    {
        switch (type) {
            case STRING:
                return stringValue;
            case BYTES:
                return new String(bytesValue, MessagePack.UTF8);
            case INT:
                return String.valueOf(intValue);
            case LONG:
                return String.valueOf(longValue);
            case DOUBLE:
                return String.valueOf(doubleValue);
            case BIG_INT:
                return String.valueOf(biValue);
            case EXT:
                return deserializedExtensionTypeValue().toString();
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public char[] getTextCharacters() throws IOException
    {
        return getText().toCharArray();
    }

    @Override
    public boolean hasTextCharacters()
    {
        return false;
    }

    @Override
    public int getTextLength() throws IOException
    {
        return getText().length();
    }

    @Override
    public int getTextOffset()
    {
        return 0;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant)
    {
        switch (type) {
            case BYTES:
                return bytesValue;
            case STRING:
                return stringValue.getBytes(MessagePack.UTF8);
            case EXT:
                return extensionTypeValue.getData();
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public Number getNumberValue()
    {
        switch (type) {
            case INT:
                return intValue;
            case LONG:
                return longValue;
            case DOUBLE:
                return doubleValue;
            case BIG_INT:
                return biValue;
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public int getIntValue()
    {
        switch (type) {
            case INT:
                return intValue;
            case LONG:
                return (int) longValue;
            case DOUBLE:
                return (int) doubleValue;
            case BIG_INT:
                return biValue.intValue();
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public long getLongValue()
    {
        switch (type) {
            case INT:
                return intValue;
            case LONG:
                return longValue;
            case DOUBLE:
                return (long) doubleValue;
            case BIG_INT:
                return biValue.longValue();
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public BigInteger getBigIntegerValue()
    {
        switch (type) {
            case INT:
                return BigInteger.valueOf(intValue);
            case LONG:
                return BigInteger.valueOf(longValue);
            case DOUBLE:
                return BigInteger.valueOf((long) doubleValue);
            case BIG_INT:
                return biValue;
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public float getFloatValue()
    {
        switch (type) {
            case INT:
                return (float) intValue;
            case LONG:
                return (float) longValue;
            case DOUBLE:
                return (float) doubleValue;
            case BIG_INT:
                return biValue.floatValue();
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public double getDoubleValue()
    {
         switch (type) {
             case INT:
                 return intValue;
            case LONG:
                return (double) longValue;
            case DOUBLE:
                return doubleValue;
            case BIG_INT:
                return biValue.doubleValue();
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public BigDecimal getDecimalValue()
    {
         switch (type) {
             case INT:
                 return BigDecimal.valueOf(intValue);
            case LONG:
                return BigDecimal.valueOf(longValue);
            case DOUBLE:
                 return BigDecimal.valueOf(doubleValue);
            case BIG_INT:
                return new BigDecimal(biValue);
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    private Object deserializedExtensionTypeValue()
            throws IOException
    {
        if (extTypeCustomDesers != null) {
            ExtensionTypeCustomDeserializers.Deser deser = extTypeCustomDesers.getDeser(extensionTypeValue.getType());
            if (deser != null) {
                return deser.deserialize(extensionTypeValue.getData());
            }
        }
        return extensionTypeValue;
    }

    @Override
    public Object getEmbeddedObject() throws IOException
    {
        switch (type) {
            case BYTES:
                return bytesValue;
            case EXT:
                return deserializedExtensionTypeValue();
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public NumberType getNumberType()
    {
        switch (type) {
            case INT:
                return NumberType.INT;
            case LONG:
                return NumberType.LONG;
            case DOUBLE:
                return NumberType.DOUBLE;
            case BIG_INT:
                return NumberType.BIG_INTEGER;
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public void close()
            throws IOException
    {
        try {
            if (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                messageUnpacker.close();
            }
        }
        finally {
            isClosed = true;
        }
    }

    @Override
    public boolean isClosed()
    {
        return isClosed;
    }

    @Override
    public JsonStreamContext getParsingContext()
    {
        return streamReadContext;
    }

    @Override
    public JsonLocation getTokenLocation()
    {
        return new JsonLocation(ioContext.getSourceReference(), tokenPosition, -1, -1, (int) tokenPosition);
    }

    @Override
    public JsonLocation getCurrentLocation()
    {
        return new JsonLocation(ioContext.getSourceReference(), currentPosition, -1, -1, (int) currentPosition);
    }

    @Override
    public void overrideCurrentName(String name)
    {
        // Simple, but need to look for START_OBJECT/ARRAY's "off-by-one" thing:
        MessagePackReadContext ctxt = streamReadContext;
        if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
            ctxt = ctxt.getParent();
        }
        // Unfortunate, but since we did not expose exceptions, need to wrap
        try {
            ctxt.setCurrentName(name);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String currentName()
    {
        if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
            MessagePackReadContext parent = streamReadContext.getParent();
            return parent.getCurrentName();
        }
        return streamReadContext.getCurrentName();
    }

    public boolean isCurrentFieldId()
    {
        return this.type == Type.INT || this.type == Type.LONG;
    }

    @Override
    public String getCurrentName()
            throws IOException
    {
        return currentName();
    }
}
