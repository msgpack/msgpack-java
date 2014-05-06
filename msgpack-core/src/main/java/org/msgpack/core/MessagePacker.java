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
import static org.msgpack.core.MessagePack.Prefix.*;

/**
 *
 */
public class MessagePacker {

    private final MessageBufferOutput out;
    private MessageBuffer currentBuffer;
    private int position;

    public MessagePacker(MessageBufferOutput out) {
        assert(out != null);
        this.out = out;
    }

    public void flush() throws IOException {
        out.flush(currentBuffer, 0, position);
        position = 0;
    }

    public void close() throws IOException {
        try {
            flush();
        }
        finally {
            out.close();
        }
    }

    private boolean ensureCapacity(int numBytesToWrite) throws IOException {
        if(position + numBytesToWrite < currentBuffer.size())
            return true;

        flush();
        return numBytesToWrite > currentBuffer.size();
    }


    private void writeByte(byte b) throws IOException {
        ensureCapacity(1);
        currentBuffer.putByte(position++, b);
    }


    private void writeByteAndByte(byte b, byte v) throws IOException {
        ensureCapacity(2);
        currentBuffer.putByte(position++, b);
        currentBuffer.putByte(position++, v);
    }

    private void writeByteAndShort(byte b, short v) throws IOException {
        ensureCapacity(3);
        currentBuffer.putByte(position++, b);
        currentBuffer.putShort(position, v);
        position += 2;
    }

    private void writeByteAndInt(byte b, int v) throws IOException {
        ensureCapacity(5);
        currentBuffer.putByte(position++, b);
        currentBuffer.putInt(position, v);
        position += 4;
    }

    private void writeByteAndLong(byte b, long v) throws IOException {
        ensureCapacity(9);
        currentBuffer.putByte(position++, b);
        currentBuffer.putLong(position, v);
        position += 8;
    }

    private void writeLong(long v) throws IOException {
        ensureCapacity(8);
        currentBuffer.putLong(position, v);
        position += 8;
    }

    public void packNil() throws IOException {
        writeByte(NIL);
    }

    public void packBoolean(boolean b) throws IOException {
        writeByte(b ? TRUE : FALSE);
    }


    public void packByte(byte b) throws IOException {
        if(b < -(1 << 5)) {
            writeByteAndByte(INT8, b);
        } else {
            writeByte(b);
        }
    }

    public void packShort(short v) throws IOException {
        if(v < -(1 << 5)) {
            if(v < -(1 << 7)) {
                writeByteAndShort(INT16, v);
            } else {
                writeByteAndByte(INT8, (byte) v);
            }
        } else if(v < (1 << 7)) {
            writeByte((byte) v);
        } else {
            if(v < (1 << 8)) {
                writeByteAndByte(UINT8, (byte) v);
            }
            else {
                writeByteAndShort(UINT16, v);
            }
        }
    }

    public void packInt(int r) throws IOException {
        if (r < -(1 << 5)) {
            if (r < -(1 << 15)) {
                writeByteAndInt(INT32, r);
            } else if (r < -(1 << 7)) {
                writeByteAndShort(INT16, (short) r);
            } else {
                writeByteAndByte(INT8, (byte) r);
            }
        } else if (r < (1 << 7)) {
            writeByte((byte) r);
        } else {
            if (r < (1 << 8)) {
                writeByteAndByte(UINT8, (byte) r);
            } else if (r < (1 << 16)) {
                writeByteAndShort(UINT16, (short) r);
            } else {
                // unsigned 32
                writeByteAndInt(UINT32, r);
            }
        }
    }

    public void packLong(long v) throws IOException {
        if (v < -(1L << 5)) {
            if (v < -(1L << 15)) {
                if (v < -(1L << 31)) {
                    writeByteAndLong(INT64, v);
                } else {
                    writeByteAndInt(INT32, (int) v);
                }
            } else {
                if (v < -(1 << 7)) {
                    writeByteAndShort(INT16, (short) v);
                } else {
                    writeByteAndByte(INT8, (byte) v);
                }
            }
        } else if (v < (1 << 7)) {
            // fixnum
            writeByte((byte) v);
        } else {
            if (v < (1L << 16)) {
                if (v < (1 << 8)) {
                    writeByteAndByte(UINT8, (byte) v);
                } else {
                    writeByteAndShort(UINT16, (short) v);
                }
            } else {
                if (v < (1L << 32)) {
                    writeByteAndInt(UINT32, (int) v);
                } else {
                    writeByteAndLong(UINT64, v);
                }
            }
        }
    }

    public void packBigInteger(BigInteger bi) throws IOException {
        if(bi.bitLength() <= 63) {
            writeLong(bi.longValue());
        } else if(bi.bitLength() == 64 && bi.signum() == 1) {
            writeByteAndLong(UINT64, bi.longValue());
        } else {
            throw new IllegalArgumentException("Messagepack cannot serialize BigInteger larger than 2^64-1");
        }
    }

    public void packFloat(float o) throws IOException {

    }

    public void packDouble(double o) throws IOException {

    }

    /**
     * pack the input String in UTF-8 encoding
     *
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
