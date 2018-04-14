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

    @Test
    public void copyWithDefaultConfig()
            throws IOException
    {
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        ObjectMapper copiedObjectMapper = new ObjectMapper(messagePackFactory).copy();
        JsonFactory copiedFactory = copiedObjectMapper.getFactory();
        assertThat(copiedFactory, is(instanceOf(MessagePackFactory.class)));
        MessagePackFactory copiedMessagePackFactory = (MessagePackFactory) copiedFactory;

        assertThat(copiedMessagePackFactory.getPackerConfig().isStr8FormatSupport(), is(true));

        assertThat(copiedMessagePackFactory.getExtTypeCustomDesers(), is(nullValue()));

        assertThat(copiedMessagePackFactory.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET), is(true));
        assertThat(copiedMessagePackFactory.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE), is(true));

        Collection<AnnotationIntrospector> annotationIntrospectors = copiedObjectMapper.getSerializationConfig().getAnnotationIntrospector().allIntrospectors();
        assertThat(annotationIntrospectors.size(), is(1));
        assertThat(annotationIntrospectors.stream().findFirst().get(), is(instanceOf(JacksonAnnotationIntrospector.class)));

        HashMap<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        Map<String, Integer> deserialized = copiedObjectMapper
            .readValue(objectMapper.writeValueAsBytes(map), new TypeReference<Map<String, Integer>>() {});
        assertThat(deserialized.size(), is(1));
        assertThat(deserialized.get("one"), is(1));
    }

    @Test
    public void copyWithAdvancedConfig()
            throws IOException
    {
        ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
        extTypeCustomDesers.addTargetClass((byte) 42, TinyPojo.class);

        MessagePack.PackerConfig msgpackPackerConfig = new MessagePack.PackerConfig().withStr8FormatSupport(false);

        MessagePackFactory messagePackFactory = new MessagePackFactory(msgpackPackerConfig);
        messagePackFactory.setExtTypeCustomDesers(extTypeCustomDesers);

        ObjectMapper objectMapper = new ObjectMapper(messagePackFactory);

        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);

        objectMapper.setAnnotationIntrospector(new JsonArrayFormat());

        ObjectMapper copiedObjectMapper = objectMapper.copy();
        JsonFactory copiedFactory = copiedObjectMapper.getFactory();
        assertThat(copiedFactory, is(instanceOf(MessagePackFactory.class)));
        MessagePackFactory copiedMessagePackFactory = (MessagePackFactory) copiedFactory;

        assertThat(copiedMessagePackFactory.getPackerConfig().isStr8FormatSupport(), is(false));

        assertThat(copiedMessagePackFactory.getExtTypeCustomDesers().getDeser((byte) 42), is(notNullValue()));
        assertThat(copiedMessagePackFactory.getExtTypeCustomDesers().getDeser((byte) 43), is(nullValue()));

        assertThat(copiedMessagePackFactory.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET), is(false));
        assertThat(copiedMessagePackFactory.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE), is(false));

        Collection<AnnotationIntrospector> annotationIntrospectors = copiedObjectMapper.getSerializationConfig().getAnnotationIntrospector().allIntrospectors();
        assertThat(annotationIntrospectors.size(), is(1));
        assertThat(annotationIntrospectors.stream().findFirst().get(), is(instanceOf(JsonArrayFormat.class)));

        HashMap<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        Map<String, Integer> deserialized = copiedObjectMapper
            .readValue(objectMapper.writeValueAsBytes(map), new TypeReference<Map<String, Integer>>() {});
        assertThat(deserialized.size(), is(1));
        assertThat(deserialized.get("one"), is(1));
    }
}
