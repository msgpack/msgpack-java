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
package org.msgpack.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.BufferOverflowException;

public class ByteBufferOutput implements Output {
    public static interface ExpandBufferCallback {
        ByteBuffer call(ByteBuffer buffer, int len) throws IOException;
    }

    private ByteBuffer buffer;
    private ExpandBufferCallback callback;

    public ByteBufferOutput(ByteBuffer buffer) {
        this(buffer, null);
    }

    public ByteBufferOutput(ByteBuffer buffer, ExpandBufferCallback callback) {
        this.buffer = buffer;
        this.callback = callback;
    }

    private void reserve(int len) throws IOException {
        if (len <= buffer.remaining()) {
            return;
        }
        if (callback == null) {
            throw new BufferOverflowException();
        }
        buffer = callback.call(buffer, len);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        reserve(len);
        buffer.put(b, off, len);
    }

    @Override
    public void write(ByteBuffer bb) throws IOException {
        reserve(bb.remaining());
        buffer.put(bb);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        reserve(1);
        buffer.put(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        reserve(2);
        buffer.putShort(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        reserve(4);
        buffer.putInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        reserve(8);
        buffer.putLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        reserve(4);
        buffer.putFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        reserve(8);
        buffer.putDouble(v);
    }

    @Override
    public void writeByteAndByte(byte b, byte v) throws IOException {
        reserve(2);
        buffer.put(b);
        buffer.put(v);
    }

    @Override
    public void writeByteAndShort(byte b, short v) throws IOException {
        reserve(3);
        buffer.put(b);
        buffer.putShort(v);
    }

    @Override
    public void writeByteAndInt(byte b, int v) throws IOException {
        reserve(5);
        buffer.put(b);
        buffer.putInt(v);
    }

    @Override
    public void writeByteAndLong(byte b, long v) throws IOException {
        reserve(9);
        buffer.put(b);
        buffer.putLong(v);
    }

    @Override
    public void writeByteAndFloat(byte b, float v) throws IOException {
        reserve(5);
        buffer.put(b);
        buffer.putFloat(v);
    }

    @Override
    public void writeByteAndDouble(byte b, double v) throws IOException {
        reserve(9);
        buffer.put(b);
        buffer.putDouble(v);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() {
    }
}
