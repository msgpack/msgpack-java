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
package org.msgpack.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class BufferedOutput implements Output {
    protected byte[] buffer;
    protected int filled;
    protected final int bufferSize;
    protected ByteBuffer castByteBuffer;

    public BufferedOutput(int bufferSize) {
        if(bufferSize < 8) {
            bufferSize = 8;
        }
        this.bufferSize = bufferSize;
    }

    private void reserve(int len) throws IOException {
        if(buffer == null) {
            buffer = new byte[bufferSize];
            castByteBuffer = ByteBuffer.wrap(buffer);
            return;
        }
        if(buffer.length - filled < len) {
            if(!flushBuffer(buffer, 0, filled)) {
                buffer = new byte[bufferSize];
                castByteBuffer = ByteBuffer.wrap(buffer);
            }
            filled = 0;
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if(buffer.length - filled < len) {
            System.arraycopy(b, off, buffer, filled, len);
            filled += len;
        } else {
            flush();
            flushBuffer(b, off, len);
        }
    }

    public void writeByte(byte v) throws IOException {
        reserve(1);
        buffer[filled++] = v;
    }

    public void writeShort(short v) throws IOException {
        reserve(2);
        castByteBuffer.putShort(filled, v);
        filled += 2;
    }

    public void writeInt(int v) throws IOException {
        reserve(4);
        castByteBuffer.putInt(filled, v);
        filled += 4;
    }

    public void writeLong(long v) throws IOException {
        reserve(8);
        castByteBuffer.putLong(filled, v);
        filled += 8;
    }

    public void writeFloat(float v) throws IOException {
        reserve(4);
        castByteBuffer.putFloat(filled, v);
        filled += 4;
    }

    public void writeDouble(double v) throws IOException {
        reserve(8);
        castByteBuffer.putDouble(filled, v);
        filled += 8;
    }

    public void writeByteAndByte(byte b, byte v) throws IOException {
        reserve(2);
        buffer[filled++] = b;
        buffer[filled++] = v;
    }

    public void writeByteAndShort(byte b, short v) throws IOException {
        reserve(3);
        buffer[filled++] = b;
        castByteBuffer.putShort(filled, v);
        filled += 2;
    }

    public void writeByteAndInt(byte b, int v) throws IOException {
        reserve(5);
        buffer[filled++] = b;
        castByteBuffer.putInt(filled, v);
        filled += 4;
    }

    public void writeByteAndLong(byte b, long v) throws IOException {
        reserve(9);
        buffer[filled++] = b;
        castByteBuffer.putLong(filled, v);
        filled += 8;
    }

    public void writeByteAndFloat(byte b, float v) throws IOException {
        reserve(5);
        buffer[filled++] = b;
        castByteBuffer.putFloat(filled, v);
        filled += 4;
    }

    public void writeByteAndDouble(byte b, double v) throws IOException {
        reserve(9);
        buffer[filled++] = b;
        castByteBuffer.putDouble(filled, v);
        filled += 8;
    }

    public void flush() throws IOException {
        if(filled > 0) {
            if(!flushBuffer(buffer, 0, filled)) {
                buffer = null;
            }
            filled = 0;
        }
    }

    protected abstract boolean flushBuffer(byte[] buffer, int off, int len) throws IOException;
}

