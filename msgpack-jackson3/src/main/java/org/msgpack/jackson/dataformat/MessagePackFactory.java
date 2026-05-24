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

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.FormatFeature;
import tools.jackson.core.FormatSchema;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.TSFBuilder;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.Version;
import tools.jackson.core.base.BinaryTSFactory;
import tools.jackson.core.io.IOContext;
import org.msgpack.core.MessagePack;
import org.msgpack.core.annotations.VisibleForTesting;

import org.msgpack.core.buffer.ArrayBufferInput;
import org.msgpack.core.buffer.MessageBufferInput;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MessagePackFactory
        extends BinaryTSFactory
        implements java.io.Serializable
{
    private static final long serialVersionUID = 2578263992015504348L;

    private final MessagePack.PackerConfig packerConfig;
    private boolean reuseResourceInGenerator = true;
    private boolean reuseResourceInParser = true;
    private boolean supportIntegerKeys = false;
    private ExtensionTypeCustomDeserializers extTypeCustomDesers;

    public MessagePackFactory()
    {
        this(MessagePack.DEFAULT_PACKER_CONFIG);
    }

    public MessagePackFactory(MessagePack.PackerConfig packerConfig)
    {
        super(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(), 0, 0);
        this.packerConfig = packerConfig;
    }

    public MessagePackFactory(MessagePackFactory src)
    {
        super(src);
        this.packerConfig = src.packerConfig.clone();
        this.reuseResourceInGenerator = src.reuseResourceInGenerator;
        this.reuseResourceInParser = src.reuseResourceInParser;
        this.supportIntegerKeys = src.supportIntegerKeys;
        if (src.extTypeCustomDesers != null) {
            this.extTypeCustomDesers = new ExtensionTypeCustomDeserializers(src.extTypeCustomDesers);
        }
    }

    protected MessagePackFactory(MessagePackFactoryBuilder b)
    {
        super(b);
        this.packerConfig = b.packerConfig().clone();
        this.reuseResourceInGenerator = b.reuseResourceInGenerator();
        this.reuseResourceInParser = b.reuseResourceInParser();
        this.supportIntegerKeys = b.supportIntegerKeys();
        this.extTypeCustomDesers = b.extTypeCustomDesers();
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

    public MessagePackFactory setSupportIntegerKeys(boolean supportIntegerKeys)
    {
        this.supportIntegerKeys = supportIntegerKeys;
        return this;
    }

    public MessagePackFactory setExtTypeCustomDesers(ExtensionTypeCustomDeserializers extTypeCustomDesers)
    {
        this.extTypeCustomDesers = extTypeCustomDesers;
        return this;
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            InputStream in) throws JacksonException
    {
        try {
            MessagePackParser parser = new MessagePackParser(readCtxt, ioCtxt,
                    readCtxt.getStreamReadFeatures(_streamReadFeatures), in, reuseResourceInParser);
            if (extTypeCustomDesers != null) {
                parser.setExtensionTypeCustomDeserializers(extTypeCustomDesers);
            }
            return parser;
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            byte[] data, int offset, int len) throws JacksonException
    {
        try {
            MessageBufferInput input = new ArrayBufferInput(data, offset, len);
            MessagePackParser parser = new MessagePackParser(readCtxt, ioCtxt,
                    readCtxt.getStreamReadFeatures(_streamReadFeatures), input, data, reuseResourceInParser);
            if (extTypeCustomDesers != null) {
                parser.setExtensionTypeCustomDeserializers(extTypeCustomDesers);
            }
            return parser;
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            DataInput input) throws JacksonException
    {
        return _unsupported();
    }

    @Override
    protected JsonGenerator _createGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt,
            OutputStream out) throws JacksonException
    {
        try {
            return new MessagePackGenerator(writeCtxt, ioCtxt,
                    writeCtxt.getStreamWriteFeatures(_streamWriteFeatures),
                    out, packerConfig, reuseResourceInGenerator, supportIntegerKeys);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public TokenStreamFactory copy()
    {
        return new MessagePackFactory(this);
    }

    @Override
    public TokenStreamFactory snapshot()
    {
        return copy();
    }

    @Override
    public TSFBuilder<?, ?> rebuild()
    {
        return new MessagePackFactoryBuilder(this);
    }

    @Override
    public Version version()
    {
        return PackageVersion.VERSION;
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
    boolean isSupportIntegerKeys()
    {
        return supportIntegerKeys;
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

    @Override
    public boolean canParseAsync()
    {
        return false;
    }

    @Override
    public boolean canUseSchema(FormatSchema schema)
    {
        return false;
    }

    @Override
    public Class<? extends FormatFeature> getFormatReadFeatureType()
    {
        return null;
    }

    @Override
    public Class<? extends FormatFeature> getFormatWriteFeatureType()
    {
        return null;
    }
}
