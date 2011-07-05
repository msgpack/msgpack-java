package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.junit.Test;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.StreamUnpacker;


public class TestByteBufferTemplate {

    private static byte[] toByteArray(ByteBuffer from) {
	byte[] bytes = new byte[from.remaining()];
	from.get(bytes, from.arrayOffset() + from.position(), from.remaining());
	return bytes;
    }

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testByteBuffer();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testByteBuffer();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testByteBuffer();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testByteBuffer();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    Template tmpl = ByteBufferTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    ByteBuffer ret = (ByteBuffer) tmpl.read(unpacker, null);
	    byte[] v2 = toByteArray(v);
	    byte[] ret2 = toByteArray(ret);
	    assertEquals(v2.length, ret2.length);
	    for (int i = 0; i < v2.length; ++i) {
		assertEquals(v2[i], ret2[i]);
	    }
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    Template tmpl = ByteBufferTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    ByteBuffer ret = (ByteBuffer) tmpl.read(unpacker, null);
	    byte[] v2 = toByteArray(v);
	    byte[] ret2 = toByteArray(ret);
	    assertEquals(v2.length, ret2.length);
	    for (int i = 0; i < v2.length; ++i) {
		assertEquals(v2[i], ret2[i]);
	    }
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    Template tmpl = ByteBufferTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    ByteBuffer ret = (ByteBuffer) tmpl.read(unpacker, null);
	    byte[] v2 = toByteArray(v);
	    byte[] ret2 = toByteArray(ret);
	    assertEquals(v2.length, ret2.length);
	    for (int i = 0; i < v2.length; ++i) {
		assertEquals(v2[i], ret2[i]);
	    }
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    Template tmpl = ByteBufferTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    ByteBuffer ret = (ByteBuffer) tmpl.read(unpacker, null);
	    byte[] v2 = toByteArray(v);
	    byte[] ret2 = toByteArray(ret);
	    assertEquals(v2.length, ret2.length);
	    for (int i = 0; i < v2.length; ++i) {
		assertEquals(v2[i], ret2[i]);
	    }
	}
    }
}
