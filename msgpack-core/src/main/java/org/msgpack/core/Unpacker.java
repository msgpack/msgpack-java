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

class ExtendedTypeHeader {
    private final int type;
    private final int length;
    ExtendedTypeHeader(int type, int length) {
        this.type = type;
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public int getLength() {
        return length;
    }
}

public interface Unpacker extends Closeable {

    public ValueType getNextType() throws IOException;

    public MessageFormat getNextFormat() throws IOException;


    public void skipToken() throws IOException;

    public boolean trySkipNil() throws IOException;

    public void unpackNil() throws IOException;

    public boolean unpackBoolean() throws IOException;

    public byte unpackByte() throws IOException;

    public short unpackShort() throws IOException;

    public int unpackInt() throws IOException;

    public long unpackLong() throws IOException;

    public BigInteger unpackBigInteger() throws IOException;

    public float unpackFloat() throws IOException;

    public double unpackDouble() throws IOException;

    public String unpackString() throws IOException;


    public int unpackArrayHeader() throws IOException;

    public int unpackMapHeader() throws IOException;

    public ExtendedTypeHeader unpackExtendedTypeHeader() throws IOException;

    public int unpackRawStringHeader() throws  IOException;
    public int unpackBinaryHeader() throws IOException;

    public void readPayload(ByteBuffer dst) throws IOException;
    public void readPayload(byte[] dst, int off, int len) throws IOException;

    // returns a buffer reference to the payload (zero-copy)
    // public long readPayload(...)

}
