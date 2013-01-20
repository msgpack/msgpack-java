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
import java.io.IOException;
import java.io.Closeable;
import java.io.Flushable;
import java.nio.ByteBuffer;
import org.msgpack.type.Value;

/**
 * Standard serializer in MessagePack for Java. It allows users to serialize
 * objects like <tt>String</tt>, <tt>List</tt>, <tt>Map</tt>, <tt>byte[]</tt>,
 * primitive types and so on.
 * 
 * @version 0.6.0
 */
public interface Packer extends Closeable, Flushable {
    public Packer write(boolean o) throws IOException;

    public Packer write(byte o) throws IOException;

    public Packer write(short o) throws IOException;

    public Packer write(int o) throws IOException;

    public Packer write(long o) throws IOException;

    public Packer write(float o) throws IOException;

    public Packer write(double o) throws IOException;

    public Packer write(Boolean o) throws IOException;

    public Packer write(Byte o) throws IOException;

    public Packer write(Short o) throws IOException;

    public Packer write(Integer o) throws IOException;

    public Packer write(Long o) throws IOException;

    public Packer write(Float o) throws IOException;

    public Packer write(Double o) throws IOException;

    public Packer write(BigInteger o) throws IOException;

    public Packer write(byte[] o) throws IOException;

    public Packer write(byte[] o, int off, int len) throws IOException;

    public Packer write(ByteBuffer o) throws IOException;

    public Packer write(String o) throws IOException;

    public Packer write(Value v) throws IOException;

    public Packer write(Object o) throws IOException;

    public Packer writeNil() throws IOException;

    public Packer writeArrayBegin(int size) throws IOException;

    public Packer writeArrayEnd(boolean check) throws IOException;

    public Packer writeArrayEnd() throws IOException;

    public Packer writeMapBegin(int size) throws IOException;

    public Packer writeMapEnd(boolean check) throws IOException;

    public Packer writeMapEnd() throws IOException;
}
