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
        NormalPojo orig = new NormalPojo();
        orig.setS("komamitsu");
        orig.i = Integer.MAX_VALUE;
        orig.l = Long.MIN_VALUE;
        orig.f = Float.MIN_VALUE;
        orig.d = Double.MAX_VALUE;
        orig.b = new byte[] {0x01, 0x02, (byte) 0xFE, (byte) 0xFF};
        orig.bi = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);

        byte[] bytes = objectMapper.writeValueAsBytes(orig);
        NormalPojo value = objectMapper.readValue(bytes, NormalPojo.class);
        assertEquals(orig.s, value.getS());
        assertEquals(orig.i, value.i);
        assertEquals(orig.l, value.l);
        assertEquals(orig.f, value.f, 0.000001f);
        assertEquals(orig.d, value.d, 0.000001f);
        assertTrue(Arrays.equals(orig.b, value.b));
        assertEquals(orig.bi, value.bi);
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

    public static class NormalPojo {
        private String s;
        public int i;
        public long l;
        public Float f;
        public Double d;
        public byte[] b;
        public BigInteger bi;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

    public static class UsingCustomConstructorPojo
    {
        private final String name;
        private final int age;

        public UsingCustomConstructorPojo(@JsonProperty("name") String name, @JsonProperty("age") int age)
        {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    @JsonIgnoreProperties({ "foo", "bar" })
    public static class IgnoringPropertiesPojo
    {
        private int _code;

        // will not be written as JSON; nor assigned from JSON:
        @JsonIgnore
        public String internal;

        // no annotation, public field is read/written normally
        public String external;

        @JsonIgnore
        public void setCode(int c) { _code = c; }

        // note: will also be ignored because setter has annotation!
        public int getCode() { return _code; }
    }

    public static class ChangingPropertyNamesPojo {
        private String _name;

        // without annotation, we'd get "theName", but we want "name":
        @JsonProperty("name")
        public String getTheName() { return _name; }

        // note: it is enough to add annotation on just getter OR setter;
        // so we can omit it here
        public void setTheName(String n) { _name = n; }
    }

}
