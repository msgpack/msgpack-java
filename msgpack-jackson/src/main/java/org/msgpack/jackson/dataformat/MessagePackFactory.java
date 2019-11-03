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

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.IOContext;
import org.msgpack.core.MessagePack;
import org.msgpack.core.annotations.VisibleForTesting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;

public class MessagePackFactory
        extends JsonFactory
{
    private static final long serialVersionUID = 2578263992015504347L;

    private final MessagePack.PackerConfig packerConfig;
    private boolean reuseResourceInGenerator = true;
    private boolean reuseResourceInParser = true;
    private ExtensionTypeCustomDeserializers extTypeCustomDesers;

    public MessagePackFactory()
    {
        this(MessagePack.DEFAULT_PACKER_CONFIG);
    }

    public MessagePackFactory(MessagePack.PackerConfig packerConfig)
    {
        this.packerConfig = packerConfig;
    }

    public MessagePackFactory(MessagePackFactory src)
    {
        super(src, null);
        this.packerConfig = src.packerConfig.clone();
        this.reuseResourceInGenerator = src.reuseResourceInGenerator;
        this.reuseResourceInParser = src.reuseResourceInParser;
        if (src.extTypeCustomDesers != null) {
            this.extTypeCustomDesers = new ExtensionTypeCustomDeserializers(src.extTypeCustomDesers);
        }
    }

    public MessagePackFactory setReuseResourceInGenerator(boolean reuseResourceInGenerator)
    {
        this.reuseResourceInGenerator = reuseResourceInGenerator;
        return this;
    }

    public MessagePackFactory setReuseResourceInParser(boolean reuseResourceInParser)
    {
        this.reuseResourceInParser = reuseResourceInParser;
        return this;
    }

    public MessagePackFactory setExtTypeCustomDesers(ExtensionTypeCustomDeserializers extTypeCustomDesers)
    {
        this.extTypeCustomDesers = extTypeCustomDesers;
        return this;
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc)
            throws IOException
    {
        return new MessagePackGenerator(_generatorFeatures, _objectCodec, out, packerConfig, reuseResourceInGenerator);
    }

    @Override
    public JsonGenerator createGenerator(File f, JsonEncoding enc)
            throws IOException
    {
        return createGenerator(new FileOutputStream(f), enc);
    }

    @Override
    public JsonGenerator createGenerator(Writer w)
            throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonParser createParser(byte[] data)
            throws IOException, JsonParseException
    {
        IOContext ioContext = _createContext(data, false);
        return _createParser(data, 0, data.length, ioContext);
    }

    @Override
    public JsonParser createParser(InputStream in)
            throws IOException, JsonParseException
    {
        IOContext ioContext = _createContext(in, false);
        return _createParser(in, ioContext);
    }

    @Override
    protected MessagePackParser _createParser(InputStream in, IOContext ctxt)
            throws IOException
    {
        MessagePackParser parser = new MessagePackParser(ctxt, _parserFeatures, _objectCodec, in, reuseResourceInParser);
        if (extTypeCustomDesers != null) {
            parser.setExtensionTypeCustomDeserializers(extTypeCustomDesers);
        }
        return parser;
    }

    @Override
    protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt)
            throws IOException, JsonParseException
    {
        if (offset != 0 || len != data.length) {
            data = Arrays.copyOfRange(data, offset, offset + len);
        }
        MessagePackParser parser = new MessagePackParser(ctxt, _parserFeatures, _objectCodec, data, reuseResourceInParser);
        if (extTypeCustomDesers != null) {
            parser.setExtensionTypeCustomDeserializers(extTypeCustomDesers);
        }
        return parser;
    }

    @Override
    public JsonFactory copy()
    {
        return new MessagePackFactory(this);
    }

    @VisibleForTesting
    MessagePack.PackerConfig getPackerConfig()
    {
        return packerConfig;
    }

    @VisibleForTesting
    boolean isReuseResourceInGenerator()
    {
        return reuseResourceInGenerator;
    }

    @VisibleForTesting
    boolean isReuseResourceInParser()
    {
        return reuseResourceInParser;
    }

    @VisibleForTesting
    ExtensionTypeCustomDeserializers getExtTypeCustomDesers()
    {
        return extTypeCustomDesers;
    }

    @Override
    public String getFormatName()
    {
        return "msgpack";
    }
}
