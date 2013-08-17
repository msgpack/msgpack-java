package org.msgpack.simple;

import java.nio.ByteBuffer;
import java.io.IOException;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;


public class TestSimplePackUnpack {
    @SuppressWarnings("unused")
    @Test
    public void testSimplePackUnpack() throws IOException {
        MessagePack msgpack = new MessagePack();

        // serialize
        byte[] raw = msgpack.write(new int[] {1,2,3});

        // deserialize to static type
        int[] a = msgpack.read(raw, new int[3]);
        assertArrayEquals(new int[] {1,2,3}, a);

        // deserialize to dynamic type (see TestSimpleDynamicTyping.java)
        Value v = msgpack.read(raw);

        // ByteBuffer is also supported
        int[] ab = msgpack.read(ByteBuffer.wrap(raw), new int[3]);
        assertArrayEquals(new int[] {1,2,3}, ab);
    }
}

