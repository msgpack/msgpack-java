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
import java.math.BigInteger;
import java.lang.Iterable;
import org.msgpack.type.Value;
import org.msgpack.MessagePack;
import org.msgpack.packer.Unconverter;


public abstract class Unpacker implements Iterable<Value> {
    protected MessagePack msgpack;

    protected Unpacker(MessagePack msgpack) {
	this.msgpack = msgpack;
    }

    public abstract boolean tryReadNil() throws IOException;

    public abstract boolean trySkipNil() throws IOException;

    public abstract void readNil() throws IOException;


    public abstract boolean readBoolean() throws IOException;

    public abstract byte readByte() throws IOException;

    public abstract short readShort() throws IOException;

    public abstract int readInt() throws IOException;

    public abstract long readLong() throws IOException;

    public abstract BigInteger readBigInteger() throws IOException;

    public abstract float readFloat() throws IOException;

    public abstract double readDouble() throws IOException;

    public abstract byte[] readByteArray() throws IOException;


    public abstract int readArrayBegin() throws IOException;

    public abstract void readArrayEnd(boolean check) throws IOException;

    public void readArrayEnd() throws IOException {
        readArrayEnd(false);
    }


    public abstract int readMapBegin() throws IOException;

    public abstract void readMapEnd(boolean check) throws IOException;

    public void readMapEnd() throws IOException {
        readMapEnd(false);
    }


    public String readString() throws IOException {
        // TODO encoding exception
        return new String(readByteArray(), "UTF-8");
    }

    public UnpackerIterator iterator() {
        return new UnpackerIterator(this);
    }

    public abstract void skip() throws IOException;


    protected abstract void readValue(Unconverter uc) throws IOException;

    public Value readValue() throws IOException {
        Unconverter uc = new Unconverter(msgpack);
        readValue(uc);
        return uc.getResult();
    }


    public <T> T read(T to) throws IOException {
        return (T) msgpack.getTemplate(to.getClass()).read(this, to);
    }

    public <T> T read(Class<T> klass) throws IOException {
        return (T) msgpack.getTemplate(klass).read(this, null);
    }

    public <T> T readOptional(Class<T> klass, T defaultValue) throws IOException {
        if(trySkipNil()) {
            return defaultValue;
        }
        return (T) msgpack.getTemplate(klass).read(this, null);
    }

    public <T> T readOptional(T to, T defaultValue) throws IOException {
        if(trySkipNil()) {
            return defaultValue;
        }
        return (T) msgpack.getTemplate(to.getClass()).read(this, to);
    }

    public <T> T readOptional(Class<T> klass) throws IOException {
        return readOptional(klass, null);
    }

    public <T> T readOptional(T to) throws IOException {
        return readOptional(to, null);
    }
}

