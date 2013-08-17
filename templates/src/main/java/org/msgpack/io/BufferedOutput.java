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

abstract class BufferedOutput implements Output {
    protected byte[] buffer;
    protected int filled;
    protected final int bufferSize;
    protected ByteBuffer castByteBuffer;

    public BufferedOutput(int bufferSize) {
        if (bufferSize < 9) {
            bufferSize = 9;
        }
        this.bufferSize = bufferSize;
    }

    private void allocateNewBuffer() {
        buffer = new byte[bufferSize];
        castByteBuffer = ByteBuffer.wrap(buffer);
    }

    private void reserve(int len) throws IOException {
        if (buffer == null) {
            allocateNewBuffer();
            return;
        }
        if (bufferSize - filled < len) {
            if (!flushBuffer(buffer, 0, filled)) {
                buffer = new byte[bufferSize];
                castByteBuffer = ByteBuffer.wrap(buffer);
            }
            filled = 0;
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (buffer == null) {
            if (bufferSize < len) {
                flushBuffer(b, off, len);
                return;
            }
            allocateNewBuffer();
        }
        if (len <= bufferSize - filled) {
            System.arraycopy(b, off, buffer, filled, len);
            filled += len;
        } else if (len <= bufferSize) {
            if (!flushBuffer(buffer, 0, filled)) {
                allocateNewBuffer();
            }
            filled = 0;
            System.arraycopy(b, off, buffer, 0, len);
            filled = len;
        } else {
            flush();
            flushBuffer(b, off, len);
        }
    }

    @Override
    public void write(ByteBuffer bb) throws IOException {
        int len = bb.remaining();
        if (buffer == null) {
            if (bufferSize < len) {
                flushByteBuffer(bb);
                return;
            }
            allocateNewBuffer();
        }
        if (len <= bufferSize - filled) {
            bb.get(buffer, filled, len);
            filled += len;
        } else if (len <= bufferSize) {
            if (!flushBuffer(buffer, 0, filled)) {
                allocateNewBuffer();
            }
            filled = 0;
            bb.get(buffer, 0, len);
            filled = len;
        } else {
            flush();
            flushByteBuffer(bb);
        }
    }

    @Override
    public void writeByte(byte v) throws IOException {
        reserve(1);
        buffer[filled++] = v;
    }

    @Override
    public void writeShort(short v) throws IOException {
        reserve(2);
        castByteBuffer.putShort(filled, v);
        filled += 2;
    }

    @Override
    public void writeInt(int v) throws IOException {
        reserve(4);
        castByteBuffer.putInt(filled, v);
        filled += 4;
    }

    @Override
    public void writeLong(long v) throws IOException {
        reserve(8);
        castByteBuffer.putLong(filled, v);
        filled += 8;
    }

    @Override
    public void writeFloat(float v) throws IOException {
        reserve(4);
        castByteBuffer.putFloat(filled, v);
        filled += 4;
    }

    @Override
    public void writeDouble(double v) throws IOException {
        reserve(8);
        castByteBuffer.putDouble(filled, v);
        filled += 8;
    }

    @Override
    public void writeByteAndByte(byte b, byte v) throws IOException {
        reserve(2);
        buffer[filled++] = b;
        buffer[filled++] = v;
    }

    @Override
    public void writeByteAndShort(byte b, short v) throws IOException {
        reserve(3);
        buffer[filled++] = b;
        castByteBuffer.putShort(filled, v);
        filled += 2;
    }

    @Override
    public void writeByteAndInt(byte b, int v) throws IOException {
        reserve(5);
        buffer[filled++] = b;
        castByteBuffer.putInt(filled, v);
        filled += 4;
    }

    @Override
    public void writeByteAndLong(byte b, long v) throws IOException {
        reserve(9);
        buffer[filled++] = b;
        castByteBuffer.putLong(filled, v);
        filled += 8;
    }

    @Override
    public void writeByteAndFloat(byte b, float v) throws IOException {
        reserve(5);
        buffer[filled++] = b;
        castByteBuffer.putFloat(filled, v);
        filled += 4;
    }

    @Override
    public void writeByteAndDouble(byte b, double v) throws IOException {
        reserve(9);
        buffer[filled++] = b;
        castByteBuffer.putDouble(filled, v);
        filled += 8;
    }

    @Override
    public void flush() throws IOException {
        if (filled > 0) {
            if (!flushBuffer(buffer, 0, filled)) {
                buffer = null;
            }
            filled = 0;
        }
    }

    protected void flushByteBuffer(ByteBuffer bb) throws IOException {
        if (bb.hasArray()) {
            byte[] array = bb.array();
            int offset = bb.arrayOffset();
            flushBuffer(array, offset + bb.position(), bb.remaining());
            bb.position(bb.limit());
        } else {
            byte[] buf = new byte[bb.remaining()];
            bb.get(buf);
            flushBuffer(buf, 0, buf.length);
        }
    }

    protected abstract boolean flushBuffer(byte[] buffer, int off, int len)
            throws IOException;
}
