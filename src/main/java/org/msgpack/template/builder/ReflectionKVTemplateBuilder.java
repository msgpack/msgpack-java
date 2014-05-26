package org.msgpack.template.builder;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.beans.KVFieldEntry;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.util.android.TextUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ray_ni
 * on 2014/5/24 0024.下午 4:34
 */
public class ReflectionKVTemplateBuilder extends AbstractTemplateBuilder {
    private static Logger LOG = Logger.getLogger(ReflectionKVTemplateBuilder.class.getName());

    protected static abstract class ReflectionKVFieldTemplate extends AbstractTemplate<Object> {
        protected KVFieldEntry entry;

        ReflectionKVFieldTemplate(final FieldEntry entry) {
            this.entry = (KVFieldEntry) entry;
        }

        void setNil(Object v) {
            entry.set(v, null);
        }

        void writeKey(Packer packer) throws IOException {
            packer.write(entry.getKey());
        }
    }

    static final class FieldKVTemplateImpl extends ReflectionKVFieldTemplate {
        private Template template;

        public FieldKVTemplateImpl(final FieldEntry entry, final Template template) {
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

    protected static class ReflectionKVClassTemplate<T> extends AbstractTemplate<T> {
        protected Class<T> targetClass;

        protected ReflectionKVFieldTemplate[] templates;

        protected ReflectionKVClassTemplate(Class<T> targetClass, ReflectionKVFieldTemplate[] templates) {
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
                packer.writeMapBegin(templates.length);
                for (ReflectionKVFieldTemplate tmpl : templates) {
                    tmpl.writeKey(packer);
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
                packer.writeMapEnd();
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

                int size = unpacker.readMapBegin();
                for (int i = 0; i < size; i++) {
                    String key = unpacker.readString();
                    ReflectionKVFieldTemplate tmpImp = templates[i >= templates.length ? templates.length - 1 : i];// use template in same index first,
                    // so the serialize perform will be better in same version of data
                    if (!TextUtils.equals(key, tmpImp.entry.getKey())) {
                        for (ReflectionKVFieldTemplate template : templates) {
                            if (TextUtils.equals(template.entry.getKey(), key)) {
                                tmpImp = template;
                                break;
                            }
                        }
                    }

                    if (!TextUtils.equals(tmpImp.entry.getKey(), key) || !tmpImp.entry.isAvailable()) {
                        unpacker.skip();
                    } else if (tmpImp.entry.isOptional() && unpacker.trySkipNil()) {
                        // if Optional + nil, than keep default value
                    } else {
                        tmpImp.read(unpacker, to, true);
                    }
                }
                unpacker.readMapEnd();
                return to;
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new MessageTypeException(e);
            }
        }
    }

    public ReflectionKVTemplateBuilder(TemplateRegistry registry) {
        this(registry, null);
    }

    public ReflectionKVTemplateBuilder(TemplateRegistry registry, ClassLoader cl) {
        super(registry);
    }

    @Override
    public boolean matchType(Type targetType, boolean hasAnnotation) {
        Class<?> targetClass = (Class<?>) targetType;
        boolean matched = matchAtKVClassTemplateBuilder(targetClass, hasAnnotation);
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

        ReflectionKVFieldTemplate[] tmpls = toTemplates(entries);
        return new ReflectionKVClassTemplate<T>(targetClass, tmpls);
    }

    protected ReflectionKVFieldTemplate[] toTemplates(FieldEntry[] entries) {
        // TODO Now it is simply cast. #SF
        for (FieldEntry entry : entries) {
            Field field = ((DefaultFieldEntry) entry).getField();
            int mod = field.getModifiers();
            if (!Modifier.isPublic(mod)) {
                field.setAccessible(true);
            }
        }

        ReflectionKVFieldTemplate[] templates = new ReflectionKVFieldTemplate[entries.length];
        for (int i = 0; i < entries.length; i++) {
            FieldEntry entry = entries[i];
            // Class<?> t = entry.getType();
            Template template = registry.lookup(entry.getGenericType());
            templates[i] = new FieldKVTemplateImpl(entry, template);
        }
        return templates;
    }
}
