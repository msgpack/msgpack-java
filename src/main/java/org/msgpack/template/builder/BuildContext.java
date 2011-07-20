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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.msgpack.*;
import org.msgpack.template.*;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

public class BuildContext extends BuildContextBase<FieldEntry> {
    protected FieldEntry[] entries;
    protected Class<?> origClass;
    protected String origName;
    protected Template<?>[] templates;
    protected int minimumArrayLength;

    public BuildContext(JavassistTemplateBuilder director) {
	super(director);
    }

    public Template buildTemplate(Class targetClass, FieldEntry[] entries, Template[] templates) {
	this.entries = entries;
	this.templates = templates;
	this.origClass = targetClass;
	this.origName = this.origClass.getName();
	return build(this.origName);
    }

    protected void setSuperClass() throws CannotCompileException, NotFoundException {
	tmplCtClass.setSuperclass(director.getCtClass(
		JavassistTemplateBuilder.JavassistTemplate.class.getName()));
    }

    protected void buildConstructor() throws CannotCompileException,
	    NotFoundException {
	// Constructor(Class targetClass, Template[] templates)
	CtConstructor newCtCons = CtNewConstructor.make(
		new CtClass[] {
			director.getCtClass(Class.class.getName()),
			director.getCtClass(Template.class.getName() + "[]")
		}, new CtClass[0], tmplCtClass);
	tmplCtClass.addConstructor(newCtCons);
    }

    protected Template buildInstance(Class<?> c) throws NoSuchMethodException,
	    InstantiationException, IllegalAccessException, InvocationTargetException {
	Constructor<?> cons = c.getConstructor(new Class[] { Class.class, Template[].class });
	Object tmpl = cons.newInstance(new Object[] { this.origClass, this.templates });
	return (Template) tmpl;
    }

    protected void buildMethodInit() {
	minimumArrayLength = 0;
	for (int i = 0; i < entries.length; i++) {
	    FieldEntry e = entries[i];
	    // TODO #MN
	    if (e.isRequired() || !e.isNotNullable()) {
		minimumArrayLength = i + 1;
	    }
	}
    }

    protected String buildWriteMethodBody() {
	resetStringBuilder();
	buildString("{");
	buildString("%s _$$_t = (%s) $2;", this.origName, this.origName);
	buildString("$1.writeArrayBegin(%d);", entries.length);
	for (int i = 0; i < entries.length; i++) {
	    FieldEntry e = entries[i];
	    if (!e.isAvailable()) {
		buildString("$1.writeNil();");
		continue;
	    }
	    Class<?> type = e.getType();
	    if (type.isPrimitive()) {
		buildString("$1.%s(_$$_t.%s);", primitiveWriteName(type),
			e.getName());
	    } else {
		buildString("if (_$$_t.%s == null) {", e.getName());
		if (e.isNotNullable() && !e.isOptional()) {
		    buildString("throw new %s();",
			    MessageTypeException.class.getName());
		} else {
		    buildString("$1.writeNil();");
		}
		buildString("} else {");
		buildString("  this.templates[%d].write($1, _$$_t.%s);", i, e.getName());
		buildString("}");
	    }
	}
	buildString("$1.writeArrayEnd();");
	buildString("}");
	return getBuiltString();
    }

    protected String buildReadMethodBody() {
	resetStringBuilder();
	buildString("{ ");

	buildString("%s _$$_t;", origName);
	buildString("if ($2 == null) {");
	buildString("  _$$_t = new %s();", origName);
	buildString("} else {");
	buildString("  _$$_t = (%s) $2;", origName);
	buildString("}");

	buildString("int length = $1.readArrayBegin();");
	buildString("if(length < %d) {", minimumArrayLength);
	buildString("  throw new %s();", MessageTypeException.class.getName());
	buildString("}");

	int i;
	for (i = 0; i < minimumArrayLength; i++) {
	    FieldEntry e = entries[i];
	    if (!e.isAvailable()) {
		buildString("$1.skip();"); // TODO #MN
		continue;
	    }

	    buildString("if($1.tryReadNil()) {");
	    if (e.isRequired()) {
		// Required + nil => exception
		buildString("throw new %s();", MessageTypeException.class.getName());
	    } else if (e.isOptional()) {
		// Optional + nil => keep default value
	    } else { // Nullable
		     // Nullable + nil => set null
		buildString("_$$_t.%s = null;", e.getName());
	    }
	    buildString("} else {");
	    Class<?> type = e.getType();
	    if (type.isPrimitive()) {
		buildString("_$$_t.%s = $1.%s();", e.getName(), primitiveReadName(type));
	    } else {
		buildString(
			"_$$_t.%s = (%s) this.templates[%d].read($1, _$$_t.%s);",
			e.getName(), e.getJavaTypeName(), i, e.getName());
	    }
	    buildString("}");
	}

	for (; i < entries.length; i++) {
	    buildString("if (length <= %d) { return _$$_t; }", i);

	    FieldEntry e = entries[i];
	    if (!e.isAvailable()) {
		buildString("$1.skip();"); // TODO #MN
		continue;
	    }

	    buildString("if($1.tryReadNil()) {");
	    // this is Optional field becaue i >= minimumArrayLength
	    // Optional + nil => keep default value
	    buildString("} else {");
	    Class<?> type = e.getType();
	    if (type.isPrimitive()) {
		buildString("_$$_t.%s = $1.%s();", e.getName(), primitiveReadName(type));
	    } else {
		buildString("_$$_t.%s = (%s) this.templates[%d].read($1, _$$_t.%s);",
			e.getName(), e.getJavaTypeName(), i, e.getName());
	    }
	    buildString("}");
	}

	// latter entries are all Optional + nil => keep default value

	buildString("for(int i = %d; i < length; i++) {", i);
	buildString("  $1.skip();"); // TODO #MN
	buildString("}");

	buildString("$1.readArrayEnd();");
	buildString("return _$$_t;");

	buildString("}");
	return getBuiltString();
    }

    @Override
    public void writeTemplate(Class<?> targetClass, FieldEntry[] entries, Template[] templates, String directoryName) {
	this.entries = entries;
	this.templates = templates;
	this.origClass = targetClass;
	this.origName = this.origClass.getName();
	write(this.origName, directoryName);
    }

    @Override
    public Template loadTemplate(Class<?> targetClass, FieldEntry[] entries, Template[] templates) {
	this.entries = entries;
	this.templates = templates;
	this.origClass = targetClass;
	this.origName = this.origClass.getName();
	return load(this.origName);
    }
}