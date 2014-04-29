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
package org.msgpack.template;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
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
import org.msgpack.template.builder.ArrayTemplateBuilder;
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
        genericCache = new HashMap<Type, GenericTemplate>();
        registerTemplatesWhichRefersRegistry();
    }

    protected TemplateBuilderChain createTemplateBuilderChain() {
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
        register(BigDecimal.class, BigDecimalTemplate.getInstance());
        register(Date.class, DateTemplate.getInstance());

        registerTemplatesWhichRefersRegistry();

    }

    protected void registerTemplatesWhichRefersRegistry() {
        AnyTemplate anyTemplate = new AnyTemplate(this);

        register(List.class, new ListTemplate(anyTemplate));
        register(Set.class, new SetTemplate(anyTemplate));
        register(Collection.class, new CollectionTemplate(anyTemplate));
        register(Map.class, new MapTemplate(anyTemplate, anyTemplate));
        registerGeneric(List.class, new GenericCollectionTemplate(this, ListTemplate.class));
        registerGeneric(Set.class, new GenericCollectionTemplate(this, SetTemplate.class));
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
            genericCache.put(((ParameterizedType) targetType).getRawType(),
                    tmpl);
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

        if (targetType instanceof ParameterizedType) {
            // ParameterizedType is not a Class<?>
            ParameterizedType paramedType = (ParameterizedType) targetType;
            tmpl = lookupGenericType(paramedType);
            if (tmpl != null) {
                return tmpl;
            }
            targetType = paramedType.getRawType();
        }

        tmpl = lookupGenericArrayType(targetType);
        if (tmpl != null) {
            return tmpl;
        }

        tmpl = lookupCache(targetType);
        if (tmpl != null) {
            return tmpl;
        }

        if (targetType instanceof WildcardType ||
                targetType instanceof TypeVariable) {
            // WildcardType is not a Class<?>
            tmpl = new AnyTemplate<Object>(this);
            register(targetType, tmpl);
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

        if (targetClass.isInterface()) {
            // writing interfaces will succeed
            // reading into interfaces will fail
            tmpl = new AnyTemplate<Object>(this);
            register(targetType, tmpl);
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

        // lookup template of interface type of superclasss
        tmpl = lookupSuperclassInterfaceTypes(targetClass);
        if (tmpl != null) {
            return tmpl;
        }

        throw new MessageTypeException(
                "Cannot find template for " + targetClass + " class.  " +
                "Try to add @Message annotation to the class or call MessagePack.register(Type).");
    }

    private Template<Type> lookupGenericType(ParameterizedType paramedType) {
        Template<Type> tmpl = lookupGenericTypeImpl(paramedType);
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

        tmpl = lookupGenericInterfaceTypes(paramedType);
        if (tmpl != null) {
            return tmpl;
        }

        tmpl = lookupGenericSuperclasses(paramedType);
        if (tmpl != null) {
            return tmpl;
        }

        return null;
    }

    private Template lookupGenericTypeImpl(ParameterizedType targetType) {
        Type rawType = targetType.getRawType();
        return lookupGenericTypeImpl0(targetType, rawType);
    }

    private Template lookupGenericTypeImpl0(ParameterizedType targetType, Type rawType) {
        GenericTemplate gtmpl = genericCache.get(rawType);
        if (gtmpl == null) {
            return null;
        }

        Type[] types = targetType.getActualTypeArguments();
        Template[] tmpls = new Template[types.length];
        for (int i = 0; i < types.length; ++i) {
            tmpls[i] = lookup(types[i]);
        }

        return gtmpl.build(tmpls);
    }

    private <T> Template<T> lookupGenericInterfaceTypes(ParameterizedType targetType) {
        Type rawType = targetType.getRawType();
        Template<T> tmpl = null;

        try {
            Class<?>[] infTypes = ((Class) rawType).getInterfaces();
            for (Class<?> infType : infTypes) {
                tmpl = lookupGenericTypeImpl0(targetType, infType);
                if (tmpl != null) {
                    return tmpl;
                }
            }
        } catch (ClassCastException e) { // ignore
        }

        return tmpl;
    }

    private <T> Template<T> lookupGenericSuperclasses(ParameterizedType targetType) {
        Type rawType = targetType.getRawType();
        Template<T> tmpl = null;

        try {
            Class<?> superClass = ((Class) rawType).getSuperclass();
            if (superClass == null) {
                return null;
            }

            for (; superClass != Object.class; superClass = superClass.getSuperclass()) {
                tmpl = lookupGenericTypeImpl0(targetType, superClass);
                if (tmpl != null) {
                    register(targetType, tmpl);
                    return tmpl;
                }
            }
        } catch (ClassCastException e) { // ignore
        }

        return tmpl;
    }

    private Template<Type> lookupGenericArrayType(Type targetType) {
        // TODO GenericArrayType is not a Class<?> => buildArrayTemplate
        if (! (targetType instanceof GenericArrayType)) {
            return null;
        }

        GenericArrayType genericArrayType = (GenericArrayType) targetType;
        Template<Type> tmpl = lookupGenericArrayTypeImpl(genericArrayType);
        if (tmpl != null) {
            return tmpl;
        }

        try {
            tmpl = parent.lookupGenericArrayTypeImpl(genericArrayType);
            if (tmpl != null) {
                return tmpl;
            }
        } catch (NullPointerException e) { // ignore
        }

        return null;
    }

    private Template lookupGenericArrayTypeImpl(GenericArrayType genericArrayType) {
        String genericArrayTypeName = "" + genericArrayType;
        int dim = genericArrayTypeName.split("\\[").length - 1;
        if (dim <= 0) {
            throw new MessageTypeException(
                    String.format("fatal error: type=", genericArrayTypeName));
        } else if (dim > 1) {
            throw new UnsupportedOperationException(String.format(
                    "Not implemented template generation of %s", genericArrayTypeName));
        }

        String genericCompTypeName = "" + genericArrayType.getGenericComponentType();
        boolean isPrimitiveType = isPrimitiveType(genericCompTypeName);
        StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < dim; i++) {
            sbuf.append('[');
        }
        if (!isPrimitiveType) {
            sbuf.append('L');
            sbuf.append(toJvmReferenceTypeName(genericCompTypeName));
            sbuf.append(';');
        } else {
            sbuf.append(toJvmPrimitiveTypeName(genericCompTypeName));
        }

        String jvmArrayClassName = sbuf.toString();
        Class jvmArrayClass = null;
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                jvmArrayClass = cl.loadClass(jvmArrayClassName);
                if (jvmArrayClass != null) {
                    return lookupAfterBuilding(jvmArrayClass);
                }
            }
        } catch (ClassNotFoundException e) {} // ignore

        try {
            cl = getClass().getClassLoader();
            if (cl != null) {
                jvmArrayClass = cl.loadClass(jvmArrayClassName);
                if (jvmArrayClass != null) {
                    return lookupAfterBuilding(jvmArrayClass);
                }
            }
        } catch (ClassNotFoundException e) {} // ignore

        try {
            jvmArrayClass = Class.forName(jvmArrayClassName);
            if (jvmArrayClass != null) {
                return lookupAfterBuilding(jvmArrayClass);
            }
        } catch (ClassNotFoundException e) {} // ignore

        throw new MessageTypeException(String.format(
                "cannot find template of %s", jvmArrayClassName));
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
            // TODO #MN for Android, we should modify here
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
            for (; superClass != Object.class; superClass = superClass
                    .getSuperclass()) {
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

    private <T> Template<T> lookupSuperclassInterfaceTypes(Class<T> targetClass) {
        Class<?> superClass = targetClass.getSuperclass();
        Template<T> tmpl = null;
        if (superClass != null) {
            for (; superClass != Object.class; superClass = superClass.getSuperclass()) {
                tmpl = (Template<T>) lookupInterfaceTypes(superClass);
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

    private synchronized Template buildAndRegister(TemplateBuilder builder,
            final Class targetClass, final boolean hasAnnotation,
            final FieldList flist) {
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
            newTmpl = flist != null ?
                    builder.buildTemplate(targetClass, flist) : builder.buildTemplate(targetClass);
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

    private static boolean isPrimitiveType(String genericCompTypeName) {
        return (genericCompTypeName.equals("byte")
                || genericCompTypeName.equals("short")
                || genericCompTypeName.equals("int")
                || genericCompTypeName.equals("long")
                || genericCompTypeName.equals("float")
                || genericCompTypeName.equals("double")
                || genericCompTypeName.equals("boolean")
                || genericCompTypeName.equals("char"));
    }

    private static String toJvmReferenceTypeName(String typeName) {
        // delete "class " from class name
        // e.g. "class Foo" to "Foo" by this method
        return typeName.substring(6);
    }

    private static String toJvmPrimitiveTypeName(String typeName) {
        if (typeName.equals("byte")) {
            return "B";
        } else if (typeName.equals("short")) {
            return "S";
        } else if (typeName.equals("int")) {
            return "I";
        } else if (typeName.equals("long")) {
            return "J";
        } else if (typeName.equals("float")) {
            return "F";
        } else if (typeName.equals("double")) {
            return "D";
        } else if (typeName.equals("boolean")) {
            return "Z";
        } else if (typeName.equals("char")) {
            return "C";
        } else {
            throw new MessageTypeException(String.format(
                    "fatal error: type=%s", typeName));
        }
    }
}
