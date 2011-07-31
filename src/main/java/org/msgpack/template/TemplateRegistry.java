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
package org.msgpack.template;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.msgpack.MessagePackable;
import org.msgpack.MessageTypeException;
import org.msgpack.template.BigIntegerTemplate;
import org.msgpack.template.BooleanTemplate;
import org.msgpack.template.ByteArrayTemplate;
import org.msgpack.template.ByteTemplate;
import org.msgpack.template.DefaultTemplate;
import org.msgpack.template.DoubleArrayTemplate;
import org.msgpack.template.DoubleTemplate;
import org.msgpack.template.FieldList;
import org.msgpack.template.FloatArrayTemplate;
import org.msgpack.template.FloatTemplate;
import org.msgpack.template.GenericTemplate;
import org.msgpack.template.IntegerArrayTemplate;
import org.msgpack.template.IntegerTemplate;
import org.msgpack.template.LongArrayTemplate;
import org.msgpack.template.LongTemplate;
import org.msgpack.template.ShortArrayTemplate;
import org.msgpack.template.ShortTemplate;
import org.msgpack.template.StringTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.ValueTemplate;
import org.msgpack.template.builder.AbstractTemplateBuilder;
import org.msgpack.template.builder.TemplateBuilder;
import org.msgpack.template.builder.TemplateBuilderChain;
import org.msgpack.type.Value;


public class TemplateRegistry {

    private TemplateRegistry parent = null;

    private TemplateBuilderChain chain;

    private Map<Type, Template<Type>> cache;

    private Map<Type, GenericTemplate> genericCache;

    public TemplateRegistry() {
	this(null);
    }

    public TemplateRegistry(TemplateRegistry registry) {
	parent = registry;
	cache = new HashMap<Type, Template<Type>>();
	genericCache = new HashMap<Type, GenericTemplate>();
	if (parent == null) {
	    registerTemplates();
	    chain = new TemplateBuilderChain();
	    chain.init(this);
	} else {
	    chain = registry.chain;
	}
    }

    private void registerTemplates() {
        register(boolean.class, BooleanTemplate.getInstance());
        register(Boolean.class, BooleanTemplate.getInstance());
        register(byte.class, ByteTemplate.getInstance());
        register(Byte.class, ByteTemplate.getInstance());
        register(short.class, ShortTemplate.getInstance());
        register(Short.class, ShortTemplate.getInstance());
        register(int.class, IntegerTemplate.getInstance());
        register(Integer.class, IntegerTemplate.getInstance());
        register(long.class, LongTemplate.getInstance());
        register(Long.class, LongTemplate.getInstance());
        register(float.class, FloatTemplate.getInstance());
        register(Float.class, FloatTemplate.getInstance());
        register(double.class, DoubleTemplate.getInstance());
        register(Double.class, DoubleTemplate.getInstance());
        register(BigInteger.class, BigIntegerTemplate.getInstance());
        register(boolean[].class, ByteArrayTemplate.getInstance());
        register(short[].class, ShortArrayTemplate.getInstance());
        register(int[].class, IntegerArrayTemplate.getInstance());
        register(long[].class, LongArrayTemplate.getInstance());
        register(float[].class, FloatArrayTemplate.getInstance());
        register(double[].class, DoubleArrayTemplate.getInstance());
        register(String.class, StringTemplate.getInstance());
        register(byte[].class, ByteArrayTemplate.getInstance());
        register(ByteBuffer.class, ByteBufferTemplate.getInstance());
        register(Value.class, ValueTemplate.getInstance());
        //register(Value.class, AnyTemplate.getInstance(this));
        register(List.class, new ListTemplate(AnyTemplate.getInstance(this)));
        register(Collection.class, new CollectionTemplate(AnyTemplate.getInstance(this)));
        register(Map.class, new MapTemplate(AnyTemplate.getInstance(this), AnyTemplate.getInstance(this)));

        registerGeneric(List.class, new GenericCollectionTemplate(this, ListTemplate.class));
        registerGeneric(Collection.class, new GenericCollectionTemplate(this, CollectionTemplate.class));
        registerGeneric(Map.class, new GenericMapTemplate(this, MapTemplate.class));
    }

    public void register(final Class<?> targetClass) {
	register(targetClass, chain.select(targetClass, false).buildTemplate(targetClass));
    }

    public void register(final Class<?> targetClass, final FieldList flist) {
	if (flist == null) {
	    throw new NullPointerException("FieldList object is null");
	}
	register(targetClass, chain.select(targetClass, false).buildTemplate(targetClass, flist));
    }

    public synchronized void register(final Type targetType, final Template tmpl) {
        if (targetType instanceof ParameterizedType) {
            cache.put(((ParameterizedType) targetType).getRawType(), tmpl);
        } else {
            cache.put(targetType, tmpl);
        }
    }

    public synchronized void registerGeneric(final Type targetType, final GenericTemplate tmpl) {
	if(targetType instanceof ParameterizedType) {
	    genericCache.put(((ParameterizedType) targetType).getRawType(), tmpl);
	} else {
	    genericCache.put(targetType, tmpl);
	}
    }

    public boolean unregister(final Type targetType) {
	Template<Type> tmpl = cache.remove(targetType);
	return tmpl != null;
    }

