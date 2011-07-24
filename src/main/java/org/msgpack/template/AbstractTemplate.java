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
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;


public abstract class AbstractTemplate<T> implements Template<T> {
    public void write(Packer pk, T v, boolean optional) throws IOException {
        if(optional && v == null) {
            pk.writeNil();
        } else {
            write(pk, v);
        }
    }

    public T read(Unpacker u, T to, boolean optional) throws IOException {
        if(optional && u.trySkipNil()) {
            return null;
        } else {
            return read(u, to);
        }
    }
}

