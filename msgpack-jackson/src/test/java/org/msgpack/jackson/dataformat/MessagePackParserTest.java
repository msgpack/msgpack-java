package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.OutputStreamBufferOutput;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

public class MessagePackParserTest extends MessagePackDataformatTestBase {
    @Test
    public void testParserShouldReadObject() throws IOException {
        MessagePacker packer = new MessagePacker(new OutputStreamBufferOutput(out));
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
        packer.packExtendedTypeHeader(0, extPayload.length);
        packer.writePayload(extPayload);

        packer.flush();

        byte[] bytes = out.toByteArray();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>(){};
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
                List<? extends Serializable> expected = Arrays.asList((double)Float.MIN_VALUE, null, "array_child_str");
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
                MessagePackExtendedType extendedType = (MessagePackExtendedType) v;
                assertEquals(0, extendedType.extType());
                assertEquals(ByteBuffer.wrap(extPayload), extendedType.byteBuffer());
            }
        }
        assertEquals(0x7FF, bitmap);
    }

    @Test
    public void testParserShouldReadArray() throws IOException {
        MessagePacker packer = new MessagePacker(new OutputStreamBufferOutput(out));
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
        byte[] bytes = new byte[]{(byte) 0xFF, (byte) 0xFE, 0x01, 0x00};
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
        packer.packExtendedTypeHeader(-1, extPayload.length);
        packer.writePayload(extPayload);

        packer.flush();

        bytes = out.toByteArray();

        TypeReference<List<Object>> typeReference = new TypeReference<List<Object>>(){};
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
        assertEquals(Float.MAX_VALUE, (Double)array.get(i++), 0.001f);
        // #6
        assertEquals(Double.MIN_VALUE, (Double)array.get(i++), 0.001f);
        // #7
        assertEquals(bi, array.get(i++));
        // #8
        byte[] bs = (byte[]) array.get(i++);
        assertEquals(4, bs.length);
        assertEquals((byte)0xFF, bs[0]);
        assertEquals((byte)0xFE, bs[1]);
        assertEquals((byte)0x01, bs[2]);
        assertEquals((byte)0x00, bs[3]);
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
        MessagePackExtendedType extendedType = (MessagePackExtendedType) array.get(i++);
        assertEquals(-1, extendedType.extType());
        assertEquals(ByteBuffer.wrap(extPayload), extendedType.byteBuffer());
    }

    @Test
    public void testMessagePackParserDirectly() throws IOException {
        MessagePackFactory messagePackFactory = new MessagePackFactory();
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

        JsonParser parser = messagePackFactory.createParser(tempFile);
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

        assertNull(parser.nextToken());

        parser.close();
        parser.close(); // Intentional
    }

    @Test
    public void testBigDecimal() throws IOException {
        double d0 = 1.23456789;
        double d1 = 1.23450000000000000000006789;
        MessagePacker packer = new MessagePacker(new OutputStreamBufferOutput(out));
        packer.packArrayHeader(5);
        packer.packDouble(d0);
        packer.packDouble(d1);
        packer.packDouble(Double.MIN_VALUE);
        packer.packDouble(Double.MAX_VALUE);
        packer.packDouble(Double.MIN_NORMAL);
        packer.flush();

        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        List<Object> objects = mapper.readValue(out.toByteArray(), new TypeReference<List<Object>>(){});
        assertEquals(5, objects.size());
        int idx = 0;
        assertEquals(BigDecimal.valueOf(d0), objects.get(idx++));
        assertEquals(BigDecimal.valueOf(d1), objects.get(idx++));
        assertEquals(BigDecimal.valueOf(Double.MIN_VALUE), objects.get(idx++));
        assertEquals(BigDecimal.valueOf(Double.MAX_VALUE), objects.get(idx++));
        assertEquals(BigDecimal.valueOf(Double.MIN_NORMAL), objects.get(idx++));
    }

    private File createTestFile() throws IOException {
        File tempFile = File.createTempFile("test", "msgpack");
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        MessagePack.newDefaultPacker(out)
                .packArrayHeader(1).packInt(1)
                .packArrayHeader(1).packInt(1)
                .close();
        return tempFile;
    }

    @Test(expected = IOException.class)
    public void testEnableFeatureAutoCloseSource() throws IOException {
        File tempFile = createTestFile();
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        FileInputStream in = new FileInputStream(tempFile);
        ObjectMapper objectMapper = new ObjectMapper(messagePackFactory);
        objectMapper.readValue(in, new TypeReference<List<Integer>>() {});
        objectMapper.readValue(in, new TypeReference<List<Integer>>() {});
    }

    // FIXME
    @Test(expected = IOException.class)
    public void testDisableFeatureAutoCloseSource() throws IOException {
        File tempFile = createTestFile();
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        FileInputStream in = new FileInputStream(tempFile);
        ObjectMapper objectMapper = new ObjectMapper(messagePackFactory);
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper.readValue(in, new TypeReference<List<Integer>>() {});
        objectMapper.readValue(in, new TypeReference<List<Integer>>() {});
    }
}
