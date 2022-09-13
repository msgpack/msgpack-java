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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.*;

public class TimestampExtensionModuleTest
{
    private ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
    private SingleInstant singleInstant = new SingleInstant();
    private TripleInstants tripleInstants = new TripleInstants();

    private static class SingleInstant
    {
        public Instant instant;
    }

    private static class TripleInstants
    {
        public Instant a;
        public Instant b;
        public Instant c;
    }

    @Before
    public void setUp()
            throws Exception
    {
        objectMapper.registerModule(TimestampExtensionModule.INSTANCE);
    }

    @Test
    public void testSingleInstantPojo()
            throws IOException
    {
        singleInstant.instant = Instant.now();
        byte[] bytes = objectMapper.writeValueAsBytes(singleInstant);
        SingleInstant deserialized = objectMapper.readValue(bytes, SingleInstant.class);
        assertEquals(singleInstant.instant, deserialized.instant);
    }

    @Test
    public void testTripleInstantsPojo()
            throws IOException
    {
        Instant now = Instant.now();
        tripleInstants.a = now.minusSeconds(1);
        tripleInstants.b = now;
        tripleInstants.c = now.plusSeconds(1);
        byte[] bytes = objectMapper.writeValueAsBytes(tripleInstants);
        TripleInstants deserialized = objectMapper.readValue(bytes, TripleInstants.class);
        assertEquals(now.minusSeconds(1), deserialized.a);
        assertEquals(now, deserialized.b);
        assertEquals(now.plusSeconds(1), deserialized.c);
    }
}