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


public class TestDoubleTemplate {

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testDouble();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testDouble();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testDouble();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testDouble();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testDouble() throws Exception {
	    super.testDouble();
	}

	@Override
	public void testDouble(double v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<Double> tmpl = DoubleTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    double ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testDouble() throws Exception {
	    super.testDouble();
	}

	@Override
	public void testDouble(double v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<Double> tmpl = DoubleTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    double ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testDouble() throws Exception {
	    super.testDouble();
	}

	@Override
	public void testDouble(double v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<Double> tmpl = DoubleTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    double ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	@Test @Override
	public void testDouble() throws Exception {
	    super.testDouble();
	}

	@Override
	public void testDouble(double v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<Double> tmpl = DoubleTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    double ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret, 10e-10);
	}
    }
}
