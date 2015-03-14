package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonReadContext;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferInput;
import org.msgpack.core.buffer.InputStreamBufferInput;
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.value.ExtendedValue;
import org.msgpack.value.ValueRef;
import org.msgpack.value.NumberValue;
import org.msgpack.value.ValueType;
import org.msgpack.value.holder.ValueHolder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;

public class MessagePackParser extends ParserMinimalBase {
    private static final ThreadLocal<MessageUnpacker> messageUnpackerHolder = new ThreadLocal<MessageUnpacker>();

    private ObjectCodec codec;
    private JsonReadContext parsingContext;

    private final LinkedList<StackItem> stack = new LinkedList<StackItem>();
    private final ValueHolder valueHolder = new ValueHolder();
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
        this(ctxt, features, new InputStreamBufferInput(in));
    }

    public MessagePackParser(IOContext ctxt, int features, byte[] bytes) throws IOException {
        this(ctxt, features, new ArrayBufferInput(bytes));
    }

    private MessagePackParser(IOContext ctxt, int features, MessageBufferInput input) throws IOException {
        ioContext = ctxt;
        DupDetector dups = Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features)
                ? DupDetector.rootDetector(this) : null;
        parsingContext = JsonReadContext.createRootContext(dups);

        MessageUnpacker messageUnpacker = messageUnpackerHolder.get();
        if (messageUnpacker == null) {
            messageUnpacker = new MessageUnpacker(input);
        }
        else {
            messageUnpacker.reset(input);
        }
        messageUnpackerHolder.set(messageUnpacker);
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

        MessageFormat nextFormat = messageUnpacker.getNextFormat();
        ValueType valueType = nextFormat.getValueType();

        // We should push a new StackItem lazily after updating the current stack.
        StackItem newStack = null;

        switch (valueType) {
            case NIL:
                messageUnpacker.unpackNil();
                nextToken = JsonToken.VALUE_NULL;
                break;
            case BOOLEAN:
                boolean b = messageUnpacker.unpackBoolean();
                nextToken = b ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
                break;
            case INTEGER:
                messageUnpacker.unpackValue(valueHolder);
                nextToken = JsonToken.VALUE_NUMBER_INT;
                break;
            case FLOAT:
                messageUnpacker.unpackValue(valueHolder);
                nextToken = JsonToken.VALUE_NUMBER_FLOAT;
                break;
            case STRING:
                messageUnpacker.unpackValue(valueHolder);
                if (parsingContext.inObject() && _currToken != JsonToken.FIELD_NAME) {
                    parsingContext.setCurrentName(valueHolder.getRef().asRaw().toString());
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_STRING;
                }
                break;
            case BINARY:
                messageUnpacker.unpackValue(valueHolder);
                nextToken = JsonToken.VALUE_EMBEDDED_OBJECT;
                break;
            case ARRAY:
                newStack = new StackItemForArray(messageUnpacker.unpackArrayHeader());
                break;
            case MAP:
                newStack = new StackItemForObject(messageUnpacker.unpackMapHeader());
                break;
            case EXTENDED:
                messageUnpacker.unpackValue(valueHolder);
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
        return valueHolder.getRef().toString();
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
        return valueHolder.getRef().asBinary().toByteArray();
    }

    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        NumberValue numberValue = valueHolder.getRef().asNumber();
        if (numberValue.isValidInt()) {
            return numberValue.toInt();
        }
        else if (numberValue.isValidLong()) {
            return numberValue.toLong();
        }
        else {
            return numberValue.toBigInteger();
        }
    }

    @Override
    public int getIntValue() throws IOException, JsonParseException {
        return valueHolder.getRef().asNumber().toInt();
    }

    @Override
    public long getLongValue() throws IOException, JsonParseException {
        return valueHolder.getRef().asNumber().toLong();
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        return valueHolder.getRef().asNumber().toBigInteger();
    }

    @Override
    public float getFloatValue() throws IOException, JsonParseException {
        return valueHolder.getRef().asFloat().toFloat();
    }

    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return valueHolder.getRef().asFloat().toDouble();
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        ValueRef ref = valueHolder.getRef();

        if (ref.isInteger()) {
            NumberValue number = ref.asNumber();
            //optimization to not convert the value to BigInteger unnecessarily
            if (number.isValidByte() || number.isValidShort() || number.isValidInt() || number.isValidLong()) {
                return BigDecimal.valueOf(number.asInt());
            }
            else {
                return new BigDecimal(number.asBigInteger());
            }
        }

        if (ref.isFloat()) { 
            return BigDecimal.valueOf(ref.asFloat().toDouble());
        }

        throw new UnsupportedOperationException("couldn't parse value as BigDecimal");
    }

    @Override
    public Object getEmbeddedObject() throws IOException, JsonParseException {
        ValueRef ref = valueHolder.getRef();

        if (ref.isBinary()) {
            return ref.asBinary().toByteArray();
        } else if (ref.isExtended()) {
            ExtendedValue extendedValue = ref.asExtended().toValue();
            MessagePackExtendedType extendedType =
                    new MessagePackExtendedType(extendedValue.getExtType(), extendedValue.toByteBuffer());
            return extendedType;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public NumberType getNumberType() throws IOException, JsonParseException {
        NumberValue numberValue = valueHolder.getRef().asNumber();
        if (numberValue.isValidInt()) {
            return NumberType.INT;
        }
        else if (numberValue.isValidLong()) {
            return NumberType.LONG;
        }
        else {
            return NumberType.BIG_INTEGER;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            MessageUnpacker messageUnpacker = getMessageUnpacker();
            messageUnpacker.close();
        }
        catch (Exception e) {
            e.printStackTrace();
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
        MessageUnpacker messageUnpacker = messageUnpackerHolder.get();
        if (messageUnpacker == null) {
            throw new IllegalStateException("messageUnpacker is null");
        }
        return messageUnpacker;
    }
}
