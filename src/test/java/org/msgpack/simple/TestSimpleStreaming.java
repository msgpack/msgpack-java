package org.msgpack.simple;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;


public class TestSimpleStreaming {
    @SuppressWarnings("unused")
    @Test
    public void testSimpleStreamingSerialize() throws IOException {
        MessagePack msgpack = new MessagePack();

        // streaming serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Packer packer = msgpack.createPacker(out);

        packer.write(new String[] {"a", "b", "c"});
        packer.write(new int[] {1, 2, 3});
        packer.write(9.1);

        // streaming deserialize
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Unpacker unpacker = msgpack.createUnpacker(in);

        String[] msg1 = unpacker.read(new String[3]);
        int[] msg2 = unpacker.read(new int[3]);
        double msg3 = unpacker.read(double.class);

        assertArrayEquals(new String[] {"a", "b", "c"}, msg1);
        assertArrayEquals(new int[] {1, 2, 3}, msg2);
        assertEquals(9.1, msg3, 0.001);
    }

    @SuppressWarnings("unused")
    @Test
    public void testBufferPackUnpacker() throws IOException {
        MessagePack msgpack = new MessagePack();

        // streaming serialize into efficient buffer
        BufferPacker packer = msgpack.createBufferPacker();

        packer.write(new String[] {"a", "b", "c"});
        packer.write(new int[] {1, 2, 3});
        packer.write(9.1);

        // streaming deserialize from the buffer
        // BufferUnpacker reduces copies
        BufferUnpacker unpacker = msgpack.createBufferUnpacker(packer.toByteArray());

        String[] msg1 = unpacker.read(new String[3]);
        int[] msg2 = unpacker.read(new int[3]);
        double msg3 = unpacker.read(double.class);

        assertArrayEquals(new String[] {"a", "b", "c"}, msg1);
        assertArrayEquals(new int[] {1, 2, 3}, msg2);
        assertEquals(9.1, msg3, 0.01);
    }
}

