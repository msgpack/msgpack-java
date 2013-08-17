package org.msgpack.template;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Unpacker;


public class TestByteArrayTemplate {

    @Test
    public void testPackUnpack() throws Exception {
	new TestPackUnpack().testByteArray();
    }

    @Test
    public void testPackBufferUnpack() throws Exception {
	new TestPackBufferUnpack().testByteArray();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testByteArray();
    }

    @Test
    public void testBufferPackUnpack() throws Exception {
	new TestBufferPackUnpack().testByteArray();
    }

    private static class TestPackUnpack extends TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<byte[]> tmpl = ByteArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    unpacker.resetReadByteCount();
	    byte[] ret = tmpl.read(unpacker, null);
	    assertArrayEquals(v, ret);
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<byte[]> tmpl = ByteArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    unpacker.resetReadByteCount();
	    byte[] ret = tmpl.read(unpacker, null);
	    assertArrayEquals(v, ret);
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<byte[]> tmpl = ByteArrayTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    unpacker.resetReadByteCount();
	    byte[] ret = tmpl.read(unpacker, null);
	    assertArrayEquals(v, ret);
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestBufferPackUnpack extends TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<byte[]> tmpl = ByteArrayTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    unpacker.resetReadByteCount();
	    byte[] ret = tmpl.read(unpacker, null);
	    assertArrayEquals(v, ret);
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }
}
