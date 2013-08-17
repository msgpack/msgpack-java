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

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.msgpack.annotation.Ignore;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.NotNullable;
import org.msgpack.annotation.Optional;
import org.msgpack.packer.Packer;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.beans.BeanInfo;
import org.msgpack.template.builder.beans.IntrospectionException;
import org.msgpack.template.builder.beans.Introspector;
import org.msgpack.template.builder.beans.PropertyDescriptor;
import org.msgpack.unpacker.Unpacker;

/**
 * Class for building java reflection template builder for java beans class.
 * 
 * @author takeshita
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class ReflectionBeansTemplateBuilder extends ReflectionTemplateBuilder {

    private static Logger LOG = Logger.getLogger(ReflectionBeansTemplateBuilder.class.getName());

    static class ReflectionBeansFieldTemplate extends ReflectionFieldTemplate {
        ReflectionBeansFieldTemplate(final FieldEntry entry) {
            super(entry);
        }

        @Override
        public void write(Packer packer, Object v, boolean required) throws IOException {
            packer.write(v);
        }

        @Override
        public Object read(Unpacker unpacker, Object to, boolean required) throws IOException {
            Object o = unpacker.read(entry.getType());
            entry.set(to, o);
            return o;
        }
    }

    public ReflectionBeansTemplateBuilder(TemplateRegistry registry) {
        super(registry, null);
    }

    @Override
    public boolean matchType(Type targetType, boolean hasAnnotation) {
        Class<?> targetClass = (Class<?>) targetType;
        boolean matched = matchAtBeansClassTemplateBuilder(targetClass, hasAnnotation);
        if (matched && LOG.isLoggable(Level.FINE)) {
            LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }

    @Override
    protected ReflectionFieldTemplate[] toTemplates(FieldEntry[] entries) {
        ReflectionFieldTemplate[] tmpls = new ReflectionFieldTemplate[entries.length];
        for (int i = 0; i < entries.length; i++) {
            FieldEntry e = entries[i];
            Class<?> type = e.getType();
            if (type.isPrimitive()) {
                tmpls[i] = new ReflectionBeansFieldTemplate(e);
            } else {
                Template tmpl = registry.lookup(e.getGenericType());
                tmpls[i] = new FieldTemplateImpl(e, tmpl);
            }
        }
        return tmpls;
    }

    @Override
    public FieldEntry[] toFieldEntries(Class<?> targetClass, FieldOption implicitOption) {
        BeanInfo desc;
        try {
            desc = Introspector.getBeanInfo(targetClass);
        } catch (IntrospectionException e1) {
            throw new TemplateBuildException(
                    "Class must be java beans class:" + targetClass.getName());
        }

        PropertyDescriptor[] props = desc.getPropertyDescriptors();
        ArrayList<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();
        for (int i = 0; i < props.length; i++) {
            PropertyDescriptor pd = props[i];
            if (!isIgnoreProperty(pd)) {
                list.add(pd);
            }
        }
        props = new PropertyDescriptor[list.size()];
        list.toArray(props);

        BeansFieldEntry[] entries = new BeansFieldEntry[props.length];
        for (int i = 0; i < props.length; i++) {
            PropertyDescriptor p = props[i];
            int index = getPropertyIndex(p);
            if (index >= 0) {
                if (entries[index] != null) {
                    throw new TemplateBuildException("duplicated index: " + index);
                }
                if (index >= entries.length) {
                    throw new TemplateBuildException("invalid index: " + index);
                }
                entries[index] = new BeansFieldEntry(p);
                props[index] = null;
            }
        }
        int insertIndex = 0;
        for (int i = 0; i < props.length; i++) {
            PropertyDescriptor p = props[i];
            if (p != null) {
                while (entries[insertIndex] != null) {
                    insertIndex++;
                }
                entries[insertIndex] = new BeansFieldEntry(p);
            }

        }
        for (int i = 0; i < entries.length; i++) {
            BeansFieldEntry e = entries[i];
            FieldOption op = getPropertyOption(e, implicitOption);
            e.setOption(op);
        }
        return entries;
    }

    private FieldOption getPropertyOption(BeansFieldEntry e, FieldOption implicitOption) {
        FieldOption forGetter = getMethodOption(e.getPropertyDescriptor().getReadMethod());
        if (forGetter != FieldOption.DEFAULT) {
            return forGetter;
        }
        FieldOption forSetter = getMethodOption(e.getPropertyDescriptor().getWriteMethod());
        if (forSetter != FieldOption.DEFAULT) {
            return forSetter;
        } else {
            return implicitOption;
        }
    }

    private FieldOption getMethodOption(Method method) {
        if (isAnnotated(method, Ignore.class)) {
            return FieldOption.IGNORE;
        } else if (isAnnotated(method, Optional.class)) {
            return FieldOption.OPTIONAL;
        } else if (isAnnotated(method, NotNullable.class)) {
            return FieldOption.NOTNULLABLE;
        }
        return FieldOption.DEFAULT;
    }

    private int getPropertyIndex(PropertyDescriptor desc) {
        int getterIndex = getMethodIndex(desc.getReadMethod());
        if (getterIndex >= 0) {
            return getterIndex;
        }
        int setterIndex = getMethodIndex(desc.getWriteMethod());
        return setterIndex;
    }

    private int getMethodIndex(Method method) {
        Index a = method.getAnnotation(Index.class);
        if (a == null) {
            return -1;
        } else {
            return a.value();
        }
    }

    private boolean isIgnoreProperty(PropertyDescriptor desc) {
        if (desc == null) {
            return true;
        }
        Method getter = desc.getReadMethod();
        Method setter = desc.getWriteMethod();
        return getter == null || setter == null
                || !Modifier.isPublic(getter.getModifiers())
                || !Modifier.isPublic(setter.getModifiers())
                || isAnnotated(getter, Ignore.class)
                || isAnnotated(setter, Ignore.class);
    }
}
