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

import java.math.BigInteger;
import java.io.IOException;
import java.io.Closeable;
import java.io.Flushable;
import java.nio.ByteBuffer;

/**
 * Streaming serialization interface of MessagePack.
 */
public interface Packer extends Closeable, Flushable {
    public Packer writeNil() throws IOException;

    public Packer writeBoolean(boolean o) throws IOException;

    public Packer writeByte(byte o) throws IOException;

    public Packer writeShort(short o) throws IOException;

    public Packer writeInt(int o) throws IOException;

    public Packer writeLong(long o) throws IOException;

    public Packer writeBigInteger(BigInteger o) throws IOException;

    public Packer writeFloat(float o) throws IOException;

    public Packer writeDouble(double o) throws IOException;

    public Packer writeString(String o) throws IOException;

    public Packer writeBinary(ByteBuffer src) throws IOException;

    public Packer writeArrayHeader(int size) throws IOException;

    public Packer writeMapHeader(int size) throws IOException;

    public Packer writeRawStringLength(int len) throws IOException;

    public Packer writeBinaryLength(int len) throws IOException;

    public Packer rawWrite(ByteBuffer src) throws IOException;

    public Packer rawWrite(byte[] o, int off, int len) throws IOException;

    public Packer writePayloadByByteBuffer(ByteBuffer bb) throws IOException;
}
