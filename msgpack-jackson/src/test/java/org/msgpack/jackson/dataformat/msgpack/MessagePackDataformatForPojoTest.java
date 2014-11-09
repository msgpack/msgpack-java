package org.msgpack.jackson.dataformat.msgpack;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessagePackDataformatForPojoTest extends MessagePackDataformatTestBase {
    @Test
    public void testNormal() throws IOException {
        byte[] bytes = objectMapper.writeValueAsBytes(normalPojo);
        NormalPojo value = objectMapper.readValue(bytes, NormalPojo.class);
        assertEquals(normalPojo.s, value.getS());
        assertEquals(normalPojo.bool, value.bool);
        assertEquals(normalPojo.i, value.i);
        assertEquals(normalPojo.l, value.l);
        assertEquals(normalPojo.f, value.f, 0.000001f);
        assertEquals(normalPojo.d, value.d, 0.000001f);
        assertTrue(Arrays.equals(normalPojo.b, value.b));
        assertEquals(normalPojo.bi, value.bi);
        assertEquals(normalPojo.suit, Suit.HEART);
    }

    @Test
    public void testNestedList() throws IOException {
        byte[] bytes = objectMapper.writeValueAsBytes(nestedListPojo);
        NestedListPojo value = objectMapper.readValue(bytes, NestedListPojo.class);
        assertEquals(nestedListPojo.s, value.s);
        assertTrue(Arrays.equals(nestedListPojo.strs.toArray(), value.strs.toArray()));
    }

    @Test
    public void testNestedListComplex() throws IOException {
        byte[] bytes = objectMapper.writeValueAsBytes(nestedListComplexPojo);
        NestedListComplexPojo value = objectMapper.readValue(bytes, NestedListComplexPojo.class);
        assertEquals(nestedListPojo.s, value.s);
        assertEquals(nestedListComplexPojo.foos.get(0).t, value.foos.get(0).t);
    }

    @Test
    public void testUsingCustomConstructor() throws IOException {
        UsingCustomConstructorPojo orig = new UsingCustomConstructorPojo("komamitsu", 55);
        byte[] bytes = objectMapper.writeValueAsBytes(orig);
        UsingCustomConstructorPojo value = objectMapper.readValue(bytes, UsingCustomConstructorPojo.class);
        assertEquals("komamitsu", value.name);
        assertEquals(55, value.age);
    }

    @Test
    public void testIgnoringProperties() throws IOException {
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
    public void testChangingPropertyNames() throws IOException {
        ChangingPropertyNamesPojo orig = new ChangingPropertyNamesPojo();
        orig.setTheName("komamitsu");
        byte[] bytes = objectMapper.writeValueAsBytes(orig);
        ChangingPropertyNamesPojo value = objectMapper.readValue(bytes, ChangingPropertyNamesPojo.class);
        assertEquals("komamitsu", value.getTheName());
    }

}
