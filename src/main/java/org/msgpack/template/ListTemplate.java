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
import java.util.List;
import java.util.ArrayList;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessageTypeException;


public class ListTemplate implements Template {
    private Template elementTemplate;

    private ListTemplate(Template elementTemplate) {
        this.elementTemplate = elementTemplate;
    }

    public void write(Packer pk, Object target) throws IOException {
        if(!(target instanceof List)) {
            if(target == null) {
                throw new MessageTypeException("Attempted to write null");
            }
            throw new MessageTypeException("Target is not a List but "+target.getClass());
        }
        List<Object> list = (List<Object>) target;
        pk.writeArrayBegin(list.size());
        for(Object e : list) {
            elementTemplate.write(pk, e);
        }
        pk.writeArrayEnd();
    }

    public Object read(Unpacker u, Object to) throws IOException {
        int n = u.readArrayBegin();
        List<Object> list;
        if(to != null) {
            list = (List<Object>) to;
            list.clear();
        } else {
            list = new ArrayList<Object>(n);
        }
        for(int i=0; i < n; i++) {
            Object e = elementTemplate.read(u, null);
            list.add(e);
        }
        u.readArrayEnd();
        return list;
    }
}

