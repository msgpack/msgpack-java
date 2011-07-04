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
import java.util.Map;
import java.util.HashMap;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessageTypeException;


public class MapTemplate implements Template {
    private Template keyTemplate;
    private Template valueTemplate;

    private MapTemplate(Template keyTemplate, Template valueTemplate) {
        this.keyTemplate = keyTemplate;
        this.valueTemplate = valueTemplate;
    }

    public void write(Packer pk, Object target) throws IOException {
        if(!(target instanceof Map)) {
            if(target == null) {
                throw new MessageTypeException("Attempted to write null");
            }
            throw new MessageTypeException("Target is not a Map but "+target.getClass());
        }
        Map<Object,Object> map = (Map<Object,Object>) target;
        pk.writeMapBegin(map.size());
        for(Map.Entry<Object,Object> pair : map.entrySet()) {
            keyTemplate.write(pk, pair.getKey());
            valueTemplate.write(pk, pair.getValue());
        }
        pk.writeMapEnd();
    }

    public Object read(Unpacker u, Object to) throws IOException {
        int n = u.readMapBegin();
        Map<Object,Object> map;
        if(to != null) {
            map = (Map<Object,Object>) to;
            map.clear();
        } else {
            map = new HashMap<Object,Object>(n);
        }
        for(int i=0; i < n; i++) {
            Object key = keyTemplate.read(u, null);
            Object value = valueTemplate.read(u, null);
            map.put(key, value);
        }
        u.readMapEnd();
        return map;
    }
}

