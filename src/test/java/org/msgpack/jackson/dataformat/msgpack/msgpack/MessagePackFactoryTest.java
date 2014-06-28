package org.msgpack.jackson.dataformat.msgpack.msgpack;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        packer.packMapHeader(5);
        packer.packString("str");
        packer.packString("foobar");
        packer.packString("int");
        packer.packInt(Integer.MIN_VALUE);
        packer.packString("double");
        packer.packDouble(Double.MAX_VALUE);
        packer.packString("long");
        packer.packDouble(Long.MIN_VALUE);
        packer.packString("bi");
        BigInteger bigInteger = new BigInteger(Long.toString(Long.MAX_VALUE));
        packer.packBigInteger(bigInteger.add(BigInteger.ONE));
        packer.flush();

        byte[] bytes = out.toByteArray();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>(){};
        Map<String, Object> value = objectMapper.readValue(bytes, typeReference);
        assertEquals(5, value.keySet().size());
    }
}
