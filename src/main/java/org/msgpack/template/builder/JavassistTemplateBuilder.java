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

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.TemplateRegistry;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JavassistTemplateBuilder extends AbstractTemplateBuilder {

    private static Logger LOG = Logger.getLogger(JavassistTemplateBuilder.class.getName());

    public static abstract class JavassistTemplate<T> extends AbstractTemplate<T> {
        public Class<T> targetClass;
        public Template<?>[] templates;

        public JavassistTemplate(Class<T> targetClass, Template<?>[] templates) {
            this.targetClass = targetClass;
            this.templates = templates;
        }
    }

    protected ClassPool pool;

    protected int seqId = 0;

    protected ClassLoader loader;

    public JavassistTemplateBuilder(TemplateRegistry registry) {
        this(registry, null);
    }

    public JavassistTemplateBuilder(TemplateRegistry registry, ClassLoader cl) {
        super(registry);
        pool = new ClassPool();
        pool.appendClassPath(new ClassClassPath(getClass()));
        boolean appended = false;
        loader = cl;
        if (loader == null) {
            loader = pool.getClassLoader();
        }

        try {
            if (loader != null) {
                pool.appendClassPath(new LoaderClassPath(loader));
                appended = true;
            }
        } catch (SecurityException e) {
            LOG.fine("Cannot append a search path of classloader");
            e.printStackTrace();
        }
        if (!appended) {
            pool.appendSystemPath();
        }
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
    
    public void addClassLoader(ClassLoader cl) {
    	pool.appendClassPath(new LoaderClassPath(cl));
    }

    protected CtClass makeCtClass(String className) {
        return pool.makeClass(className);
    }

    protected CtClass getCtClass(String className) throws NotFoundException {
        return pool.get(className);
    }

    protected int nextSeqId() {
        return seqId++;
    }

    protected BuildContext createBuildContext() {
        return new DefaultBuildContext(this);
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries) {
        Template<?>[] tmpls = toTemplate(entries);
        BuildContext bc = createBuildContext();
        return bc.buildTemplate(targetClass, entries, tmpls);
    }

    private Template<?>[] toTemplate(FieldEntry[] from) {
        Template<?>[] tmpls = new Template<?>[from.length];
        for (int i = 0; i < from.length; ++i) {
            FieldEntry e = from[i];
            if (!e.isAvailable()) {
                tmpls[i] = null;
            } else {
                Template<?> tmpl = registry.lookup(e.getGenericType());
                tmpls[i] = tmpl;
            }
        }
        return tmpls;
    }

    @Override
    public void writeTemplate(Type targetType, String directoryName) {
        Class<?> targetClass = (Class<?>) targetType;
        checkClassValidation(targetClass);
        FieldOption implicitOption = getFieldOption(targetClass);
        FieldEntry[] entries = toFieldEntries(targetClass, implicitOption);
        writeTemplate(targetClass, entries, directoryName);
    }

    private void writeTemplate(Class<?> targetClass, FieldEntry[] entries, String directoryName) {
        Template[] tmpls = toTemplate(entries);
        BuildContext bc = createBuildContext();
        bc.writeTemplate(targetClass, entries, tmpls, directoryName);
    }

    @Override
    public <T> Template<T> loadTemplate(Type targetType) {
        // FIXME #MN must consider how to load "reference cycle class" in next
        // version
        Class<T> targetClass = (Class) targetType;
        //checkClassValidation(targetClass);
        try {
            // check loadable
            String tmplName = targetClass.getName() + "_$$_Template";
            ClassLoader cl = targetClass.getClassLoader();
            if (cl != null) {
                cl.loadClass(tmplName);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
        FieldOption implicitOption = getFieldOption(targetClass);
        FieldEntry[] entries = toFieldEntries(targetClass, implicitOption);
        Template<?>[] tmpls = toTemplate(entries);
        BuildContext bc = createBuildContext();
        return bc.loadTemplate(targetClass, entries, tmpls);
    }

    protected ClassLoader getClassLoader() {
        return loader;
    }
}
