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
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.lang.Iterable;

import org.msgpack.template.Template;
import org.msgpack.type.Value;
import org.msgpack.type.ValueType;

/**
 * Standard deserializer.
 * 
 * @version 0.6.0
 */
public interface Unpacker extends Iterable<Value>, Closeable {
    public <T> T read(Class<T> klass) throws IOException;

    public <T> T read(T to) throws IOException;

    public <T> T read(Template<T> tmpl) throws IOException;

    public <T> T read(T to, Template<T> tmpl) throws IOException;

    public void skip() throws IOException;

    public int readArrayBegin() throws IOException;

    public void readArrayEnd(boolean check) throws IOException;

    public void readArrayEnd() throws IOException;

    public int readMapBegin() throws IOException;

    public void readMapEnd(boolean check) throws IOException;

    public void readMapEnd() throws IOException;

    public void readNil() throws IOException;

    public boolean trySkipNil() throws IOException;

    public boolean readBoolean() throws IOException;

    public byte readByte() throws IOException;

    public short readShort() throws IOException;

    public int readInt() throws IOException;

    public long readLong() throws IOException;

    public BigInteger readBigInteger() throws IOException;

    public float readFloat() throws IOException;

    public double readDouble() throws IOException;

    public byte[] readByteArray() throws IOException;

    public ByteBuffer readByteBuffer() throws IOException;

    public String readString() throws IOException;

    public Value readValue() throws IOException;

    public ValueType getNextType() throws IOException;

    public UnpackerIterator iterator();

    public int getReadByteCount();

    public void resetReadByteCount();

    public void setRawSizeLimit(int size);

    public void setArraySizeLimit(int size);

    public void setMapSizeLimit(int size);
}
