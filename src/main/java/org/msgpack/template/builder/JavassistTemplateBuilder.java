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
import java.lang.Thread;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.MessagePackMessage;
import org.msgpack.packer.Packer;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.ReflectionTemplateBuilder.ReflectionFieldTemplate;
import org.msgpack.unpacker.Unpacker;


public class JavassistTemplateBuilder extends AbstractTemplateBuilder {

    private static Logger LOG = LoggerFactory.getLogger(JavassistTemplateBuilder.class);

    public static abstract class JavassistTemplate<T> extends AbstractTemplate<T> {
	public Class<T> targetClass;
	public Template<?>[] templates;

	public JavassistTemplate(Class<T> targetClass, Template<?>[] templates) {
	    this.targetClass = targetClass;
	    this.templates = templates;
	}
    }

    protected ClassPool pool;

    protected int seqId = 0;

    public JavassistTemplateBuilder(TemplateRegistry registry) {
	super(registry);
	pool = new ClassPool();
	boolean appended = false;
	ClassLoader cl = null;
	try {
	    cl = Thread.currentThread().getContextClassLoader();
	    if (cl != null) {
		pool.appendClassPath(new LoaderClassPath(cl));
		appended = true;
	    }
	} catch (SecurityException e) {
	    LOG.debug("Cannot append a search path of context classloader", e);
	}
	try {
	    ClassLoader cl2 = getClass().getClassLoader();
	    if (cl2 != null && cl2 != cl) {
		pool.appendClassPath(new LoaderClassPath(cl2));
		appended = true;
	    }
	} catch (SecurityException e) {
	    LOG.debug("Cannot append a search path of classloader", e);
	}
	if (!appended) {
	    pool.appendSystemPath();
	}
    }

    @Override
    public boolean matchType(Type targetType) {
        // TODO reject enum
	return AbstractTemplateBuilder.isAnnotated((Class<?>) targetType, Message.class)
		|| AbstractTemplateBuilder.isAnnotated((Class<?>) targetType, MessagePackMessage.class);
    }

    public void addClassLoader(ClassLoader cl) {
	pool.appendClassPath(new LoaderClassPath(cl));
    }

    protected CtClass makeCtClass(String className) {
	return pool.makeClass(className);
    }

    protected CtClass getCtClass(String className) throws NotFoundException {
	return pool.get(className);
    }

    protected int nextSeqId() {
	return seqId++;
    }

    protected BuildContext createBuildContext() {
	return new DefaultBuildContext(this);
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries) {
	Template<?>[] tmpls = toTemplate(entries);
	BuildContext bc = createBuildContext();
	return bc.buildTemplate(targetClass, entries, tmpls);
    }

    private Template<?>[] toTemplate(FieldEntry[] from) {
	Template<?>[] tmpls = new Template<?>[from.length];
	for(int i = 0; i < from.length; ++i) {
	    FieldEntry e = from[i];
	    if(!e.isAvailable()) {
		tmpls[i] = null;
	    } else {
		Template<?> tmpl = registry.lookup(e.getGenericType(), true);
		tmpls[i] = tmpl;
	    }
	}
	return tmpls;
    }

    @Override
    public void writeTemplate(Type targetType, String directoryName) {
	Class<?> targetClass = (Class<?>)targetType;
	checkClassValidation(targetClass);
	FieldOption implicitOption = getFieldOption(targetClass);
	FieldEntry[] entries = toFieldEntries(targetClass, implicitOption);
	writeTemplate(targetClass, entries, directoryName);
    }

    private void writeTemplate(Class<?> targetClass, FieldEntry[] entries, String directoryName) {
	Template[] tmpls = toTemplate(entries);
	BuildContext bc = createBuildContext();
	bc.writeTemplate(targetClass, entries, tmpls, directoryName);
    }

    @Override
    public <T> Template<T> loadTemplate(Type targetType) {
	Class<T> targetClass = (Class) targetType;
	checkClassValidation(targetClass);
	FieldOption implicitOption = getFieldOption(targetClass);
	FieldEntry[] entries = toFieldEntries(targetClass, implicitOption);
	return loadTemplate(targetClass, entries);
    }

    private <T> Template<T> loadTemplate(Class<T> targetClass, FieldEntry[] entries) {
	Template[] tmpls = toTemplate(entries);
	BuildContext bc = createBuildContext();
	return bc.loadTemplate(targetClass, entries, tmpls);
    }
}
