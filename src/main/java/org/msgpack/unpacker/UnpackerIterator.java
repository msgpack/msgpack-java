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
package org.msgpack.unpacker;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.msgpack.value.Value;

public class UnpackerIterator implements Iterator<Value> {
    private Unpacker u;
    private Value value;

    public UnpackerIterator(Unpacker u) {
        this.u = u;
    }

    public boolean hasNext() {
        if(value != null) {
            return true;
        }
        value = u.read();
        return value != null;
    }

    public Value next() {
        if(value != null) {
            Value v = value;
            value = null;
            return v;
        } else {
            Value v = u.read();
            if(v == null) {
                throw new NoSuchElementException();
            }
            return v;
        }
    }

    public void remove() {
		throw new UnsupportedOperationException();
    }
}

