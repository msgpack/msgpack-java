package org.msgpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Iterator;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;

import org.junit.Test;


public class TestCrossLang {
    private byte[] readData(String path) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        FileInputStream input = new FileInputStream(path);
        byte[] buffer = new byte[32*1024];
        while(true) {
            int count = input.read(buffer);
            if(count < 0) {
                break;
            }
            bo.write(buffer, 0, count);
        }
        return bo.toByteArray();
    }

    private byte[] readCompactTestData() throws IOException {
        return readData("src/test/resources/cases_compact.mpac");
    }

    private byte[] readTestData() throws IOException {
        return readData("src/test/resources/cases.mpac");
    }

    @Test
    public void testReadValue() throws IOException {
        MessagePack msgpack = new MessagePack();
        byte[] a = readTestData();
        byte[] b = readCompactTestData();

        BufferUnpacker au = msgpack.createBufferUnpacker().wrap(a);
        BufferUnpacker bu = msgpack.createBufferUnpacker().wrap(b);

        Iterator<Value> at = au.iterator();
        Iterator<Value> bt = bu.iterator();

        while(at.hasNext()) {
            assertTrue(bt.hasNext());
            Value av = at.next();
            Value bv = bt.next();
            assertEquals(av, bv);
        }
        assertFalse(bt.hasNext());
    }

    @Test
    public void testCompactSerialize() throws IOException {
        MessagePack msgpack = new MessagePack();
        byte[] a = readTestData();
        byte[] b = readCompactTestData();

        BufferPacker pk = msgpack.createBufferPacker();

        BufferUnpacker au = msgpack.createBufferUnpacker().wrap(a);
        for(Value av : au) {
            pk.write(av);
        }

        byte[] c = pk.toByteArray();

        assertEquals(b.length, c.length);
        assertArrayEquals(b, c);
    }
}

