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


public class TestStringTemplate {

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testString();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testString();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testString();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testString();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    Template<String> tmpl = StringTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    String ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    Template<String> tmpl = StringTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    String ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    Template<String> tmpl = StringTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    String ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    Template<String> tmpl = StringTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    String ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }
}
