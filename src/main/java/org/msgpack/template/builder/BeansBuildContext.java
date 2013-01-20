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
import java.lang.reflect.InvocationTargetException;

import org.msgpack.*;
import org.msgpack.template.*;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.NotFoundException;

@SuppressWarnings("rawtypes")
public class BeansBuildContext extends BuildContext<BeansFieldEntry> {
    protected BeansFieldEntry[] entries;

    protected Class<?> origClass;

    protected String origName;

    protected Template<?>[] templates;

    public BeansBuildContext(JavassistTemplateBuilder director) {
        super(director);
    }

    public Template buildTemplate(Class<?> targetClass,
            BeansFieldEntry[] entries, Template[] templates) {
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
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> cons = c.getConstructor(new Class[] { Class.class, Template[].class });
        Object tmpl = cons.newInstance(new Object[] { origClass, templates });
        return (Template) tmpl;
    }

    protected void buildMethodInit() {
    }

    @Override
    protected String buildWriteMethodBody() {
        resetStringBuilder();
        buildString("{");

        buildString("if($2 == null) {");
        buildString("  if($3) {");
        buildString("    throw new %s(\"Attempted to write null\");", MessageTypeException.class.getName());
        buildString("  }");
        buildString("  $1.writeNil();");
        buildString("  return;");
        buildString("}");

        buildString("%s _$$_t = (%s)$2;", origName, origName);
        buildString("$1.writeArrayBegin(%d);", entries.length);

        for (int i = 0; i < entries.length; i++) {
            BeansFieldEntry e = entries[i];
            if (!e.isAvailable()) {
                buildString("$1.writeNil();");
                continue;
            }
            Class<?> type = e.getType();
            if (type.isPrimitive()) {
                buildString("$1.%s(_$$_t.%s());", primitiveWriteName(type), e.getGetterName());
            } else {
                buildString("if(_$$_t.%s() == null) {", e.getGetterName());
                if (e.isNotNullable()) {
                    buildString("throw new %s();", MessageTypeException.class.getName());
                } else {
                    buildString("$1.writeNil();");
                }
                buildString("} else {");
                buildString("  this.templates[%d].write($1, _$$_t.%s());", i, e.getGetterName());
                buildString("}");
            }
        }

        buildString("$1.writeArrayEnd();");
        buildString("}");
        return getBuiltString();
    }

    @Override
    protected String buildReadMethodBody() {
        resetStringBuilder();
        buildString("{ ");

        buildString("if(!$3 && $1.trySkipNil()) {");
        buildString("  return null;");
        buildString("}");

        buildString("%s _$$_t;", origName);
        buildString("if($2 == null) {");
        buildString("  _$$_t = new %s();", origName);
        buildString("} else {");
        buildString("  _$$_t = (%s)$2;", origName);
        buildString("}");

        buildString("$1.readArrayBegin();");

        for (int i = 0; i < entries.length; i++) {
            BeansFieldEntry e = entries[i];

            if (!e.isAvailable()) {
                buildString("$1.skip();"); // TODO #MN
                continue;
            }

            if (e.isOptional()) {
                buildString("if($1.trySkipNil()) {");
                buildString("_$$_t.%s(null);", e.getSetterName());
                buildString("} else {");
            }

            Class<?> type = e.getType();
            if (type.isPrimitive()) {
                buildString("_$$_t.%s( $1.%s() );", e.getSetterName(), primitiveReadName(type));
            } else {
                buildString(
                        "_$$_t.%s( (%s)this.templates[%d].read($1, _$$_t.%s()) );",
                        e.getSetterName(), e.getJavaTypeName(), i, e.getGetterName());
            }

            if (e.isOptional()) {
                buildString("}");
            }
        }

        buildString("$1.readArrayEnd();");
        buildString("return _$$_t;");

        buildString("}");

        return getBuiltString();
    }

    @Override
    public void writeTemplate(Class<?> targetClass, BeansFieldEntry[] entries,
            Template[] templates, String directoryName) {
        throw new UnsupportedOperationException(targetClass.getName());
    }

    @Override
    public Template loadTemplate(Class<?> targetClass,
            BeansFieldEntry[] entries, Template[] templates) {
        return null;
    }
}
