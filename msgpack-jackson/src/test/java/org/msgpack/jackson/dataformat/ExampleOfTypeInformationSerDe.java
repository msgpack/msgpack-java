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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ExampleOfTypeInformationSerDe
        extends MessagePackDataformatTestBase
{
    static class A
    {
        private List<String> list = new ArrayList<String>();

        public List<String> getList()
        {
            return list;
        }

        public void setList(List<String> list)
        {
            this.list = list;
        }
    }

    static class B
    {
        private String str;

        public String getStr()
        {
            return str;
        }

        public void setStr(String str)
        {
            this.str = str;
        }
    }

    @JsonSerialize(using = ObjectContainerSerializer.class)
    @JsonDeserialize(using = ObjectContainerDeserializer.class)
    static class ObjectContainer
    {
        private final Map<String, Object> objects;

        public ObjectContainer(Map<String, Object> objects)
        {
            this.objects = objects;
        }

        public Map<String, Object> getObjects()
        {
            return objects;
        }
    }

    static class ObjectContainerSerializer
            extends JsonSerializer<ObjectContainer>
    {
        @Override
        public void serialize(ObjectContainer value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException
        {
            gen.writeStartObject();
            HashMap<String, String> metadata = new HashMap<String, String>();
            for (Map.Entry<String, Object> entry : value.getObjects().entrySet()) {
                metadata.put(entry.getKey(), entry.getValue().getClass().getName());
            }
            gen.writeObjectField("__metadata", metadata);
            gen.writeObjectField("objects", value.getObjects());
            gen.writeEndObject();
        }
    }

    static class ObjectContainerDeserializer
        extends JsonDeserializer<ObjectContainer>
    {
        @Override
        public ObjectContainer deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException
        {
            ObjectContainer objectContainer = new ObjectContainer(new HashMap<String, Object>());
            TreeNode treeNode = p.readValueAsTree();

            Map<String, String> metadata = treeNode.get("__metadata").traverse(p.getCodec()).readValueAs(new TypeReference<Map<String, String>>() {});
            TreeNode dataMapTree = treeNode.get("objects");
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                try {
                    Object o = dataMapTree.get(entry.getKey()).traverse(p.getCodec()).readValueAs(Class.forName(entry.getValue()));
                    objectContainer.getObjects().put(entry.getKey(), o);
                }
                catch (ClassNotFoundException e) {
                    throw new RuntimeException("Failed to deserialize: " + entry, e);
                }
            }

            return objectContainer;
        }
    }

    @Test
    public void test()
            throws IOException
    {
        ObjectContainer objectContainer = new ObjectContainer(new HashMap<String, Object>());
        {
            A a = new A();
            a.setList(Arrays.asList("first", "second", "third"));
            objectContainer.getObjects().put("a", a);

            B b = new B();
            b.setStr("hello world");
            objectContainer.getObjects().put("b", b);

            Double pi = 3.14;
            objectContainer.getObjects().put("pi", pi);
        }

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] bytes = objectMapper.writeValueAsBytes(objectContainer);
        ObjectContainer restored = objectMapper.readValue(bytes, ObjectContainer.class);

        {
            assertEquals(3, restored.getObjects().size());
            A a = (A) restored.getObjects().get("a");
            assertArrayEquals(new String[] {"first", "second", "third"}, a.getList().toArray());
            B b = (B) restored.getObjects().get("b");
            assertEquals("hello world", b.getStr());
            assertEquals(3.14, restored.getObjects().get("pi"));
        }
    }
}
