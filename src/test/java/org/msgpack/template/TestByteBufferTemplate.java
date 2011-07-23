package org.msgpack.template;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.junit.Test;
import org.msgpack.MessagePack;
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
	    MessagePack msgpack = new MessagePack();
	    Template<ByteBuffer> tmpl = ByteBufferTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = msgpack.createStreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = msgpack.createStreamUnpacker(new ByteArrayInputStream(bytes));
	    ByteBuffer ret = tmpl.read(unpacker, null);
	    assertArrayEquals(toByteArray(v), toByteArray(ret));
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testByteBuffer() throws Exception {
	    super.testByteBuffer();
	}

	@Override
	public void testByteBuffer(ByteBuffer v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<ByteBuffer> tmpl = ByteBufferTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = msgpack.createStreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    ByteBuffer ret = tmpl.read(unpacker, null);
	    assertArrayEquals(toByteArray(v), toByteArray(ret));
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
	    ByteBuffer ret = tmpl.read(unpacker, null);
	    assertArrayEquals(toByteArray(v), toByteArray(ret));
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
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
	    StreamUnpacker unpacker = msgpack.createStreamUnpacker(new ByteArrayInputStream(bytes));
	    ByteBuffer ret = tmpl.read(unpacker, null);
	    assertArrayEquals(toByteArray(v), toByteArray(ret));
	}
    }
}
