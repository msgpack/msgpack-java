package org.msgpack.jackson.dataformat.msgpack;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.IOContext;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferInput;
import org.msgpack.core.buffer.InputStreamBufferInput;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.value.NumberValue;
import org.msgpack.value.ValueType;
import org.msgpack.value.holder.IntegerHolder;
import org.msgpack.value.holder.ValueHolder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class MessagePackParser extends ParserBase {
    private ObjectCodec codec;
    private final LinkedList<StackItem> stack = new LinkedList<StackItem>();
    private static final ThreadLocal<MessageUnpacker> messageUnpackerHolder = new ThreadLocal<MessageUnpacker>();

    private ValueHolder valueHolder = new ValueHolder();

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
        super(ctxt, features);
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
    protected boolean loadMore() throws IOException {
        return messageUnpackerHolder.get().hasNext();
    }

    @Override
    protected void _finishString() throws IOException, JsonParseException {
    }

    @Override
    protected void _closeInput() throws IOException {
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
    public JsonToken nextToken() throws IOException, JsonParseException {
        MessageUnpacker messageUnpacker = messageUnpackerHolder.get();
        JsonToken nextToken = null;
        if (_parsingContext.inObject() || _parsingContext.inArray()) {
            if (stack.getFirst().isEmpty()) {
                stack.pop();
                _currToken = _parsingContext.inObject() ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
                _parsingContext = _parsingContext.getParent();
                return _currToken;
            }
        }

        if (!messageUnpacker.hasNext()) {
            if (!_parsingContext.inObject() && !_parsingContext.inArray()) {
                throw new IllegalStateException("Not in Object nor Array");
            }
            _currToken = _parsingContext.inObject() ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
            _parsingContext = _parsingContext.getParent();
            messageUnpacker.close();
            _handleEOF();
            return _currToken;
        }

        MessageFormat nextFormat = messageUnpacker.getNextFormat();
        ValueType valueType = nextFormat.getValueType();
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
                if (_parsingContext.inObject() && _currToken != JsonToken.FIELD_NAME) {
                    _parsingContext.setCurrentName(valueHolder.getRef().asRaw().toString());
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
                nextToken = JsonToken.START_ARRAY;
                stack.push(new StackItemForArray(messageUnpacker.unpackArrayHeader()));
                _parsingContext = _parsingContext.createChildArrayContext(-1, -1);
                break;
            case MAP:
                nextToken = JsonToken.START_OBJECT;
                stack.push(new StackItemForObject(messageUnpacker.unpackMapHeader()));
                _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
                break;
            case EXTENDED:
                throw new NotImplementedException();
            default:
                throw new IllegalStateException("Shouldn't reach here");
        }
        _currToken = nextToken;

        if ((_parsingContext.inObject() &&
                (_currToken != JsonToken.START_OBJECT && _currToken != JsonToken.FIELD_NAME)) ||
            (_parsingContext.inArray() && _currToken != JsonToken.START_ARRAY)) {
            stack.getFirst().consume();
        }

        return nextToken;
    }

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
    public Object getEmbeddedObject() throws IOException, JsonParseException {
        return valueHolder.getRef().asBinary().toByteArray();
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
}
