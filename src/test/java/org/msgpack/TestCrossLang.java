package org.msgpack.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.value.Value;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.BufferUnpacker;

import junit.framework.TestCase;

public class TestCrossLang extends TestCase {
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

        BufferUnpacker au = new BufferUnpacker().wrap(a);
        BufferUnpacker bu = new BufferUnpacker().wrap(b);

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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamPacker pk = new StreamPacker(out);

        BufferUnpacker au = new BufferUnpacker().wrap(a);
        for(Value av : au) {
            pk.write(av);
        }

        byte[] c = out.toByteArray();

        assertTrue(Arrays.equals(b, c));
    }
}

