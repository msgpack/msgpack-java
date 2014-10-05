package org.msgpack.jackson.dataformat.msgpack;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.OutputStreamBufferOutput;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MessagePackParserTest extends MessagePackDataformatTestBase {
    @Test
    public void testParserShouldReadObject() throws IOException {
        MessagePacker packer = new MessagePacker(new OutputStreamBufferOutput(out));
        packer.packMapHeader(8);
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

        packer.flush();

        byte[] bytes = out.toByteArray();

        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>(){};
        Map<String, Object> object = objectMapper.readValue(bytes, typeReference);
        assertEquals(8, object.keySet().size());

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
                List<? extends Serializable> expected = Arrays.asList((double)Float.MIN_VALUE, null, "array_child_str");
                assertEquals(expected, v);
            }
            else if (k.equals("bool")) {
                // #8
                bitmap |= 1 << 9;
                assertEquals(false, v);
            }
        }
        assertEquals(0x3FF, bitmap);
    }

    @Test
    public void testParserShouldReadArray() throws IOException {
        MessagePacker packer = new MessagePacker(new OutputStreamBufferOutput(out));
        packer.packArrayHeader(10);
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

        packer.flush();

        bytes = out.toByteArray();

        TypeReference<List<Object>> typeReference = new TypeReference<List<Object>>(){};
        List<Object> array = objectMapper.readValue(bytes, typeReference);
        assertEquals(10, array.size());
        int i = 0;
        // #1
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
    }
}
