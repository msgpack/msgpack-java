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

public class MessagePackAnnotationTest extends MessagePackTestBase {
    @Test
    public void testNormalBean() throws IOException {
        NormalBean bean = new NormalBean();
        bean.setS("komamitsu");
        bean.i = Integer.MAX_VALUE;
        bean.l = Long.MIN_VALUE;
        bean.f = Float.MIN_VALUE;
        bean.d = Double.MAX_VALUE;
        bean.b = new byte[] {0x01, 0x02, (byte) 0xFE, (byte) 0xFF};
        bean.bi = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);

        byte[] bytes = objectMapper.writeValueAsBytes(bean);
        NormalBean value = objectMapper.readValue(bytes, NormalBean.class);
        assertEquals(bean.s, value.getS());
        assertEquals(bean.i, value.i);
        assertEquals(bean.l, value.l);
        assertEquals(bean.f, value.f, 0.000001f);
        assertEquals(bean.d, value.d, 0.000001f);
        assertTrue(Arrays.equals(bean.b, value.b));
        assertEquals(bean.bi, value.bi);
    }

    @Test
    public void testUsingCustomConstructorBean() throws IOException {
        UsingCustomConstructorBean bean = new UsingCustomConstructorBean("komamitsu", 55);
        byte[] bytes = objectMapper.writeValueAsBytes(bean);
        UsingCustomConstructorBean value = objectMapper.readValue(bytes, UsingCustomConstructorBean.class);
        assertEquals("komamitsu", value.name);
        assertEquals(55, value.age);
    }

    @Test
    public void testIgnoringPropertiesBean() throws IOException {
        IgnoringPropertiesBean bean = new IgnoringPropertiesBean();
        bean.internal = "internal";
        bean.external = "external";
        bean.setCode(1234);
        byte[] bytes = objectMapper.writeValueAsBytes(bean);
        IgnoringPropertiesBean myBean = objectMapper.readValue(bytes, IgnoringPropertiesBean.class);
        assertEquals(0, myBean.getCode());
        assertEquals(null, myBean.internal);
        assertEquals("external", myBean.external);
    }

    @Test
    public void testChangingPropertyNames() throws IOException {
        ChangingPropertyNamesBean bean = new ChangingPropertyNamesBean();
        bean.setTheName("komamitsu");
        byte[] bytes = objectMapper.writeValueAsBytes(bean);
        ChangingPropertyNamesBean myBean = objectMapper.readValue(bytes, ChangingPropertyNamesBean.class);
        assertEquals("komamitsu", myBean.getTheName());
    }

    public static class NormalBean {
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

    public static class UsingCustomConstructorBean
    {
        private final String name;
        private final int age;

        public UsingCustomConstructorBean(@JsonProperty("name") String name, @JsonProperty("age") int age)
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
    public static class IgnoringPropertiesBean
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

    public static class ChangingPropertyNamesBean {
        private String _name;

        // without annotation, we'd get "theName", but we want "name":
        @JsonProperty("name")
        public String getTheName() { return _name; }

        // note: it is enough to add annotation on just getter OR setter;
        // so we can omit it here
        public void setTheName(String n) { _name = n; }
    }

}
