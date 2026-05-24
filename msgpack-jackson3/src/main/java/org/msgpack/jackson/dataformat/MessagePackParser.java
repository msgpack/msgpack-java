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

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.Version;
import tools.jackson.core.base.ParserMinimalBase;
import tools.jackson.core.exc.UnexpectedEndOfInputException;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.InputStreamBufferInput;
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.value.ValueType;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class MessagePackParser
        extends ParserMinimalBase
{
    private static final ThreadLocal<Tuple<Object, MessageUnpacker>> messageUnpackerHolder = new ThreadLocal<>();
    private final MessageUnpacker messageUnpacker;

    private static final BigInteger LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    private MessagePackReadContext streamReadContext;

    private boolean isClosed;
    private long tokenPosition;
    private long currentPosition;
    private final IOContext ioContext;
    private ExtensionTypeCustomDeserializers extTypeCustomDesers;

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
            ObjectReadContext readCtxt,
            IOContext ioCtxt,
            int streamReadFeatures,
            InputStream in,
            boolean reuseResourceInParser)
            throws IOException
    {
        this(readCtxt, ioCtxt, streamReadFeatures, new InputStreamBufferInput(in), in, reuseResourceInParser);
    }

    MessagePackParser(ObjectReadContext readCtxt,
            IOContext ioCtxt,
            int streamReadFeatures,
            MessageBufferInput input,
            Object src,
            boolean reuseResourceInParser)
            throws IOException
    {
        super(readCtxt, ioCtxt, streamReadFeatures);

        ioContext = ioCtxt;
        DupDetector dups = StreamReadFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamReadFeatures)
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
            if (StreamReadFeature.AUTO_CLOSE_SOURCE.enabledIn(streamReadFeatures) || messageUnpackerTuple.first() != src || src instanceof byte[]) {
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
    public Version version()
    {
        return PackageVersion.VERSION;
    }

    private String unpackString(MessageUnpacker messageUnpacker) throws IOException
    {
        return messageUnpacker.unpackString();
    }

    @Override
    public JsonToken nextToken() throws JacksonException
    {
        try {
            return _nextToken();
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    private JsonToken _nextToken() throws IOException
    {
        tokenPosition = messageUnpacker.getTotalReadBytes();

        boolean isObjectValueSet = streamReadContext.inObject() && _currToken != JsonToken.PROPERTY_NAME;
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
            if (streamReadContext.inRoot()) {
                return null;
            }
            throw new UnexpectedEndOfInputException(this, null, null);
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
                    nextToken = JsonToken.PROPERTY_NAME;
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
                    nextToken = JsonToken.PROPERTY_NAME;
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
                    nextToken = JsonToken.PROPERTY_NAME;
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
                    nextToken = JsonToken.PROPERTY_NAME;
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
                    nextToken = JsonToken.PROPERTY_NAME;
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
                    nextToken = JsonToken.PROPERTY_NAME;
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
    public String getString()
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
                try {
                    return deserializedExtensionTypeValue().toString();
                }
                catch (IOException e) {
                    throw _wrapIOFailure(e);
                }
            default:
                throw new IllegalStateException("Invalid type=" + type);
        }
    }

    @Override
    public char[] getStringCharacters()
    {
        return getString().toCharArray();
    }

    @Override
    public boolean hasStringCharacters()
    {
        return false;
    }

    @Override
    public int getStringLength()
    {
        return getString().length();
    }

    @Override
    public int getStringOffset()
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
    public Object getEmbeddedObject()
    {
        switch (type) {
            case BYTES:
                return bytesValue;
            case EXT:
                try {
                    return deserializedExtensionTypeValue();
                }
                catch (IOException e) {
                    throw _wrapIOFailure(e);
                }
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
    protected void _closeInput() throws IOException
    {
        if (StreamReadFeature.AUTO_CLOSE_SOURCE.enabledIn(_streamReadFeatures)) {
            messageUnpacker.close();
        }
    }

    @Override
    protected void _releaseBuffers()
    {
    }

    @Override
    public boolean isClosed()
    {
        return isClosed;
    }

    @Override
    public void close()
    {
        try {
            _closeInput();
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        finally {
            isClosed = true;
            Tuple<Object, MessageUnpacker> tuple = messageUnpackerHolder.get();
            if (tuple != null && tuple.first() instanceof byte[]) {
                messageUnpackerHolder.set(new Tuple<>(null, tuple.second()));
            }
        }
    }

    @Override
    public TokenStreamContext streamReadContext()
    {
        return streamReadContext;
    }

    @Override
    public TokenStreamLocation currentTokenLocation()
    {
        // columnNr repurposed as byte offset; truncates for inputs > 2 GB
        return new TokenStreamLocation(ioContext.contentReference(), tokenPosition, -1, (int) tokenPosition);
    }

    @Override
    public TokenStreamLocation currentLocation()
    {
        // columnNr repurposed as byte offset; truncates for inputs > 2 GB
        return new TokenStreamLocation(ioContext.contentReference(), currentPosition, -1, (int) currentPosition);
    }

    @Override
    public String currentName()
    {
        if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
            MessagePackReadContext parent = streamReadContext.getParent();
            return parent.currentName();
        }
        return streamReadContext.currentName();
    }

    @Override
    public Object streamReadInputSource()
    {
        return ioContext.contentReference().getRawContent();
    }

    @Override
    public Object currentValue()
    {
        return streamReadContext.currentValue();
    }

    @Override
    public void assignCurrentValue(Object v)
    {
        streamReadContext.assignCurrentValue(v);
    }

    @Override
    public boolean isNaN()
    {
        if (type == Type.DOUBLE) {
            return Double.isNaN(doubleValue) || Double.isInfinite(doubleValue);
        }
        return false;
    }

    public boolean isCurrentFieldId()
    {
        return this.type == Type.INT || this.type == Type.LONG;
    }
}
