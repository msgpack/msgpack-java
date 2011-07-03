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
package org.msgpack.packer;

import java.math.BigInteger;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.msgpack.value.Value;
import org.msgpack.MessagePackable;

public abstract class Packer {
    public abstract void writeNil() throws IOException;

    public abstract void writeBoolean(boolean v) throws IOException;

    public abstract void writeByte(byte v) throws IOException;

    public abstract void writeShort(short v) throws IOException;

    public abstract void writeInt(int v) throws IOException;

    public abstract void writeLong(long v) throws IOException;

    public abstract void writeBigInteger(BigInteger v) throws IOException;

    public abstract void writeFloat(float v) throws IOException;

    public abstract void writeDouble(double v) throws IOException;

    public void writeByteArray(byte[] b) throws IOException {
        writeByteArray(b, 0, b.length);
    }

    public abstract void writeByteArray(byte[] b, int off, int len) throws IOException;

    //public abstract void writeByteArray(ByteBuffer b) throws IOException;

    public abstract void writeString(String s) throws IOException;

    public abstract void writeArrayBegin(int size) throws IOException;

    public abstract void writeArrayEnd(boolean check) throws IOException;

    public void writeArrayEnd() throws IOException {
        writeArrayEnd(true);
    }

    public abstract void writeMapBegin(int size) throws IOException;

    public abstract void writeMapEnd(boolean check) throws IOException;

    public void writeMapEnd() throws IOException {
        writeMapEnd(true);
    }

    public void write(Value v) throws IOException {
        v.writeTo(this);
    }

    public void write(MessagePackable v) throws IOException {
        v.writeTo(this);
    }

    public void flush() throws IOException {
    }

    /* TODO
    public void write(boolean v) throws IOException {
        writeBoolean(v);
    }

    public void write(byte v) throws IOException {
        writeByte(v);
    }

    public void write(short v) throws IOException {
        writeShort(v);
    }

    public void write(int v) throws IOException {
        writeInt(v);
    }

    public void write(long v) throws IOException {
        writeLong(v);
    }

    public void write(float v) throws IOException {
        writeFloat(v);
    }

    public void write(double v) throws IOException {
        writeDouble(v);
    }

    public void write(Boolean v) throws IOException {
        if(v == null) {
            writeNil();
        } else {
            writeBoolean(v);
        }
    }

    public void write(Byte v) throws IOException {
        if(v == null) {
            writeNil();
        } else {
            writeByte(v);
        }
    }

    public void write(Short v) throws IOException {
        if(v == null) {
            writeNil();
        } else {
            writeShort(v);
        }
    }

    public void write(Integer v) throws IOException {
        if(v == null) {
            writeNil();
        } else {
            writeInt(v);
        }
    }

    public void write(Long v) throws IOException {
        if(v == null) {
            writeNil();
        } else {
            writeLong(v);
        }
    }

    public void write(Float v) throws IOException {
        if(v == null) {
            writeNil();
        } else {
            writeFloat(v);
        }
    }

    public void write(Double v) throws IOException {
        if(v == null) {
            writeNil();
        } else {
            writeDouble(v);
        }
    }

    public abstract void write(String s) throws IOException;

    public void write(byte[] b) throws IOException {
        writeByteArray(b.length);
        writeByteArray(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        writeByteArray(len);
        writeByteArray(b, off, len);
    }

    public void write(ByteBuffer b) throws IOException {
        // TODO
    }

    public void write(MessagePackable o) throws IOException {
        o.writeTo(this);
    }

    public abstract void write(Object o) throws IOException;
    */
}

