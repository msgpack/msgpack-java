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
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessageTypeException;

public class IntegerArrayTemplate extends AbstractTemplate<int[]> {
    private IntegerArrayTemplate() {
    }

    public void write(Packer pk, int[] target, boolean required)
            throws IOException {
        if (target == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            pk.writeNil();
            return;
        }
        pk.writeArrayBegin(target.length);
        for (int a : target) {
            pk.write(a);
        }
        pk.writeArrayEnd();
    }

    public int[] read(Unpacker u, int[] to, boolean required)
            throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        int n = u.readArrayBegin();
        int[] array;
        if (to != null && to.length == n) {
            array = to;
        } else {
            array = new int[n];
        }
        for (int i = 0; i < n; i++) {
            array[i] = u.readInt();
        }
        u.readArrayEnd();
        return array;
    }

    static public IntegerArrayTemplate getInstance() {
        return instance;
    }

    static final IntegerArrayTemplate instance = new IntegerArrayTemplate();
}
