package org.msgpack.core;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Created on 2014/05/04.
 */
public class MessageBufferOutput {

    private MessagePackerChannel out;


    public MessageBufferOutput writeNil() throws IOException {
        out.writeByte((byte) 0xc0);
        return this;
    }

    public MessageBufferOutput writeBoolean(boolean d) throws IOException {
        if (d) {
            // true
            out.writeByte((byte) 0xc3);
        } else {
            // false
            out.writeByte((byte) 0xc2);
        }
        return this;
    }

    public MessageBufferOutput writeByte(byte d) throws IOException {
        if (d < -(1 << 5)) {
            out.writeByteAndByte((byte) 0xd0, d);
        } else {
            out.writeByte(d);
        }
        return this;
    }

    public MessageBufferOutput writeShort(short d) throws IOException {
        if (d < -(1 << 5)) {
            if (d < -(1 << 7)) {
                // signed 16
                out.writeByteAndShort((byte) 0xd1, d);
            } else {
                // signed 8
                out.writeByteAndByte((byte) 0xd0, (byte) d);
            }
        } else if (d < (1 << 7)) {
            // fixnum
            out.writeByte((byte) d);
        } else {
            if (d < (1 << 8)) {
                // unsigned 8
                out.writeByteAndByte((byte) 0xcc, (byte) d);
            } else {
                // unsigned 16
                out.writeByteAndShort((byte) 0xcd, d);
            }
        }
        return this;
    }

    public MessageBufferOutput writeInt(int d) throws IOException {
        if (d < -(1 << 5)) {
            if (d < -(1 << 15)) {
                // signed 32
                out.writeByteAndInt((byte) 0xd2, d);
            } else if (d < -(1 << 7)) {
                // signed 16
                out.writeByteAndShort((byte) 0xd1, (short) d);
            } else {
                // signed 8
                out.writeByteAndByte((byte) 0xd0, (byte) d);
            }
        } else if (d < (1 << 7)) {
            // fixnum
            out.writeByte((byte) d);
        } else {
            if (d < (1 << 8)) {
                // unsigned 8
                out.writeByteAndByte((byte) 0xcc, (byte) d);
            } else if (d < (1 << 16)) {
                // unsigned 16
                out.writeByteAndShort((byte) 0xcd, (short) d);
            } else {
                // unsigned 32
                out.writeByteAndInt((byte) 0xce, d);
            }
        }
        return this;
    }

    public MessageBufferOutput writeLong(long d) throws IOException {
        if (d < -(1L << 5)) {
            if (d < -(1L << 15)) {
                if (d < -(1L << 31)) {
                    // signed 64
                    out.writeByteAndLong((byte) 0xd3, d);
                } else {
                    // signed 32
                    out.writeByteAndInt((byte) 0xd2, (int) d);
                }
            } else {
                if (d < -(1 << 7)) {
                    // signed 16
                    out.writeByteAndShort((byte) 0xd1, (short) d);
                } else {
                    // signed 8
                    out.writeByteAndByte((byte) 0xd0, (byte) d);
                }
            }
        } else if (d < (1 << 7)) {
            // fixnum
            out.writeByte((byte) d);
        } else {
            if (d < (1L << 16)) {
                if (d < (1 << 8)) {
                    // unsigned 8
                    out.writeByteAndByte((byte) 0xcc, (byte) d);
                } else {
                    // unsigned 16
                    out.writeByteAndShort((byte) 0xcd, (short) d);
                }
            } else {
                if (d < (1L << 32)) {
                    // unsigned 32
                    out.writeByteAndInt((byte) 0xce, (int) d);
                } else {
                    // unsigned 64
                    out.writeByteAndLong((byte) 0xcf, d);
                }
            }
        }
        return this;
    }

    public MessageBufferOutput writeBigInteger(BigInteger d) throws IOException {
        if (d.bitLength() <= 63) {
            writeLong(d.longValue());
        } else if (d.bitLength() == 64 && d.signum() == 1) {
            // unsigned 64
            out.writeByteAndLong((byte) 0xcf, d.longValue());
        } else {
            throw new IllegalArgumentException(
                    "MessagePack can't serialize BigInteger larger than (2^64)-1");
        }
        return this;
    }

    public MessageBufferOutput writeFloat(float d) throws IOException {
        out.writeByteAndFloat((byte) 0xca, d);
        return this;
    }

    public MessageBufferOutput writeDouble(double d) throws IOException {
        out.writeByteAndDouble((byte) 0xcb, d);
        return this;
    }

    public MessageBufferOutput writeString(String o) throws IOException {
        // TODO not implemented yet
        return this;
    }

    public MessageBufferOutput writeBinary(ByteBuffer o) throws IOException {
        // TODO not implemented yet
        return this;
    }

    public MessageBufferOutput writeRawStringLength(int len) throws IOException {
        // TODO not implemented yet
        return this;
    }

    public MessageBufferOutput writeBinaryLength(int len) throws IOException {
        // TODO not implemented yet
        return this;
    }

    public MessageBufferOutput rawWrite(ByteBuffer o) throws IOException {
        // TODO not implemented yet
        return this;
    }

    public MessageBufferOutput rawWrite(byte[] o, int off, int len) throws IOException {
        // TODO not implemented yet
        return this;
    }

    @Override
    public MessageBufferOutput writePayloadByByteBuffer(ByteBuffer bb) throws IOException {
        // TODO
        return null;
    }

    /*
    public MessageBufferOutput writeByteArray(byte[] o) throws IOException {
        return writeByteArray(o, 0, o.length);
    }

    public MessageBufferOutput writeByteArray(byte[] b, int off, int len)
            throws IOException {
        if (len < 32) {
            out.writeByte((byte) (0xa0 | len));
        } else if (len < 65536) {
            out.writeByteAndShort((byte) 0xda, (short) len);
        } else {
            out.writeByteAndInt((byte) 0xdb, len);
        }
        out.write(b, off, len);
        return this;
    }

    public MessageBufferOutput writeByteBuffer(ByteBuffer bb) throws IOException {
        int len = bb.remaining();
        if (len < 32) {
            out.writeByte((byte) (0xa0 | len));
        } else if (len < 65536) {
            out.writeByteAndShort((byte) 0xda, (short) len);
        } else {
            out.writeByteAndInt((byte) 0xdb, len);
        }
        int pos = bb.position();
        try {
            out.write(bb);
        } finally {
            bb.position(pos);
        }
        return this;
    }

    public MessageBufferOutput writeString(String s) throws IOException {
        byte[] b;
        try {
            // TODO encoding error?
            b = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new MessageTypeException(ex);
        }
        return writeByteArray(b, 0, b.length);
    }
    */

    public MessageBufferOutput writeArrayHeader(int size) throws IOException {
        // TODO check size < 0?
        if (size < 16) {
            // FixArray
            out.writeByte((byte) (0x90 | size));
        } else if (size < 65536) {
            out.writeByteAndShort((byte) 0xdc, (short) size);
        } else {
            out.writeByteAndInt((byte) 0xdd, size);
        }
        return this;
    }

    public MessageBufferOutput writeMapHeader(int size) throws IOException {
        // TODO check size < 0?
        if (size < 16) {
            // FixMap
            out.writeByte((byte) (0x80 | size));
        } else if (size < 65536) {
            out.writeByteAndShort((byte) 0xde, (short) size);
        } else {
            out.writeByteAndInt((byte) 0xdf, size);
        }
        return this;
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void close() throws IOException {
        out.close();
    }

}
