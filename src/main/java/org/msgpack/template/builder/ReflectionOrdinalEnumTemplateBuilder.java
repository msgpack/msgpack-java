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

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.TemplateBuildException;
import org.msgpack.unpacker.Unpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReflectionOrdinalEnumTemplateBuilder extends AbstractTemplateBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionOrdinalEnumTemplateBuilder.class);

    static class ReflectionOrdinalEnumTemplate<T> extends AbstractTemplate<T> {
	private T[] entries;

	ReflectionOrdinalEnumTemplate(Class<T> targetClass) {
	    entries = targetClass.getEnumConstants();
	}

	@Override
	public void write(Packer pk, T target, boolean required) throws IOException {
	    pk.writeInt(((Enum) target).ordinal());
	}

	@Override
	public T read(Unpacker pac, T to, boolean required) throws IOException, MessageTypeException {
	    int ordinal = pac.readInt();
	    if (ordinal < 0 || ordinal >= entries.length) {
		throw new MessageTypeException("illegal ordinal");
	    }
	    return entries[ordinal];
	}
    }

    public ReflectionOrdinalEnumTemplateBuilder(TemplateRegistry registry) {
	super(registry);
    }

    @Override
    public boolean matchType(Type targetType, boolean hasAnnotation) {
	Class<?> targetClass = (Class<?>) targetType;
	boolean matched = matchAtOrdinalEnumTemplateBuilder(targetClass, hasAnnotation);
	if (matched) {
	    LOG.debug("matched type: " + targetClass.getName());
	}
	return matched;
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries) {
	throw new UnsupportedOperationException("fatal error: " + targetClass.getName());
    }

    @Override
    public <T> Template<T> buildTemplate(Type targetType) {
	@SuppressWarnings("unchecked")
	Class<T> targetClass = (Class<T>) targetType;
	checkOrdinalEnumValidation(targetClass);
	return new ReflectionOrdinalEnumTemplate<T>(targetClass);
    }

    protected void checkOrdinalEnumValidation(Class<?> targetClass) {
	if(! targetClass.isEnum()) {
	    throw new TemplateBuildException("tried to build ordinal enum template of non-enum class: " + targetClass.getName());
	}
    }
}
