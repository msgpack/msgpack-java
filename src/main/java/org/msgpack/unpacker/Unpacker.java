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

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.lang.Iterable;
import org.msgpack.value.Value;
import org.msgpack.packer.Unconverter;
import org.msgpack.io.EndOfBufferException;

public abstract class Unpacker implements Iterable<Value> {
    public abstract void readNil() throws IOException;

    public abstract boolean tryReadNil() throws IOException;


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


    public class Iterator implements java.util.Iterator<Value> {
        private final Unconverter uc;
        private IOException exception;

        public Iterator() {
            this.uc = new Unconverter();
        }

        public boolean hasNext() {
            if(uc.getResult() != null) {
                return true;
            }
            try {
                readValue(uc);  // protected method
            } catch (EndOfBufferException ex) {
                return false;
            } catch (IOException ex) {
                // TODO error
                exception = ex;
                return false;
            }
            return uc.getResult() != null;
        }

        public Value next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            Value v = uc.getResult();
            uc.resetResult();
            return v;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public IOException getException() {
            return exception;
        }
    }

    public Iterator iterator() {
        return new Iterator();
    }

    public abstract void skip() throws IOException;


    protected abstract void readValue(Unconverter uc) throws IOException;

    public Value readValue() throws IOException {
        Unconverter uc = new Unconverter();
        readValue(uc);
        return uc.getResult();
    }


    public <T> T read(T to) throws IOException {
        // TODO template
        return null;
    }

    public <T> T read(Class<T> klass) throws IOException {
        // TODO template
        return null;
    }
}

