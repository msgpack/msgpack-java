package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.StreamUnpacker;


public class TestDateTemplate {

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testDate();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testDate();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testDate();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testDate();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testDate() throws Exception {
	    super.testDate();
	}

	@Override
	public void testDate(Date v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<Date> tmpl = DateTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = msgpack.createStreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = msgpack.createStreamUnpacker(new ByteArrayInputStream(bytes));
	    Date ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testDate() throws Exception {
	    super.testDate();
	}

	@Override
	public void testDate(Date v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<Date> tmpl = DateTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = msgpack.createStreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    Date ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testDate() throws Exception {
	    super.testDate();
	}

	@Override
	public void testDate(Date v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<Date> tmpl = DateTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    Date ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	@Test @Override
	public void testDate() throws Exception {
	    super.testDate();
	}

	@Override
	public void testDate(Date v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<Date> tmpl = DateTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = msgpack.createStreamUnpacker(new ByteArrayInputStream(bytes));
	    Date ret = tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }
}
