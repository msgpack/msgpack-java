package org.msgpack.annotation;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.JavassistTemplateBuilder;
import org.msgpack.unpacker.Unpacker;


public class TestOptionalJavassistPackBufferUnpack {

    @org.junit.Ignore
    public static class TestMessagePack extends TestSetOptional {
	public void testOptional0101() throws Exception {
	    super.testOptional0101();
	}

	public MyMessage01 testOptional0101(MyMessage01 src) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    TemplateRegistry registry = new TemplateRegistry(null);
	    JavassistTemplateBuilder builder = new JavassistTemplateBuilder(registry);
	    Template<MyMessage01> tmpl01 = builder.buildTemplate(MyMessage01.class);
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl01.write(packer, src);
	    byte[] bytes = packer.toByteArray();
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    Unpacker unpacker = msgpack.createUnpacker(in);
	    unpacker.resetReadByteCount();
            MyMessage01 dst = tmpl01.read(unpacker, null);
            assertEquals(bytes.length, unpacker.getReadByteCount());
            return dst;
	}

	public void testOptional0102() throws Exception {
	    super.testOptional0102();
	}

	public MyMessage02 testOptional0102(MyMessage01 src) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    TemplateRegistry registry = new TemplateRegistry(null);
	    JavassistTemplateBuilder builder = new JavassistTemplateBuilder(registry);
	    Template<MyMessage01> tmpl01 = builder.buildTemplate(MyMessage01.class);
	    Template<MyMessage02> tmpl02 = builder.buildTemplate(MyMessage02.class);
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl01.write(packer, src);
	    byte[] bytes = packer.toByteArray();
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    Unpacker unpacker = msgpack.createUnpacker(in);
	    unpacker.resetReadByteCount();
            MyMessage02 dst = tmpl02.read(unpacker, null);
            assertEquals(bytes.length, unpacker.getReadByteCount());
            return dst;
	}

	public void testOptional0103() throws Exception {
	    super.testOptional0103();
	}

	public MyMessage03 testOptional0103(MyMessage01 src) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    TemplateRegistry registry = new TemplateRegistry(null);
	    JavassistTemplateBuilder builder = new JavassistTemplateBuilder(registry);
	    Template<MyMessage01> tmpl01 = builder.buildTemplate(MyMessage01.class);
	    Template<MyMessage03> tmpl03 = builder.buildTemplate(MyMessage03.class);
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl01.write(packer, src);
	    byte[] bytes = packer.toByteArray();
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    Unpacker unpacker = msgpack.createUnpacker(in);
	    unpacker.resetReadByteCount();
            MyMessage03 dst = tmpl03.read(unpacker, null);
            assertEquals(bytes.length, unpacker.getReadByteCount());
            return dst;
	}

	public void testOptional0203() throws Exception {
	    super.testOptional0203();
	}

	public MyMessage03 testOptional0202(MyMessage02 src) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    TemplateRegistry registry = new TemplateRegistry(null);
	    JavassistTemplateBuilder builder = new JavassistTemplateBuilder(registry);
	    Template<MyMessage02> tmpl02 = builder.buildTemplate(MyMessage02.class);
	    Template<MyMessage03> tmpl03 = builder.buildTemplate(MyMessage03.class);
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl02.write(packer, src);
	    byte[] bytes = packer.toByteArray();
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    Unpacker unpacker = msgpack.createUnpacker(in);
	    unpacker.resetReadByteCount();
            MyMessage03 dst = tmpl03.read(unpacker, null);
            assertEquals(bytes.length, unpacker.getReadByteCount());
            return dst;
	}
    }

    @Test
    public void test0101() throws Exception {
	new TestMessagePack().testOptional0101();
    }
    @Test
    public void test0102() throws Exception {
	new TestMessagePack().testOptional0102();
    }
    @Test
    public void test0103() throws Exception {
	new TestMessagePack().testOptional0103();
    }
    @Test
    public void test0203() throws Exception {
	new TestMessagePack().testOptional0203();
    }
}
