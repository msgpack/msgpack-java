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
import java.util.HashSet;
import java.util.Set;

import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessageTypeException;

public class SetTemplate<E> extends AbstractTemplate<Set<E>> {
    private Template<E> elementTemplate;

    public SetTemplate(Template<E> elementTemplate) {
        this.elementTemplate = elementTemplate;
    }

    public void write(Packer pk, Set<E> target, boolean required)
            throws IOException {
        if (!(target instanceof Set)) {
            if (target == null) {
                if (required) {
                    throw new MessageTypeException("Attempted to write null");
                }
                pk.writeNil();
                return;
            }
            throw new MessageTypeException("Target is not a List but "
                    + target.getClass());
        }
        pk.writeArrayBegin(target.size());
        for (E e : target) {
            elementTemplate.write(pk, e);
        }
        pk.writeArrayEnd();
    }

    public Set<E> read(Unpacker u, Set<E> to, boolean required)
            throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        int n = u.readArrayBegin();
        if (to == null) {
            to = new HashSet<E>(n);
        } else {
            to.clear();
        }
        for (int i = 0; i < n; i++) {
            E e = elementTemplate.read(u, null);
            to.add(e);
        }
        u.readArrayEnd();
        return to;
    }
}
