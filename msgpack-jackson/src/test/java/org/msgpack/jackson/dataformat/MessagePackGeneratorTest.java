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

import org.junit.Test;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessagePackGeneratorTest extends MessagePackDataformatTestBase {
    @Test
    public void testGeneratorShouldWriteObject() throws IOException {
        Map<String, Object> hashMap = new HashMap<String, Object>();
        // #1
        hashMap.put("str", "komamitsu");
        // #2
        hashMap.put("boolean", true);
        // #3
        hashMap.put("int", Integer.MAX_VALUE);
        // #4
        hashMap.put("long", Long.MIN_VALUE);
        // #5
        hashMap.put("float", 3.14159f);
        // #6
        hashMap.put("double", 3.14159d);
        // #7
        hashMap.put("bin", new byte[]{0x00, 0x01, (byte)0xFE, (byte)0xFF});
        // #8
        Map<String, Object> childObj = new HashMap<String, Object>();
        childObj.put("co_str", "child#0");
        childObj.put("co_int", 12345);
        hashMap.put("childObj", childObj);
        // #9
        List<Object> childArray = new ArrayList<Object>();
        childArray.add("child#1");
        childArray.add(1.23f);
        hashMap.put("childArray", childArray);

        long bitmap = 0;
        byte[] bytes = objectMapper.writeValueAsBytes(hashMap);
        MessageUnpacker messageUnpacker = new MessageUnpacker(new ArrayBufferInput(bytes));
        assertEquals(hashMap.size(), messageUnpacker.unpackMapHeader());
        for (int i = 0; i < hashMap.size(); i++) {
            String key = messageUnpacker.unpackString();
            if (key.equals("str")) {
                // #1
                assertEquals("komamitsu", messageUnpacker.unpackString());
                bitmap |= 0x1 << 0;
            }
            else if (key.equals("boolean")) {
                // #2
                assertTrue(messageUnpacker.unpackBoolean());
                bitmap |= 0x1 << 1;
            }
            else if (key.equals("int")) {
                // #3
                assertEquals(Integer.MAX_VALUE, messageUnpacker.unpackInt());
                bitmap |= 0x1 << 2;
            }
            else if (key.equals("long")) {
                // #4
                assertEquals(Long.MIN_VALUE, messageUnpacker.unpackLong());
                bitmap |= 0x1 << 3;
            }
            else if (key.equals("float")) {
                // #5
                assertEquals(3.14159f, messageUnpacker.unpackFloat(), 0.01f);
                bitmap |= 0x1 << 4;
            }
            else if (key.equals("double")) {
                // #6
                assertEquals(3.14159d, messageUnpacker.unpackDouble(), 0.01f);
                bitmap |= 0x1 << 5;
            }
            else if (key.equals("bin")) {
                // #7
                assertEquals(4,  messageUnpacker.unpackBinaryHeader());
                assertEquals((byte)0x00, messageUnpacker.unpackByte());
                assertEquals((byte)0x01, messageUnpacker.unpackByte());
                assertEquals((byte)0xFE, messageUnpacker.unpackByte());
                assertEquals((byte)0xFF, messageUnpacker.unpackByte());
                bitmap |= 0x1 << 6;
            }
            else if (key.equals("childObj")) {
                // #8
                assertEquals(2, messageUnpacker.unpackMapHeader());
                for (int j = 0; j < 2; j++) {
                    String childKey = messageUnpacker.unpackString();
                    if (childKey.equals("co_str")) {
                        assertEquals("child#0", messageUnpacker.unpackString());
                        bitmap |= 0x1 << 7;
                    }
                    else if (childKey.equals("co_int")) {
                        assertEquals(12345, messageUnpacker.unpackInt());
                        bitmap |= 0x1 << 8;
                    }
                    else {
                        assertTrue(false);
                    }
                }
            }
            else if (key.equals("childArray")) {
                // #9
                assertEquals(2, messageUnpacker.unpackArrayHeader());
                assertEquals("child#1", messageUnpacker.unpackString());
                assertEquals(1.23f, messageUnpacker.unpackFloat(), 0.01f);
                bitmap |= 0x1 << 9;
            }
            else {
                assertTrue(false);
            }
        }
        assertEquals(0x03FF, bitmap);
    }

    @Test
    public void testGeneratorShouldWriteArray() throws IOException {
        List<Object> array = new ArrayList<Object>();
        // #1
        array.add("komamitsu");
        // #2
        array.add(Integer.MAX_VALUE);
        // #3
        array.add(Long.MIN_VALUE);
        // #4
        array.add(3.14159f);
        // #5
        array.add(3.14159d);
        // #6
        Map<String, Object> childObject = new HashMap<String, Object>();
        childObject.put("str", "foobar");
        childObject.put("num", 123456);
        array.add(childObject);
        // #7
        array.add(false);

        long bitmap = 0;
        byte[] bytes = objectMapper.writeValueAsBytes(array);
        MessageUnpacker messageUnpacker = new MessageUnpacker(new ArrayBufferInput(bytes));
        assertEquals(array.size(), messageUnpacker.unpackArrayHeader());
        // #1
        assertEquals("komamitsu", messageUnpacker.unpackString());
        // #2
        assertEquals(Integer.MAX_VALUE, messageUnpacker.unpackInt());
        // #3
        assertEquals(Long.MIN_VALUE, messageUnpacker.unpackLong());
        // #4
        assertEquals(3.14159f, messageUnpacker.unpackFloat(), 0.01f);
        // #5
        assertEquals(3.14159d, messageUnpacker.unpackDouble(), 0.01f);
        // #6
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
        // #7
        assertEquals(false, messageUnpacker.unpackBoolean());
    }
}
