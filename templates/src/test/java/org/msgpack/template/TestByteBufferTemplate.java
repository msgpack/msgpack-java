package org.msgpack.template;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Unpacker;


public class TestByteBufferTemplate {

    private static byte[] toByteArray(ByteBuffer from) {
	if (from == null) {
	    return null;
	}
	byte[] bytes = new byte[from.remaining()];
	from.get(bytes, from.arrayOffset() + from.position(), from.remaining());
	return bytes;
    }

    @Test
    public void testPackUnpack() throws Exception {
	new TestPackUnpack().testByteBuffer();
    }

    @Test
    public void testPackBufferUnpack() throws Exception {
	new TestPackBufferUnpack().testByteBuffer();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testByteBuffer();
    }

    @Test
    public void testBufferPackUnpack() throws Exception {
	new TestBufferPackUnpack().testByteBuffer();
    }

    private static class TestPackUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<ByteBuffer> tmpl = ByteBufferTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    unpacker.resetReadByteCount();
	    ByteBuffer ret = tmpl.read(unpacker, null);
	    assertArrayEquals(toByteArray(v), toByteArray(ret));
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<ByteBuffer> tmpl = ByteBufferTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    unpacker.resetReadByteCount();
	    ByteBuffer ret = tmpl.read(unpacker, null);
	    assertArrayEquals(toByteArray(v), toByteArray(ret));
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<ByteBuffer> tmpl = ByteBufferTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    unpacker.resetReadByteCount();
	    ByteBuffer ret = tmpl.read(unpacker, null);
	    assertArrayEquals(toByteArray(v), toByteArray(ret));
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestBufferPackUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<ByteBuffer> tmpl = ByteBufferTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    unpacker.resetReadByteCount();
	    ByteBuffer ret = tmpl.read(unpacker, null);
	    assertArrayEquals(toByteArray(v), toByteArray(ret));
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }
}
