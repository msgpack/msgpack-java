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
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.BooleanArrayTemplate;
import org.msgpack.template.ByteArrayTemplate;
import org.msgpack.template.DoubleArrayTemplate;
import org.msgpack.template.FieldList;
import org.msgpack.template.FloatArrayTemplate;
import org.msgpack.template.IntegerArrayTemplate;
import org.msgpack.template.LongArrayTemplate;
import org.msgpack.template.ObjectArrayTemplate;
import org.msgpack.template.ShortArrayTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.unpacker.Unpacker;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArrayTemplateBuilder extends AbstractTemplateBuilder {

    private static final Logger LOG = Logger.getLogger(ArrayTemplateBuilder.class.getName());

    static class ReflectionMultidimentionalArrayTemplate extends AbstractTemplate {

        private Class componentClass;

        private Template componentTemplate;

        public ReflectionMultidimentionalArrayTemplate(Class componentClass, Template componentTemplate) {
            this.componentClass = componentClass;
            this.componentTemplate = componentTemplate;
        }

        Class getComponentClass() {
            return componentClass;
        }

        @Override
        public void write(Packer packer, Object v, boolean required) throws IOException {
            if (v == null) {
                if (required) {
                    throw new MessageTypeException("Attempted to write null");
                }
                packer.writeNil();
                return;
            }
            if (!(v instanceof Object[])
                    || !componentClass.isAssignableFrom(v.getClass().getComponentType())) {
                throw new MessageTypeException();
            }

            Object[] array = (Object[]) v;
            int length = array.length;
            packer.writeArrayBegin(length);
            for (int i = 0; i < length; i++) {
                componentTemplate.write(packer, array[i], required);
            }
            packer.writeArrayEnd();
        }

        @Override
        public Object read(Unpacker unpacker, Object to, boolean required) throws IOException {
            if (!required && unpacker.trySkipNil()) {
                return null;
            }

            int length = unpacker.readArrayBegin();
            Object[] array = (Object[]) Array.newInstance(componentClass, length);
            for (int i = 0; i < length; i++) {
                array[i] = componentTemplate.read(unpacker, null, required);
            }
            unpacker.readArrayEnd();
            return array;
        }
    }

    public ArrayTemplateBuilder(TemplateRegistry registry) {
        super(registry);
    }

    @Override
    public boolean matchType(Type targetType, boolean forceBuild) {
        Class<?> targetClass = (Class<?>) targetType;
        boolean matched = AbstractTemplateBuilder.matchAtArrayTemplateBuilder(targetClass, false);
        if (matched && LOG.isLoggable(Level.FINE)) {
            LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }

    @Override
    public <T> Template<T> buildTemplate(Type arrayType) {
        Type baseType;
        Class<?> baseClass;
        int dim = 1;
        if (arrayType instanceof GenericArrayType) {
            GenericArrayType type = (GenericArrayType) arrayType;
            baseType = type.getGenericComponentType();
            while (baseType instanceof GenericArrayType) {
                baseType = ((GenericArrayType) baseType).getGenericComponentType();
                dim += 1;
            }
            if (baseType instanceof ParameterizedType) {
                baseClass = (Class<?>) ((ParameterizedType) baseType).getRawType();
            } else {
                baseClass = (Class<?>) baseType;
            }
        } else {
            Class<?> type = (Class<?>) arrayType;
            baseClass = type.getComponentType();
            while (baseClass.isArray()) {
                baseClass = baseClass.getComponentType();
                dim += 1;
            }
            baseType = baseClass;
        }
        return toTemplate(arrayType, baseType, baseClass, dim);
    }

    private Template toTemplate(Type arrayType, Type genericBaseType, Class baseClass, int dim) {
        if (dim == 1) {
            if (baseClass == boolean.class) {
                return BooleanArrayTemplate.getInstance();
            } else if (baseClass == short.class) {
                return ShortArrayTemplate.getInstance();
            } else if (baseClass == int.class) {
                return IntegerArrayTemplate.getInstance();
            } else if (baseClass == long.class) {
                return LongArrayTemplate.getInstance();
            } else if (baseClass == float.class) {
                return FloatArrayTemplate.getInstance();
            } else if (baseClass == double.class) {
                return DoubleArrayTemplate.getInstance();
            } else if (baseClass == byte.class) {
                return ByteArrayTemplate.getInstance();
            } else {
                Template baseTemplate = registry.lookup(genericBaseType);
                return new ObjectArrayTemplate(baseClass, baseTemplate);
            }
        } else if (dim == 2) {
            Class componentClass = Array.newInstance(baseClass, 0).getClass();
            Template componentTemplate = toTemplate(arrayType, genericBaseType, baseClass, dim - 1);
            return new ReflectionMultidimentionalArrayTemplate(componentClass, componentTemplate);
        } else {
            ReflectionMultidimentionalArrayTemplate componentTemplate =
                (ReflectionMultidimentionalArrayTemplate) toTemplate(arrayType, genericBaseType, baseClass, dim - 1);
            Class componentClass = Array.newInstance(componentTemplate.getComponentClass(), 0).getClass();
            return new ReflectionMultidimentionalArrayTemplate(componentClass, componentTemplate);
        }
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldList flist)
            throws TemplateBuildException {
        throw new UnsupportedOperationException(targetClass.getName());
    }

    @Override
    protected <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries) {
        throw new UnsupportedOperationException(targetClass.getName());
    }

    @Override
    public void writeTemplate(Type targetType, String directoryName) {
        throw new UnsupportedOperationException(targetType.toString());
    }

    @Override
    public <T> Template<T> loadTemplate(Type targetType) {
        return null;
    }

}
