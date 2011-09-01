package org.msgpack;

import java.nio.ByteBuffer;
import java.io.IOException;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;


public class TestSimplePackUnpack {
    @SuppressWarnings("unused")
    @Test
    public void testSimplePackUnpack() throws IOException {
        MessagePack msgpack = new MessagePack();
        byte[] raw = msgpack.write(new int[] {1,2,3});

        Value v = msgpack.read(raw);
        int[] a = msgpack.read(raw, new int[3]);

        Value vb = msgpack.read(ByteBuffer.wrap(raw));
        int[] ab = msgpack.read(ByteBuffer.wrap(raw), new int[3]);
    }
}

