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
package org.msgpack.template.builder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.template.TemplateRegistry;

public class TemplateBuilderChain {

    private static boolean enableDynamicCodeGeneration() {
        try {
            return !System.getProperty("java.vm.name").equals("Dalvik");
        } catch (Exception e) {
            return true;
        }
    }

    protected List<TemplateBuilder> templateBuilders;

    protected TemplateBuilder forceBuilder;

    public TemplateBuilderChain(final TemplateRegistry registry) {
        this(registry, null);
    }

    public TemplateBuilderChain(final TemplateRegistry registry, final ClassLoader cl) {
        templateBuilders = new ArrayList<TemplateBuilder>();
        reset(registry, cl);
    }

    protected void reset(final TemplateRegistry registry, final ClassLoader cl) {
        if (registry == null) {
            throw new NullPointerException("registry is null");
        }

        // forceBuilder
        forceBuilder = new JavassistTemplateBuilder(registry);
        if (cl != null) {
            ((JavassistTemplateBuilder) forceBuilder).addClassLoader(cl);
        }

        // builder
        TemplateBuilder builder;
        templateBuilders.add(new ArrayTemplateBuilder(registry));
        templateBuilders.add(new OrdinalEnumTemplateBuilder(registry));
        if (enableDynamicCodeGeneration()) { // use dynamic code generation
            builder = forceBuilder;
            templateBuilders.add(builder);
            // FIXME #MN next version
            // templateBuilders.add(new
            // JavassistBeansTemplateBuilder(registry));
            templateBuilders.add(new ReflectionBeansTemplateBuilder(registry));
        } else { // use reflection
            builder = new ReflectionTemplateBuilder(registry);
            templateBuilders.add(builder);
            templateBuilders.add(new ReflectionBeansTemplateBuilder(registry));
        }
    }

    public TemplateBuilder getForceBuilder() {
        return forceBuilder;
    }

    public TemplateBuilder select(final Type targetType, final boolean hasAnnotation) {
        for (TemplateBuilder tb : templateBuilders) {
            if (tb.matchType(targetType, hasAnnotation)) {
                return tb;
            }
        }
        return null;
    }
}
