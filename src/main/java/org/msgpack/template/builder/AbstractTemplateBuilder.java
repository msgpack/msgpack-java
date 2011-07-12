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

import java.lang.reflect.Type;

import org.msgpack.template.FieldList;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import org.msgpack.template.builder.TemplateBuildException;


public abstract class AbstractTemplateBuilder implements TemplateBuilder {

    public FieldEntryReader getFieldEntryReader() {
	throw new UnsupportedOperationException();
    }

    @Override
    public <T> Template<T> buildTemplate(Type type) throws TemplateBuildException {
	Class<T> c = (Class<T>) type;
	checkValidation(c);
	FieldEntryReader reader = getFieldEntryReader();
	FieldOption implicitOption = reader.readImplicitFieldOption(c);
	FieldEntry[] entries = reader.readFieldEntries(c, implicitOption);
	return buildTemplate(c, entries);
    }

    public <T> Template<T> buildTemplate(Class<T> c, FieldList flist) throws TemplateBuildException {
	try {
	    checkValidation(c);
	    return buildTemplate(c, getFieldEntryReader().convertFieldEntries(c, flist));
	} catch (NoSuchFieldException e) {
	    throw new TemplateBuildException(e);
	}
    }

    public abstract <T> Template<T> buildTemplate(Class<T> type, FieldEntry[] entries);

    protected void checkValidation(Class<?> type) {
	if (type.isInterface()) {
	    throw new TemplateBuildException("Cannot build template for interface: " + type.getName());
	}
	if (type.isArray()) {
	    throw new TemplateBuildException("Cannot build template for array class: " + type.getName());
	}
	if (type.isPrimitive()) {
	    throw new TemplateBuildException("Cannot build template of primitive type: " + type.getName());
	}
    }

    @Override
    public void writeTemplate(Type type, String directoryName) {
	throw new UnsupportedOperationException(type.toString());
    }

    @Override
    public <T> Template<T> loadTemplate(Type type) {
	return null;
    }
}
