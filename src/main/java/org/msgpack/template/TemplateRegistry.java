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
import java.util.Collections;
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
import org.msgpack.template.builder.TemplateBuilder;
import org.msgpack.template.builder.TemplateBuilderChain;
import org.msgpack.type.Value;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class TemplateRegistry {

    private TemplateRegistry parent = null;

    private TemplateBuilderChain chain;

    Map<Type, Template<Type>> cache;

    private Map<Type, GenericTemplate> genericCache;

    /**
     * create <code>TemplateRegistry</code> object of root.
     */
    private TemplateRegistry() {
	parent = null;
	chain = createTemplateBuilderChain();
	genericCache = new HashMap<Type, GenericTemplate>();
	cache = new HashMap<Type, Template<Type>>();
	registerTemplates();
	cache = Collections.unmodifiableMap(cache);
    }

    /**
     * 
     * @param registry
     */
    public TemplateRegistry(TemplateRegistry registry) {
	if (registry != null) {
	    parent = registry;
	} else {
	    parent = new TemplateRegistry();
	}
	chain = createTemplateBuilderChain();
	cache = new HashMap<Type, Template<Type>>();
	genericCache = parent.genericCache;
    }

    protected TemplateBuilderChain createTemplateBuilderChain(){
        return new TemplateBuilderChain(this);
    }


    public void setClassLoader(final ClassLoader cl) {
	chain = new TemplateBuilderChain(this, cl);
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
        register(char.class, CharacterTemplate.getInstance());
        register(Character.class, CharacterTemplate.getInstance());
        register(boolean[].class, BooleanArrayTemplate.getInstance());
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
	buildAndRegister(null, targetClass, false, null);
    }

    public void register(final Class<?> targetClass, final FieldList flist) {
	if (flist == null) {
	    throw new NullPointerException("FieldList object is null");
	}

	buildAndRegister(null, targetClass, false, flist);
    }

    public synchronized void register(final Type targetType, final Template tmpl) {
	if (tmpl == null) {
	    throw new NullPointerException("Template object is null");
	}

	if (targetType instanceof ParameterizedType) {
            cache.put(((ParameterizedType) targetType).getRawType(), tmpl);
        } else {
            cache.put(targetType, tmpl);
        }
    }

    public synchronized void registerGeneric(final Type targetType, final GenericTemplate tmpl) {
	if (targetType instanceof ParameterizedType) {
	    genericCache.put(((ParameterizedType) targetType).getRawType(), tmpl);
	} else {
	    genericCache.put(targetType, tmpl);
	}
    }

    public synchronized boolean unregister(final Type targetType) {
	Template<Type> tmpl = cache.remove(targetType);
	return tmpl != null;
    }

    public synchronized void unregister() {
	cache.clear();
    }

    public synchronized Template lookup(Type targetType) {
	Template tmpl;

	tmpl = lookupGenericType(targetType);
	if (tmpl != null) {
	    return tmpl;
	}

	tmpl = lookupCache(targetType);
	if (tmpl != null) {
	    return tmpl;
	}

	Class<?> targetClass = (Class<?>) targetType;

	// MessagePackable interface is implemented
	if (MessagePackable.class.isAssignableFrom(targetClass)) {
	    // FIXME #MN
	    // following processing should be merged into lookAfterBuilding
	    // or lookupInterfaceTypes method in next version
	    tmpl = new MessagePackableTemplate(targetClass);
	    register(targetClass, tmpl);
	    return tmpl;
	}

	// find matched template builder and build template
	tmpl = lookupAfterBuilding(targetClass);
	if (tmpl != null) {
	    return tmpl;
	}

	// lookup template of interface type
	tmpl = lookupInterfaceTypes(targetClass);
	if (tmpl != null) {
	    return tmpl;
	}

	// lookup template of superclass type
	tmpl = lookupSuperclasses(targetClass);
	if (tmpl != null) {
	    return tmpl;
	}

	throw new MessageTypeException(
		"Cannot find template for " + targetClass + " class. Try to add @Message annotation to the class or call MessagePack.register(Type).");
    }

    private Template<Type> lookupGenericType(Type targetType) {
	Template<Type> tmpl = null;
	if (targetType instanceof ParameterizedType) {
	    ParameterizedType paramedType = (ParameterizedType) targetType;

	    // ParameterizedType is not a Class<?>?
	    tmpl = lookupGenericTypeImpl(paramedType);
	    if (tmpl != null) {
		return tmpl;
	    }

	    try {
		tmpl = parent.lookupGenericTypeImpl(paramedType);
		if (tmpl != null) {
		    return tmpl;
		}
	    } catch (NullPointerException e) { // ignore
	    }
	    targetType = paramedType.getRawType();
	}
	return tmpl;
    }

    private Template lookupGenericTypeImpl(final ParameterizedType targetType) {
	Type rawType = targetType.getRawType();

	GenericTemplate tmpl = genericCache.get(rawType);
	if (tmpl == null) {
	    return null;
	}

	Type[] types = targetType.getActualTypeArguments();
	Template[] tmpls = new Template[types.length];
	for (int i = 0; i < types.length; ++i) {
	    tmpls[i] = lookup(types[i]);
	}

	return tmpl.build(tmpls);
    }

    private Template<Type> lookupCache(Type targetType) {
	Template<Type> tmpl = cache.get(targetType);
	if (tmpl != null) {
	    return tmpl;
	}

	try {
	    tmpl = parent.lookupCache(targetType);
	} catch (NullPointerException e) { // ignore
	}
	return tmpl;
    }

    private <T> Template<T> lookupAfterBuilding(Class<T> targetClass) {
	TemplateBuilder builder = chain.select(targetClass, true);
	Template<T> tmpl = null;
	if (builder != null) {
	    tmpl = chain.getForceBuilder().loadTemplate(targetClass);
	    if (tmpl != null) {
		register(targetClass, tmpl);
		return tmpl;
	    }
	    tmpl = buildAndRegister(builder, targetClass, true, null);
	}
	return tmpl;
    }

    private <T> Template<T> lookupInterfaceTypes(Class<T> targetClass) {
	Class<?>[] infTypes = targetClass.getInterfaces();
	Template<T> tmpl = null;
	for (Class<?> infType : infTypes) {
	    tmpl = (Template<T>) cache.get(infType);
	    if (tmpl != null) {
		register(targetClass, tmpl);
		return tmpl;
	    } else {
		try {
		    tmpl = (Template<T>) parent.lookupCache(infType);
		    if (tmpl != null) {
			register(targetClass, tmpl);
			return tmpl;
		    }
		} catch (NullPointerException e) { // ignore
		}
	    }
	}
	return tmpl;
    }

    private <T> Template<T> lookupSuperclasses(Class<T> targetClass) {
	Class<?> superClass = targetClass.getSuperclass();
	Template<T> tmpl = null;
	if (superClass != null) {
	    for (; superClass != Object.class; superClass = superClass.getSuperclass()) {
		tmpl = (Template<T>) cache.get(superClass);
		if (tmpl != null) {
		    register(targetClass, tmpl);
		    return tmpl;
		} else {
		    try {
			tmpl = (Template<T>) parent.lookupCache(superClass);
			if (tmpl != null) {
			    register(targetClass, tmpl);
			    return tmpl;
			}
		    } catch (NullPointerException e) { // ignore
		    }
		}
	    }
	}
	return tmpl;
    }

    private synchronized Template buildAndRegister(TemplateBuilder builder, final Class targetClass,
	    final boolean hasAnnotation, final FieldList flist) {
	Template newTmpl = null;
	Template oldTmpl = null;
	try {
	    if (cache.containsKey(targetClass)) {
		oldTmpl = cache.get(targetClass);
	    }
	    newTmpl = new TemplateReference(this, targetClass);
	    cache.put(targetClass, newTmpl);
	    if (builder == null) {
		builder = chain.select(targetClass, hasAnnotation);
	    }
	    newTmpl = flist != null ? builder.buildTemplate(targetClass, flist) : builder.buildTemplate(targetClass);
	    return newTmpl;
	} catch (Exception e) {
	    if (oldTmpl != null) {
		cache.put(targetClass, oldTmpl);
	    } else {
		cache.remove(targetClass);
	    }
	    newTmpl = null;
	    if (e instanceof MessageTypeException) {
		throw (MessageTypeException) e;
	    } else {
		throw new MessageTypeException(e);
	    }
	} finally {
	    if (newTmpl != null) {
		cache.put(targetClass, newTmpl);
	    }
	}
    }
}
