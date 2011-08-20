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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.math.BigInteger;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Unconverter;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

public abstract class AbstractIndirectUnpacker extends AbstractUnpacker {
    protected abstract Value nextValue() throws IOException;

    protected Converter converter;

    public AbstractIndirectUnpacker(MessagePack msgpack) {
        super(msgpack);
        this.converter = new Converter(msgpack, null);
    }

    private void ensureValue() throws IOException {
        if(converter.getSourceValue() == null) {
            converter.getSourceValue(nextValue());
        }
    }

    @Override
    public boolean tryReadNil() throws IOException {
        ensureValue();
        return converter.tryReadNil();
    }

    @Override
    public boolean trySkipNil() throws IOException {
        ensureValue();
        return converter.trySkipNil();
    }

    @Override
    public void readNil() throws IOException {
        ensureValue();
        converter.readNil();
    }


    @Override
    public boolean readBoolean() throws IOException {
        ensureValue();
        return converter.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        ensureValue();
        return converter.readByte();
    }

    @Override
    public short readShort() throws IOException {
        ensureValue();
        return converter.readShort();
    }

    @Override
    public int readInt() throws IOException {
        ensureValue();
        return converter.readInt();
    }

    @Override
    public long readLong() throws IOException {
        ensureValue();
        return converter.readLong();
    }

    @Override
    public BigInteger readBigInteger() throws IOException {
        ensureValue();
        return converter.readBigInteger();
    }

    @Override
    public float readFloat() throws IOException {
        ensureValue();
        return converter.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        ensureValue();
        return converter.readDouble();
    }

    @Override
    public byte[] readByteArray() throws IOException {
        ensureValue();
        return converter.readByteArray();
    }


    @Override
    public int readArrayBegin() throws IOException {
        ensureValue();
        return converter.readArrayBegin();
    }

    @Override
    public void readArrayEnd(boolean check) throws IOException {
        ensureValue();
        converter.readArrayEnd(check);
    }


    @Override
    public int readMapBegin() throws IOException {
        ensureValue();
        return converter.readMapBegin();
    }

    @Override
    public void readMapEnd(boolean check) throws IOException {
        ensureValue();
        converter.readMapEnd(check);
    }


    @Override
    public String readString() throws IOException {
        ensureValue();
        return converter.readString();
    }


    @Override
    protected void readValue(Unconverter uc) throws IOException {
        ensureValue();
        converter.readValue(uc);
    }

    @Override
    public void skip() throws IOException {
        converter.skip();
    }

    @Override
    public void close() throws IOException {
        converter.close();
    }
}

