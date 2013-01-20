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

import java.io.IOException;
import java.nio.ByteBuffer;
import org.msgpack.type.Value;
import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.packer.Unconverter;

public abstract class AbstractUnpacker implements Unpacker {
    protected MessagePack msgpack;

    protected int rawSizeLimit = 134217728;

    protected int arraySizeLimit = 4194304;

    protected int mapSizeLimit = 2097152;

    protected AbstractUnpacker(MessagePack msgpack) {
        this.msgpack = msgpack;
    }

    @Override
    public ByteBuffer readByteBuffer() throws IOException {
        return ByteBuffer.wrap(readByteArray());
    }

    @Override
    public void readArrayEnd() throws IOException {
        readArrayEnd(false);
    }

    @Override
    public void readMapEnd() throws IOException {
        readMapEnd(false);
    }

    @Override
    public UnpackerIterator iterator() {
        return new UnpackerIterator(this);
    }

    protected abstract void readValue(Unconverter uc) throws IOException;

    @Override
    public Value readValue() throws IOException {
        Unconverter uc = new Unconverter(msgpack);
        readValue(uc);
        return uc.getResult();
    }

    protected abstract boolean tryReadNil() throws IOException;

    @Override
    public <T> T read(Class<T> klass) throws IOException {
        if (tryReadNil()) {
            return null;
        }
        Template<T> tmpl = msgpack.lookup(klass);
        return tmpl.read(this, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T read(T to) throws IOException {
        if (tryReadNil()) {
            return null;
        }
        Template<T> tmpl = msgpack.lookup((Class<T>) to.getClass());
        return tmpl.read(this, to);
    }

    @Override
    public <T> T read(Template<T> tmpl) throws IOException {
        if (tryReadNil()) {
            return null;
        }
        return (T) tmpl.read(this, null);
    }

    @Override
    public <T> T read(T to, Template<T> tmpl) throws IOException {
        if (tryReadNil()) {
            return null;
        }
        return (T) tmpl.read(this, to);
    }

    public int getReadByteCount() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void resetReadByteCount() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void setRawSizeLimit(int size) {
        if (size < 32) {
            rawSizeLimit = 32;
        } else {
            rawSizeLimit = size;
        }
    }

    public void setArraySizeLimit(int size) {
        if (size < 16) {
            arraySizeLimit = 16;
        } else {
            arraySizeLimit = size;
        }
    }

    public void setMapSizeLimit(int size) {
        if (size < 16) {
            mapSizeLimit = 16;
        } else {
            mapSizeLimit = size;
        }
    }
}
