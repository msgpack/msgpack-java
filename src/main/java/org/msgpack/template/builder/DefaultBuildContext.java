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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.msgpack.*;
import org.msgpack.packer.Packer;
import org.msgpack.template.*;
import org.msgpack.unpacker.Unpacker;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultBuildContext extends BuildContext<FieldEntry> {
    protected FieldEntry[] entries;

    protected Class<?> origClass;

    protected String origName;

    protected Template<?>[] templates;

    public DefaultBuildContext(JavassistTemplateBuilder director) {
        super(director);
    }

    public Template buildTemplate(Class targetClass, FieldEntry[] entries,
            Template[] templates) {
        this.entries = entries;
        this.templates = templates;
        this.origClass = targetClass;
        this.origName = origClass.getName();
        return build(origName);
    }

    protected void setSuperClass() throws CannotCompileException, NotFoundException {
        tmplCtClass.setSuperclass(director.getCtClass(
                JavassistTemplateBuilder.JavassistTemplate.class.getName()));
    }

    protected void buildConstructor() throws CannotCompileException,
            NotFoundException {
        // Constructor(Class targetClass, Template[] templates)
        CtConstructor newCtCons = CtNewConstructor.make(
                new CtClass[] {
                        director.getCtClass(Class.class.getName()),
                        director.getCtClass(Template.class.getName() + "[]")
                }, new CtClass[0], tmplCtClass);
        tmplCtClass.addConstructor(newCtCons);
    }

    protected Template buildInstance(Class<?> c) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Constructor<?> cons = c.getConstructor(new Class[] { Class.class, Template[].class });
        Object tmpl = cons.newInstance(new Object[] { origClass, templates });
        return (Template) tmpl;
    }

    protected void buildMethodInit() {
    }

    protected String buildWriteMethodBody() {
        resetStringBuilder();
        buildString("\n{\n");

        buildString("  if ($2 == null) {\n");
        buildString("    if ($3) {\n");
        buildString("      throw new %s(\"Attempted to write null\");\n", MessageTypeException.class.getName());
        buildString("    }\n");
        buildString("    $1.writeNil();\n");
        buildString("    return;\n");
        buildString("  }\n");

        buildString("  %s _$$_t = (%s) $2;\n", origName, origName);
        buildString("  $1.writeArrayBegin(%d);\n", entries.length);

        for (int i = 0; i < entries.length; i++) {
            FieldEntry e = entries[i];
            if (!e.isAvailable()) {
                buildString("  $1.writeNil();\n");
                continue;
            }
            DefaultFieldEntry de = (DefaultFieldEntry) e;
            boolean isPrivate = Modifier.isPrivate(de.getField().getModifiers());
            Class<?> type = de.getType();
            if (type.isPrimitive()) { // primitive types
                if (!isPrivate) {
                    buildString("  $1.%s(_$$_t.%s);\n", primitiveWriteName(type), de.getName());
                } else {
                    buildString(
                            "  %s.writePrivateField($1, _$$_t, %s.class, \"%s\", templates[%d]);\n",
                            DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName(), i);
                }
            } else { // reference types
                if (!isPrivate) {
                    buildString("  if (_$$_t.%s == null) {\n", de.getName());
                } else {
                    buildString(
                            "  if (%s.readPrivateField(_$$_t, %s.class, \"%s\") == null) {\n",
                            DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName());
                }
                if (de.isNotNullable()) {
                    buildString(
                            "    throw new %s(\"%s cannot be null by @NotNullable\");\n",
                            MessageTypeException.class.getName(), de.getName());
                } else {
                    buildString("    $1.writeNil();\n");
                }
                buildString("  } else {\n");
                if (!isPrivate) {
                    buildString("    templates[%d].write($1, _$$_t.%s);\n", i, de.getName());
                } else {
                    buildString(
                            "    %s.writePrivateField($1, _$$_t, %s.class, \"%s\", templates[%d]);\n",
                            DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName(), i);
                }
                buildString("  }\n");
            }
        }

        buildString("  $1.writeArrayEnd();\n");
        buildString("}\n");
        return getBuiltString();
    }

    public static Object readPrivateField(Object target, Class targetClass, String fieldName) {
        Field field = null;
        try {
            field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object valueReference = field.get(target);
            return valueReference;
        } catch (Exception e) {
            throw new MessageTypeException(e);
        } finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
    }

    public static void writePrivateField(Packer packer, Object target,
            Class targetClass, String fieldName, Template tmpl) {
        Field field = null;
        try {
            field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object valueReference = field.get(target);
            tmpl.write(packer, valueReference);
        } catch (Exception e) {
            throw new MessageTypeException(e);
        } finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
    }

    protected String buildReadMethodBody() {
        resetStringBuilder();
        buildString("\n{\n");

        buildString("  if (!$3 && $1.trySkipNil()) {\n");
        buildString("    return null;\n");
        buildString("  }\n");

        buildString("  %s _$$_t;\n", origName);
        buildString("  if ($2 == null) {\n");
        buildString("    _$$_t = new %s();\n", origName);
        buildString("  } else {\n");
        buildString("    _$$_t = (%s) $2;\n", origName);
        buildString("  }\n");
        buildString("  $1.readArrayBegin();\n");

        int i;
        for (i = 0; i < entries.length; i++) {
            FieldEntry e = entries[i];
            if (!e.isAvailable()) {
                buildString("  $1.skip();\n");
                continue;
            }

            if (e.isOptional()) {
                buildString("  if ($1.trySkipNil()) {");
                // if Optional and nil, then keep default value
                buildString("  } else {\n");
            }

            DefaultFieldEntry de = (DefaultFieldEntry) e;
            boolean isPrivate = Modifier.isPrivate(de.getField().getModifiers());
            Class<?> type = de.getType();
            if (type.isPrimitive()) {
                if (!isPrivate) {
                    buildString("    _$$_t.%s = $1.%s();\n", de.getName(), primitiveReadName(type));
                } else {
                    buildString(
                            "    %s.readPrivateField($1, _$$_t, %s.class, \"%s\", templates[%d]);\n",
                            DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName(), i);
                }
            } else {
                if (!isPrivate) {
                    buildString(
                            "    _$$_t.%s = (%s) this.templates[%d].read($1, _$$_t.%s);\n",
                            de.getName(), de.getJavaTypeName(), i, de.getName());
                } else {
                    buildString(
                            "    %s.readPrivateField($1, _$$_t, %s.class, \"%s\", templates[%d]);\n",
                            DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName(), i);
                }
            }

            if (de.isOptional()) {
                buildString("  }\n");
            }
        }

        buildString("  $1.readArrayEnd();\n");
        buildString("  return _$$_t;\n");

        buildString("}\n");
        return getBuiltString();
    }

    public static void readPrivateField(Unpacker unpacker, Object target,
            Class targetClass, String fieldName, Template tmpl) {
        Field field = null;
        try {
            field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldReference = field.get(target);
            Object valueReference = tmpl.read(unpacker, fieldReference);
            if (valueReference != fieldReference) {
                field.set(target, valueReference);
            }
        } catch (Exception e) {
            throw new MessageTypeException(e);
        } finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
    }

    @Override
    public void writeTemplate(Class<?> targetClass, FieldEntry[] entries,
            Template[] templates, String directoryName) {
        this.entries = entries;
        this.templates = templates;
        this.origClass = targetClass;
        this.origName = origClass.getName();
        write(origName, directoryName);
    }

    @Override
    public Template loadTemplate(Class<?> targetClass, FieldEntry[] entries, Template[] templates) {
        this.entries = entries;
        this.templates = templates;
        this.origClass = targetClass;
        this.origName = origClass.getName();
        return load(origName);
    }
}
