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

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.msgpack.MessageTypeException;
import org.msgpack.template.FieldOption;

public class DefaultFieldEntry extends FieldEntry {
    protected Field field;

    public DefaultFieldEntry() {
        this(null, FieldOption.IGNORE);
    }

    public DefaultFieldEntry(final DefaultFieldEntry e) {
        this(e.field, e.option);
    }

    public DefaultFieldEntry(final Field field, final FieldOption option) {
        super(option);
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public void setField(final Field field) {
        this.field = field;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public Object get(Object target) {
        try {
            return getField().get(target);
        } catch (IllegalArgumentException e) {
            throw new MessageTypeException(e);
        } catch (IllegalAccessException e) {
            throw new MessageTypeException(e);
        }
    }

    @Override
    public void set(Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalArgumentException e) {
            throw new MessageTypeException(e);
        } catch (IllegalAccessException e) {
            throw new MessageTypeException(e);
        }
    }
}