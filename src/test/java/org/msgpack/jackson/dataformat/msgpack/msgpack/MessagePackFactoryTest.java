package org.msgpack.jackson.dataformat.msgpack.msgpack;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.jackson.dataformat.msgpack.msgpack.MessagePackFactory;
import org.msgpack.jackson.dataformat.msgpack.msgpack.MessagePackGenerator;
import org.msgpack.jackson.dataformat.msgpack.msgpack.MessagePackParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessagePackFactoryTest {
    MessagePackFactory factory;
    private OutputStream out;
    private InputStream in;

    @Before
    public void setup() {
        factory = new MessagePackFactory();
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
    public void testGeneratorSimply() throws IOException {
        MessagePackFactory factory = new MessagePackFactory();
        factory.setCodec(new MessagePackCodec());
        ObjectMapper objectMapper = new ObjectMapper(factory);
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
        /* TODO
        List<Object> childArray = new ArrayList<Object>();
        childArray.add("child#1");
        childArray.add(1.23f);
        hashMap.put("childArray", childArray);
        */

        byte[] bytes = objectMapper.writeValueAsBytes(hashMap);
        MessageUnpacker messageUnpacker = new MessageUnpacker(bytes);
        assertEquals(6, messageUnpacker.unpackMapHeader());
        for (int i = 0; i < 6; i++) {
            String key = messageUnpacker.unpackString();
            if (key.equals("str")) {
                assertEquals("komamitsu", messageUnpacker.unpackString());
            }
            else if (key.equals("int")) {
                assertEquals(Integer.MAX_VALUE, messageUnpacker.unpackInt());
            }
            else if (key.equals("long")) {
                assertEquals(Long.MIN_VALUE, messageUnpacker.unpackLong());
            }
            else if (key.equals("float")) {
                assertEquals(3.14159f, messageUnpacker.unpackFloat(), 0.01f);
            }
            else if (key.equals("double")) {
                assertEquals(3.14159d, messageUnpacker.unpackDouble(), 0.01f);
            }
            else if (key.equals("childObj")) {
                assertEquals(2, messageUnpacker.unpackMapHeader());
                for (int j = 0; j < 2; j++) {
                    String childKey = messageUnpacker.unpackString();
                    if (childKey.equals("co_str")) {
                        assertEquals("child#0", messageUnpacker.unpackString());
                    }
                    else if (childKey.equals("co_int")) {
                        assertEquals(12345, messageUnpacker.unpackInt());
                    }
                    else {
                        assertTrue(false);
                    }
                }
            }
            else {
                assertTrue(false);
            }
        }

        /*
        Map<String, Object> result =
                objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
        assertEquals(hashMap, result);
        */
    }
}
