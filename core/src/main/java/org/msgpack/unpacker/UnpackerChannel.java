//
// MessagePack for Java
//
// Copyright (C) 2009-2013 FURUHASHI Sadayuki
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

public interface UnpackerChannel extends Closeable {
    public int read(byte[] b, int off, int len) throws IOException;

    public int skip(int n) throws IOException;

    public byte readByte() throws IOException;

    public short readShort() throws IOException;

    public int readInt() throws IOException;

    public long readLong() throws IOException;

    public float readFloat() throws IOException;

    public double readDouble() throws IOException;
}

