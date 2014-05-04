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
import java.nio.ByteBuffer;

public class MessagePacker {

    private final MessageBufferOutput out;
    private MessageBuffer currentBuffer;
    private int position;

    public MessagePacker(MessageBufferOutput out) {
        this.out = out;
    }

    public void flush() throws IOException {

    }

    public void close() throws IOException {

    }

    public void packNil() throws IOException {

    }

    public void packBoolean(boolean o) throws IOException {

    }


    public void packByte(byte o) throws IOException {

    }

    public void packShort(short o) throws IOException {

    }

    public void packInt(int o) throws IOException {

    }


    public void packLong(long o) throws IOException {

    }

    public void packBigInteger(BigInteger o) throws IOException {

    }

    public void packFloat(float o) throws IOException {

    }

    public void packDouble(double o) throws IOException {

    }

    /**
     * pack the input String in UTF-8 encoding
     * @param o
     * @return
     * @throws IOException
     */
    public void packString(String o) throws IOException {

    }

    public void packArrayHeader(int size) throws IOException {

    }

    public void packMapHeader(int size) throws IOException {

    }

    public void packExtendedTypeHeader(int type, int dataLen) throws IOException {

    }

    public void packRawStringHeader(int len) throws IOException {

    }
    public void packBinaryHeader(int len) throws IOException {

    }

    public void writePayload(ByteBuffer bb) throws IOException {

    }

    public void writePayload(byte[] o, int off, int len) throws IOException {

    }


}
