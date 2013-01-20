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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.msgpack.type.Value;
import org.msgpack.MessagePack;
import org.msgpack.template.Template;

public abstract class AbstractPacker implements Packer {
    protected MessagePack msgpack;

    protected AbstractPacker(MessagePack msgpack) {
        this.msgpack = msgpack;
    }

    @Override
    public Packer write(boolean o) throws IOException {
        writeBoolean(o);
        return this;
    }

    @Override
    public Packer write(byte o) throws IOException {
        writeByte(o);
        return this;
    }

    @Override
    public Packer write(short o) throws IOException {
        writeShort(o);
        return this;
    }

    @Override
    public Packer write(int o) throws IOException {
        writeInt(o);
        return this;
    }

    @Override
    public Packer write(long o) throws IOException {
        writeLong(o);
        return this;
    }

    @Override
    public Packer write(float o) throws IOException {
        writeFloat(o);
        return this;
    }

    @Override
    public Packer write(double o) throws IOException {
        writeDouble(o);
        return this;
    }

    @Override
    public Packer write(Boolean o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBoolean(o);
        }
        return this;
    }

    @Override
    public Packer write(Byte o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByte(o);
        }
        return this;
    }

    @Override
    public Packer write(Short o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeShort(o);
        }
        return this;
    }

    @Override
    public Packer write(Integer o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeInt(o);
        }
        return this;
    }

    @Override
    public Packer write(Long o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeLong(o);
        }
        return this;
    }

    @Override
    public Packer write(BigInteger o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeBigInteger(o);
        }
        return this;
    }

    @Override
    public Packer write(Float o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeFloat(o);
        }
        return this;
    }

    @Override
    public Packer write(Double o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeDouble(o);
        }
        return this;
    }

    @Override
    public Packer write(byte[] o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteArray(o);
        }
        return this;
    }

    @Override
    public Packer write(byte[] o, int off, int len) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteArray(o, off, len);
        }
        return this;
    }

    @Override
    public Packer write(ByteBuffer o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeByteBuffer(o);
        }
        return this;
    }

    @Override
    public Packer write(String o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            writeString(o);
        }
        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Packer write(Object o) throws IOException {
        if (o == null) {
            writeNil();
        } else {
            Template tmpl = msgpack.lookup(o.getClass());
            tmpl.write(this, o);
        }
        return this;
    }

    @Override
    public Packer write(Value v) throws IOException {
        if (v == null) {
            writeNil();
        } else {
            v.writeTo(this);
        }
        return this;
    }

    @Override
    public Packer writeArrayEnd() throws IOException {
        writeArrayEnd(true);
        return this;
    }

    @Override
    public Packer writeMapEnd() throws IOException {
        writeMapEnd(true);
        return this;
    }

    @Override
    public void close() throws IOException {
    }

    abstract protected void writeBoolean(boolean v) throws IOException;

    abstract protected void writeByte(byte v) throws IOException;

    abstract protected void writeShort(short v) throws IOException;

    abstract protected void writeInt(int v) throws IOException;

    abstract protected void writeLong(long v) throws IOException;

    abstract protected void writeBigInteger(BigInteger v) throws IOException;

    abstract protected void writeFloat(float v) throws IOException;

    abstract protected void writeDouble(double v) throws IOException;

    protected void writeByteArray(byte[] b) throws IOException {
        writeByteArray(b, 0, b.length);
    }

    abstract protected void writeByteArray(byte[] b, int off, int len) throws IOException;

    abstract protected void writeByteBuffer(ByteBuffer bb) throws IOException;

    abstract protected void writeString(String s) throws IOException;
}
