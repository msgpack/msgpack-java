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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.unpacker.Unpacker;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReflectionTemplateBuilder extends AbstractTemplateBuilder {

    private static Logger LOG = Logger.getLogger(ReflectionBeansTemplateBuilder.class.getName());

    protected static abstract class ReflectionFieldTemplate extends AbstractTemplate<Object> {
        protected FieldEntry entry;

        ReflectionFieldTemplate(final FieldEntry entry) {
            this.entry = entry;
        }

        void setNil(Object v) {
            entry.set(v, null);
        }
    }

    static final class FieldTemplateImpl extends ReflectionFieldTemplate {
        private Template template;

        public FieldTemplateImpl(final FieldEntry entry, final Template template) {
            super(entry);
            this.template = template;
        }

        @Override
        public void write(Packer packer, Object v, boolean required)
                throws IOException {
            template.write(packer, v, required);
        }

        @Override
        public Object read(Unpacker unpacker, Object to, boolean required)
                throws IOException {
            // Class<Object> type = (Class<Object>) entry.getType();
            Object f = entry.get(to);
            Object o = template.read(unpacker, f, required);
            if (o != f) {
                entry.set(to, o);
            }
            return o;
        }
    }

    protected static class ReflectionClassTemplate<T> extends AbstractTemplate<T> {
        protected Class<T> targetClass;

        protected ReflectionFieldTemplate[] templates;

        protected ReflectionClassTemplate(Class<T> targetClass, ReflectionFieldTemplate[] templates) {
            this.targetClass = targetClass;
            this.templates = templates;
        }

        @Override
        public void write(Packer packer, T target, boolean required)
                throws IOException {
            if (target == null) {
                if (required) {
                    throw new MessageTypeException("attempted to write null");
                }
                packer.writeNil();
                return;
            }
            try {
                packer.writeArrayBegin(templates.length);
                for (ReflectionFieldTemplate tmpl : templates) {
                    if (!tmpl.entry.isAvailable()) {
                        packer.writeNil();
                        continue;
                    }
                    Object obj = tmpl.entry.get(target);
                    if (obj == null) {
                        if (tmpl.entry.isNotNullable()) {
                            throw new MessageTypeException(tmpl.entry.getName()
                                    + " cannot be null by @NotNullable");
                        }
                        packer.writeNil();
                    } else {
                        tmpl.write(packer, obj, true);
                    }
                }
                packer.writeArrayEnd();
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new MessageTypeException(e);
            }
        }

        @Override
        public T read(Unpacker unpacker, T to, boolean required)
                throws IOException {
            if (!required && unpacker.trySkipNil()) {
                return null;
            }
            try {
                if (to == null) {
                    to = targetClass.newInstance();
                }

                unpacker.readArrayBegin();
                for (int i = 0; i < templates.length; i++) {
                    ReflectionFieldTemplate tmpl = templates[i];
                    if (!tmpl.entry.isAvailable()) {
                        unpacker.skip();
                    } else if (tmpl.entry.isOptional() && unpacker.trySkipNil()) {
                        // if Optional + nil, than keep default value
                    } else {
                        tmpl.read(unpacker, to, false);
                    }
                }

                unpacker.readArrayEnd();
                return to;
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new MessageTypeException(e);
            }
        }
    }

    public ReflectionTemplateBuilder(TemplateRegistry registry) {
        this(registry, null);
    }

    public ReflectionTemplateBuilder(TemplateRegistry registry, ClassLoader cl) {
        super(registry);
    }

    @Override
    public boolean matchType(Type targetType, boolean hasAnnotation) {
        Class<?> targetClass = (Class<?>) targetType;
        boolean matched = matchAtClassTemplateBuilder(targetClass, hasAnnotation);
        if (matched && LOG.isLoggable(Level.FINE)) {
            LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries) {
        if (entries == null) {
            throw new NullPointerException("entries is null: " + targetClass);
        }

        ReflectionFieldTemplate[] tmpls = toTemplates(entries);
        return new ReflectionClassTemplate<T>(targetClass, tmpls);
    }

    protected ReflectionFieldTemplate[] toTemplates(FieldEntry[] entries) {
        // TODO Now it is simply cast. #SF
        for (FieldEntry entry : entries) {
            Field field = ((DefaultFieldEntry) entry).getField();
            int mod = field.getModifiers();
            if (!Modifier.isPublic(mod)) {
                field.setAccessible(true);
            }
        }

        ReflectionFieldTemplate[] templates = new ReflectionFieldTemplate[entries.length];
        for (int i = 0; i < entries.length; i++) {
            FieldEntry entry = entries[i];
            // Class<?> t = entry.getType();
            Template template = registry.lookup(entry.getGenericType());
            templates[i] = new FieldTemplateImpl(entry, template);
        }
        return templates;
    }
}
