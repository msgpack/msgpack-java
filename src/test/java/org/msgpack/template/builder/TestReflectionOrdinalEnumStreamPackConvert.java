package org.msgpack.template.builder;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.packer.StreamPacker;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.Template;
import org.msgpack.testclasses.EnumTypeFieldsClass;
import org.msgpack.testclasses.EnumTypeFieldsClassNotNullable;
import org.msgpack.type.Value;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Converter;


public class TestReflectionOrdinalEnumStreamPackConvert extends TestSet {

    @Test @Override
    public void testEnumTypeFieldsClass() throws Exception {
	super.testEnumTypeFieldsClass();
    }

    @Override
    public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	TemplateRegistry registry = new TemplateRegistry();
	registry.register(EnumTypeFieldsClass.SampleEnum.class,
		new ReflectionOrdinalEnumTemplateBuilder(registry).buildTemplate(EnumTypeFieldsClass.SampleEnum.class));
	ReflectionTemplateBuilder builder = new ReflectionTemplateBuilder(registry);
	Template<EnumTypeFieldsClass> tmpl = builder.buildTemplate(EnumTypeFieldsClass.class);
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	tmpl.write(packer, v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker u = new BufferUnpacker();
	u.wrap(bytes);
	Value value = u.readValue();
	Converter unpacker = new Converter(value);
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
	registry.register(EnumTypeFieldsClassNotNullable.SampleEnum.class,
		new ReflectionOrdinalEnumTemplateBuilder(registry).buildTemplate(EnumTypeFieldsClassNotNullable.SampleEnum.class));
	ReflectionTemplateBuilder builder = new ReflectionTemplateBuilder(registry);
	Template<EnumTypeFieldsClassNotNullable> tmpl = builder.buildTemplate(EnumTypeFieldsClassNotNullable.class);
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	tmpl.write(packer, v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker u = new BufferUnpacker();
	u.wrap(bytes);
	Value value = u.readValue();
	Converter unpacker = new Converter(value);
	EnumTypeFieldsClassNotNullable ret = tmpl.read(unpacker, null);
	if (v == null) {
	    assertEquals(null, ret);
	    return;
	}
	assertEquals(v, ret);
    }
}
