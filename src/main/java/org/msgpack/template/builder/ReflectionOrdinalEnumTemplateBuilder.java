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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.TemplateBuildException;
import org.msgpack.unpacker.Unpacker;


public class ReflectionOrdinalEnumTemplateBuilder extends ReflectionTemplateBuilder {

    static class ReflectionOrdinalEnumTemplate<T> implements Template<T> {
	private T[] entries;

	private Map<T, Integer> reverse;

	ReflectionOrdinalEnumTemplate(Class<T> targetClass) {
	    entries = targetClass.getEnumConstants();
	    reverse = new HashMap<T, Integer>();
	    for (int i = 0; i < entries.length; ++i) {
		reverse.put(entries[i], i);
	    }
	}

	@Override
	public void write(Packer pk, T target) throws IOException {
	    Integer ord = reverse.get(target);
	    if (ord == null) {
		throw new MessageTypeException();
	    }
	    pk.writeInt((int) ord);
	}

	@Override
	public T read(Unpacker pac, T to) throws IOException, MessageTypeException {
	    int ord = pac.readInt();
	    if (entries.length <= ord) {
		throw new MessageTypeException();
	    }
	    return entries[ord];
	}
    }

    public ReflectionOrdinalEnumTemplateBuilder(TemplateRegistry registry) {
	super(registry);
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries) {
	return super.buildTemplate(targetClass, entries);
    }

    @Override
    public <T> Template<T> buildTemplate(Type targetType) {
	Class<T> targetClass = (Class<T>) targetType;
	if (! targetClass.isEnum()) {
	    return super.buildTemplate(targetClass);
	}
	checkOrdinalEnumValidation(targetClass);
	return new ReflectionOrdinalEnumTemplate<T>(targetClass);
    }

    protected void checkOrdinalEnumValidation(Class<?> targetClass) {
	if(! targetClass.isEnum()) {
	    throw new TemplateBuildException(
		    "tried to build ordinal enum template of non-enum class: " + targetClass.getName());
	}
    }
}
