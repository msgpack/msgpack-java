package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.StreamUnpacker;


@Ignore
public class TestMapTemplate {

    @Test
    public void testStreamPackStreamUnpack() throws Exception {
	new TestStreamPackStreamUnpack().testMap();
    }

    @Test
    public void testStreamPackBufferUnpack() throws Exception {
	new TestStreamPackBufferUnpack().testMap();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
	new TestBufferPackBufferUnpack().testMap();
    }

    @Test
    public void testBufferPackStreamUnpack() throws Exception {
	new TestBufferPackStreamUnpack().testMap();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	@Test @Override
	public void testMap() throws Exception {
	    super.testMap();
	}

	@Override
	public void testMap(Map v, Class<?> keyElementClass, Class<?> valueElementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template keyElementTemplate = msgpack.getTemplate(keyElementClass);
	    Template valueElementTemplate = msgpack.getTemplate(valueElementClass);
	    Template tmpl = new MapTemplate(keyElementTemplate, valueElementTemplate);
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
	public void testMap() throws Exception {
	    super.testMap();
	}

	@Override
	public void testMap(Map v, Class<?> keyElementClass, Class<?> valueElementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template keyElementTemplate = msgpack.getTemplate(keyElementClass);
	    Template valueElementTemplate = msgpack.getTemplate(valueElementClass);
	    Template tmpl = new MapTemplate(keyElementTemplate, valueElementTemplate);
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
	public void testMap() throws Exception {
	    super.testMap();
	}

	@Override
	public void testMap(Map v, Class<?> keyElementClass, Class<?> valueElementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template keyElementTemplate = msgpack.getTemplate(keyElementClass);
	    Template valueElementTemplate = msgpack.getTemplate(valueElementClass);
	    Template tmpl = new MapTemplate(keyElementTemplate, valueElementTemplate);
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
	public void testMap() throws Exception {
	    super.testMap();
	}

	@Override
	public void testMap(Map v, Class<?> keyElementClass, Class<?> valueElementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template keyElementTemplate = msgpack.getTemplate(keyElementClass);
	    Template valueElementTemplate = msgpack.getTemplate(valueElementClass);
	    Template tmpl = new MapTemplate(keyElementTemplate, valueElementTemplate);
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    String ret = (String) tmpl.read(unpacker, null);
	    assertEquals(v, ret);
	}
    }
}
