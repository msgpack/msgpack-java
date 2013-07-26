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
package org.msgpack.unpacker;

import java.io.IOException;
import java.nio.ByteBuffer;

final class ByteArrayAccept extends Accept {
    byte[] value;

    ByteArrayAccept() {
        super("raw value");
    }

    @Override
    void acceptRaw(byte[] raw) {
        this.value = raw;
    }

    @Override
    void acceptEmptyRaw() {
        this.value = new byte[0];
    }

    @Override
    public void refer(ByteBuffer bb, boolean gift) throws IOException {
        // TODO gift
        this.value = new byte[bb.remaining()];
        bb.get(value);
    }
}
