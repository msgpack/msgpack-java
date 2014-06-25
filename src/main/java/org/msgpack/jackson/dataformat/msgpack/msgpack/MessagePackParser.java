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
    private ObjectCodec codec;

    public MessagePackParser(IOContext ctxt, int features, InputStream in) {
        super(ctxt, features);
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
        System.out.println("nextToken");
        return null;
    }

    @Override
    public String getText() throws IOException, JsonParseException {
        System.out.println("getText");
        return null;
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
}
