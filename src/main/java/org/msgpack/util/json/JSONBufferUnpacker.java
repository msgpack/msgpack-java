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
package org.msgpack.util.json;

import java.io.IOException;
import java.io.EOFException;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.BufferUnpacker;


public class JSONBufferUnpacker extends JSONUnpacker implements BufferUnpacker {
    private static final int DEFAULT_BUFFER_SIZE = 512;  // TODO default buffer size

    public JSONBufferUnpacker() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public JSONBufferUnpacker(int bufferSize) {
        this(new MessagePack(), bufferSize);
    }

    public JSONBufferUnpacker(MessagePack msgpack) {
        this(msgpack, DEFAULT_BUFFER_SIZE);
    }

    public JSONBufferUnpacker(MessagePack msgpack, int bufferSize) {
        super(msgpack, (Reader)null);
    }

    @Override
    protected Value nextValue() {
        if(in == null) {
            // FIXME exception
            throw new MessageTypeException(new EOFException());
        }
        return super.nextValue();
    }

    public JSONBufferUnpacker wrap(byte[] b) {
        return wrap(b, 0, b.length);
    }

    public JSONBufferUnpacker wrap(byte[] b, int off, int len) {
        ByteArrayInputStream in = new ByteArrayInputStream(b, off, len);
        this.in = new InputStreamReader(in);
        return this;
    }

    public JSONBufferUnpacker wrap(ByteBuffer buf) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support wrap(ByteBuffer buf)");
    }

    public JSONBufferUnpacker feed(byte[] b) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }

    public JSONBufferUnpacker feed(byte[] b, boolean nocopy) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }

    public JSONBufferUnpacker feed(byte[] b, int off, int len) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }

    public JSONBufferUnpacker feed(byte[] b, int off, int len, boolean nocopy) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }

    public JSONBufferUnpacker feed(ByteBuffer buf) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }

    public JSONBufferUnpacker feed(ByteBuffer buf, boolean nocopy) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }
}

