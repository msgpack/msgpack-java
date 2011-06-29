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

import java.io.EOFException;
import java.util.List;
import java.util.LinkedList;
import java.nio.ByteBuffer;
import org.msgpack.io.LinkedBufferInput;

public class BufferUnpacker extends AbstractMessagePackUnpacker {
    public BufferUnpacker() {
        this(512);  // TODO default buffer size
    }

    public BufferUnpacker(int bufferSize) {
        super(new LinkedBufferInput(bufferSize));
    }

    public void wrap(byte[] b) {
        wrap(b, 0, b.length);
    }

    public void wrap(byte[] b, int off, int len) {
        ((LinkedBufferInput) in).clear();
        ((LinkedBufferInput) in).feed(b, off, len);
    }
}

