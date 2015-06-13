package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonReadContext;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferInput;
import org.msgpack.core.buffer.InputStreamBufferInput;
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.value.Value;
import org.msgpack.value.Variable;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.ValueType;
import org.msgpack.value.ValueFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;

public class MessagePackParser extends ParserMinimalBase {
    private static final ThreadLocal<Tuple<Object, MessageUnpacker>> messageUnpackerHolder =
            new ThreadLocal<Tuple<Object, MessageUnpacker>>();

    private ObjectCodec codec;
    private JsonReadContext parsingContext;

    private final LinkedList<StackItem> stack = new LinkedList<StackItem>();
    private Value value = ValueFactory.newNilValue();
    private Variable var = new Variable();
    private boolean isClosed;
    private long tokenPosition;
    private long currentPosition;
    private final IOContext ioContext;

    private static abstract class StackItem {
        private long numOfElements;

        protected StackItem(long numOfElements) {
            this.numOfElements = numOfElements;
        }

        public void consume() {
           numOfElements--;
        }

        public boolean isEmpty() {
            return numOfElements == 0;
        }
    }

    private static class StackItemForObject extends StackItem {
        StackItemForObject(long numOfElements) {
            super(numOfElements);
        }
    }

    private static class StackItemForArray extends StackItem {
        StackItemForArray(long numOfElements) {
            super(numOfElements);
        }
    }

    public MessagePackParser(IOContext ctxt, int features, InputStream in) throws IOException {
        this(ctxt, features, new InputStreamBufferInput(in), in);
    }

    public MessagePackParser(IOContext ctxt, int features, byte[] bytes) throws IOException {
        this(ctxt, features, new ArrayBufferInput(bytes), bytes);
    }

    private MessagePackParser(IOContext ctxt, int features, MessageBufferInput input, Object src) throws IOException {
        super(features);

        ioContext = ctxt;
        DupDetector dups = Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features)
                ? DupDetector.rootDetector(this) : null;
        parsingContext = JsonReadContext.createRootContext(dups);

        MessageUnpacker messageUnpacker;
        Tuple<Object, MessageUnpacker> messageUnpackerTuple = messageUnpackerHolder.get();
        if (messageUnpackerTuple == null) {
            messageUnpacker = new MessageUnpacker(input);
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
        messageUnpackerHolder.set(new Tuple<Object, MessageUnpacker>(src, messageUnpacker));
    }

    @Override
    public ObjectCodec getCodec() {
        return codec;
    }

    @Override
    public void setCodec(ObjectCodec c) {
        codec = c;
    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        MessageUnpacker messageUnpacker = getMessageUnpacker();
        tokenPosition = messageUnpacker.getTotalReadBytes();

        JsonToken nextToken = null;
        if (parsingContext.inObject() || parsingContext.inArray()) {
            if (stack.getFirst().isEmpty()) {
                stack.pop();
                _currToken = parsingContext.inObject() ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
                parsingContext = parsingContext.getParent();

                return _currToken;
            }
        }

        if (!messageUnpacker.hasNext()) {
            return null;
        }

        ValueType type = messageUnpacker.getNextFormat().getValueType();

        // We should push a new StackItem lazily after updating the current stack.
        StackItem newStack = null;

        switch (type) {
            case NIL:
                messageUnpacker.unpackNil();
                value = ValueFactory.newNilValue();
                nextToken = JsonToken.VALUE_NULL;
                break;
            case BOOLEAN:
                boolean b = messageUnpacker.unpackBoolean();
                value = ValueFactory.newNilValue();
                nextToken = b ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
                break;
            case INTEGER:
                value = messageUnpacker.unpackValue(var);
                nextToken = JsonToken.VALUE_NUMBER_INT;
                break;
            case FLOAT:
                value = messageUnpacker.unpackValue(var);
                nextToken = JsonToken.VALUE_NUMBER_FLOAT;
                break;
            case STRING:
                value = messageUnpacker.unpackValue(var);
                if (parsingContext.inObject() && _currToken != JsonToken.FIELD_NAME) {
                    parsingContext.setCurrentName(value.asRawValue().stringValue());
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_STRING;
                }
                break;
            case BINARY:
                value = messageUnpacker.unpackValue(var);
                if (parsingContext.inObject() && _currToken != JsonToken.FIELD_NAME) {
                    parsingContext.setCurrentName(value.asRawValue().stringValue());
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_EMBEDDED_OBJECT;
                }
                break;
            case ARRAY:
                value = ValueFactory.newNilValue();
                newStack = new StackItemForArray(messageUnpacker.unpackArrayHeader());
                break;
            case MAP:
                value = ValueFactory.newNilValue();
                newStack = new StackItemForObject(messageUnpacker.unpackMapHeader());
                break;
            case EXTENSION:
                value = messageUnpacker.unpackValue(var);
                nextToken = JsonToken.VALUE_EMBEDDED_OBJECT;
                break;
            default:
                throw new IllegalStateException("Shouldn't reach here");
        }
        currentPosition = messageUnpacker.getTotalReadBytes();

        if (parsingContext.inObject() && nextToken != JsonToken.FIELD_NAME || parsingContext.inArray()) {
            stack.getFirst().consume();
        }

        if (newStack != null) {
            stack.push(newStack);
            if (newStack instanceof StackItemForArray) {
                nextToken = JsonToken.START_ARRAY;
                parsingContext = parsingContext.createChildArrayContext(-1, -1);
            }
            else if (newStack instanceof StackItemForObject) {
                nextToken = JsonToken.START_OBJECT;
                parsingContext = parsingContext.createChildObjectContext(-1, -1);
            }
        }
        _currToken = nextToken;

        return nextToken;
    }

