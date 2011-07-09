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

import java.nio.ByteBuffer;

import org.msgpack.MessagePack;
import org.msgpack.io.LinkedBufferInput;


public class BufferUnpacker extends AbstractMessagePackUnpacker {
    private static final int DEFAULT_BUFFER_SIZE = 512;  // TODO default buffer size

    public BufferUnpacker() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public BufferUnpacker(int bufferSize) {
	this(new MessagePack(), bufferSize);
    }

    public BufferUnpacker(MessagePack msgpack) {
	this(msgpack, DEFAULT_BUFFER_SIZE);
    }

    public BufferUnpacker(MessagePack msgpack, int bufferSize) {
	super(msgpack, new LinkedBufferInput(bufferSize));
    }

    public BufferUnpacker wrap(byte[] b) {
        return wrap(b, 0, b.length);
    }

    public BufferUnpacker wrap(byte[] b, int off, int len) {
        ((LinkedBufferInput) in).clear();
        ((LinkedBufferInput) in).feed(b, off, len, true);
        return this;
    }

    public BufferUnpacker wrap(ByteBuffer buf) {
        ((LinkedBufferInput) in).clear();
        ((LinkedBufferInput) in).feed(buf, true);
        return this;
    }
}

