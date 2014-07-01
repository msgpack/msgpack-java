package org.msgpack.jackson.dataformat.msgpack;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.IOContext;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ValueType;
import org.msgpack.value.holder.IntegerHolder;
import org.msgpack.value.holder.ValueHolder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class MessagePackParser extends ParserBase {
    private final MessageUnpacker unpacker;
    private final IntegerHolder integerHolder = new IntegerHolder();
    private ObjectCodec codec;
    private final LinkedList<StackItem> stack = new LinkedList<StackItem>();

    private String currentString;
    private Number currentNumber;
    private double currentDouble;


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

    public MessagePackParser(IOContext ctxt, int features, InputStream in) {
        super(ctxt, features);
        unpacker = new MessageUnpacker(in);
    }

    @Override
    protected boolean loadMore() throws IOException {
        return false;
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
        JsonToken nextToken = null;
        if (_parsingContext.inObject() || _parsingContext.inArray()) {
            if (stack.getFirst().isEmpty()) {
                stack.pop();
                _currToken = _parsingContext.inObject() ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
                _parsingContext = _parsingContext.getParent();
                return _currToken;
            }
        }

        if (!unpacker.hasNext()) {
            if (!_parsingContext.inObject() && !_parsingContext.inArray()) {
                throw new IllegalStateException("Not in Object nor Array");
            }
            _currToken = _parsingContext.inObject() ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
            _parsingContext = _parsingContext.getParent();
            unpacker.close();
            _handleEOF();
            return _currToken;
        }

        MessageFormat nextFormat = unpacker.getNextFormat();
        ValueType valueType = nextFormat.getValueType();
        switch (valueType) {
            case NIL:
                unpacker.unpackNil();
                nextToken = JsonToken.VALUE_NULL;
                break;
            case BOOLEAN:
                boolean b = unpacker.unpackBoolean();
                nextToken = b ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
                break;
            case INTEGER:
                unpacker.unpackInteger(integerHolder);
                currentNumber = integerHolder.isBigInteger() ? integerHolder.toBigInteger() : integerHolder.toLong();
                nextToken = JsonToken.VALUE_NUMBER_INT;
                break;
            case FLOAT:
                currentDouble = unpacker.unpackDouble();
                nextToken = JsonToken.VALUE_NUMBER_FLOAT;
                break;
            case STRING:
                String str = unpacker.unpackString();
                currentString = str;
                if (_parsingContext.inObject() && _currToken != JsonToken.FIELD_NAME) {
                    _parsingContext.setCurrentName(str);
                    nextToken = JsonToken.FIELD_NAME;
                }
                else {
                    nextToken = JsonToken.VALUE_STRING;
                }
                break;
            case BINARY:
                nextToken = JsonToken.VALUE_STRING;
                break;
            case ARRAY:
                nextToken = JsonToken.START_ARRAY;
                stack.push(new StackItemForArray(unpacker.unpackMapHeader()));
                _parsingContext = _parsingContext.createChildArrayContext(-1, -1);
                break;
            case MAP:
                nextToken = JsonToken.START_OBJECT;
                stack.push(new StackItemForObject(unpacker.unpackMapHeader()));
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
        return currentString;
    }

    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        return new char[0];
    }

    @Override
    public int getTextLength() throws IOException, JsonParseException {
        return 0;
    }

    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        return 0;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException {
        return new byte[0];
    }

    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        return currentNumber;
    }

    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return currentDouble;
    }
}
