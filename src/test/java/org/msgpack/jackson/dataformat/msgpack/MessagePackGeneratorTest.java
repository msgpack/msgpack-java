package org.msgpack.jackson.dataformat.msgpack;

import org.junit.Test;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.holder.ValueHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessagePackGeneratorTest extends MessagePackTestBase {
    @Test
    public void testGeneratorShouldWriteObject() throws IOException {
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("str", "komamitsu");
        hashMap.put("int", Integer.MAX_VALUE);
        hashMap.put("long", Long.MIN_VALUE);
        hashMap.put("float", 3.14159f);
        hashMap.put("double", 3.14159d);
        hashMap.put("bin", new byte[]{0x00, 0x01, (byte)0xFE, (byte)0xFF});
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
            else if (key.equals("bin")) {
                assertEquals(4,  messageUnpacker.unpackBinaryHeader());
                assertEquals((byte)0x00, messageUnpacker.unpackByte());
                assertEquals((byte)0x01, messageUnpacker.unpackByte());
                assertEquals((byte)0xFE, messageUnpacker.unpackByte());
                assertEquals((byte)0xFF, messageUnpacker.unpackByte());
                bitmap |= 0x1 << 5;
            }
            else if (key.equals("childObj")) {
                assertEquals(2, messageUnpacker.unpackMapHeader());
                for (int j = 0; j < 2; j++) {
                    String childKey = messageUnpacker.unpackString();
                    if (childKey.equals("co_str")) {
                        assertEquals("child#0", messageUnpacker.unpackString());
                        bitmap |= 0x1 << 6;
                    }
                    else if (childKey.equals("co_int")) {
                        assertEquals(12345, messageUnpacker.unpackInt());
                        bitmap |= 0x1 << 7;
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
                bitmap |= 0x1 << 8;
            }
            else {
                assertTrue(false);
            }
        }
        assertEquals(0x01FF, bitmap);
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
}
