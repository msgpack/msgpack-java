package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.StreamUnpacker;


public class TestByteArrayTemplate {

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testByteArray();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testByteArray();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testByteArray();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testByteArray();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    Template<byte[]> tmpl = ByteArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    byte[] ret = tmpl.read(unpacker, null);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i]);
	    }
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    Template<byte[]> tmpl = ByteArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    byte[] ret = tmpl.read(unpacker, null);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i]);
	    }
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    Template<byte[]> tmpl = ByteArrayTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    byte[] ret = tmpl.read(unpacker, null);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i]);
	    }
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    Template<byte[]> tmpl = ByteArrayTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    byte[] ret = tmpl.read(unpacker, null);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i]);
	    }
	}
    }
}
