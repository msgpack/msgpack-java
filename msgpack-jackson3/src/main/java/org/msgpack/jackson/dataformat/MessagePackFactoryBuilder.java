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
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.base.DecorableTSFactory;
import org.msgpack.core.MessagePack;

public class MessagePackFactoryBuilder
        extends DecorableTSFactory.DecorableTSFBuilder<MessagePackFactory, MessagePackFactoryBuilder>
{
    private MessagePack.PackerConfig packerConfig;
    private boolean reuseResourceInGenerator;
    private boolean reuseResourceInParser;
    private boolean supportIntegerKeys;
    private ExtensionTypeCustomDeserializers extTypeCustomDesers;

    public MessagePackFactoryBuilder()
    {
        super(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(), 0, 0);
        this.packerConfig = MessagePack.DEFAULT_PACKER_CONFIG;
        this.reuseResourceInGenerator = true;
        this.reuseResourceInParser = true;
        this.supportIntegerKeys = false;
    }

    public MessagePackFactoryBuilder(MessagePackFactory base)
    {
        super(base);
        this.packerConfig = base.getPackerConfig().clone();
        this.reuseResourceInGenerator = base.isReuseResourceInGenerator();
        this.reuseResourceInParser = base.isReuseResourceInParser();
        this.supportIntegerKeys = base.isSupportIntegerKeys();
        ExtensionTypeCustomDeserializers srcDesers = base.getExtTypeCustomDesers();
        this.extTypeCustomDesers = srcDesers == null ? null : new ExtensionTypeCustomDeserializers(srcDesers);
    }

    public MessagePackFactoryBuilder packerConfig(MessagePack.PackerConfig config)
    {
        this.packerConfig = config;
        return this;
    }

    public MessagePackFactoryBuilder reuseResourceInGenerator(boolean v)
    {
        this.reuseResourceInGenerator = v;
        return this;
    }

    public MessagePackFactoryBuilder reuseResourceInParser(boolean v)
    {
        this.reuseResourceInParser = v;
        return this;
    }

    public MessagePackFactoryBuilder supportIntegerKeys(boolean v)
    {
        this.supportIntegerKeys = v;
        return this;
    }

    public MessagePackFactoryBuilder extTypeCustomDesers(ExtensionTypeCustomDeserializers desers)
    {
        this.extTypeCustomDesers = desers;
        return this;
    }

    public MessagePack.PackerConfig packerConfig()
    {
        return packerConfig;
    }

    public boolean reuseResourceInGenerator()
    {
        return reuseResourceInGenerator;
    }

    public boolean reuseResourceInParser()
    {
        return reuseResourceInParser;
    }

    public boolean supportIntegerKeys()
    {
        return supportIntegerKeys;
    }

    public ExtensionTypeCustomDeserializers extTypeCustomDesers()
    {
        return extTypeCustomDesers;
    }

    @Override
    public MessagePackFactory build()
    {
        return new MessagePackFactory(this);
    }
}
