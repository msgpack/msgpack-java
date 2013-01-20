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

public interface Input extends Closeable {
    public int read(byte[] b, int off, int len) throws IOException;

    public boolean tryRefer(BufferReferer ref, int len) throws IOException;

    public byte readByte() throws IOException;

    public void advance();

    public byte getByte() throws IOException;

    public short getShort() throws IOException;

    public int getInt() throws IOException;

    public long getLong() throws IOException;

    public float getFloat() throws IOException;

    public double getDouble() throws IOException;

    public int getReadByteCount();

    public void resetReadByteCount();
}
