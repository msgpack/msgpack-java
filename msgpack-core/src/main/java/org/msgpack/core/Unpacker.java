//
// MessagePack for Java
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
package org.msgpack.core;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.math.BigInteger;

public interface Unpacker extends Closeable {
    public ValueType getNextType() throws IOException;

    public void skipToken() throws IOException;

    public boolean trySkipNil() throws IOException;

    public void readNil() throws IOException;

    public boolean readBoolean() throws IOException;

    public byte readByte() throws IOException;

    public short readShort() throws IOException;

    public int readInt() throws IOException;

    public long readLong() throws IOException;

    public BigInteger readBigInteger() throws IOException;

    public float readFloat() throws IOException;

    public double readDouble() throws IOException;

    public String readString() throws IOException;

    public ByteBuffer readRawString() throws IOException;

    public ByteBuffer readBinary() throws IOException;

    public int readArrayHeader() throws IOException;

    public int readMapHeader() throws IOException;

    // TODO ext type

    public int readRawStringLength() throws IOException;

    public int readBinaryLength() throws IOException;

    public void rawRead(ByteBuffer dst) throws IOException;

    public void rawRead(byte[] b, int off, int len) throws IOException;
}
