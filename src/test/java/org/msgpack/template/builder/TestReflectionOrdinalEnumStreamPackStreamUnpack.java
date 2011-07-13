package org.msgpack.template.builder;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.packer.StreamPacker;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.Template;
import org.msgpack.testclasses.EnumTypeFieldsClass;
import org.msgpack.testclasses.EnumTypeFieldsClassNotNullable;
import org.msgpack.unpacker.StreamUnpacker;


public class TestReflectionOrdinalEnumStreamPackStreamUnpack extends TestSet {

    @Test @Override
    public void testEnumTypeFieldsClass() throws Exception {
	super.testEnumTypeFieldsClass();
    }

    @Override
    public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	TemplateRegistry registry = new TemplateRegistry();
	ReflectionOrdinalEnumTemplateBuilder builder = new ReflectionOrdinalEnumTemplateBuilder(registry);
	Template<EnumTypeFieldsClass> tmpl = builder.buildTemplate(EnumTypeFieldsClass.class);
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	tmpl.write(packer, v);
	byte[] bytes = out.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	EnumTypeFieldsClass ret = tmpl.read(unpacker, null);
	if (v == null) {
	    assertEquals(null, ret);
	    return;
	}
	assertEquals(v, ret);
    }

    @Test @Override
    public void testEnumTypeFieldsClassNotNullable() throws Exception {
	super.testEnumTypeFieldsClassNotNullable();
    }

    @Override
    public void testEnumTypeFieldsClassNotNullable(EnumTypeFieldsClassNotNullable v) throws Exception {
	TemplateRegistry registry = new TemplateRegistry();
	ReflectionOrdinalEnumTemplateBuilder builder = new ReflectionOrdinalEnumTemplateBuilder(registry);
	Template<EnumTypeFieldsClassNotNullable> tmpl = builder.buildTemplate(EnumTypeFieldsClassNotNullable.class);
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	tmpl.write(packer, v);
	byte[] bytes = out.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	EnumTypeFieldsClassNotNullable ret = tmpl.read(unpacker, null);
	if (v == null) {
	    assertEquals(null, ret);
	    return;
	}
	assertEquals(v, ret);
    }
}
