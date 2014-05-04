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
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

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

// TODO impl
// TODO rename Unpacker -> MessageUnpacker
public class Unpacker extends Closeable {

    public static class Options {
        // allow unpackBinaryHeader to read str format family  // default:true
        // allow unpackRawStringHeader and unpackString to read bin format family // default: true
        // string decode malformed input action  // default:report
        // string decode unmappable character action  // default:report

        // unpackString size limit // default: Integer.MAX_VALUE
    }

    //
    private static final byte HEAD_BYTE_NEVER_USED_TYPE = (byte) 0xc1;

    private static final byte REQUIRE_TO_READ_HEAD_BYTE = HEAD_BYTE_NEVER_USED_TYPE;
    private static final int REQUIRE_TO_READ_SIZE = -1;

    private static Charset UTF_8 = Charset.forName("UTF-8");

    private final MessageBufferInput in;

    private MessageBuffer buffer;
    private int position;

    // For storing data at the buffer boundary (except in unpackString)
    private MessageBuffer extraBuffer;
    private int extraPosition;

    // For decoding String in unpackString
    private CharsetDecoder decorder;
    private int stringLength;

    // internal states
    private byte head;

    public Unpacker(MessageBufferInput in) {
        this.in = in;
    }

    public ValueType getNextType() throws IOException {
        // TODO
        return null;
    }

    public MessageFormat getNextFormat() throws IOException {
        return null;
    }


    public void skipToken() throws IOException {

    }

    public boolean trySkipNil() throws IOException {

    }

    public void unpackNil() throws IOException {

    }

    public boolean unpackBoolean() throws IOException {

    }

    public byte unpackByte() throws IOException {

    }

    public short unpackShort() throws IOException {

    }

    public int unpackInt() throws IOException {

    }

    public long unpackLong() throws IOException {

    }

    public BigInteger unpackBigInteger() throws IOException {

    }

    public float unpackFloat() throws IOException {

    }

    public double unpackDouble() throws IOException {

    }

    public String unpackString() throws IOException {
        // unpackRawStringHeader
        // ..
    }


    public int unpackArrayHeader() throws IOException {

    }

    public int unpackMapHeader() throws IOException {

    }

    public ExtendedTypeHeader unpackExtendedTypeHeader() throws IOException {

    }

    public int unpackRawStringHeader() throws IOException {

    }
    public int unpackBinaryHeader() throws IOException {

    }

    public void readPayload(ByteBuffer dst) throws IOException {

    }

    public void readPayload(byte[] dst, int off, int len) throws IOException {

    }

    // TODO returns a buffer reference to the payload (zero-copy)
    // public long readPayload(...)

}
