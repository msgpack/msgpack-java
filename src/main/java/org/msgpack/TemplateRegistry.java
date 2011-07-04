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
package org.msgpack;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import java.math.BigInteger;

import org.msgpack.template.BigIntegerTemplate;
import org.msgpack.template.BooleanTemplate;
import org.msgpack.template.ByteArrayTemplate;
import org.msgpack.template.ByteTemplate;
import org.msgpack.template.DoubleArrayTemplate;
import org.msgpack.template.DoubleTemplate;
import org.msgpack.template.FloatArrayTemplate;
import org.msgpack.template.FloatTemplate;
import org.msgpack.template.IntArrayTemplate;
import org.msgpack.template.IntTemplate;
import org.msgpack.template.LongArrayTemplate;
import org.msgpack.template.LongTemplate;
import org.msgpack.template.ShortArrayTemplate;
import org.msgpack.template.ShortTemplate;
import org.msgpack.template.StringTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.ValueTemplate;
import org.msgpack.value.Value;


class TemplateRegistry {

    static void loadDefaultTemplates(TemplateRegistry reg) {
        reg.register(boolean.class, BooleanTemplate.getInstance());
        reg.register(Boolean.class, BooleanTemplate.getInstance());
        reg.register(byte.class, ByteTemplate.getInstance());
        reg.register(Byte.class, ByteTemplate.getInstance());
        reg.register(short.class, ShortTemplate.getInstance());
        reg.register(Short.class, ShortTemplate.getInstance());
        reg.register(int.class, IntTemplate.getInstance());
        reg.register(Integer.class, IntTemplate.getInstance());
        reg.register(long.class, LongTemplate.getInstance());
        reg.register(Long.class, LongTemplate.getInstance());
        reg.register(float.class, FloatTemplate.getInstance());
        reg.register(Float.class, FloatTemplate.getInstance());
        reg.register(double.class, DoubleTemplate.getInstance());
        reg.register(Double.class, DoubleTemplate.getInstance());
        reg.register(BigInteger.class, BigIntegerTemplate.getInstance());
        reg.register(boolean[].class, ByteArrayTemplate.getInstance());
        reg.register(short[].class, ShortArrayTemplate.getInstance());
        reg.register(int[].class, IntArrayTemplate.getInstance());
        reg.register(long[].class, LongArrayTemplate.getInstance());
        reg.register(float[].class, FloatArrayTemplate.getInstance());
        reg.register(double[].class, DoubleArrayTemplate.getInstance());
        reg.register(String.class, StringTemplate.getInstance());
        reg.register(byte[].class, ByteArrayTemplate.getInstance());
        reg.register(Value.class, ValueTemplate.getInstance());
    }

    private TemplateRegistry parent = null;

    private Map<Type, Template> templateCache;

    public TemplateRegistry() {
	this(null);
    }

    public TemplateRegistry(TemplateRegistry parent) {
	this.parent = parent;
	templateCache = new HashMap<Type, Template>();
	if (this.parent == null) {
	    loadDefaultTemplates(this);
	}
    }

    public Template lookup(Type type) {
        Template tmpl = tryLookup(type);
        if (tmpl == null && parent != null) {
            tmpl = parent.tryLookup(type);
        }
        return tmpl;
    }

    private Template tryLookup(Type type) {
        // TODO
        return templateCache.get(type);
    }

    public void register(Type type, Template tmpl) {
        // TODO
        templateCache.put(type, tmpl);
    }

    public void unregister(Type type) {
	// TODO
	if (type == null) {
	    throw new NullPointerException("Type is null");
	}

	templateCache.clear();
	if (parent != null) {
	    parent.unregister(type);
	}
    }

    public void unregister() {
	// TODO
	templateCache.clear();
	if (parent != null) {
	    parent.unregister();
	}
    }
}
