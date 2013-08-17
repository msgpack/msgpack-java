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
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.msgpack.*;
import org.msgpack.packer.Packer;
import org.msgpack.template.*;
import org.msgpack.unpacker.Unpacker;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

@SuppressWarnings({ "rawtypes" })
public abstract class BuildContext<T extends FieldEntry> {

    private static Logger LOG = Logger.getLogger(BuildContext.class.getName());

    protected JavassistTemplateBuilder director;

    protected String tmplName;

    protected CtClass tmplCtClass;

    protected abstract Template buildTemplate(Class<?> targetClass, T[] entries, Template[] templates);

    protected abstract void setSuperClass() throws CannotCompileException, NotFoundException;

    protected abstract void buildConstructor() throws CannotCompileException, NotFoundException;

    public BuildContext(JavassistTemplateBuilder director) {
        this.director = director;
    }

    protected Template build(final String className) {
        try {
            reset(className, false);
            LOG.fine(String.format("started generating template class %s for original class %s",
                    new Object[] { tmplCtClass.getName(), className }));
            buildClass();
            buildConstructor();
            buildMethodInit();
            buildWriteMethod();
            buildReadMethod();
            LOG.fine(String.format("finished generating template class %s for original class %s",
                    new Object[] { tmplCtClass.getName(), className }));
            return buildInstance(createClass());
        } catch (Exception e) {
            String code = getBuiltString();
            if (code != null) {
                LOG.severe("builder: " + code);
                throw new TemplateBuildException("Cannot compile: " + code, e);
            } else {
                throw new TemplateBuildException(e);
            }
        }
    }

    protected void reset(String className, boolean isWritten) {
        String tmplName = null;
        if (!isWritten) {
            tmplName = className + "_$$_Template" + "_" + director.hashCode()
                    + "_" + director.nextSeqId();
        } else {
            tmplName = className + "_$$_Template";
        }
        tmplCtClass = director.makeCtClass(tmplName);
    }

    protected void buildClass() throws CannotCompileException, NotFoundException {
        setSuperClass();
        tmplCtClass.addInterface(director.getCtClass(Template.class.getName()));
    }

    protected void buildMethodInit() {
    }

    protected abstract Template buildInstance(Class<?> c)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException;

    protected void buildWriteMethod() throws CannotCompileException, NotFoundException {
        LOG.fine(String.format("started generating write method in template class %s",
                new Object[] { tmplCtClass.getName() }));
        String mbody = buildWriteMethodBody();
        int mod = javassist.Modifier.PUBLIC;
        CtClass returnType = CtClass.voidType;
        String mname = "write";
        CtClass[] paramTypes = new CtClass[] {
                director.getCtClass(Packer.class.getName()),
                director.getCtClass(Object.class.getName()),
                CtClass.booleanType
        };
        CtClass[] exceptTypes = new CtClass[] {
                director.getCtClass(IOException.class.getName())
        };
        LOG.fine(String.format("compiling write method body: %s", new Object[] { mbody }));
        CtMethod newCtMethod = CtNewMethod.make(
                mod, returnType, mname, paramTypes, exceptTypes, mbody, tmplCtClass);
        tmplCtClass.addMethod(newCtMethod);
        LOG.fine(String.format("finished generating write method in template class %s",
                new Object[] { tmplCtClass.getName() }));
    }

    protected abstract String buildWriteMethodBody();

    protected void buildReadMethod() throws CannotCompileException, NotFoundException {
        LOG.fine(String.format("started generating read method in template class %s",
                new Object[] { tmplCtClass.getName() }));
        String mbody = buildReadMethodBody();
        int mod = javassist.Modifier.PUBLIC;
        CtClass returnType = director.getCtClass(Object.class.getName());
        String mname = "read";
        CtClass[] paramTypes = new CtClass[] {
                director.getCtClass(Unpacker.class.getName()),
                director.getCtClass(Object.class.getName()),
                CtClass.booleanType
        };
        CtClass[] exceptTypes = new CtClass[] {
                director.getCtClass(MessageTypeException.class.getName())
        };
        LOG.fine(String.format("compiling read method body: %s", new Object[] { mbody }));
        CtMethod newCtMethod = CtNewMethod.make(
                mod, returnType, mname, paramTypes, exceptTypes, mbody, tmplCtClass);
        tmplCtClass.addMethod(newCtMethod);
        LOG.fine(String.format("finished generating read method in template class %s",
                new Object[] { tmplCtClass.getName() }));
    }

    protected abstract String buildReadMethodBody();

    protected Class<?> createClass() throws CannotCompileException {
        return (Class<?>) tmplCtClass.toClass(director.getClassLoader(), getClass().getProtectionDomain());
    }

    protected void saveClass(final String directoryName) throws CannotCompileException, IOException {
        tmplCtClass.writeFile(directoryName);
    }

    protected StringBuilder stringBuilder = null;

    protected void resetStringBuilder() {
        stringBuilder = new StringBuilder();
    }

    protected void buildString(String str) {
        stringBuilder.append(str);
    }

    protected void buildString(String format, Object... args) {
        stringBuilder.append(String.format(format, args));
    }

    protected String getBuiltString() {
        if (stringBuilder == null) {
            return null;
        }
        return stringBuilder.toString();
    }

    protected String primitiveWriteName(Class<?> type) {
        return "write";
    }

    protected String primitiveReadName(Class<?> type) {
        if (type == boolean.class) {
            return "readBoolean";
        } else if (type == byte.class) {
            return "readByte";
        } else if (type == short.class) {
            return "readShort";
        } else if (type == int.class) {
            return "readInt";
        } else if (type == long.class) {
            return "readLong";
        } else if (type == float.class) {
            return "readFloat";
        } else if (type == double.class) {
            return "readDouble";
        } else if (type == char.class) {
            return "readInt";
        }
        return null;
    }

    protected abstract void writeTemplate(Class<?> targetClass, T[] entries,
            Template[] templates, String directoryName);

    protected void write(final String className, final String directoryName) {
        try {
            reset(className, true);
            buildClass();
            buildConstructor();
            buildMethodInit();
            buildWriteMethod();
            buildReadMethod();
            saveClass(directoryName);
        } catch (Exception e) {
            String code = getBuiltString();
            if (code != null) {
                LOG.severe("builder: " + code);
                throw new TemplateBuildException("Cannot compile: " + code, e);
            } else {
                throw new TemplateBuildException(e);
            }
        }
    }

    protected abstract Template loadTemplate(Class<?> targetClass, T[] entries, Template[] templates);

    protected Template load(final String className) {
        String tmplName = className + "_$$_Template";
        try {
            Class<?> tmplClass = getClass().getClassLoader().loadClass(tmplName);
            return buildInstance(tmplClass);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Exception e) {
            String code = getBuiltString();
            if (code != null) {
                LOG.severe("builder: " + code);
                throw new TemplateBuildException("Cannot compile: " + code, e);
            } else {
                throw new TemplateBuildException(e);
            }
        }
    }
}
