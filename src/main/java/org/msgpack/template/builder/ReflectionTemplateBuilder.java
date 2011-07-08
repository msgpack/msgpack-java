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

import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.TemplateRegistry;
import org.msgpack.packer.Packer;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import org.msgpack.unpacker.Unpacker;


public class ReflectionTemplateBuilder extends AbstractTemplateBuilder {

    static abstract class ReflectionFieldEntry extends FieldEntry {
	ReflectionFieldEntry(FieldEntry e) {
	    super(e.getField(), e.getOption());
	}

	public abstract void write(Packer packer, Object target) throws IOException;

	public abstract void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException;

	public void setNull(Object target) throws IllegalAccessException {
	    getField().set(target, null);
	}
    }

    static class NullFieldEntry extends ReflectionFieldEntry {
	NullFieldEntry(FieldEntry e) {
	    super(e);
	}

	public void write(Packer packer, Object target) throws IOException {
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	}
    }

    static class ObjectFieldEntry extends ReflectionFieldEntry {
	private Template template;

	ObjectFieldEntry(FieldEntry e, Template template) {
	    super(e);
	    this.template = template;
	}

	public void write(Packer packer, Object target) throws IOException {
	    template.write(packer, target);
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	    Field f = getField();
	    Class<Object> type = (Class<Object>) f.getType();
	    Object fieldReference = f.get(target);
	    Object valueReference = template.read(unpacker, fieldReference);
	    if (valueReference != fieldReference) {
		f.set(target, valueReference);
	    }
	}
    }

    static class BooleanFieldEntry extends ReflectionFieldEntry {
	BooleanFieldEntry(FieldEntry e) {
	    super(e);
	}

	public void write(Packer packer, Object target) throws IOException {
	    packer.writeBoolean((Boolean) target);
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	    getField().setBoolean(target, unpacker.readBoolean());
	}
    }

    static class ByteFieldEntry extends ReflectionFieldEntry {
	ByteFieldEntry(FieldEntry e) {
	    super(e);
	}

	public void write(Packer packer, Object target) throws IOException {
	    packer.writeByte((Byte) target);
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	    getField().setByte(target, unpacker.readByte());
	}
    }

    static class ShortFieldEntry extends ReflectionFieldEntry {
	ShortFieldEntry(FieldEntry e) {
	    super(e);
	}

	public void write(Packer packer, Object target) throws IOException {
	    packer.writeShort((Short) target);
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	    getField().setShort(target, unpacker.readShort());
	}
    }

    static class IntFieldEntry extends ReflectionFieldEntry {
	IntFieldEntry(FieldEntry e) {
	    super(e);
	}

	public void write(Packer packer, Object target) throws IOException {
	    packer.writeInt((Integer) target);
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	    getField().setInt(target, unpacker.readInt());
	}
    }

    static class LongFieldEntry extends ReflectionFieldEntry {
	LongFieldEntry(FieldEntry e) {
	    super(e);
	}

	public void write(Packer packer, Object target) throws IOException {
	    packer.writeLong((Long) target);
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	    getField().setLong(target, unpacker.readLong());
	}
    }

    static class FloatFieldEntry extends ReflectionFieldEntry {
	FloatFieldEntry(FieldEntry e) {
	    super(e);
	}

	public void write(Packer packer, Object target) throws IOException {
	    packer.writeFloat((Float) target);
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	    getField().setFloat(target, unpacker.readFloat());
	}
    }

    static class DoubleFieldEntry extends ReflectionFieldEntry {
	DoubleFieldEntry(FieldEntry e) {
	    super(e);
	}

	public void write(Packer packer, Object target) throws IOException {
	    packer.writeDouble((Double) target);
	}

	public void read(Unpacker unpacker, Object target) throws IOException, MessageTypeException, IllegalAccessException {
	    getField().setDouble(target, unpacker.readDouble());
	}
    }

    static class ReflectionTemplate implements Template {
	private Class<?> targetClass;

	private ReflectionFieldEntry[] entries;

	private int minimumArrayLength;

	ReflectionTemplate(Class<?> targetClass, ReflectionFieldEntry[] entries) {
	    this.targetClass = targetClass;
	    this.entries = entries;
	    this.minimumArrayLength = 0;
	    for (int i = 0; i < entries.length; i++) {
		ReflectionFieldEntry e = entries[i];
		if (e.isRequired() || e.isNotNullable()) {
		    this.minimumArrayLength = i + 1;
		}
	    }
	}

