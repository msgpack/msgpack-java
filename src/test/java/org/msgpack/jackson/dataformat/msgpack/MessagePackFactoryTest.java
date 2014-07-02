package org.msgpack.jackson.dataformat.msgpack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessagePackFactoryTest {
    MessagePackFactory factory;
    private ByteArrayOutputStream out;
    private ByteArrayInputStream in;
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        factory = new MessagePackFactory();
        objectMapper = new ObjectMapper(factory);
        out = new ByteArrayOutputStream();
        in = new ByteArrayInputStream(new byte[4096]);
    }

    @After
    public void teardown() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCreateGenerator() throws IOException {
        JsonEncoding enc = JsonEncoding.UTF8;
        JsonGenerator generator = factory.createGenerator(out, enc);
        assertEquals(MessagePackGenerator.class, generator.getClass());
    }

    @Test
    public void testCreateParser() throws IOException {
        JsonEncoding enc = JsonEncoding.UTF8;
        JsonParser parser = factory.createParser(in);
        assertEquals(MessagePackParser.class, parser.getClass());
    }

    @Test
    public void testGeneratorShouldWriteObject() throws IOException {
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("str", "komamitsu");
        hashMap.put("int", Integer.MAX_VALUE);
        hashMap.put("long", Long.MIN_VALUE);
        hashMap.put("float", 3.14159f);
        hashMap.put("double", 3.14159d);
        Map<String, Object> childObj = new HashMap<String, Object>();
        childObj.put("co_str", "child#0");
        childObj.put("co_int", 12345);
        hashMap.put("childObj", childObj);

        List<Object> childArray = new ArrayList<Object>();
        childArray.add("child#1");
        childArray.add(1.23f);
        hashMap.put("childArray", childArray);

        long bitmap = 0;
        byte[] bytes = objectMapper.writeValueAsBytes(hashMap);
        MessageUnpacker messageUnpacker = new MessageUnpacker(bytes);
        assertEquals(hashMap.size(), messageUnpacker.unpackMapHeader());
        for (int i = 0; i < hashMap.size(); i++) {
            String key = messageUnpacker.unpackString();
            if (key.equals("str")) {
                assertEquals("komamitsu", messageUnpacker.unpackString());
                bitmap |= 0x1 << 0;
            }
            else if (key.equals("int")) {
                assertEquals(Integer.MAX_VALUE, messageUnpacker.unpackInt());
                bitmap |= 0x1 << 1;
            }
            else if (key.equals("long")) {
                assertEquals(Long.MIN_VALUE, messageUnpacker.unpackLong());
                bitmap |= 0x1 << 2;
            }
            else if (key.equals("float")) {
                assertEquals(3.14159f, messageUnpacker.unpackFloat(), 0.01f);
                bitmap |= 0x1 << 3;
            }
            else if (key.equals("double")) {
                assertEquals(3.14159d, messageUnpacker.unpackDouble(), 0.01f);
                bitmap |= 0x1 << 4;
            }
            else if (key.equals("childObj")) {
                assertEquals(2, messageUnpacker.unpackMapHeader());
                for (int j = 0; j < 2; j++) {
                    String childKey = messageUnpacker.unpackString();
                    if (childKey.equals("co_str")) {
                        assertEquals("child#0", messageUnpacker.unpackString());
                        bitmap |= 0x1 << 5;
                    }
                    else if (childKey.equals("co_int")) {
                        assertEquals(12345, messageUnpacker.unpackInt());
                        bitmap |= 0x1 << 6;
                    }
                    else {
                        assertTrue(false);
                    }
                }
            }
            else if (key.equals("childArray")) {
                assertEquals(2, messageUnpacker.unpackArrayHeader());
                assertEquals("child#1", messageUnpacker.unpackString());
                assertEquals(1.23f, messageUnpacker.unpackFloat(), 0.01f);
                bitmap |= 0x1 << 6;
            }
            else {
                assertTrue(false);
            }
        }
        assertEquals(0x7F, bitmap);
    }

    @Test
    public void testGeneratorShouldWriteArray() throws IOException {
        List<Object> array = new ArrayList<Object>();
        array.add("komamitsu");
        array.add(Integer.MAX_VALUE);
        array.add(Long.MIN_VALUE);
        array.add(3.14159f);
        array.add(3.14159d);
        Map<String, Object> childObject = new HashMap<String, Object>();
        childObject.put("str", "foobar");
        childObject.put("num", 123456);
        array.add(childObject);

        long bitmap = 0;
        byte[] bytes = objectMapper.writeValueAsBytes(array);
        MessageUnpacker messageUnpacker = new MessageUnpacker(bytes);
        assertEquals(array.size(), messageUnpacker.unpackArrayHeader());
        assertEquals("komamitsu", messageUnpacker.unpackString());
        assertEquals(Integer.MAX_VALUE, messageUnpacker.unpackInt());
        assertEquals(Long.MIN_VALUE, messageUnpacker.unpackLong());
        assertEquals(3.14159f, messageUnpacker.unpackFloat(), 0.01f);
        assertEquals(3.14159d, messageUnpacker.unpackDouble(), 0.01f);
        assertEquals(2, messageUnpacker.unpackMapHeader());
        for (int i = 0; i < childObject.size(); i++) {
            String key = messageUnpacker.unpackString();
            if (key.equals("str")) {
                assertEquals("foobar", messageUnpacker.unpackString());
                bitmap |= 0x1 << 0;
            }
            else if (key.equals("num")) {
                assertEquals(123456, messageUnpacker.unpackInt());
                bitmap |= 0x1 << 1;
            }
            else {
                assertTrue(false);
            }
        }
        assertEquals(0x3, bitmap);
    }

    @Test
    public void testParserShouldReadObject() throws IOException {
        MessagePacker packer = new MessagePacker(out);
        packer.packMapHeader(7);
        packer.packString("str");
        packer.packString("foobar");
        packer.packString("int");
        packer.packInt(Integer.MIN_VALUE);
        packer.packString("map");
        {
            packer.packMapHeader(2);
            packer.packString("child_str");
            packer.packString("bla bla bla");
            packer.packString("child_int");
            packer.packInt(Integer.MAX_VALUE);
        }
        packer.packString("double");
        packer.packDouble(Double.MAX_VALUE);
        packer.packString("long");
        packer.packLong(Long.MIN_VALUE);
        packer.packString("bi");
        BigInteger bigInteger = new BigInteger(Long.toString(Long.MAX_VALUE));
        packer.packBigInteger(bigInteger.add(BigInteger.ONE));
        packer.packString("array");
        {
            packer.packArrayHeader(3);
            packer.packFloat(Float.MIN_VALUE);
            packer.packNil();
            packer.packString("array_child_str");
        }
        packer.flush();

        byte[] bytes = out.toByteArray();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>(){};
        Map<String, Object> object = objectMapper.readValue(bytes, typeReference);
        assertEquals(7, object.keySet().size());

        int bitmap = 0;
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if (k.equals("str")) {
                bitmap |= 1 << 0;
                assertEquals("foobar", v);
            }
            else if (k.equals("int")) {
                bitmap |= 1 << 1;
                assertEquals(Integer.MIN_VALUE, v);
            }
            else if (k.equals("map")) {
                bitmap |= 1 << 2;
                Map<String, Object> child = (Map<String, Object>) v;
                assertEquals(2, child.keySet().size());
                for (Map.Entry<String, Object> childEntry : child.entrySet()) {
                    String ck = childEntry.getKey();
                    Object cv = childEntry.getValue();
                    if (ck.equals("child_str")) {
                        bitmap |= 1 << 3;
                        assertEquals("bla bla bla", cv);
                    }
                    else if (ck.equals("child_int")) {
                        bitmap |= 1 << 4;
                        assertEquals(Integer.MAX_VALUE, cv);
                    }
                }
            }
            else if (k.equals("double")) {
                bitmap |= 1 << 5;
                assertEquals(Double.MAX_VALUE, (Double) v, 0.0001f);
            }
            else if (k.equals("long")) {
                bitmap |= 1 << 6;
                assertEquals(Long.MIN_VALUE, v);
            }
            else if (k.equals("bi")) {
                bitmap |= 1 << 7;
                BigInteger bi = new BigInteger(Long.toString(Long.MAX_VALUE));
                assertEquals(bi.add(BigInteger.ONE), v);
            }
            else if (k.equals("array")) {
                bitmap |= 1 << 8;
                List<? extends Serializable> expected = Arrays.asList((double)Float.MIN_VALUE, null, "array_child_str");
                assertEquals(expected, v);
            }
        }
        assertEquals(0x1FF, bitmap);
    }

    @Test
    public void testParserShouldReadArray() throws IOException {
        MessagePacker packer = new MessagePacker(out);
        packer.packArrayHeader(8);
        packer.packArrayHeader(3);
        {
            packer.packLong(Long.MAX_VALUE);
            packer.packNil();
            packer.packString("FOO BAR");
        }
        packer.packString("str");
        packer.packInt(Integer.MAX_VALUE);
        packer.packLong(Long.MIN_VALUE);
        packer.packFloat(Float.MAX_VALUE);
        packer.packDouble(Double.MIN_VALUE);
        BigInteger bi = new BigInteger(Long.toString(Long.MAX_VALUE));
        bi = bi.add(BigInteger.ONE);
        packer.packBigInteger(bi);
        packer.packMapHeader(2);
        {
            packer.packString("child_map_name");
            packer.packString("komamitsu");
            packer.packString("child_map_age");
            packer.packInt(42);
        }
        packer.flush();

        byte[] bytes = out.toByteArray();

        TypeReference<List<Object>> typeReference = new TypeReference<List<Object>>(){};
        List<Object> array = objectMapper.readValue(bytes, typeReference);
        assertEquals(8, array.size());
        int i = 0;
        List<Object> childArray = (List<Object>) array.get(i++);
        {
            int j = 0;
            assertEquals(Long.MAX_VALUE, childArray.get(j++));
            assertEquals(null, childArray.get(j++));
            assertEquals("FOO BAR", childArray.get(j++));
        }
        assertEquals("str", array.get(i++));
        assertEquals(Integer.MAX_VALUE, array.get(i++));
        assertEquals(Long.MIN_VALUE, array.get(i++));
        assertEquals(Float.MAX_VALUE, (Double)array.get(i++), 0.001f);
        assertEquals(Double.MIN_VALUE, (Double)array.get(i++), 0.001f);
        assertEquals(bi, array.get(i++));
        Map<String, Object> childMap = (Map<String, Object>) array.get(i++);
        {
            assertEquals(2, childMap.keySet().size());
            for (Map.Entry<String, Object> entry : childMap.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if (k.equals("child_map_name")) {
                    assertEquals("komamitsu", v);
                }
                else if (k.equals("child_map_age")) {
                    assertEquals(42, v);
                }
            }
        }
    }

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
