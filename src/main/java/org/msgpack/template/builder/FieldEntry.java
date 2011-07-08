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

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.msgpack.template.FieldOption;



public class FieldEntry {
    private Field field;

    private FieldOption option;

    public FieldEntry() {
	this(null, FieldOption.IGNORE);
    }

    public FieldEntry(final FieldEntry e) {
	this(e.field, e.option);
    }

    public FieldEntry(final Field field, final FieldOption option) {
	this.field = field;
	this.option = option;
    }

    public Field getField() {
	return field;
    }

    public String getName() {
	return field.getName();
    }

    public Class<?> getType() {
	return field.getType();
    }

    public String getJavaTypeName() {
	Class<?> type = field.getType();
	if (type.isArray()) {
	    return arrayTypeToString(type);
	} else {
	    return type.getName();
	}
    }

    public Type getGenericType() {
	return field.getGenericType();
    }

    public FieldOption getOption() {
	return option;
    }

    public boolean isAvailable() {
	return option != FieldOption.IGNORE;
    }

    public boolean isRequired() {
	return option == FieldOption.REQUIRED;
    }

    public boolean isOptional() {
	return option == FieldOption.OPTIONAL;
    }

    public boolean isNotNullable() {
	return option == FieldOption.NOTNULLABLE;
    }

    static String arrayTypeToString(Class<?> type) {
	int dim = 1;
	Class<?> baseType = type.getComponentType();
	while (baseType.isArray()) {
	    baseType = baseType.getComponentType();
	    dim += 1;
	}
	StringBuilder sb = new StringBuilder();
	sb.append(baseType.getName());
	for (int i = 0; i < dim; ++i) {
	    sb.append("[]");
	}
	return sb.toString();
    }
}