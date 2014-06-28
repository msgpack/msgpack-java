package org.msgpack.jackson.dataformat.msgpack.msgpack;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.IOContext;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ValueType;
import org.msgpack.value.holder.IntegerHolder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;

public class MessagePackParser extends ParserBase {
    private final MessageUnpacker unpacker;
    private final IntegerHolder integerHolder = new IntegerHolder();
    private ObjectCodec codec;
    private long mapHeaderNum = -1;

    public MessagePackParser(IOContext ctxt, int features, InputStream in) {
        super(ctxt, features);
        unpacker = new MessageUnpacker(in);
    }

    @Override
    protected boolean loadMore() throws IOException {
        System.out.println("loadMore");
        return false;
    }

    @Override
    protected void _finishString() throws IOException, JsonParseException {
        System.out.println("_finishString");
    }

    @Override
    protected void _closeInput() throws IOException {
        System.out.println("_closeInput");
    }

    @Override
    public ObjectCodec getCodec() {
        System.out.println("getCodec");
        return codec;
    }

    @Override
    public void setCodec(ObjectCodec c) {
        System.out.println("setCodec");
        codec = c;
    }

    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        JsonToken nextToken = null;
        if (!unpacker.hasNext()) {
            _currToken = null;
            _parsingContext = _parsingContext.getParent();
            _handleEOF();
            return null;
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
                nextToken = JsonToken.VALUE_NUMBER_INT;
                break;
            case FLOAT:
                nextToken = JsonToken.VALUE_NUMBER_FLOAT;
                break;
            case STRING:
                if (_parsingContext.inObject() && _currToken != JsonToken.FIELD_NAME) {
                    String fieldName = unpacker.unpackString();
                    _parsingContext.setCurrentName(fieldName);
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
                break;
            case MAP:
                nextToken = JsonToken.START_OBJECT;
                mapHeaderNum = unpacker.unpackMapHeader();
                _parsingContext = _parsingContext.createChildObjectContext(-1, -1);
                break;
            case EXTENDED:
                throw new NotImplementedException();
            default:
                throw new IllegalStateException("Shouldn't reach here");
        }
        _currToken = nextToken;
        return nextToken;
    }

    @Override
    public String getText() throws IOException, JsonParseException {
        System.out.println("getText");
        return unpacker.unpackString();
    }

    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        System.out.println("getTextCharacters");
        return new char[0];
    }

    @Override
    public int getTextLength() throws IOException, JsonParseException {
        System.out.println("getTextLength");
        return 0;
    }

    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        System.out.println("getTextOffset");
        return 0;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException {
        System.out.println("getBinaryValue");
        return new byte[0];
    }

    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        unpacker.unpackInteger(integerHolder);
        return integerHolder.isBigInteger() ? integerHolder.toBigInteger() : integerHolder.toLong();
    }

    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return unpacker.unpackDouble();
    }
}