    @Override
    protected void _handleEOF() throws JsonParseException {}

    @Override
    public String getText() throws IOException, JsonParseException {
        // This method can be called for new BigInteger(text)
        return value.asRawValue().stringValue();
    }

    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        return getText().toCharArray();
    }

    @Override
    public boolean hasTextCharacters() {
        return false;
    }

    @Override
    public int getTextLength() throws IOException, JsonParseException {
        return getText().length();
    }

    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        return 0;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException {
        return value.asRawValue().getByteArray();
    }

    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        if (value.isIntegerValue()) {
            IntegerValue integerValue = value.asIntegerValue();
            if (integerValue.isInIntRange()) {
                return integerValue.intValue();
            }
            else if (integerValue.isInLongRange()) {
                return integerValue.longValue();
            }
            else {
                return integerValue.bigIntegerValue();
            }
        } else {
            return value.asNumberValue().doubleValue();
        }
    }

    @Override
    public int getIntValue() throws IOException, JsonParseException {
        return value.asNumberValue().intValue();
    }

    @Override
    public long getLongValue() throws IOException, JsonParseException {
        return value.asNumberValue().longValue();
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        return value.asNumberValue().bigIntegerValue();
    }

    @Override
    public float getFloatValue() throws IOException, JsonParseException {
        return value.asNumberValue().floatValue();
    }

    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return value.asNumberValue().doubleValue();
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        if (value.isIntegerValue()) {
            IntegerValue number = value.asIntegerValue();
            //optimization to not convert the value to BigInteger unnecessarily
            if (number.isInLongRange()) {
                return BigDecimal.valueOf(number.longValue());
            }
            else {
                return new BigDecimal(number.bigIntegerValue());
            }
        }
        else if (value.isFloatValue()) {
            return BigDecimal.valueOf(value.asFloatValue().doubleValue());
        }
        else {
            throw new UnsupportedOperationException("Couldn't parse value as BigDecimal. " + value);
        }
    }

    @Override
    public Object getEmbeddedObject() throws IOException, JsonParseException {
        if (value.isBinaryValue()) {
            return value.asBinaryValue().getByteArray();
        } else if (value.isExtensionValue()) {
            return value.asExtensionValue();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public NumberType getNumberType() throws IOException, JsonParseException {
        if (value.isIntegerValue()) {
            IntegerValue integerValue = value.asIntegerValue();
            if (integerValue.isInIntRange()) {
                return NumberType.INT;
            }
            else if (integerValue.isInLongRange()) {
                return NumberType.LONG;
            }
            else {
                return NumberType.BIG_INTEGER;
            }
        } else {
            value.asNumberValue();
            return NumberType.DOUBLE;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                MessageUnpacker messageUnpacker = getMessageUnpacker();
                messageUnpacker.close();
            }
        }
        finally {
            isClosed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public JsonStreamContext getParsingContext() {
        return parsingContext;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return new JsonLocation(ioContext.getSourceReference(), tokenPosition, -1, -1, (int) tokenPosition);
    }

    @Override
    public JsonLocation getCurrentLocation() {
        return new JsonLocation(ioContext.getSourceReference(), currentPosition, -1, -1, (int) currentPosition);
    }

    @Override
    public void overrideCurrentName(String name) {
        try {
            if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
                JsonReadContext parent = parsingContext.getParent();
                parent.setCurrentName(name);
            }
            else {
                parsingContext.setCurrentName(name);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override public String getCurrentName() throws IOException {
        if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
            JsonReadContext parent = parsingContext.getParent();
            return parent.getCurrentName();
        }
        return parsingContext.getCurrentName();
    }

    private MessageUnpacker getMessageUnpacker() {
        Tuple<Object, MessageUnpacker> messageUnpackerTuple = messageUnpackerHolder.get();
        if (messageUnpackerTuple == null) {
            throw new IllegalStateException("messageUnpacker is null");
        }
        return messageUnpackerTuple.second();
    }
}
