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

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.IOContext;

import java.io.*;
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
    public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
        return createGenerator(new FileOutputStream(f), enc);
    }

    @Override
    public JsonGenerator createGenerator(Writer w) throws IOException {
        throw new UnsupportedOperationException();
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
