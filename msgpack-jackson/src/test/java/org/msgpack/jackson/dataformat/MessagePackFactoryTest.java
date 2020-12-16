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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.junit.Test;
import org.msgpack.core.MessagePack;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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

    private void assertCopy(boolean advancedConfig)
            throws IOException
    {
        // Build base ObjectMapper
        ObjectMapper objectMapper;
        if (advancedConfig) {
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

            objectMapper = new ObjectMapper(messagePackFactory);

            objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);

            objectMapper.setAnnotationIntrospector(new JsonArrayFormat());
        }
        else {
            MessagePackFactory messagePackFactory = new MessagePackFactory();
            objectMapper = new ObjectMapper(messagePackFactory);
        }

        // Use the original ObjectMapper in advance
        {
            byte[] bytes = objectMapper.writeValueAsBytes(1234);
            assertThat(objectMapper.readValue(bytes, Integer.class), is(1234));
        }

        // Copy the ObjectMapper
        ObjectMapper copiedObjectMapper = objectMapper.copy();

        // Assert the copied ObjectMapper
        JsonFactory copiedFactory = copiedObjectMapper.getFactory();
        assertThat(copiedFactory, is(instanceOf(MessagePackFactory.class)));
        MessagePackFactory copiedMessagePackFactory = (MessagePackFactory) copiedFactory;

        Collection<AnnotationIntrospector> annotationIntrospectors =
                copiedObjectMapper.getSerializationConfig().getAnnotationIntrospector().allIntrospectors();
        assertThat(annotationIntrospectors.size(), is(1));

        if (advancedConfig) {
            assertThat(copiedMessagePackFactory.getPackerConfig().isStr8FormatSupport(), is(false));

            assertThat(copiedMessagePackFactory.getExtTypeCustomDesers().getDeser((byte) 42), is(notNullValue()));
            assertThat(copiedMessagePackFactory.getExtTypeCustomDesers().getDeser((byte) 43), is(nullValue()));

            assertThat(copiedMessagePackFactory.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET), is(false));
            assertThat(copiedMessagePackFactory.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE), is(false));

            assertThat(annotationIntrospectors.stream().findFirst().get(), is(instanceOf(JsonArrayFormat.class)));
        }
        else {
            assertThat(copiedMessagePackFactory.getPackerConfig().isStr8FormatSupport(), is(true));

            assertThat(copiedMessagePackFactory.getExtTypeCustomDesers(), is(nullValue()));

            assertThat(copiedMessagePackFactory.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET), is(true));
            assertThat(copiedMessagePackFactory.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE), is(true));

            assertThat(annotationIntrospectors.stream().findFirst().get(),
                    is(instanceOf(JacksonAnnotationIntrospector.class)));
        }

        // Check the copied ObjectMapper works fine
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        Map<String, Integer> deserialized = copiedObjectMapper
            .readValue(objectMapper.writeValueAsBytes(map), new TypeReference<Map<String, Integer>>() {});
        assertThat(deserialized.size(), is(1));
        assertThat(deserialized.get("one"), is(1));
    }

    @Test
    public void copyWithDefaultConfig()
            throws IOException
    {
        assertCopy(false);
    }

    @Test
    public void copyWithAdvancedConfig()
            throws IOException
    {
        assertCopy(true);
    }
}
