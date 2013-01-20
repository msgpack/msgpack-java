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

import org.msgpack.template.OrdinalEnumTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.TemplateBuildException;

public class OrdinalEnumTemplateBuilder extends AbstractTemplateBuilder {

    private static final Logger LOG = Logger.getLogger(OrdinalEnumTemplateBuilder.class.getName());

    public OrdinalEnumTemplateBuilder(TemplateRegistry registry) {
        super(registry);
    }

    @Override
    public boolean matchType(Type targetType, boolean hasAnnotation) {
        Class<?> targetClass = (Class<?>) targetType;
        boolean matched = matchAtOrdinalEnumTemplateBuilder(targetClass, hasAnnotation);
        if (matched && LOG.isLoggable(Level.FINE)) {
            LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries) {
        throw new UnsupportedOperationException("fatal error: " + targetClass.getName());
    }

    @Override
    public <T> Template<T> buildTemplate(Type targetType) throws TemplateBuildException {
        @SuppressWarnings("unchecked")
        Class<T> targetClass = (Class<T>) targetType;
        checkOrdinalEnumValidation(targetClass);
        return new OrdinalEnumTemplate<T>(targetClass);
    }

    protected void checkOrdinalEnumValidation(Class<?> targetClass) {
        if (!targetClass.isEnum()) {
            throw new TemplateBuildException(
                    "tried to build ordinal enum template of non-enum class: " + targetClass.getName());
        }
    }
}
