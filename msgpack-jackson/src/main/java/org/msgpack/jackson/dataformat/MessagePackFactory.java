package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.IOContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class MessagePackFactory extends JsonFactory {
    private static final long serialVersionUID = 2578263992015504347L;
    protected int messagePackGeneratorFeature = 0;
    protected int messagePackParserFeature = 0;

    @Override
    public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        return new MessagePackGenerator(messagePackGeneratorFeature, _objectCodec, out);
    }

    @Override
    public JsonParser createParser(byte[] data) throws IOException, JsonParseException {
        IOContext ioContext = _createContext(data, false);
        return _createParser(data, 0, data.length, ioContext);
    }

    @Override
    public JsonParser createParser(InputStream in) throws IOException, JsonParseException {
        IOContext ioContext = _createContext(in, false);
        return _createParser(in, ioContext);
    }

    @Override
    protected MessagePackParser _createParser(InputStream in, IOContext ctxt) throws IOException {
        MessagePackParser parser = new MessagePackParser(ctxt, messagePackParserFeature, in);
        return parser;
    }

    @Override
    protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException, JsonParseException {
        if (offset != 0 || len != data.length) {
            data = Arrays.copyOfRange(data, offset, offset + len);
        }
        MessagePackParser parser = new MessagePackParser(ctxt, messagePackParserFeature, data);
        return parser;
    }
}
