package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.StreamUnpacker;


@Ignore
public class TestListTemplate {

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testList();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testList();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testList();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testList();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testList() throws Exception {
	    super.testList();
	}

	@Override
	public void testList(List v, Class<?> elementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template elementTemplate = msgpack.getTemplate(elementClass);
	    Template tmpl = new ListTemplate(elementTemplate);
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    String ret = (String) tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	@Test @Override
	public void testList() throws Exception {
	    super.testList();
	}

	@Override
	public void testList(List v, Class<?> elementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template elementTemplate = msgpack.getTemplate(elementClass);
	    Template tmpl = new ListTemplate(elementTemplate);
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    String ret = (String) tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	@Test @Override
	public void testList() throws Exception {
	    super.testList();
	}

	@Override
	public void testList(List v, Class<?> elementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template elementTemplate = msgpack.getTemplate(elementClass);
	    Template tmpl = new ListTemplate(elementTemplate);
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    String ret = (String) tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	@Test @Override
	public void testList() throws Exception {
	    super.testList();
	}

	@Override
	public void testList(List v, Class<?> elementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template elementTemplate = msgpack.getTemplate(elementClass);
	    Template tmpl = new ListTemplate(elementTemplate);
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    String ret = (String) tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }
}
