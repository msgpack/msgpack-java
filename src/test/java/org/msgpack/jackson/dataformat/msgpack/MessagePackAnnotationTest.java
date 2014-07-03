package org.msgpack.jackson.dataformat.msgpack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MessagePackAnnotationTest extends MessagePackTestBase {
    @Test
    public void testJsonProperty() throws IOException {
        CtorBean bean = new CtorBean("komamitsu", 55);
        byte[] bytes = objectMapper.writeValueAsBytes(bean);
        CtorBean value = objectMapper.readValue(bytes, CtorBean.class);
        assertEquals("komamitsu", value.name);
        assertEquals(55, value.age);
    }

    public static class CtorBean
    {
        private final String name;
        private final int age;

        public CtorBean(@JsonProperty("name") String name, @JsonProperty("age") int age)
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
}
