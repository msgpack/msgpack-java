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
import java.util.Map;
import java.util.HashMap;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessageTypeException;

public class MapTemplate<K, V> extends AbstractTemplate<Map<K, V>> {
    private Template<K> keyTemplate;
    private Template<V> valueTemplate;

    public MapTemplate(Template<K> keyTemplate, Template<V> valueTemplate) {
        this.keyTemplate = keyTemplate;
        this.valueTemplate = valueTemplate;
    }

    public void write(Packer pk, Map<K, V> target, boolean required)
            throws IOException {
        if (!(target instanceof Map)) {
            if (target == null) {
                if (required) {
                    throw new MessageTypeException("Attempted to write null");
                }
                pk.writeNil();
                return;
            }
            throw new MessageTypeException("Target is not a Map but " + target.getClass());
        }
        Map<K, V> map = (Map<K, V>) target;
        pk.writeMapBegin(map.size());
        for (Map.Entry<K, V> pair : map.entrySet()) {
            keyTemplate.write(pk, pair.getKey());
            valueTemplate.write(pk, pair.getValue());
        }
        pk.writeMapEnd();
    }

    public Map<K, V> read(Unpacker u, Map<K, V> to, boolean required)
            throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        int n = u.readMapBegin();
        Map<K, V> map;
        if (to != null) {
            map = (Map<K, V>) to;
            map.clear();
        } else {
            map = new HashMap<K, V>(n);
        }
        for (int i = 0; i < n; i++) {
            K key = keyTemplate.read(u, null);
            V value = valueTemplate.read(u, null);
            map.put(key, value);
        }
        u.readMapEnd();
        return map;
    }
}
