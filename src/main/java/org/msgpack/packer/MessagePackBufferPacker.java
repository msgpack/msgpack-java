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
package org.msgpack.packer;

import org.msgpack.MessagePack;
import org.msgpack.io.LinkedBufferOutput;

public class MessagePackBufferPacker extends MessagePackPacker implements BufferPacker {
    private static final int DEFAULT_BUFFER_SIZE = 512;

    public MessagePackBufferPacker(MessagePack msgpack) {
        this(msgpack, DEFAULT_BUFFER_SIZE);
    }

    public MessagePackBufferPacker(MessagePack msgpack, int bufferSize) {
        super(msgpack, new LinkedBufferOutput(bufferSize));
    }

    public int getBufferSize() {
        return ((LinkedBufferOutput) out).getSize();
    }

    public byte[] toByteArray() {
        return ((LinkedBufferOutput) out).toByteArray();
    }

    public void clear() {
        reset();
        ((LinkedBufferOutput) out).clear();
    }
}
