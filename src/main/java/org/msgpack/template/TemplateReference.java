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
package org.msgpack.template;

import java.io.IOException;
import java.lang.reflect.Type;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

public class TemplateReference<T> extends AbstractTemplate<T> {

    private TemplateRegistry registry;

    private Type targetType;

    private Template<T> actualTemplate;

    public TemplateReference(TemplateRegistry registry, Type targetType) {
        this.registry = registry;
        this.targetType = targetType;
    }

    @SuppressWarnings("unchecked")
    private void validateActualTemplate() {
        if (actualTemplate == null) {
            actualTemplate = (Template<T>) registry.cache.get(targetType);
            if (actualTemplate == null) {
                throw new MessageTypeException(
                        "Actual template have not been created");
            }
        }
    }

    @Override
    public void write(Packer pk, T v, boolean required) throws IOException {
        validateActualTemplate();
        actualTemplate.write(pk, v, required);
    }

    @Override
    public void write(Packer pk, T v) throws IOException {
        validateActualTemplate();
        actualTemplate.write(pk, v, false);
    }

    @Override
    public T read(Unpacker u, T to, boolean required) throws IOException {
        validateActualTemplate();
        return actualTemplate.read(u, to, required);
    }

    @Override
    public T read(Unpacker u, T to) throws IOException {
        validateActualTemplate();
        return actualTemplate.read(u, to, false);
    }
}
