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

import java.nio.ByteBuffer;

import org.msgpack.MessagePack;
import org.msgpack.io.LinkedBufferInput;

public class MessagePackBufferUnpacker extends MessagePackUnpacker implements BufferUnpacker {
    private static final int DEFAULT_BUFFER_SIZE = 512; // TODO default buffer
                                                        // size

    public MessagePackBufferUnpacker(MessagePack msgpack) {
        this(msgpack, DEFAULT_BUFFER_SIZE);
    }

    public MessagePackBufferUnpacker(MessagePack msgpack, int bufferSize) {
        super(msgpack, new LinkedBufferInput(bufferSize));
    }

    @Override
    public MessagePackBufferUnpacker wrap(byte[] b) {
        return wrap(b, 0, b.length);
    }

    @Override
    public MessagePackBufferUnpacker wrap(byte[] b, int off, int len) {
        ((LinkedBufferInput) in).clear();
        ((LinkedBufferInput) in).feed(b, off, len, true);
        return this;
    }

    @Override
    public MessagePackBufferUnpacker wrap(ByteBuffer buf) {
        ((LinkedBufferInput) in).clear();
        ((LinkedBufferInput) in).feed(buf, true);
        return this;
    }

    @Override
    public MessagePackBufferUnpacker feed(byte[] b) {
        ((LinkedBufferInput) in).feed(b);
        return this;
    }

    @Override
    public MessagePackBufferUnpacker feed(byte[] b, boolean reference) {
        ((LinkedBufferInput) in).feed(b, reference);
        return this;
    }

    @Override
    public MessagePackBufferUnpacker feed(byte[] b, int off, int len) {
        ((LinkedBufferInput) in).feed(b, off, len);
        return this;
    }

    @Override
    public MessagePackBufferUnpacker feed(byte[] b, int off, int len, boolean reference) {
        ((LinkedBufferInput) in).feed(b, off, len, reference);
        return this;
    }

    @Override
    public MessagePackBufferUnpacker feed(ByteBuffer b) {
        ((LinkedBufferInput) in).feed(b);
        return this;
    }

    @Override
    public MessagePackBufferUnpacker feed(ByteBuffer buf, boolean reference) {
        ((LinkedBufferInput) in).feed(buf, reference);
        return this;
    }

    @Override
    public int getBufferSize() {
        return ((LinkedBufferInput) in).getSize();
    }

    @Override
    public void copyReferencedBuffer() {
        ((LinkedBufferInput) in).copyReferencedBuffer();
    }

    @Override
    public void clear() {
        ((LinkedBufferInput) in).clear();
        reset();
    }
}
