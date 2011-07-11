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


public class TestFloatTemplate {

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testFloat();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testFloat();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testFloat();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testFloat();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testFloat() throws Exception {
	    super.testFloat();
	}

	@Override
	public void testFloat(float v) throws Exception {
	    Template<Float> tmpl = FloatTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    Float ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testFloat() throws Exception {
	    super.testFloat();
	}

	@Override
	public void testFloat(float v) throws Exception {
	    Template<Float> tmpl = FloatTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    Float ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testFloat() throws Exception {
	    super.testFloat();
	}

	@Override
	public void testFloat(float v) throws Exception {
	    Template<Float> tmpl = FloatTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    Float ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	@Test @Override
	public void testFloat() throws Exception {
	    super.testFloat();
	}

	@Override
	public void testFloat(float v) throws Exception {
	    Template<Float> tmpl = FloatTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    Float ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }
}
