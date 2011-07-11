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


public class TestLongTemplate {

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testLong();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testLong();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testLong();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testLong();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testLong() throws Exception {
	    super.testLong();
	}

	@Override
	public void testLong(long v) throws Exception {
	    Template<Long> tmpl = LongTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    long ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testLong() throws Exception {
	    super.testLong();
	}

	@Override
	public void testLong(long v) throws Exception {
	    Template<Long> tmpl = LongTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    long ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testLong() throws Exception {
	    super.testLong();
	}

	@Override
	public void testLong(long v) throws Exception {
	    Template<Long> tmpl = LongTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    long ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	@Test @Override
	public void testLong() throws Exception {
	    super.testLong();
	}

	@Override
	public void testLong(long v) throws Exception {
	    Template<Long> tmpl = LongTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    long ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }
}
