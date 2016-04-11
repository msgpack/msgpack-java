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
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.containsString;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertArrayEquals;

public class MessagePackDataformatForPojoTest
        extends MessagePackDataformatTestBase
{
    @Test
    public void testNormal()
            throws IOException
    {
        byte[] bytes = objectMapper.writeValueAsBytes(normalPojo);
        NormalPojo value = objectMapper.readValue(bytes, NormalPojo.class);
        assertEquals(normalPojo.s, value.getS());
        assertEquals(normalPojo.bool, value.bool);
        assertEquals(normalPojo.i, value.i);
        assertEquals(normalPojo.l, value.l);
        assertEquals(normalPojo.f, value.f, 0.000001f);
        assertEquals(normalPojo.d, value.d, 0.000001f);
        assertArrayEquals(normalPojo.b, value.b);
        assertEquals(normalPojo.bi, value.bi);
        assertEquals(normalPojo.suit, Suit.HEART);
    }

    @Test
    public void testNestedList()
            throws IOException
    {
        byte[] bytes = objectMapper.writeValueAsBytes(nestedListPojo);
        NestedListPojo value = objectMapper.readValue(bytes, NestedListPojo.class);
        assertEquals(nestedListPojo.s, value.s);
        assertArrayEquals(nestedListPojo.strs.toArray(), value.strs.toArray());
    }

    @Test
    public void testNestedListComplex()
            throws IOException
    {
        byte[] bytes = objectMapper.writeValueAsBytes(nestedListComplexPojo);
        NestedListComplexPojo value = objectMapper.readValue(bytes, NestedListComplexPojo.class);
        assertEquals(nestedListPojo.s, value.s);
        assertEquals(nestedListComplexPojo.foos.get(0).t, value.foos.get(0).t);
    }

    @Test
    public void testUsingCustomConstructor()
            throws IOException
    {
        UsingCustomConstructorPojo orig = new UsingCustomConstructorPojo("komamitsu", 55);
        byte[] bytes = objectMapper.writeValueAsBytes(orig);
        UsingCustomConstructorPojo value = objectMapper.readValue(bytes, UsingCustomConstructorPojo.class);
        assertEquals("komamitsu", value.name);
        assertEquals(55, value.age);
    }

    @Test
    public void testIgnoringProperties()
            throws IOException
    {
        IgnoringPropertiesPojo orig = new IgnoringPropertiesPojo();
        orig.internal = "internal";
        orig.external = "external";
        orig.setCode(1234);
        byte[] bytes = objectMapper.writeValueAsBytes(orig);
        IgnoringPropertiesPojo value = objectMapper.readValue(bytes, IgnoringPropertiesPojo.class);
        assertEquals(0, value.getCode());
        assertEquals(null, value.internal);
        assertEquals("external", value.external);
    }

    @Test
    public void testChangingPropertyNames()
            throws IOException
    {
        ChangingPropertyNamesPojo orig = new ChangingPropertyNamesPojo();
        orig.setTheName("komamitsu");
        byte[] bytes = objectMapper.writeValueAsBytes(orig);
        ChangingPropertyNamesPojo value = objectMapper.readValue(bytes, ChangingPropertyNamesPojo.class);
        assertEquals("komamitsu", value.getTheName());
    }

    @Test
    public void testSerializationWithoutSchema()
            throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper(factory); // to not affect shared objectMapper state
        objectMapper.setAnnotationIntrospector(new JsonArrayFormat());
        byte[] bytes = objectMapper.writeValueAsBytes(complexPojo);
        String scheme = new String(bytes, Charset.forName("UTF-8"));
        assertThat(scheme, not(containsString("name"))); // validating schema doesn't contains keys, that's just array
        ComplexPojo value = objectMapper.readValue(bytes, ComplexPojo.class);
        assertEquals("komamitsu", value.name);
        assertEquals(20, value.age);
        assertArrayEquals(complexPojo.values.toArray(), value.values.toArray());
        assertEquals(complexPojo.grades.get("math"), value.grades.get("math"));
    }
}
