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
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.value.ValueType;

import java.io.IOException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MessagePackParser
        extends ParserMinimalBase
{
    // Retained heap per idle thread: ~0.2 KB (MessageUnpacker with cleared input buffer).
    // Negligible compared to Jackson's own per-thread buffer retention.
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
    private final boolean ownsThreadLocalUnpacker;

    private enum Type
    {
        INT, LONG, DOUBLE, STRING, BYTES, BOOL, BIG_INT, EXT, NULL
    }
    private Type type;
    private int intValue;
    private long longValue;
    private double doubleValue;
    private boolean booleanValue;
    private byte[] bytesValue;
    private String stringValue;
    private BigInteger biValue;
    private MessagePackExtensionType extensionTypeValue;

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
            ownsThreadLocalUnpacker = false;
            return;
        }

        Tuple<Object, MessageUnpacker> messageUnpackerTuple = messageUnpackerHolder.get();
        if (messageUnpackerTuple == null) {
            messageUnpacker = MessagePack.newDefaultUnpacker(input);
        }
        else {
            // Considering to reuse InputStream with StreamReadFeature.AUTO_CLOSE_SOURCE,
            // MessagePackParser needs to use the MessageUnpacker that has the same InputStream
            // since it has buffer which has loaded the InputStream data ahead.
            // However, it needs to call MessageUnpacker#reset when the source is different from the previous one.
            Object cachedSrc = messageUnpackerTuple.first();
            if (StreamReadFeature.AUTO_CLOSE_SOURCE.enabledIn(streamReadFeatures) || cachedSrc != src || src instanceof byte[]) {
                // reset() replaces the internal MessageBufferInput and clears the unpacker's
                // internal read buffer to EMPTY_BUFFER. The old ArrayBufferInput becomes
                // unreachable here (we discard the return value), so its byte[] is GC-eligible.
                messageUnpackerTuple.second().reset(input);
            }
            messageUnpacker = messageUnpackerTuple.second();
        }
        messageUnpackerHolder.set(new Tuple<>(src, messageUnpacker));
        ownsThreadLocalUnpacker = true;
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
        type = null;
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
                _streamReadConstraints.validateStringLength(stringValue.length());
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
                type = Type.NULL;
                messageUnpacker.unpackNil();
                if (isObjectValueSet) {
                    streamReadContext.setCurrentName(null);
                    nextToken = JsonToken.PROPERTY_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_NULL;
                }
                break;
            case BOOLEAN:
                boolean b = messageUnpacker.unpackBoolean();
                type = Type.BOOL;
                booleanValue = b;
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
                _streamReadConstraints.validateStringLength(len);
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
                _streamReadConstraints.validateNestingDepth(streamReadContext.getNestingDepth());
                break;
            case MAP:
                nextToken = JsonToken.START_OBJECT;
                streamReadContext = streamReadContext.createChildObjectContext(messageUnpacker.unpackMapHeader());
                _streamReadConstraints.validateNestingDepth(streamReadContext.getNestingDepth());
                break;
            case EXTENSION:
                type = Type.EXT;
                ExtensionTypeHeader header = messageUnpacker.unpackExtensionTypeHeader();
                _streamReadConstraints.validateStringLength(header.getLength());
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
                nextToken = _reportError("Unexpected MessagePack format type: " + valueType);
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
        if (type == null) {
            return _currToken == null ? null : _currToken.asString();
        }
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
            case BOOL:
                return Boolean.toString(booleanValue);
            case BIG_INT:
                return String.valueOf(biValue);
            case EXT:
                try {
                    return deserializedExtensionTypeValue().toString();
                }
                catch (IOException e) {
                    throw _wrapIOFailure(e);
                }
            case NULL:
                return "null";
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
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
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not of binary type");
        }
        switch (type) {
            case BYTES:
                return bytesValue;
            case STRING:
                return stringValue.getBytes(MessagePack.UTF8);
            case EXT:
                return extensionTypeValue.getData();
            case INT:
            case LONG:
            case DOUBLE:
            case BOOL:
            case BIG_INT:
            case NULL:
                return _reportError("Current token (" + _currToken + ") not of binary type");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    public Number getNumberValue()
    {
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
        }
        switch (type) {
            case INT:
                return intValue;
            case LONG:
                return longValue;
            case DOUBLE:
                return doubleValue;
            case BIG_INT:
                return biValue;
            case NULL:
            case BOOL:
            case STRING:
            case BYTES:
            case EXT:
                return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    public int getIntValue()
    {
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
        }
        switch (type) {
            case INT:
                return intValue;
            case LONG:
                if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
                    return _reportError("Numeric value (" + longValue + ") out of range for `int`");
                }
                return (int) longValue;
            case DOUBLE:
                if (!Double.isFinite(doubleValue)) {
                    return _reportError("Cannot convert non-finite double (" + doubleValue + ") to `int`");
                }
                if (doubleValue < Integer.MIN_VALUE || doubleValue > Integer.MAX_VALUE) {
                    return _reportError("Numeric value (" + doubleValue + ") out of range for `int`");
                }
                return (int) doubleValue;
            case BIG_INT:
                try {
                    return biValue.intValueExact();
                }
                catch (ArithmeticException e) {
                    return _reportError("Numeric value (" + biValue + ") out of range for `int`");
                }
            case NULL:
            case BOOL:
            case STRING:
            case BYTES:
            case EXT:
                return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    public long getLongValue()
    {
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
        }
        switch (type) {
            case INT:
                return intValue;
            case LONG:
                return longValue;
            case DOUBLE:
                if (!Double.isFinite(doubleValue)) {
                    return _reportError("Cannot convert non-finite double (" + doubleValue + ") to `long`");
                }
                // (double)Long.MAX_VALUE rounds up to 2^63; use >= to reject 2^63 itself.
                if (doubleValue < Long.MIN_VALUE || doubleValue >= (double) Long.MAX_VALUE) {
                    return _reportError("Numeric value (" + doubleValue + ") out of range for `long`");
                }
                return (long) doubleValue;
            case BIG_INT:
                try {
                    return biValue.longValueExact();
                }
                catch (ArithmeticException e) {
                    return _reportError("Numeric value (" + biValue + ") out of range for `long`");
                }
            case NULL:
            case BOOL:
            case STRING:
            case BYTES:
            case EXT:
                return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    public BigInteger getBigIntegerValue()
    {
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
        }
        switch (type) {
            case INT:
                return BigInteger.valueOf(intValue);
            case LONG:
                return BigInteger.valueOf(longValue);
            case DOUBLE:
                if (!Double.isFinite(doubleValue)) {
                    return _reportError("Cannot convert non-finite double (" + doubleValue + ") to BigInteger");
                }
                return BigDecimal.valueOf(doubleValue).toBigInteger(); // truncates fractional part
            case BIG_INT:
                return biValue;
            case NULL:
            case BOOL:
            case STRING:
            case BYTES:
            case EXT:
                return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    public float getFloatValue()
    {
        // No bounds/range check: a finite double or large BigInteger may overflow to
        // Float.POSITIVE_INFINITY. This is intentional — same as ParserBase and CBORParser.
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
        }
        switch (type) {
            case INT:
                return (float) intValue;
            case LONG:
                return (float) longValue;
            case DOUBLE:
                return (float) doubleValue;
            case BIG_INT:
                return biValue.floatValue();
            case NULL:
            case BOOL:
            case STRING:
            case BYTES:
            case EXT:
                return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    public double getDoubleValue()
    {
        // No bounds/range check: large BigInteger may overflow to Double.POSITIVE_INFINITY,
        // and large long values may lose precision. Intentional — same as ParserBase.
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
        }
        switch (type) {
            case INT:
                return intValue;
            case LONG:
                return (double) longValue;
            case DOUBLE:
                return doubleValue;
            case BIG_INT:
                return biValue.doubleValue();
            case NULL:
            case BOOL:
            case STRING:
            case BYTES:
            case EXT:
                return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    public BigDecimal getDecimalValue()
    {
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
        }
        switch (type) {
            case INT:
                return BigDecimal.valueOf(intValue);
            case LONG:
                return BigDecimal.valueOf(longValue);
            case DOUBLE:
                if (!Double.isFinite(doubleValue)) {
                    return _reportError("Cannot convert non-finite double (" + doubleValue + ") to BigDecimal");
                }
                return BigDecimal.valueOf(doubleValue);
            case BIG_INT:
                return new BigDecimal(biValue);
            case NULL:
            case BOOL:
            case STRING:
            case BYTES:
            case EXT:
                return _reportError("Current token (" + _currToken + ") not numeric, cannot use numeric value accessors");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
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
        if (type == null) {
            return _reportError("Current token (" + _currToken + ") not of embeddable type");
        }
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
            case INT:
            case LONG:
            case DOUBLE:
            case BOOL:
            case BIG_INT:
            case STRING:
            case NULL:
                return _reportError("Current token (" + _currToken + ") not of embeddable type");
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    public NumberType getNumberType()
    {
        if (type == null) {
            return null;
        }
        switch (type) {
            case INT:
                return NumberType.INT;
            case LONG:
                return NumberType.LONG;
            case DOUBLE:
                return NumberType.DOUBLE;
            case BIG_INT:
                return NumberType.BIG_INTEGER;
            case NULL:
            case BOOL:
            case STRING:
            case BYTES:
            case EXT:
                return null;
            default:
                return _reportError("Unexpected MessagePack value type: " + type);
        }
    }

    @Override
    protected void _closeInput() throws IOException
    {
        if (StreamReadFeature.AUTO_CLOSE_SOURCE.enabledIn(_streamReadFeatures)) {
            messageUnpacker.close();
        }
        if (ownsThreadLocalUnpacker) {
            Tuple<Object, MessageUnpacker> tuple = messageUnpackerHolder.get();
            if (tuple != null) {
                if (tuple.first() instanceof byte[]) {
                    // close() calls ArrayBufferInput.close() which sets buffer = null,
                    // releasing the byte[] payload reference held by the unpacker's input.
                    // The unpacker itself is kept alive for reuse on the next parse.
                    tuple.second().close();
                    messageUnpackerHolder.set(new Tuple<>(null, tuple.second()));
                }
                else if (StreamReadFeature.AUTO_CLOSE_SOURCE.enabledIn(_streamReadFeatures)) {
                    // Stream is already closed above; release the reference so it doesn't
                    // linger on the thread until the next parse.
                    messageUnpackerHolder.set(new Tuple<>(null, tuple.second()));
                }
                // else: InputStream with AUTO_CLOSE_SOURCE disabled — keep the reference
                // so the next parse on the same thread can detect same-stream reuse and
                // avoid resetting the unpacker (which would discard its read-ahead buffer).
            }
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
        if (isClosed) {
            return;
        }
        // Parsers are single-threaded by contract; close() is expected on the same
        // thread that created the parser. Cross-thread close is not a supported use case.
        try {
            _closeInput();
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        finally {
            isClosed = true;
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
        // Simple, but need to look for START_OBJECT/ARRAY's "off-by-one" thing:
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
