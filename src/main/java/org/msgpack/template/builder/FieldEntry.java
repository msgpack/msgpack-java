//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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

import org.msgpack.template.FieldOption;

public abstract class FieldEntry {

    protected FieldOption option;

    public FieldEntry() {
        this(FieldOption.IGNORE);
    }

    public FieldEntry(FieldOption option) {
        this.option = option;
    }

    public FieldOption getOption() {
        return option;
    }

    public void setOption(FieldOption option) {
        this.option = option;
    }

    public boolean isAvailable() {
        return option != FieldOption.IGNORE;
    }

    public boolean isOptional() {
        return option == FieldOption.OPTIONAL;
    }

    public boolean isNotNullable() {
        return option == FieldOption.NOTNULLABLE;
    }

    public abstract String getName();

    public abstract Class<?> getType();

    public abstract Type getGenericType();

    public abstract Object get(Object target);

    public abstract void set(Object target, Object value);

    public String getJavaTypeName() {
        Class<?> type = getType();
        if (type.isArray()) {
            return arrayTypeToString(type);
        } else {
            return type.getName();
        }
    }

    public String arrayTypeToString(Class<?> type) {
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
