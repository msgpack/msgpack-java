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
package org.msgpack.template;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessageTypeException;


public class CollectionTemplate<E> extends AbstractTemplate<Collection<E>> {
    private Template<E> elementTemplate;

    public CollectionTemplate(Template<E> elementTemplate) {
        this.elementTemplate = elementTemplate;
    }

    public void write(Packer pk, Collection<E> target) throws IOException {
        if(target == null) {
            throw new MessageTypeException("Attempted to write null");
        }
        Collection<E> col = target;
        pk.writeArrayBegin(col.size());
        for(E e : col) {
            elementTemplate.write(pk, e);
        }
        pk.writeArrayEnd();
    }

    public Collection<E> read(Unpacker u, Collection<E> to) throws IOException {
        int n = u.readArrayBegin();
        if(to == null) {
            to = new LinkedList<E>();
        } else {
            to.clear();
        }
        for(int i=0; i < n; i++) {
            E e = elementTemplate.read(u, null);
            to.add(e);
        }
        u.readArrayEnd();
        return to;
    }
}

