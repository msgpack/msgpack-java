package org.msgpack.template;

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
	    MessagePack msgpack = new MessagePack();
	    Template<Float> tmpl = FloatTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	    MessagePack msgpack = new MessagePack();
	    Template<Float> tmpl = FloatTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
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
	    MessagePack msgpack = new MessagePack();
	    Template<Float> tmpl = FloatTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
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
	    MessagePack msgpack = new MessagePack();
	    Template<Float> tmpl = FloatTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    Float ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }
}
