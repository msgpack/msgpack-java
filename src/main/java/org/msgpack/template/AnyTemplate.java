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

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

public class AnyTemplate<T> extends AbstractTemplate<T> {

    private TemplateRegistry registry;

    public AnyTemplate(TemplateRegistry registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public void write(Packer pk, T target, boolean required) throws IOException {
        if (target == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            pk.writeNil();
        } else {
            registry.lookup(target.getClass()).write(pk, target);
        }
    }

    public T read(Unpacker u, T to, boolean required) throws IOException,
            MessageTypeException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        if (to == null) {
            throw new MessageTypeException("convert into unknown type is invalid");
        }
        T o = u.read(to);
        if (required && o == null) {
            throw new MessageTypeException("Unexpected nil value");
        }
        return o;
    }
}
