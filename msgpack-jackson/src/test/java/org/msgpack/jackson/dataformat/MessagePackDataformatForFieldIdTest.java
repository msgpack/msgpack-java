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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.JDKValueInstantiators;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessagePackDataformatForFieldIdTest
{
    static class MessagePackMapDeserializer extends MapDeserializer
    {
        public static KeyDeserializer keyDeserializer = new KeyDeserializer()
        {
            @Override
            public Object deserializeKey(String s, DeserializationContext deserializationContext)
                    throws IOException
            {
                JsonParser parser = deserializationContext.getParser();
                if (parser instanceof MessagePackParser) {
                    MessagePackParser p = (MessagePackParser) parser;
                    if (p.isCurrentFieldId()) {
                        return Integer.valueOf(s);
                    }
                }
                return s;
            }
        };

        public MessagePackMapDeserializer()
        {
            super(
                    TypeFactory.defaultInstance().constructMapType(Map.class, Object.class, Object.class),
                    JDKValueInstantiators.findStdValueInstantiator(null, LinkedHashMap.class),
                    keyDeserializer, null, null);
        }

        public MessagePackMapDeserializer(MapDeserializer src, KeyDeserializer keyDeser,
                JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, NullValueProvider nuller,
                Set<String> ignorable, Set<String> includable)
        {
            super(src, keyDeser, valueDeser, valueTypeDeser, nuller, ignorable, includable);
        }

        @Override
        protected MapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser,
                JsonDeserializer<?> valueDeser, NullValueProvider nuller, Set<String> ignorable,
                Set<String> includable)
        {
            return new MessagePackMapDeserializer(this, keyDeser, (JsonDeserializer<Object>) valueDeser, valueTypeDeser,
                    nuller, ignorable, includable);
        }
    }

    @Test
    public void testMixedKeys()
            throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory())
                .registerModule(new SimpleModule()
                .addDeserializer(Map.class, new MessagePackMapDeserializer()));

        Map<Object, Object> map = new HashMap<>();
        map.put(1, "one");
        map.put("2", "two");

        byte[] bytes = mapper.writeValueAsBytes(map);
        Map<Object, Object> deserializedInit = mapper.readValue(bytes, new TypeReference<Map<Object, Object>>() {});

        Map<Object, Object> expected = new HashMap<>(map);
        Map<Object, Object> actual = new HashMap<>(deserializedInit);

        assertEquals(expected, actual);
    }
}