	public void write(Packer packer, Object target) throws IOException {
	    try {
		packer.writeArrayBegin(entries.length);
		for (ReflectionFieldEntry e : entries) {
		    if (!e.isAvailable()) {
			packer.writeNil();
			continue;
		    }
		    Object obj = e.getField().get(target);
		    if (obj == null) {
			if (!e.isNotNullable() && !e.isOptional()) {
			    throw new MessageTypeException();
			}
			packer.writeNil();
		    } else {
			e.write(packer, obj);
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

	public Object read(Unpacker unpacker, Object to) throws IOException, MessageTypeException {
	    try {
		if (to == null) {
		    to = targetClass.newInstance();
		}

		int length = unpacker.readArrayBegin();
		if (length < minimumArrayLength) {
		    throw new MessageTypeException();
		}

		int i;
		for (i = 0; i < minimumArrayLength; ++i) {
		    ReflectionFieldEntry e = entries[i];
		    if (!e.isAvailable()) {
			unpacker.readValue(); // FIXME
			continue;
		    }

		    if (unpacker.tryReadNil()) {
			if (e.isRequired()) {
			    // Required + nil => exception
			    throw new MessageTypeException();
			} else if (e.isOptional()) {
			    // Optional + nil => keep default value
			} else { // Nullable
				 // Nullable + nil => set null
			    e.setNull(to);
			}
		    } else {
			e.read(unpacker, to);
		    }
		}

		int max = length < entries.length ? length : entries.length;
		for (; i < max; ++i) {
		    ReflectionFieldEntry e = entries[i];
		    if (!e.isAvailable()) {
			unpacker.readValue(); // FIXME
			continue;
		    }

		    if (unpacker.tryReadNil()) {
			// this is Optional field becaue i >= minimumArrayLength
			// Optional + nil => keep default value
		    } else {
			e.read(unpacker, to);
		    }
		}

		// latter entries are all Optional + nil => keep default value
		for (; i < length; ++i) {
		    unpacker.readValue(); // FIXME
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

    private TemplateRegistry registry;

    private FieldEntryReader reader = new FieldEntryReader();

    public ReflectionTemplateBuilder(TemplateRegistry registry) {
	this.registry = registry;
    }

    @Override
    public FieldEntryReader getFieldEntryReader() {
	return reader;
    }

    @Override
    public Template buildTemplate(Class<?> type, FieldEntry[] entries) {
	if (entries == null) {
	    throw new NullPointerException("entries is null: " + type);
	}

	// TODO Now it is simply cast.
	for (FieldEntry e : entries) {
	    Field f = ((FieldEntry) e).getField();
	    int mod = f.getModifiers();
	    if (!Modifier.isPublic(mod)) {
		f.setAccessible(true);
	    }
	}

	ReflectionFieldEntry[] res = new ReflectionFieldEntry[entries.length];
	for (int i = 0; i < entries.length; i++) {
	    FieldEntry e = (FieldEntry) entries[i];
	    Class<?> t = e.getType();
	    if (!e.isAvailable()) {
		res[i] = new NullFieldEntry(e);
	    } else if (t.equals(boolean.class)) {
		res[i] = new BooleanFieldEntry(e);
	    } else if (t.equals(byte.class)) {
		res[i] = new ByteFieldEntry(e);
	    } else if (t.equals(short.class)) {
		res[i] = new ShortFieldEntry(e);
	    } else if (t.equals(int.class)) {
		res[i] = new IntFieldEntry(e);
	    } else if (t.equals(long.class)) {
		res[i] = new LongFieldEntry(e);
	    } else if (t.equals(float.class)) {
		res[i] = new FloatFieldEntry(e);
	    } else if (t.equals(double.class)) {
		res[i] = new DoubleFieldEntry(e);
	    } else {
		Template tmpl = registry.lookup((Class<?>) e.getGenericType());
		// TODO #MN
		//Template tmpl = msgpack.lookup(e.getGenericType(), true);
		res[i] = new ObjectFieldEntry(e, tmpl);
	    }
	}
	return new ReflectionTemplate(type, res);
    }
}