    public void unregister() {
	cache.clear();
    }

    public synchronized Template lookup(Type targetType) {
        return lookupImpl(targetType, true, false, true);
    }

    public synchronized Template lookup(Type targetType, final boolean forceBuild) {
	return lookupImpl(targetType, true, forceBuild, true);
    }

    public synchronized Template lookup(Type targetType, final boolean forceLoad, final boolean forceBuild) {
	return lookupImpl(targetType, forceLoad, forceBuild, true);
    }

    public synchronized Template tryLookup(Type targetType) {
	return lookupImpl(targetType, true, false, false);
    }

    public synchronized Template tryLookup(Type targetType, final boolean forceBuild) {
	return lookupImpl(targetType, true, forceBuild, false);
    }

    private synchronized Template lookupImpl(Type targetType,
	    final boolean forceLoad, final boolean forceBuild, final boolean fallbackDefault) {
	Template tmpl;

	if (targetType instanceof ParameterizedType) {
	    ParameterizedType pType = (ParameterizedType) targetType;
	    // ParameterizedType is not a Class<?>?
	    tmpl = lookupGenericImpl(pType);
	    if (tmpl != null) {
		return tmpl;
	    }
	    try {
		tmpl = parent.lookupGenericImpl(pType);
		if (tmpl != null) {
		    return tmpl;
		}
	    } catch (NullPointerException e) { // ignore
	    }
	    targetType = pType.getRawType();
	}

	tmpl = cache.get(targetType);
	if (tmpl != null) {
	    return tmpl;
	}
	try {
	    tmpl = parent.cache.get(targetType);
	    if (tmpl != null) {
		return tmpl;
	    }
	} catch (NullPointerException e) { // ignore
	}

	Class<?> targetClass = (Class<?>) targetType;

	if (MessagePackable.class.isAssignableFrom(targetClass)) {
	    tmpl = new MessagePackableTemplate(targetClass);
	    register(targetClass, tmpl);
	    return tmpl;
	}

	// find match TemplateBuilder
	TemplateBuilder builder = chain.select(targetClass, true);
	if (builder != null) {
	    if (forceLoad) {
		tmpl = builder.loadTemplate(targetClass);
		if (tmpl != null) {
		    register(targetClass, tmpl);
		    return tmpl;
		}
	    }

	    tmpl = builder.buildTemplate(targetClass);
	    if (tmpl != null) {
		register(targetClass, tmpl);
		return tmpl;
	    }
	}

	// lookup template of interface type
	Class<?>[] infTypes = targetClass.getInterfaces();
	for (Class<?> infType : infTypes) {
	    tmpl = cache.get(infType);
	    if (tmpl != null) {
		register(targetClass, tmpl);
		return tmpl;
	    } else {
		try {
		    tmpl = parent.cache.get(infType);
		    if (tmpl != null) {
			parent.register(targetClass, tmpl);
			return tmpl;
		    }
		} catch (NullPointerException e) { // ignore
		}
	    }
	}

	// lookup template of superclass type
	Class<?> superClass = targetClass.getSuperclass();
	if (superClass != null) {
	    for (; superClass != Object.class; superClass = superClass.getSuperclass()) {
		tmpl = cache.get(superClass);
		if (tmpl != null) {
		    register(targetClass, tmpl);
		    return tmpl;
		} else {
		    try {
			tmpl = parent.cache.get(superClass);
			if (tmpl != null) {
			    register(targetClass, tmpl);
			    return tmpl;
			}
		    } catch (NullPointerException e) { // ignore
		    }
		}
	    }

	    if (forceBuild) {
		tmpl = chain.select(targetClass, true).buildTemplate(targetClass);
		register(targetClass, tmpl);
		return tmpl;
	    }
	}

	if (fallbackDefault) {
	    tmpl = new DefaultTemplate(this, (Class<?>) targetClass);
	    register(targetClass, tmpl);
	    return tmpl;
	} else {
	    throw new MessageTypeException(
		    "Cannot find template for " + targetClass + " class. Try to add @Message annotation to the class or call MessagePack.register(Type).");
	}
    }

    public synchronized Template lookupGeneric(final Type targetType) {
	if (targetType instanceof ParameterizedType) {
	    ParameterizedType parameterizedType = (ParameterizedType)targetType;
	    Template tmpl = lookupGenericImpl(parameterizedType);
	    if (tmpl != null) {
		return tmpl;
	    }
	    return new DefaultTemplate(this, (Class<?>) parameterizedType.getRawType(), parameterizedType);
	} else {
	    throw new IllegalArgumentException("Actual types of the generic type are erased: "+targetType);
	}
    }

    private synchronized Template lookupGenericImpl(final ParameterizedType targetType) {
	Type rawType = targetType.getRawType();
	GenericTemplate tmpl = genericCache.get(rawType);
	if (tmpl == null) {
	    return null;
	}

	Type[] types = targetType.getActualTypeArguments();
	Template[] tmpls = new Template[types.length];
	for (int i=0; i < types.length; ++i) {
	    tmpls[i] = lookup(types[i]);
	}

	return tmpl.build(tmpls);
    }
}
