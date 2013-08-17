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
import java.io.Closeable;
import java.io.Flushable;
import java.nio.ByteBuffer;

public interface Output extends Closeable, Flushable {
    public void write(byte[] b, int off, int len) throws IOException;

    public void write(ByteBuffer bb) throws IOException;

    public void writeByte(byte v) throws IOException;

    public void writeShort(short v) throws IOException;

    public void writeInt(int v) throws IOException;

    public void writeLong(long v) throws IOException;

    public void writeFloat(float v) throws IOException;

    public void writeDouble(double v) throws IOException;

    public void writeByteAndByte(byte b, byte v) throws IOException;

    public void writeByteAndShort(byte b, short v) throws IOException;

    public void writeByteAndInt(byte b, int v) throws IOException;

    public void writeByteAndLong(byte b, long v) throws IOException;

    public void writeByteAndFloat(byte b, float v) throws IOException;

    public void writeByteAndDouble(byte b, double v) throws IOException;
}
