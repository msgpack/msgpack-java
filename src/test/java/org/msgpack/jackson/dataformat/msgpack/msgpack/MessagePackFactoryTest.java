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
import java.util.HashMap;
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
    public void testGeneraterSimply() throws IOException {
        MessagePackFactory factory = new MessagePackFactory();
        factory.setCodec(new MessagePackCodec());
        ObjectMapper objectMapper = new ObjectMapper(factory);
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("name", "komamitsu");
        hashMap.put("age", 99);
        byte[] bytes = objectMapper.writeValueAsBytes(hashMap);
        MessageUnpacker messageUnpacker = new MessageUnpacker(bytes);
        assertEquals(2, messageUnpacker.unpackMapHeader());

        /*
        Map<String, Object> result =
                objectMapper.readValue(bytes, new TypeReference<Map<String, Object>>() {});
        assertEquals(hashMap, result);
        */
    }
}
