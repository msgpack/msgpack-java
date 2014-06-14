package org.msgpack.jackson.dataformat.msgpack.msgpack;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.IOContext;

import java.io.IOException;
import java.io.InputStream;

public class MessagePackParser extends ParserBase {
    public MessagePackParser(IOContext ctxt, int features, InputStream in) {
        super(ctxt, features);
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
        return null;
    }

    @Override
    public void setCodec(ObjectCodec c) {

    }

    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        return null;
    }

    @Override
    public String getText() throws IOException, JsonParseException {
        return null;
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
}
