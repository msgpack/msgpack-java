//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.template.builder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.msgpack.MessageTypeException;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.MessagePackMessage;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.unpacker.Unpacker;


public class ReflectionTemplateBuilder extends AbstractTemplateBuilder {

    protected static abstract class ReflectionFieldTemplate extends AbstractTemplate<Object> {
	protected FieldEntry entry;

	ReflectionFieldTemplate(final FieldEntry entry) {
	    this.entry = entry;
	}

	void setNil(Object target) {
	    entry.set(target, null);
	}
    }

    static class NullFieldTemplate extends ReflectionFieldTemplate {
	NullFieldTemplate(final FieldEntry entry) {
	    super(entry);
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    return null;
	}
    }

    public static class ObjectFieldTemplate extends ReflectionFieldTemplate {
	private Template template;

	public ObjectFieldTemplate(final FieldEntry entry, Template template) {
	    super(entry);
	    this.template = template;
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	    template.write(packer, target);
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    Class<Object> type = (Class<Object>) entry.getType();
	    Object fieldReference = entry.get(target);
	    Object valueReference = template.read(unpacker, fieldReference);
	    if (valueReference != fieldReference) {
		entry.set(target, valueReference);
	    }
	    return valueReference;
	}
    }

    static class BooleanFieldTemplate extends ReflectionFieldTemplate {
	BooleanFieldTemplate(final FieldEntry entry) {
	    super(entry);
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	    packer.writeBoolean((Boolean) target);
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    boolean o = unpacker.readBoolean();
	    try {
		((DefaultFieldEntry) entry).getField().setBoolean(target, o);
	    } catch (IllegalArgumentException e) {
		throw new MessageTypeException(e);
	    } catch (IllegalAccessException e) {
		throw new MessageTypeException(e);
	    }
	    return o;
	}
    }

    static class ByteFieldTemplate extends ReflectionFieldTemplate {
	ByteFieldTemplate(final FieldEntry entry) {
	    super(entry);
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	    packer.writeByte((Byte) target);
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    byte o = unpacker.readByte();
	    try {
		((DefaultFieldEntry) entry).getField().setByte(target, o);
	    } catch (IllegalArgumentException e) {
		throw new MessageTypeException(e);
	    } catch (IllegalAccessException e) {
		throw new MessageTypeException(e);
	    }
	    return o;
	}
    }

    static class ShortFieldTemplate extends ReflectionFieldTemplate {
	ShortFieldTemplate(final FieldEntry entry) {
	    super(entry);
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	    packer.writeShort((Short) target);
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    short o = unpacker.readShort();
	    try {
		((DefaultFieldEntry) entry).getField().setShort(target, o);
	    } catch (IllegalArgumentException e) {
		throw new MessageTypeException(e);
	    } catch (IllegalAccessException e) {
		throw new MessageTypeException(e);
	    }
	    return o;
	}
    }

    static class IntFieldTemplate extends ReflectionFieldTemplate {
	IntFieldTemplate(final FieldEntry entry) {
	    super(entry);
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	    packer.writeInt((Integer) target);
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    int o = unpacker.readInt();
	    try {
		((DefaultFieldEntry) entry).getField().setInt(target, o);
	    } catch (IllegalArgumentException e) {
		throw new MessageTypeException(e);
	    } catch (IllegalAccessException e) {
		throw new MessageTypeException(e);
	    }
	    return o;
	}
    }

    static class LongFieldTemplate extends ReflectionFieldTemplate {
	LongFieldTemplate(final FieldEntry entry) {
	    super(entry);
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	    packer.writeLong((Long) target);
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    long o = unpacker.readLong();
	    try {
		((DefaultFieldEntry) entry).getField().setLong(target, o);
	    } catch (IllegalArgumentException e) {
		throw new MessageTypeException(e);
	    } catch (IllegalAccessException e) {
		throw new MessageTypeException(e);
	    }
	    return o;
	}
    }

    static class FloatFieldTemplate extends ReflectionFieldTemplate {
	FloatFieldTemplate(final FieldEntry entry) {
	    super(entry);
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	    packer.writeFloat((Float) target);
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    float o = unpacker.readFloat();
	    try {
		((DefaultFieldEntry) entry).getField().setFloat(target, o);
	    } catch (IllegalArgumentException e) {
		throw new MessageTypeException(e);
	    } catch (IllegalAccessException e) {
		throw new MessageTypeException(e);
	    }
	    return o;
	}
    }

    static class DoubleFieldTemplate extends ReflectionFieldTemplate {
	DoubleFieldTemplate(final FieldEntry entry) {
	    super(entry);
	}

