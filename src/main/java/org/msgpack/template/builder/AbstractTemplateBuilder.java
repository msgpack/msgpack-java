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

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.MessagePackMessage;
import org.msgpack.annotation.NotNullable;
import org.msgpack.annotation.Optional;
import org.msgpack.annotation.Required;
import org.msgpack.template.FieldList;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.TemplateBuildException;


public abstract class AbstractTemplateBuilder implements TemplateBuilder {

    protected TemplateRegistry registry;

    protected AbstractTemplateBuilder(TemplateRegistry registry) {
	this.registry = registry;
    }

    @Override
    public <T> Template<T> buildTemplate(Type targetType) throws TemplateBuildException {
	Class<T> targetClass = (Class<T>) targetType;
	checkClassValidation(targetClass);
	FieldOption implicitOption = readImplicitFieldOption(targetClass);
	FieldEntry[] entries = readFieldEntries(targetClass, implicitOption);
	return buildTemplate(targetClass, entries);
    }

    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldList flist) throws TemplateBuildException {
	try {
	    checkClassValidation(targetClass);
	    return buildTemplate(targetClass, convertFieldEntries(targetClass, flist));
	} catch (NoSuchFieldException e) {
	    throw new TemplateBuildException(e);
	}
    }

    public abstract <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries);

    protected void checkClassValidation(Class<?> targetClass) {
	if (targetClass.isInterface()) {
	    throw new TemplateBuildException("Cannot build template for interface: " + targetClass.getName());
	}
	if (targetClass.isArray()) {
	    throw new TemplateBuildException("Cannot build template for array class: " + targetClass.getName());
	}
	if (targetClass.isPrimitive()) {
	    throw new TemplateBuildException("Cannot build template of primitive type: " + targetClass.getName());
	}
    }

    @Override
    public void writeTemplate(Type targetType, String directoryName) {
	throw new UnsupportedOperationException(targetType.toString());
    }

    @Override
    public <T> Template<T> loadTemplate(Type targetType) {
	return null;
    }

    protected FieldEntry[] convertFieldEntries(Class<?> targetClass, FieldList flist) throws NoSuchFieldException {
	List<FieldList.Entry> src = flist.getList();
	FieldEntry[] result = new FieldEntry[src.size()];
	for(int i=0; i < src.size(); i++) {
	    FieldList.Entry s = src.get(i);
	    if(s.isAvailable()) {
		result[i] = new DefaultFieldEntry(targetClass.getDeclaredField(s.getName()), s.getOption());
	    } else {
		result[i] = new DefaultFieldEntry();
	    }
	}
	return result;
    }

    protected FieldEntry[] readFieldEntries(Class<?> targetClass, FieldOption implicitOption) {
	Field[] allFields = readAllFields(targetClass);

	/* index:
	 *   @Index(0) int field_a;   // 0
	 *             int field_b;   // 1
	 *   @Index(3) int field_c;   // 3
	 *             int field_d;   // 4
	 *   @Index(2) int field_e;   // 2
	 *             int field_f;   // 5
	 */
	List<FieldEntry> indexed = new ArrayList<FieldEntry>();
	int maxIndex = -1;
	for(Field f : allFields) {
	    FieldOption opt = readFieldOption(f, implicitOption);
	    if(opt == FieldOption.IGNORE) {
		// skip
		continue;
	    }

	    int index = readFieldIndex(f, maxIndex);
	    if(indexed.size() > index && indexed.get(index) != null) {
		throw new TemplateBuildException("duplicated index: "+index);
	    }
	    if(index < 0) {
		throw new TemplateBuildException("invalid index: "+index);
	    }

	    while(indexed.size() <= index) {
		indexed.add(null);
	    }
	    indexed.set(index, new DefaultFieldEntry(f, opt));

	    if(maxIndex < index) {
		maxIndex = index;
	    }
	}

	FieldEntry[] result = new FieldEntry[maxIndex+1];
	for(int i=0; i < indexed.size(); i++) {
	    FieldEntry e = indexed.get(i);
	    if(e == null) {
		result[i] = new DefaultFieldEntry();
	    } else {
		result[i] = e;
	    }
	}
	return result;
    }

    protected FieldOption readImplicitFieldOption(Class<?> targetClass) {
	Message m = targetClass.getAnnotation(Message.class);
	if (m == null) {
	    return FieldOption.DEFAULT;
	}
	MessagePackMessage mpm = targetClass.getAnnotation(MessagePackMessage.class);
	if (mpm == null) {
	    return FieldOption.DEFAULT;
	}
	// TODO #MN
	return m.value();
    }

    private Field[] readAllFields(Class<?> targetClass) {
	// order: [fields of super class, ..., fields of this class]
	List<Field[]> succ = new ArrayList<Field[]>();
	int total = 0;
	for(Class<?> c = targetClass; c != Object.class; c = c.getSuperclass()) {
	    Field[] fields = c.getDeclaredFields();
	    total += fields.length;
	    succ.add(fields);
	}
	Field[] result = new Field[total];
	int off = 0;
	for(int i=succ.size()-1; i >= 0; i--) {
	    Field[] fields = succ.get(i);
	    System.arraycopy(fields, 0, result, off, fields.length);
	    off += fields.length;
	}
	return result;
    }

    private static FieldOption readFieldOption(Field field, FieldOption implicitOption) {
	int mod = field.getModifiers();
	if(Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
	    return FieldOption.IGNORE;
	}

	if(isAnnotated(field, Ignore.class)) {
	    return FieldOption.IGNORE;
	} else if(isAnnotated(field, Required.class)) {
	    return FieldOption.REQUIRED;
	} else if(isAnnotated(field, Optional.class)) {
	    return FieldOption.OPTIONAL;
	} else if(isAnnotated(field, NotNullable.class)) {
	    if(field.getDeclaringClass().isPrimitive()) {
		return FieldOption.REQUIRED;
	    } else {
		return FieldOption.NOTNULLABLE;
	    }
	}

	if(implicitOption != FieldOption.DEFAULT) {
	    return implicitOption;
	}

	// default mode:
	//   transient : Ignore
	//   public    : Required
	//   others    : Ignore
	if(Modifier.isTransient(mod)) {
	    return FieldOption.IGNORE;
	} else if(Modifier.isPublic(mod)) {
	    return FieldOption.REQUIRED;
	} else {
	    return FieldOption.IGNORE;
	}
    }

    private static int readFieldIndex(Field field, int maxIndex) {
	Index a = field.getAnnotation(Index.class);
	if(a == null) {
	    return maxIndex + 1;
	} else {
	    return a.value();
	}
    }

    public static boolean isAnnotated(Class<?> targetClass, Class<? extends Annotation> with) {
	return targetClass.getAnnotation(with) != null;
    }
    public static boolean isAnnotated(AccessibleObject ao, Class<? extends Annotation> with) {
	return ao.getAnnotation(with) != null;
    }
}
