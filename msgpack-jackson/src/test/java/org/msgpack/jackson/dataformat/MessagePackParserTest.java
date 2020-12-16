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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.ExtensionValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.ValueFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MessagePackParserTest
        extends MessagePackDataformatTestBase
{
    @Test
    public void testParserShouldReadObject()
            throws IOException
    {
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packMapHeader(9);
        // #1
        packer.packString("str");
        packer.packString("foobar");
        // #2
        packer.packString("int");
        packer.packInt(Integer.MIN_VALUE);
        // #3
        packer.packString("map");
        {
            packer.packMapHeader(2);
            packer.packString("child_str");
            packer.packString("bla bla bla");
            packer.packString("child_int");
            packer.packInt(Integer.MAX_VALUE);
        }
        // #4
        packer.packString("double");
        packer.packDouble(Double.MAX_VALUE);
        // #5
        packer.packString("long");
        packer.packLong(Long.MIN_VALUE);
        // #6
        packer.packString("bi");
        BigInteger bigInteger = new BigInteger(Long.toString(Long.MAX_VALUE));
        packer.packBigInteger(bigInteger.add(BigInteger.ONE));
        // #7
        packer.packString("array");
        {
            packer.packArrayHeader(3);
            packer.packFloat(Float.MIN_VALUE);
            packer.packNil();
            packer.packString("array_child_str");
        }
        // #8
        packer.packString("bool");
        packer.packBoolean(false);
        // #9
        byte[] extPayload = {-80, -50, -25, -114, -25, 16, 60, 68};
        packer.packString("ext");
        packer.packExtensionTypeHeader((byte) 0, extPayload.length);
        packer.writePayload(extPayload);

        packer.flush();

        byte[] bytes = out.toByteArray();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
        Map<String, Object> object = objectMapper.readValue(bytes, typeReference);
        assertEquals(9, object.keySet().size());

        int bitmap = 0;
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if (k.equals("str")) {
                // #1
                bitmap |= 1 << 0;
                assertEquals("foobar", v);
            }
            else if (k.equals("int")) {
                // #2
                bitmap |= 1 << 1;
                assertEquals(Integer.MIN_VALUE, v);
            }
            else if (k.equals("map")) {
                // #3
                bitmap |= 1 << 2;
                @SuppressWarnings("unchecked")
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
                // #4
                bitmap |= 1 << 5;
                assertEquals(Double.MAX_VALUE, (Double) v, 0.0001f);
            }
            else if (k.equals("long")) {
                // #5
                bitmap |= 1 << 6;
                assertEquals(Long.MIN_VALUE, v);
            }
            else if (k.equals("bi")) {
                // #6
                bitmap |= 1 << 7;
                BigInteger bi = new BigInteger(Long.toString(Long.MAX_VALUE));
                assertEquals(bi.add(BigInteger.ONE), v);
            }
            else if (k.equals("array")) {
                // #7
                bitmap |= 1 << 8;
                @SuppressWarnings("unchecked")
                List<? extends Serializable> expected = Arrays.asList((double) Float.MIN_VALUE, null, "array_child_str");
                assertEquals(expected, v);
            }
            else if (k.equals("bool")) {
                // #8
                bitmap |= 1 << 9;
                assertEquals(false, v);
            }
            else if (k.equals("ext")) {
                // #9
                bitmap |= 1 << 10;
                MessagePackExtensionType extensionType = (MessagePackExtensionType) v;
                assertEquals(0, extensionType.getType());
                assertArrayEquals(extPayload, extensionType.getData());
            }
        }
        assertEquals(0x7FF, bitmap);
    }

    @Test
    public void testParserShouldReadArray()
            throws IOException
    {
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packArrayHeader(11);
        // #1
        packer.packArrayHeader(3);
        {
            packer.packLong(Long.MAX_VALUE);
            packer.packNil();
            packer.packString("FOO BAR");
        }
        // #2
        packer.packString("str");
        // #3
        packer.packInt(Integer.MAX_VALUE);
        // #4
        packer.packLong(Long.MIN_VALUE);
        // #5
        packer.packFloat(Float.MAX_VALUE);
        // #6
        packer.packDouble(Double.MIN_VALUE);
        // #7
        BigInteger bi = new BigInteger(Long.toString(Long.MAX_VALUE));
        bi = bi.add(BigInteger.ONE);
        packer.packBigInteger(bi);
        // #8
        byte[] bytes = new byte[] {(byte) 0xFF, (byte) 0xFE, 0x01, 0x00};
        packer.packBinaryHeader(bytes.length);
        packer.writePayload(bytes);
        // #9
        packer.packMapHeader(2);
        {
            packer.packString("child_map_name");
            packer.packString("komamitsu");
            packer.packString("child_map_age");
            packer.packInt(42);
        }
        // #10
        packer.packBoolean(true);
        // #11
        byte[] extPayload = {-80, -50, -25, -114, -25, 16, 60, 68};
        packer.packExtensionTypeHeader((byte) -1, extPayload.length);
        packer.writePayload(extPayload);

        packer.flush();

        bytes = out.toByteArray();

        TypeReference<List<Object>> typeReference = new TypeReference<List<Object>>() {};
        List<Object> array = objectMapper.readValue(bytes, typeReference);
        assertEquals(11, array.size());
        int i = 0;
        // #1
        @SuppressWarnings("unchecked")
        List<Object> childArray = (List<Object>) array.get(i++);
        {
            int j = 0;
            assertEquals(Long.MAX_VALUE, childArray.get(j++));
            assertEquals(null, childArray.get(j++));
            assertEquals("FOO BAR", childArray.get(j++));
        }
        // #2
        assertEquals("str", array.get(i++));
        // #3
        assertEquals(Integer.MAX_VALUE, array.get(i++));
        // #4
        assertEquals(Long.MIN_VALUE, array.get(i++));
        // #5
        assertEquals(Float.MAX_VALUE, (Double) array.get(i++), 0.001f);
        // #6
        assertEquals(Double.MIN_VALUE, (Double) array.get(i++), 0.001f);
        // #7
        assertEquals(bi, array.get(i++));
        // #8
        byte[] bs = (byte[]) array.get(i++);
        assertEquals(4, bs.length);
        assertEquals((byte) 0xFF, bs[0]);
        assertEquals((byte) 0xFE, bs[1]);
        assertEquals((byte) 0x01, bs[2]);
        assertEquals((byte) 0x00, bs[3]);
        // #9
        @SuppressWarnings("unchecked")
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
        // #10
        assertEquals(true, array.get(i++));
        // #11
        MessagePackExtensionType extensionType = (MessagePackExtensionType) array.get(i++);
        assertEquals(-1, extensionType.getType());
        assertArrayEquals(extPayload, extensionType.getData());
    }

    @Test
    public void testMessagePackParserDirectly()
            throws IOException
    {
        MessagePackFactory factory = new MessagePackFactory();
        File tempFile = File.createTempFile("msgpackTest", "msgpack");
        tempFile.deleteOnExit();

        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        MessagePacker packer = MessagePack.newDefaultPacker(fileOutputStream);
        packer.packMapHeader(2);
        packer.packString("zero");
        packer.packInt(0);
        packer.packString("one");
        packer.packFloat(1.0f);
        packer.close();

        JsonParser parser = factory.createParser(tempFile);
        assertTrue(parser instanceof MessagePackParser);

        JsonToken jsonToken = parser.nextToken();
        assertEquals(JsonToken.START_OBJECT, jsonToken);
        assertEquals(-1, parser.getTokenLocation().getLineNr());
        assertEquals(0, parser.getTokenLocation().getColumnNr());
        assertEquals(-1, parser.getCurrentLocation().getLineNr());
        assertEquals(1, parser.getCurrentLocation().getColumnNr());

        jsonToken = parser.nextToken();
        assertEquals(JsonToken.FIELD_NAME, jsonToken);
        assertEquals("zero", parser.getCurrentName());
        assertEquals(1, parser.getTokenLocation().getColumnNr());
        assertEquals(6, parser.getCurrentLocation().getColumnNr());

        jsonToken = parser.nextToken();
        assertEquals(JsonToken.VALUE_NUMBER_INT, jsonToken);
        assertEquals(0, parser.getIntValue());
        assertEquals(6, parser.getTokenLocation().getColumnNr());
        assertEquals(7, parser.getCurrentLocation().getColumnNr());

        jsonToken = parser.nextToken();
        assertEquals(JsonToken.FIELD_NAME, jsonToken);
        assertEquals("one", parser.getCurrentName());
        assertEquals(7, parser.getTokenLocation().getColumnNr());
        assertEquals(11, parser.getCurrentLocation().getColumnNr());
        parser.overrideCurrentName("two");
        assertEquals("two", parser.getCurrentName());

        jsonToken = parser.nextToken();
        assertEquals(JsonToken.VALUE_NUMBER_FLOAT, jsonToken);
        assertEquals(1.0f, parser.getIntValue(), 0.001f);
        assertEquals(11, parser.getTokenLocation().getColumnNr());
        assertEquals(16, parser.getCurrentLocation().getColumnNr());

        jsonToken = parser.nextToken();
        assertEquals(JsonToken.END_OBJECT, jsonToken);
        assertEquals(-1, parser.getTokenLocation().getLineNr());
        assertEquals(16, parser.getTokenLocation().getColumnNr());
        assertEquals(-1, parser.getCurrentLocation().getLineNr());
        assertEquals(16, parser.getCurrentLocation().getColumnNr());

        parser.close();
        parser.close(); // Intentional
    }

    @Test
    public void testReadPrimitives()
            throws Exception
    {
        MessagePackFactory factory = new MessagePackFactory();
        File tempFile = createTempFile();

        FileOutputStream out = new FileOutputStream(tempFile);
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packString("foo");
        packer.packDouble(3.14);
        packer.packInt(Integer.MIN_VALUE);
        packer.packLong(Long.MAX_VALUE);
        packer.packBigInteger(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
        byte[] bytes = {0x00, 0x11, 0x22};
        packer.packBinaryHeader(bytes.length);
        packer.writePayload(bytes);
        packer.close();

        JsonParser parser = factory.createParser(new FileInputStream(tempFile));
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("foo", parser.getText());

        assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        assertEquals(3.14, parser.getDoubleValue(), 0.0001);
        assertEquals("3.14", parser.getText());

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(Integer.MIN_VALUE, parser.getIntValue());
        assertEquals(Integer.MIN_VALUE, parser.getLongValue());
        assertEquals("-2147483648", parser.getText());

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(Long.MAX_VALUE, parser.getLongValue());
        assertEquals("9223372036854775807", parser.getText());

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE), parser.getBigIntegerValue());
        assertEquals("9223372036854775808", parser.getText());

        assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
        assertEquals(bytes.length, parser.getBinaryValue().length);
        assertEquals(bytes[0], parser.getBinaryValue()[0]);
        assertEquals(bytes[1], parser.getBinaryValue()[1]);
        assertEquals(bytes[2], parser.getBinaryValue()[2]);
    }

    @Test
    public void testBigDecimal()
            throws IOException
    {
        double d0 = 1.23456789;
        double d1 = 1.23450000000000000000006789;
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packArrayHeader(5);
        packer.packDouble(d0);
        packer.packDouble(d1);
        packer.packDouble(Double.MIN_VALUE);
        packer.packDouble(Double.MAX_VALUE);
        packer.packDouble(Double.MIN_NORMAL);
        packer.flush();

        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        List<Object> objects = mapper.readValue(out.toByteArray(), new TypeReference<List<Object>>() {});
        assertEquals(5, objects.size());
        int idx = 0;
        assertEquals(BigDecimal.valueOf(d0), objects.get(idx++));
        assertEquals(BigDecimal.valueOf(d1), objects.get(idx++));
        assertEquals(BigDecimal.valueOf(Double.MIN_VALUE), objects.get(idx++));
        assertEquals(BigDecimal.valueOf(Double.MAX_VALUE), objects.get(idx++));
        assertEquals(BigDecimal.valueOf(Double.MIN_NORMAL), objects.get(idx++));
    }

    private File createTestFile()
            throws Exception
    {
        File tempFile = createTempFile(new FileSetup()
        {
            @Override
            public void setup(File f)
                    throws IOException
            {
                MessagePack.newDefaultPacker(new FileOutputStream(f))
                        .packArrayHeader(1).packInt(1)
                        .packArrayHeader(1).packInt(1)
                        .close();
            }
        });
        return tempFile;
    }

    @Test(expected = IOException.class)
    public void testEnableFeatureAutoCloseSource()
            throws Exception
    {
        File tempFile = createTestFile();
        MessagePackFactory factory = new MessagePackFactory();
        FileInputStream in = new FileInputStream(tempFile);
        ObjectMapper objectMapper = new ObjectMapper(factory);
        objectMapper.readValue(in, new TypeReference<List<Integer>>() {});
        objectMapper.readValue(in, new TypeReference<List<Integer>>() {});
    }

    @Test
    public void testDisableFeatureAutoCloseSource()
            throws Exception
    {
        File tempFile = createTestFile();
        FileInputStream in = new FileInputStream(tempFile);
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper.readValue(in, new TypeReference<List<Integer>>() {});
        objectMapper.readValue(in, new TypeReference<List<Integer>>() {});
    }

    @Test
    public void testParseBigDecimal()
            throws IOException
    {
        ArrayList<BigDecimal> list = new ArrayList<BigDecimal>();
        list.add(new BigDecimal(Long.MAX_VALUE));
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] bytes = objectMapper.writeValueAsBytes(list);

        ArrayList<BigDecimal> result = objectMapper.readValue(
                bytes, new TypeReference<ArrayList<BigDecimal>>() {});
        assertEquals(list, result);
    }

    @Test
    public void testReadPrimitiveObjectViaObjectMapper()
            throws Exception
    {
        File tempFile = createTempFile();
        FileOutputStream out = new FileOutputStream(tempFile);

        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packString("foo");
        packer.packLong(Long.MAX_VALUE);
        packer.packDouble(3.14);
        byte[] bytes = {0x00, 0x11, 0x22};
        packer.packBinaryHeader(bytes.length);
        packer.writePayload(bytes);
        packer.close();

        FileInputStream in = new FileInputStream(tempFile);
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        assertEquals("foo", objectMapper.readValue(in, new TypeReference<String>() {}));
        long l = objectMapper.readValue(in, new TypeReference<Long>() {});
        assertEquals(Long.MAX_VALUE, l);
        double d = objectMapper.readValue(in, new TypeReference<Double>() {});
        assertEquals(3.14, d, 0.001);
        byte[] bs = objectMapper.readValue(in, new TypeReference<byte[]>() {});
        assertEquals(bytes.length, bs.length);
        assertEquals(bytes[0], bs[0]);
        assertEquals(bytes[1], bs[1]);
        assertEquals(bytes[2], bs[2]);
    }

    @Test
    public void testBinaryKey()
            throws Exception
    {
        File tempFile = createTempFile();
        FileOutputStream out = new FileOutputStream(tempFile);
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packMapHeader(2);
        packer.packString("foo");
        packer.packDouble(3.14);
        byte[] bytes = "bar".getBytes();
        packer.packBinaryHeader(bytes.length);
        packer.writePayload(bytes);
        packer.packLong(42);
        packer.close();

        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
        Map<String, Object> object = mapper.readValue(new FileInputStream(tempFile), new TypeReference<Map<String, Object>>() {});
        assertEquals(2, object.size());
        assertEquals(3.14, object.get("foo"));
        assertEquals(42, object.get("bar"));
    }

    @Test
    public void testBinaryKeyInNestedObject()
            throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packArrayHeader(2);
        packer.packMapHeader(1);
        byte[] bytes = "bar".getBytes();
        packer.packBinaryHeader(bytes.length);
        packer.writePayload(bytes);
        packer.packInt(12);
        packer.packInt(1);
        packer.close();

        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
        List<Object> objects = mapper.readValue(out.toByteArray(), new TypeReference<List<Object>>() {});
        assertEquals(2, objects.size());
        @SuppressWarnings(value = "unchecked")
        Map<String, Object> map = (Map<String, Object>) objects.get(0);
        assertEquals(1, map.size());
        assertEquals(12, map.get("bar"));
        assertEquals(1, objects.get(1));
    }

    @Test
    public void testByteArrayKey()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker messagePacker = MessagePack.newDefaultPacker(out).packMapHeader(2);
        byte[] k0 = new byte[] {0};
        byte[] k1 = new byte[] {1};
        messagePacker.packBinaryHeader(1).writePayload(k0).packInt(10);
        messagePacker.packBinaryHeader(1).writePayload(k1).packInt(11);
        messagePacker.close();

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(byte[].class, new KeyDeserializer()
        {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException
            {
                return key.getBytes();
            }
        });
        objectMapper.registerModule(module);

        Map<byte[], Integer> map = objectMapper.readValue(
                out.toByteArray(), new TypeReference<Map<byte[], Integer>>() {});
        assertEquals(2, map.size());
        for (Map.Entry<byte[], Integer> entry : map.entrySet()) {
            if (Arrays.equals(entry.getKey(), k0)) {
                assertEquals((Integer) 10, entry.getValue());
            }
            else if (Arrays.equals(entry.getKey(), k1)) {
                assertEquals((Integer) 11, entry.getValue());
            }
        }
    }

    @Test
    public void testIntegerKey()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker messagePacker = MessagePack.newDefaultPacker(out).packMapHeader(2);
        for (int i = 0; i < 2; i++) {
            messagePacker.packInt(i).packInt(i + 10);
        }
        messagePacker.close();

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Integer.class, new KeyDeserializer()
        {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException
            {
                return Integer.valueOf(key);
            }
        });
        objectMapper.registerModule(module);

        Map<Integer, Integer> map = objectMapper.readValue(
                out.toByteArray(), new TypeReference<Map<Integer, Integer>>() {});
        assertEquals(2, map.size());
        assertEquals((Integer) 10, map.get(0));
        assertEquals((Integer) 11, map.get(1));
    }

    @Test
    public void testFloatKey()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker messagePacker = MessagePack.newDefaultPacker(out).packMapHeader(2);
        for (int i = 0; i < 2; i++) {
            messagePacker.packFloat(i).packInt(i + 10);
        }
        messagePacker.close();

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Float.class, new KeyDeserializer()
        {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException
            {
                return Float.valueOf(key);
            }
        });
        objectMapper.registerModule(module);

        Map<Float, Integer> map = objectMapper.readValue(
                out.toByteArray(), new TypeReference<Map<Float, Integer>>() {});
        assertEquals(2, map.size());
        assertEquals((Integer) 10, map.get(0f));
        assertEquals((Integer) 11, map.get(1f));
    }

    @Test
    public void testBooleanKey()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker messagePacker = MessagePack.newDefaultPacker(out).packMapHeader(2);
        messagePacker.packBoolean(true).packInt(10);
        messagePacker.packBoolean(false).packInt(11);
        messagePacker.close();

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Boolean.class, new KeyDeserializer()
        {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException
            {
                return Boolean.valueOf(key);
            }
        });
        objectMapper.registerModule(module);

        Map<Boolean, Integer> map = objectMapper.readValue(
                out.toByteArray(), new TypeReference<Map<Boolean, Integer>>() {});
        assertEquals(2, map.size());
        assertEquals((Integer) 10, map.get(true));
        assertEquals((Integer) 11, map.get(false));
    }

    @Test
    public void extensionTypeCustomDeserializers()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packArrayHeader(3);
        // 0: Integer
        packer.packInt(42);
        // 1: String
        packer.packString("foo bar");
        // 2: ExtensionType
        {
            packer.packExtensionTypeHeader((byte) 31, 4);
            packer.addPayload(new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE});
        }
        packer.close();

        ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
        extTypeCustomDesers.addCustomDeser((byte) 31, new ExtensionTypeCustomDeserializers.Deser() {
                    @Override
                    public Object deserialize(byte[] data)
                            throws IOException
                    {
                        if (Arrays.equals(data, new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE})) {
                            return "Java";
                        }
                        return "Not Java";
                    }
                }
        );
        ObjectMapper objectMapper =
                new ObjectMapper(new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers));

        List<Object> values = objectMapper.readValue(new ByteArrayInputStream(out.toByteArray()), new TypeReference<List<Object>>() {});
        assertThat(values.size(), is(3));
        assertThat((Integer) values.get(0), is(42));
        assertThat((String) values.get(1), is("foo bar"));
        assertThat((String) values.get(2), is("Java"));
    }

    static class TripleBytesPojo
    {
        public byte first;
        public byte second;
        public byte third;

        public TripleBytesPojo(byte first, byte second, byte third)
        {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TripleBytesPojo)) {
                return false;
            }

            TripleBytesPojo that = (TripleBytesPojo) o;

            if (first != that.first) {
                return false;
            }
            if (second != that.second) {
                return false;
            }
            return third == that.third;
        }

        @Override
        public int hashCode()
        {
            int result = first;
            result = 31 * result + (int) second;
            result = 31 * result + (int) third;
            return result;
        }

        @Override
        public String toString()
        {
            // This key format is used when serialized as map key
            return String.format("%d-%d-%d", first, second, third);
        }

        static class Deserializer
                extends StdDeserializer<TripleBytesPojo>
        {
            protected Deserializer()
            {
                super(TripleBytesPojo.class);
            }

            @Override
            public TripleBytesPojo deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException
            {
                return TripleBytesPojo.deserialize(p.getBinaryValue());
            }
        }

        static class KeyDeserializer
                extends com.fasterxml.jackson.databind.KeyDeserializer
        {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt)
                    throws IOException
            {
                String[] values = key.split("-");
                return new TripleBytesPojo(
                        Byte.parseByte(values[0]),
                        Byte.parseByte(values[1]),
                        Byte.parseByte(values[2]));
            }
        }

        static byte[] serialize(TripleBytesPojo obj)
        {
            return new byte[] { obj.first, obj.second, obj.third };
        }

        static TripleBytesPojo deserialize(byte[] bytes)
        {
            return new TripleBytesPojo(bytes[0], bytes[1], bytes[2]);
        }
    }

    @Test
    public void extensionTypeWithPojoInMap()
            throws IOException
    {
        byte extTypeCode = 42;

        ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
        extTypeCustomDesers.addCustomDeser(extTypeCode, new ExtensionTypeCustomDeserializers.Deser()
        {
            @Override
            public Object deserialize(byte[] value)
                    throws IOException
            {
                return TripleBytesPojo.deserialize(value);
            }
        });

        SimpleModule module = new SimpleModule();
        module.addDeserializer(TripleBytesPojo.class, new TripleBytesPojo.Deserializer());
        module.addKeyDeserializer(TripleBytesPojo.class, new TripleBytesPojo.KeyDeserializer());
        ObjectMapper objectMapper = new ObjectMapper(
                new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers))
                .registerModule(module);

        // Prepare serialized data
        Map<TripleBytesPojo, TripleBytesPojo> originalMap = new HashMap<>();
        byte[] serializedData;
        {
            ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
            for (int i = 0; i < 4; i++) {
                TripleBytesPojo keyObj = new TripleBytesPojo((byte) i, (byte) (i + 1), (byte) (i + 2));
                TripleBytesPojo valueObj = new TripleBytesPojo((byte) (i * 2), (byte) (i * 3), (byte) (i * 4));
                ExtensionValue k = ValueFactory.newExtension(extTypeCode, TripleBytesPojo.serialize(keyObj));
                ExtensionValue v = ValueFactory.newExtension(extTypeCode, TripleBytesPojo.serialize(valueObj));
                mapBuilder.put(k, v);
                originalMap.put(keyObj, valueObj);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MessagePacker packer = MessagePack.newDefaultPacker(output);
            MapValue mapValue = mapBuilder.build();
            mapValue.writeTo(packer);
            packer.close();

            serializedData = output.toByteArray();
        }

        Map<TripleBytesPojo, TripleBytesPojo> deserializedMap = objectMapper.readValue(serializedData,
                new TypeReference<Map<TripleBytesPojo, TripleBytesPojo>>() {});

        assertEquals(originalMap.size(), deserializedMap.size());
        for (Map.Entry<TripleBytesPojo, TripleBytesPojo> entry : originalMap.entrySet()) {
            assertEquals(entry.getValue(), deserializedMap.get(entry.getKey()));
        }
    }

    @Test
    public void extensionTypeWithUuidInMap()
            throws IOException
    {
        byte extTypeCode = 42;

        ExtensionTypeCustomDeserializers extTypeCustomDesers = new ExtensionTypeCustomDeserializers();
        extTypeCustomDesers.addCustomDeser(extTypeCode, new ExtensionTypeCustomDeserializers.Deser()
        {
            @Override
            public Object deserialize(byte[] value)
                    throws IOException
            {
                return UUID.fromString(new String(value));
            }
        });

        // In this case with UUID, we don't need to add custom deserializers
        // since jackson-databind already has it.
        ObjectMapper objectMapper = new ObjectMapper(
                new MessagePackFactory().setExtTypeCustomDesers(extTypeCustomDesers));

        // Prepare serialized data
        Map<UUID, UUID> originalMap = new HashMap<>();
        byte[] serializedData;
        {
            ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
            for (int i = 0; i < 4; i++) {
                UUID keyObj = UUID.randomUUID();
                UUID valueObj = UUID.randomUUID();
                ExtensionValue k = ValueFactory.newExtension(extTypeCode, keyObj.toString().getBytes());
                ExtensionValue v = ValueFactory.newExtension(extTypeCode, valueObj.toString().getBytes());
                mapBuilder.put(k, v);
                originalMap.put(keyObj, valueObj);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MessagePacker packer = MessagePack.newDefaultPacker(output);
            MapValue mapValue = mapBuilder.build();
            mapValue.writeTo(packer);
            packer.close();

            serializedData = output.toByteArray();
        }

        Map<UUID, UUID> deserializedMap = objectMapper.readValue(serializedData,
                new TypeReference<Map<UUID, UUID>>() {});

        assertEquals(originalMap.size(), deserializedMap.size());
        for (Map.Entry<UUID, UUID> entry : originalMap.entrySet()) {
            assertEquals(entry.getValue(), deserializedMap.get(entry.getKey()));
        }
    }

    @Test
    public void parserShouldReadStrAsBin()
            throws IOException
    {
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packMapHeader(2);
        // #1
        packer.packString("s");
        packer.packString("foo");
        // #2
        packer.packString("b");
        packer.packString("bar");

        packer.flush();

        byte[] bytes = out.toByteArray();

        BinKeyPojo binKeyPojo = objectMapper.readValue(bytes, BinKeyPojo.class);
        assertEquals("foo", binKeyPojo.s);
        assertArrayEquals("bar".getBytes(), binKeyPojo.b);
    }

    // Test deserializers that parse a string as a number.
    // Actually, com.fasterxml.jackson.databind.deser.std.StdDeserializer._parseInteger() takes care of it.

    @Test
    public void deserializeStringAsInteger()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePack.newDefaultPacker(out).packString(String.valueOf(Integer.MAX_VALUE)).close();

        Integer v = objectMapper.readValue(out.toByteArray(), Integer.class);
        assertThat(v, is(Integer.MAX_VALUE));
    }

    @Test
    public void deserializeStringAsLong()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePack.newDefaultPacker(out).packString(String.valueOf(Long.MIN_VALUE)).close();

        Long v = objectMapper.readValue(out.toByteArray(), Long.class);
        assertThat(v, is(Long.MIN_VALUE));
    }

    @Test
    public void deserializeStringAsFloat()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePack.newDefaultPacker(out).packString(String.valueOf(Float.MAX_VALUE)).close();

        Float v = objectMapper.readValue(out.toByteArray(), Float.class);
        assertThat(v, is(Float.MAX_VALUE));
    }

    @Test
    public void deserializeStringAsDouble()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePack.newDefaultPacker(out).packString(String.valueOf(Double.MIN_VALUE)).close();

        Double v = objectMapper.readValue(out.toByteArray(), Double.class);
        assertThat(v, is(Double.MIN_VALUE));
    }

    @Test
    public void deserializeStringAsBigInteger()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        MessagePack.newDefaultPacker(out).packString(bi.toString()).close();

        BigInteger v = objectMapper.readValue(out.toByteArray(), BigInteger.class);
        assertThat(v, is(bi));
    }

    @Test
    public void deserializeStringAsBigDecimal()
            throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BigDecimal bd = BigDecimal.valueOf(Double.MAX_VALUE);
        MessagePack.newDefaultPacker(out).packString(bd.toString()).close();

        BigDecimal v = objectMapper.readValue(out.toByteArray(), BigDecimal.class);
        assertThat(v, is(bd));
    }

    @Test
    public void handleMissingItemInArray()
            throws IOException
    {
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packArrayHeader(3);
        packer.packString("one");
        packer.packString("two");
        packer.close();

        try {
            objectMapper.readValue(out.toByteArray(), new TypeReference<List<String>>() {});
            fail();
        }
        catch (JsonMappingException e) {
            assertTrue(e.getCause() instanceof JsonEOFException);
        }
    }

    @Test
    public void handleMissingKeyValueInMap()
            throws IOException
    {
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packMapHeader(3);
        packer.packString("one");
        packer.packInt(1);
        packer.packString("two");
        packer.packInt(2);
        packer.close();

        try {
            objectMapper.readValue(out.toByteArray(), new TypeReference<Map<String, Integer>>() {});
            fail();
        }
        catch (JsonEOFException e) {
            assertTrue(true);
        }
    }

    @Test
    public void handleMissingValueInMap()
            throws IOException
    {
        MessagePacker packer = MessagePack.newDefaultPacker(out);
        packer.packMapHeader(3);
        packer.packString("one");
        packer.packInt(1);
        packer.packString("two");
        packer.packInt(2);
        packer.packString("three");
        packer.close();

        try {
            objectMapper.readValue(out.toByteArray(), new TypeReference<Map<String, Integer>>() {});
            fail();
        }
        catch (JsonEOFException e) {
            assertTrue(true);
        }
    }
}
