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
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

public class StreamOutput implements Output {
    private DataOutputStream out;

    public StreamOutput(OutputStream out) {
        this.out = new DataOutputStream(out);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void write(ByteBuffer bb) throws IOException {
        if (bb.hasArray()) {
            byte[] array = bb.array();
            int offset = bb.arrayOffset();
            out.write(array, offset, bb.remaining());
            bb.position(bb.limit());
        } else {
            // int pos = bb.position();
            byte[] buf = new byte[bb.remaining()];
            bb.get(buf);
            out.write(buf);
        }
    }

    @Override
    public void writeByte(byte v) throws IOException {
        out.write(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        out.writeShort(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        out.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        out.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        out.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        out.writeDouble(v);
    }

    @Override
    public void writeByteAndByte(byte b, byte v) throws IOException {
        out.write(b);
        out.write(v);
    }

    @Override
    public void writeByteAndShort(byte b, short v) throws IOException {
        out.write(b);
        out.writeShort(v);
    }

    @Override
    public void writeByteAndInt(byte b, int v) throws IOException {
        out.write(b);
        out.writeInt(v);
    }

    @Override
    public void writeByteAndLong(byte b, long v) throws IOException {
        out.write(b);
        out.writeLong(v);
    }

    @Override
    public void writeByteAndFloat(byte b, float v) throws IOException {
        out.write(b);
        out.writeFloat(v);
    }

    @Override
    public void writeByteAndDouble(byte b, double v) throws IOException {
        out.write(b);
        out.writeDouble(v);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
