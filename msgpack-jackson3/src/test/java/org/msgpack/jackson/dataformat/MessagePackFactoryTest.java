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

import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TSFBuilder;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.msgpack.core.MessagePack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class MessagePackFactoryTest
        extends MessagePackDataformatTestBase
{
    @Test
    public void testCreateGenerator()
            throws IOException
    {
        JsonEncoding enc = JsonEncoding.UTF8;
        JsonGenerator generator = factory.createGenerator(out, enc);
        assertEquals(MessagePackGenerator.class, generator.getClass());
    }

    @Test
    public void testCreateParser()
            throws IOException
    {
        JsonParser parser = factory.createParser(in);
        assertEquals(MessagePackParser.class, parser.getClass());
    }

    @Test
    public void testCopyWithDefaultConfig()
            throws IOException
    {
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        ObjectMapper objectMapper = new MessagePackMapper(messagePackFactory);

        // Use the original ObjectMapper in advance
        {
            byte[] bytes = objectMapper.writeValueAsBytes(1234);
            assertThat(objectMapper.readValue(bytes, Integer.class), is(1234));
        }

        // Copy the factory
        TokenStreamFactory copiedFactory = messagePackFactory.copy();
        assertThat(copiedFactory, is(instanceOf(MessagePackFactory.class)));
        MessagePackFactory copiedMessagePackFactory = (MessagePackFactory) copiedFactory;

        assertThat(copiedMessagePackFactory.getPackerConfig().isStr8FormatSupport(), is(true));
        assertThat(copiedMessagePackFactory.getExtTypeCustomDesers(), is(nullValue()));

        // Check the copied factory works fine
        ObjectMapper copiedObjectMapper = new MessagePackMapper(copiedMessagePackFactory);
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        Map<String, Integer> deserialized = copiedObjectMapper
            .readValue(objectMapper.writeValueAsBytes(map), new TypeReference<Map<String, Integer>>() {});
        assertThat(deserialized.size(), is(1));
        assertThat(deserialized.get("one"), is(1));
    }

    @Test
    public void testRebuildWithDefaultConfig()
            throws IOException
    {
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        TSFBuilder<?, ?> builder = messagePackFactory.rebuild();
        assertThat(builder, is(instanceOf(MessagePackFactoryBuilder.class)));

        MessagePackFactory rebuilt = (MessagePackFactory) builder.build();
        assertThat(rebuilt, is(not(sameInstance(messagePackFactory))));
        assertThat(rebuilt.getPackerConfig().isStr8FormatSupport(), is(true));
        assertThat(rebuilt.getExtTypeCustomDesers(), is(nullValue()));

        ObjectMapper rebuiltObjectMapper = new MessagePackMapper(rebuilt);
        byte[] bytes = rebuiltObjectMapper.writeValueAsBytes(42);
        assertThat(rebuiltObjectMapper.readValue(bytes, Integer.class), is(42));
    }

    @Test
    public void testRebuildWithAdvancedConfig()
            throws IOException
    {
        ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
        extTypeCustomDesers.addCustomDeser((byte) 42,
                new ExtensionTypeCustomDeserializers.Deser()
                {
                    @Override
                    public Object deserialize(byte[] data)
                            throws IOException
                    {
                        TinyPojo pojo = new TinyPojo();
                        pojo.t = new String(data);
                        return pojo;
                    }
                }
        );
        MessagePack.PackerConfig packerConfig = new MessagePack.PackerConfig().withStr8FormatSupport(false);
        MessagePackFactory messagePackFactory = new MessagePackFactory(packerConfig);
        messagePackFactory.setExtTypeCustomDesers(extTypeCustomDesers);

        MessagePackFactory rebuilt = (MessagePackFactory) messagePackFactory.rebuild().build();
        assertThat(rebuilt, is(not(sameInstance(messagePackFactory))));
        assertThat(rebuilt.getPackerConfig().isStr8FormatSupport(), is(false));
        assertThat(rebuilt.getExtTypeCustomDesers().getDeser((byte) 42), is(notNullValue()));
        assertThat(rebuilt.getExtTypeCustomDesers().getDeser((byte) 43), is(nullValue()));
    }

    @Test
    public void testSnapshotReturnsNewInstance()
    {
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        TokenStreamFactory snapshot = messagePackFactory.snapshot();
        assertThat(snapshot, is(not(sameInstance(messagePackFactory))));
        assertThat(snapshot, is(instanceOf(MessagePackFactory.class)));
    }

    @Test
    public void testCopyWithAdvancedConfig()
            throws IOException
    {
        ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
        extTypeCustomDesers.addCustomDeser((byte) 42,
                new ExtensionTypeCustomDeserializers.Deser()
                {
                    @Override
                    public Object deserialize(byte[] data)
                            throws IOException
                    {
                        TinyPojo pojo = new TinyPojo();
                        pojo.t = new String(data);
                        return pojo;
                    }
                }
        );

        MessagePack.PackerConfig msgpackPackerConfig = new MessagePack.PackerConfig().withStr8FormatSupport(false);

        MessagePackFactory messagePackFactory = new MessagePackFactory(msgpackPackerConfig);
        messagePackFactory.setExtTypeCustomDesers(extTypeCustomDesers);

        ObjectMapper objectMapper = new MessagePackMapper(messagePackFactory);

        // Use the original ObjectMapper in advance
        {
            byte[] bytes = objectMapper.writeValueAsBytes(1234);
            assertThat(objectMapper.readValue(bytes, Integer.class), is(1234));
        }

        // Copy the factory
        TokenStreamFactory copiedFactory = messagePackFactory.copy();
        assertThat(copiedFactory, is(instanceOf(MessagePackFactory.class)));
        MessagePackFactory copiedMessagePackFactory = (MessagePackFactory) copiedFactory;

        assertThat(copiedMessagePackFactory.getPackerConfig().isStr8FormatSupport(), is(false));
        assertThat(copiedMessagePackFactory.getExtTypeCustomDesers().getDeser((byte) 42), is(notNullValue()));
        assertThat(copiedMessagePackFactory.getExtTypeCustomDesers().getDeser((byte) 43), is(nullValue()));

        // Check the copied factory works fine
        ObjectMapper copiedObjectMapper = new MessagePackMapper(copiedMessagePackFactory);
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        Map<String, Integer> deserialized = copiedObjectMapper
            .readValue(objectMapper.writeValueAsBytes(map), new TypeReference<Map<String, Integer>>() {});
        assertThat(deserialized.size(), is(1));
        assertThat(deserialized.get("one"), is(1));
    }
}
