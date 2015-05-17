package org.msgpack.unpacker;

import org.junit.Before;
import org.junit.Test;
import org.msgpack.MessagePack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class TestMessagePackUnpacker {
    private MessagePack msgpack;

    @Before
    public void setup() {
        msgpack = new MessagePack();
    }

    @Test
    public void testStr8() throws IOException {
        // Deserialize a data that another platform serialized a string "xxx...xxx" (length: 128).
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 0xD9: str8, 0x80: length: 128
        out.write(new byte[] {(byte) 0xD9, (byte) 0x80});
        for (int i = 0; i < 128; i++) {
            // 0x78: 'x'
            out.write(0x78);
        }
        Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(out.toByteArray()));
        String string = unpacker.readString();
        assertEquals(128, string.length());
        for (int i = 0; i < 128; i++) {
            assertEquals('x', string.charAt(i));
        }
    }
}