	@Override
	public void write(Packer packer, Object target) throws IOException {
	    packer.writeDouble((Double) target);
	}

	@Override
	public Object read(Unpacker unpacker, Object target) throws IOException {
	    double o = unpacker.readDouble();
	    try {
		((DefaultFieldEntry) entry).getField().setDouble(target, o);
	    } catch (IllegalArgumentException e) {
		throw new MessageTypeException(e);
	    } catch (IllegalAccessException e) {
		throw new MessageTypeException(e);
	    }
	    return o;
	}
    }

    protected static class ReflectionClassTemplate<T> extends AbstractTemplate<T> {
	protected Class<T> targetClass;

	protected ReflectionFieldTemplate[] templates;

	protected ReflectionClassTemplate(Class<T> targetClass, ReflectionFieldTemplate[] templates) {
	    this.targetClass = targetClass;
	    this.templates = templates;
	}

	@Override
	public void write(Packer packer, T target) throws IOException {
	    try {
		packer.writeArrayBegin(templates.length);
		for (ReflectionFieldTemplate tmpl : templates) {
		    if (!tmpl.entry.isAvailable()) {
			packer.writeNil();
			continue;
		    }
		    Object obj = tmpl.entry.get(target);
		    if (obj == null) {
			if (tmpl.entry.isNotNullable()) {
			    throw new MessageTypeException();
			}
			packer.writeNil();
		    } else {
			tmpl.write(packer, obj);
		    }
		}
		packer.writeArrayEnd();
	    } catch (MessageTypeException e) {
		throw e;
	    } catch (IOException e) {
		throw e;
	    } catch (Exception e) {
		throw new MessageTypeException(e);
	    }
	}

	@Override
	public T read(Unpacker unpacker, T to) throws IOException {
	    try {
		if (to == null) {
		    to = targetClass.newInstance();
		}

		unpacker.readArrayBegin();

                for (int i=0; i < templates.length; i++) {
		    ReflectionFieldTemplate tmpl = templates[i];
                    if (!tmpl.entry.isAvailable()) {
                        unpacker.skip();
                    } else if (tmpl.entry.isOptional() && unpacker.trySkipNil()) {
                        tmpl.setNil(to);
                    } else {
                        tmpl.read(unpacker, to);
                    }
                }

		unpacker.readArrayEnd();
		return to;
	    } catch (MessageTypeException e) {
		throw e;
	    } catch (IOException e) {
		throw e;
	    } catch (Exception e) {
		throw new MessageTypeException(e);
	    }
	}
    }

    public ReflectionTemplateBuilder(TemplateRegistry registry) {
	super(registry);
    }

    @Override
    public boolean matchType(Type targetType) {
	return AbstractTemplateBuilder.isAnnotated((Class<?>) targetType, Message.class)
		|| AbstractTemplateBuilder.isAnnotated((Class<?>) targetType, MessagePackMessage.class);
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> type, FieldEntry[] entries) {
	if (entries == null) {
	    throw new NullPointerException("entries is null: " + type);
	}

	// TODO Now it is simply cast.
	for (FieldEntry entry : entries) {
	    Field f = ((DefaultFieldEntry) entry).getField();
	    int mod = f.getModifiers();
	    if (!Modifier.isPublic(mod)) {
		f.setAccessible(true);
	    }
	}

	ReflectionFieldTemplate[] tmpls = new ReflectionFieldTemplate[entries.length];
	for (int i = 0; i < entries.length; i++) {
	    FieldEntry entry = entries[i];
	    Class<?> t = entry.getType();
	    if (!entry.isAvailable()) {
		tmpls[i] = new NullFieldTemplate(entry);
	    } else if (t.equals(boolean.class)) {
		tmpls[i] = new BooleanFieldTemplate(entry);
	    } else if (t.equals(byte.class)) {
		tmpls[i] = new ByteFieldTemplate(entry);
	    } else if (t.equals(short.class)) {
		tmpls[i] = new ShortFieldTemplate(entry);
	    } else if (t.equals(int.class)) {
		tmpls[i] = new IntFieldTemplate(entry);
	    } else if (t.equals(long.class)) {
		tmpls[i] = new LongFieldTemplate(entry);
	    } else if (t.equals(float.class)) {
		tmpls[i] = new FloatFieldTemplate(entry);
	    } else if (t.equals(double.class)) {
		tmpls[i] = new DoubleFieldTemplate(entry);
	    } else {
		Template tmpl = registry.lookup(entry.getGenericType(), true);
		tmpls[i] = new ObjectFieldTemplate(entry, tmpl);
	    }
	}
	return new ReflectionClassTemplate(type, tmpls);
    }
}
