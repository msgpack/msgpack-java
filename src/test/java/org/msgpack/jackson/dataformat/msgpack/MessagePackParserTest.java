package org.msgpack.jackson.dataformat.msgpack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.msgpack.core.MessagePacker;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MessagePackParserTest extends MessagePackTestBase {
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
        packer.packArrayHeader(9);
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
        packer.packBinary(new byte[]{(byte) 0xFF, (byte) 0xFE, 0x01, 0x00});
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
        assertEquals(9, array.size());
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
        byte[] bs = (byte[]) array.get(i++);
        assertEquals(4, bs.length);
        assertEquals((byte)0xFF, bs[0]);
        assertEquals((byte)0xFE, bs[1]);
        assertEquals((byte)0x01, bs[2]);
        assertEquals((byte)0x00, bs[3]);
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
}
