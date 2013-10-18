package org.msgpack.unpacker;

import org.junit.Assert;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class TestUnpackerByteArray {

    @Test
    public void testBin8MaxSize() throws IOException {
        byte[] in = getBytes(2 << 7 - 1);

        byte[] out = u(in).readByteArray();

        Assert.assertArrayEquals(in, out);
    }

    @Test
    public void testBin8MaxSizeWithBufferUnpacker() throws IOException {
        byte[] in = getBytes(2 << 7 - 1);

        byte[] out = bu(in).readByteArray();

        Assert.assertArrayEquals(in, out);
    }

    @Test
    public void testBin16MaxSize() throws IOException {
        byte[] in = getBytes(2 << 15 - 1);

        byte[] out = bu(in).readByteArray();

        Assert.assertArrayEquals(in, out);
    }

    @Test
    public void testBin16MaxSizeWithBufferUnpacker() throws IOException {
        byte[] in = getBytes(2 << 15 - 1);

        byte[] out = bu(in).readByteArray();

        Assert.assertArrayEquals(in, out);
    }

    private byte[] getBytes(int len) {
        byte[] in = new byte[len];
        new Random().nextBytes(in);
        return in;
    }

    private Unpacker u(byte[] bytes) throws IOException {
        MessagePack msgpack = new MessagePack();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Packer pk = msgpack.createPacker(buf);
        pk.write(bytes);
        return msgpack.createUnpacker(new ByteArrayInputStream(buf.toByteArray()));
    }

    private BufferUnpacker bu(byte[] bytes) throws IOException {
        MessagePack msgpack = new MessagePack();
        BufferPacker pk = msgpack.createBufferPacker();
        pk.write(bytes);
        return msgpack.createBufferUnpacker(pk.toByteArray());
    }
}
