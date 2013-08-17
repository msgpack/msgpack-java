package org.msgpack.unpacker;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

public class TestUnpackerIterator {

    @Test
    public void testSample() throws Exception {
        MessagePack msgpack = new MessagePack();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Packer packer = msgpack.createPacker(out);
        packer.write(1);
        packer.write(2);
        packer.write(3);
        byte[] bytes = out.toByteArray();

        Unpacker unpacker = msgpack.createUnpacker(
                new ByteArrayInputStream(bytes));
        UnpackerIterator iter = unpacker.iterator();
        unpacker.resetReadByteCount();
        iter.hasNext();
        iter.next();
        assertEquals(1, unpacker.getReadByteCount());
        unpacker.resetReadByteCount();
        iter.hasNext();
        iter.next();
        assertEquals(1, unpacker.getReadByteCount());
        unpacker.resetReadByteCount();
        iter.hasNext();
        iter.next();
        assertEquals(1, unpacker.getReadByteCount());
    }
}
