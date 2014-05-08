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
import java.nio.charset.CharsetDecoder;
import org.msgpack.core.MessagePack.Code;


// TODO impl
public class MessageUnpacker implements Closeable {

    public static class Options {
        // allow unpackBinaryHeader to read str format family  // default:true
        // allow unpackRawStringHeader and unpackString to read bin format family // default: true
        // string decode malformed input action  // default:report
        // string decode unmappable character action  // default:report

        // unpackString size limit // default: Integer.MAX_VALUE
    }

    private static final byte READ_NEXT = Code.NEVER_USED;


    private final MessageBufferInput in;

    private MessageBuffer buffer;
    private int position;
    private int nextSize;

    // For storing data at the buffer boundary (except in unpackString)
    private MessageBuffer extraBuffer;
    private int extraPosition;

    // For decoding String in unpackString
    private CharsetDecoder decoder;
    private int stringLength;

    // internal state
    private byte head = Code.NEVER_USED;
    private static final int READ_SIZE = -1;

    public MessageUnpacker(MessageBufferInput in) {
        this.in = in;
    }

    private boolean ensure(int readSize) throws IOException {
        if(buffer == null) {
            buffer = in.next();
        }

        assert(buffer != null);

        if(position + readSize < buffer.limit())
            return true;

        if(readSize < buffer.limit()) {
            // TODO need to relocate the buffer contents

        }
        // Reached an EOF
        return false;
    }

    private CharsetDecoder getCharsetDecoder() {
        // TODO options
        CharsetDecoder d = decoder;
        if (d == null) {
            d = decoder = MessagePack.UTF8.newDecoder();
        }
        return d;
    }


    private static ValueType getTypeFromHeadByte(final byte b) throws MessageFormatException {
        ValueType vt = ValueType.lookUp(b);
        if(vt == ValueType.UNKNOWN)
            throw new MessageFormatException(String.format("Invalid format code: %02x", b));
        return vt;
    }

    public ValueType getNextType() throws IOException {
        return ValueType.lookUp(lookAhead());
    }

    public MessageFormat getNextFormat() throws IOException {
        byte h = lookAhead();
        if(h == READ_NEXT)
            return MessageFormat.EOF;
        else
            return MessageFormat.lookUp(h);
    }

    /**
     * Look-ahead the byte value at the current cursor position.
     * This method does not proceed the cursor
     * @return
     * @throws IOException
     */
    byte lookAhead() throws IOException {
        if(head == READ_NEXT) {
            if(ensure(1))
                head = buffer.getByte(position);
        }
        return head;
    }

    /**
     * Proceeds the cursor by 1
     */
    void consume() {
        position++;
        head = READ_NEXT;
    }

    /**
     * Proceeds the cursor by the specified bytes
     */
    void consume(int numBytes) {
        position += numBytes;
        head = READ_NEXT;
    }

    /**
     * Read a byte value at the cursor and proceed the cursor.
     * It also rests the head value to READ_NEXT.
     * @return
     * @throws IOException
     */
    private byte readByte() throws IOException {
        if(!ensure(1)) {
            throw new MessageFormatException("insufficient data length for reading byte value");
        }
        byte b = buffer.getByte(position++);
        consume();
        return b;
    }

    private short readShort() throws IOException {
        if(!ensure(2)) {
            throw new MessageFormatException("insufficient data length for reading short value");
        }
        short s = buffer.getShort(position);
        consume(2);
        return s;
    }

    private int readInt() throws IOException {
        if(!ensure(4)) {
            throw new MessageFormatException("insufficient data length for reading int value");
        }
        int i = buffer.getInt(position);
        consume(4);
        return i;
    }

    private float readFloat() throws IOException {
        if(!ensure(4)) {
            throw new MessageFormatException("insufficient data length for reading float value");
        }
        float f = buffer.getFloat(position);
        consume(4);
        return f;
    }

    private long readLong() throws IOException {
        if(!ensure(8)) {
            throw new MessageFormatException("insufficient data length for reading long value");
        }
        long l = buffer.getLong(position);
        consume(8);
        return l;
    }

    private double readDouble() throws IOException {
        if(!ensure(8)) {
            throw new MessageFormatException("insufficient data length for reading double value");
        }
        double d = buffer.getDouble(position);
        consume(8);
        return d;
    }

    public void skipValue() throws IOException {
        int remainingValues = 1;
        while(remainingValues > 0) {
            MessageFormat f = getNextFormat();
            remainingValues += f.skip(this);
        }
    }

    public void skipValueWithSwitch() throws IOException {
        // NOTE: This implementation must be as efficient as possible
        int remainingValues = 1;
        while(remainingValues > 0) {
            MessageFormat f = getNextFormat();
            byte b = lookAhead();
            consume();
            // TODO Proceeds the cursor in MessageFormat values
            switch(f) {
                case POSFIXINT:
                case NEGFIXINT:
                case BOOLEAN:
                case NIL:
                    break;
                case FIXMAP: {
                    int mapLen = b & 0x0f;
                    remainingValues += mapLen * 2;
                    break;
                }
                case FIXARRAY: {
                    int arrayLen = b & 0x0f;
                    remainingValues += arrayLen;
                    break;
                }
                case FIXSTR: {
                    int strLen = b & 0x1f;
                    consume(strLen);
                    break;
                }
                case INT8:
                case UINT8:
                    consume(1);
                    break;
                case INT16:
                case UINT16:
                    consume(2);
                    break;
                case INT32:
                case UINT32:
                case FLOAT32:
                    consume(4);
                    break;
                case INT64:
                case UINT64:
                case FLOAT64:
                    consume(8);
                    break;
                case BIN8:
                case STR8:
                    consume(readNextLength8());
                    break;
                case BIN16:
                case STR16:
                    consume(readNextLength16());
                    break;
                case BIN32:
                case STR32:
                    consume(readNextLength32());
                    break;
                case FIXEXT1:
                    consume(2);
                    break;
                case FIXEXT2:
                    consume(3);
                    break;
                case FIXEXT4:
                    consume(5);
                    break;
                case FIXEXT8:
                    consume(9);
                    break;
                case FIXEXT16:
                    consume(17);
                    break;
                case EXT8:
                    consume(readNextLength8() + 1);
                    break;
                case EXT16:
                    consume(readNextLength16() + 1);
                    break;
                case EXT32:
                    consume(readNextLength32() + 1);
                    break;
                case ARRAY16:
                    remainingValues += readNextLength16();
                    consume(2);
                    break;
                case ARRAY32:
                    remainingValues += readNextLength32();
                    consume(4);
                    break;
                case MAP16:
                    remainingValues += readNextLength16() * 2;
                    consume(2);
                    break;
                case MAP32:
                    remainingValues += readNextLength32() * 2; // TODO check int overflow
                    consume(2);
                    break;
                case UNKNOWN:
                    throw new MessageFormatException(String.format("unknown code: %02x is found", b));
            }

            remainingValues--;
        }
    }

    private static MessageTypeCastException unexpectedHeadByte(final String expectedTypeName, final byte b)
            throws MessageFormatException {
        ValueType type = getTypeFromHeadByte(b);
        String name = type.name();
        return new MessageTypeCastException(
                "Expected " + expectedTypeName + " type but got " +
                        name.substring(0, 1) + name.substring(1).toLowerCase() + " type");
    }

    public void unpackNil() throws IOException {
        final byte b = lookAhead();
        if (b == Code.NIL) {
            consume();
            return;
        }
        throw unexpectedHeadByte("Nil", b);
    }


    public boolean unpackBoolean() throws IOException {
        final byte b = lookAhead();
        if(b == Code.FALSE) {
            consume();
            return false;
        }
        else if(b == Code.TRUE) {
            consume();
            return true;
        }

        throw unexpectedHeadByte("boolean", b);
    }

    public byte unpackByte() throws IOException {
        final byte b = lookAhead();
        if (Code.isFixInt(b)) {
            consume();
            return b;
        }
        switch (b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                if (u8 < (byte) 0) {
                    throw overflowU8(u8);
                }
                return u8;
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                if (u16 < 0 || u16 > Byte.MAX_VALUE) {
                    throw overflowU16(u16);
                }
                return (byte) u16;
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if (u32 < 0 || u32 > Byte.MAX_VALUE) {
                    throw overflowU32(u32);
                }
                return (byte) u32;
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if (u64 < 0L || u64 > Byte.MAX_VALUE) {
                    throw overflowU64(u64);
                }
                return (byte) u64;
            case Code.INT8: // signed int 8
                byte i8 = readByte();
                return i8;
            case Code.INT16: // signed int 16
                short i16 = readShort();
                if (i16 < Byte.MIN_VALUE || i16 > Byte.MAX_VALUE) {
                    throw overflowI16(i16);
                }
                return (byte) i16;
            case Code.INT32: // signed int 32
                int i32 = readInt();
                if (i32 < Byte.MIN_VALUE || i32 > Byte.MAX_VALUE) {
                    throw overflowI32(i32);
                }
                return (byte) i32;
            case Code.INT64: // signed int 64
                long i64 = readLong();
                if (i64 < Byte.MIN_VALUE || i64 > Byte.MAX_VALUE) {
                    throw overflowI64(i64);
                }
                return (byte) i64;
        }
        throw unexpectedHeadByte("Integer", b);
    }

    public short unpackShort() throws IOException {
        final byte b = lookAhead();
        if (Code.isFixInt(b)) {
            consume();
            return (short) b;
        }
        switch (b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                return (short) (u8 & 0xff);
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                if (u16 < (short) 0) {
                    throw overflowU16(u16);
                }
                return u16;
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if (u32 < 0 || u32 >  Short.MAX_VALUE) {
                    throw overflowU32(u32);
                }
                return (short) u32;
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if (u64 < 0L || u64 > Short.MAX_VALUE) {
                    throw overflowU64(u64);
                }
                return (short) u64;
            case Code.INT8: // signed int 8
                byte i8 = readByte();
                return (short) i8;
            case Code.INT16: // signed int 16
                short i16 = readShort();
                return i16;
            case Code.INT32: // signed int 32
                int i32 = readInt();
                if (i32 < Short.MIN_VALUE || i32 > Short.MAX_VALUE) {
                    throw overflowI32(i32);
                }
                return (short) i32;
            case Code.INT64: // signed int 64
                long i64 = readLong();
                if (i64 < Short.MIN_VALUE || i64 > Short.MAX_VALUE) {
                    throw overflowI64(i64);
                }
                return (short) i64;
        }
        throw unexpectedHeadByte("Integer", b);

    }

    public int unpackInt() throws IOException {
        final byte b = lookAhead();
        if (Code.isFixInt(b)) {
            consume();
            return (int) b;
        }
        switch (b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                return u8 & 0xff;
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                return u16 & 0xffff;
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if (u32 < 0) {
                    throw overflowU32(u32);
                }
                return u32;
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if (u64 < 0L || u64 > (long) Integer.MAX_VALUE) {
                    throw overflowU64(u64);
                }
                return (int) u64;
            case Code.INT8: // signed int 8
                byte i8 = readByte();
                return i8;
            case Code.INT16: // signed int 16
                short i16 = readShort();
                return i16;
            case Code.INT32: // signed int 32
                int i32 = readInt();
                return i32;
            case Code.INT64: // signed int 64
                long i64 = readLong();
                if (i64 < (long) Integer.MIN_VALUE || i64 > (long) Integer.MAX_VALUE) {
                    throw overflowI64(i64);
                }
                return (int) i64;
        }
        throw unexpectedHeadByte("Integer", b);

    }

    public long unpackLong() throws IOException {
        final byte b = lookAhead();
        if (Code.isFixInt(b)) {
            consume();
            return (long) b;
        }
        switch (b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                return (long) (u8 & 0xff);
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                return (long) (u16 & 0xffff);
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if (u32 < 0) {
                    return (long) (u32 & 0x7fffffff) + 0x80000000L;
                } else {
                    return (long) u32;
                }
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if (u64 < 0L) {
                    throw overflowU64(u64);
                }
                return u64;
            case Code.INT8: // signed int 8
                byte i8 = readByte();
                return (long) i8;
            case Code.INT16: // signed int 16
                short i16 = readShort();
                return (long) i16;
            case Code.INT32: // signed int 32
                int i32 = readInt();
                return (long) i32;
            case Code.INT64: // signed int 64
                long i64 = readLong();
                return i64;
        }
        throw unexpectedHeadByte("Integer", b);

    }

    public BigInteger unpackBigInteger() throws IOException {
        final byte b = lookAhead();
        if (Code.isFixInt(b)) {
            consume();
            return BigInteger.valueOf((long) b);
        }
        switch (b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                return BigInteger.valueOf((long) (u8 & 0xff));
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                return BigInteger.valueOf((long) (u16 & 0xffff));
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if (u32 < 0) {
                    return BigInteger.valueOf((long) (u32 & 0x7fffffff) + 0x80000000L);
                } else {
                    return BigInteger.valueOf((long) u32);
                }
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if (u64 < 0L) {
                    BigInteger bi = BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
                    return bi;
                } else {
                    return BigInteger.valueOf(u64);
                }
            case Code.INT8: // signed int 8
                byte i8 = readByte();
                return BigInteger.valueOf((long) i8);
            case Code.INT16: // signed int 16
                short i16 = readShort();
                return BigInteger.valueOf((long) i16);
            case Code.INT32: // signed int 32
                int i32 = readInt();
                return BigInteger.valueOf((long) i32);
            case Code.INT64: // signed int 64
                long i64 = readLong();
                return BigInteger.valueOf(i64);
        }
        throw unexpectedHeadByte("Integer", b);
    }

    public float unpackFloat() throws IOException {
        final byte b = lookAhead();
        switch (b) {
            case Code.FLOAT32: // float
                float fv = readFloat();
                return fv;
            case Code.FLOAT64: // double
                double dv = readDouble();
                return (float) dv;
        }
        throw unexpectedHeadByte("Float", b);

    }

    public double unpackDouble() throws IOException {
        final byte b = lookAhead();
        switch (b) {
            case Code.FLOAT32: // float
                float fv = readFloat();
                return (double) fv;
            case Code.FLOAT64: // double
                double dv = readDouble();
                return dv;
        }
        throw unexpectedHeadByte("Float", b);
    }

    public String unpackString() throws IOException {
        int strLen = unpackRawStringHeader();
        ensure(strLen);
        ByteBuffer bb = buffer.toByteBuffer(position, strLen);
        consume(strLen);
        return getCharsetDecoder().decode(bb).toString();
    }


    public int unpackArrayHeader() throws IOException {
        final byte b = lookAhead();
        if (Code.isFixedArray(b)) { // fixarray
            consume();
            return b & 0x0f;
        }
        switch (b) {
            case Code.ARRAY16: // array 16
                return readNextLength16();
            case Code.ARRAY32: // array 32
                return readNextLength32();
        }
        throw unexpectedHeadByte("Array", b);
    }

    public int unpackMapHeader() throws IOException {
        final byte b = lookAhead();
        if (Code.isFixedMap(b)) { // fixmap
            consume();
            return b & 0x0f;
        }
        switch (b) {
            case Code.MAP16: // map 16
                return readNextLength16();
            case Code.MAP32: // map 32
                return readNextLength32();
        }
        throw unexpectedHeadByte("Map", b);
    }

    public MessagePack.ExtendedTypeHeader unpackExtendedTypeHeader() throws IOException {
        // TODO
        return null;
    }

    public int unpackRawStringHeader() throws IOException {
        final byte b = lookAhead();
        consume();
        if (Code.isFixedRaw(b)) { // FixRaw
            return b & 0x1f;
        }
        switch (b) {
            case Code.STR8: // str 8
                return readNextLength8();
            case Code.STR16: // str 16
                return readNextLength16();
            case Code.STR32: // str 32
                return readNextLength32();
        }
        throw unexpectedHeadByte("String", b);
    }
    public int unpackBinaryHeader() throws IOException {
        // TODO option to allow str format family
        final byte b = lookAhead();
        switch (b) {
            case Code.BIN8: // bin 8
                return readNextLength8();
            case Code.BIN16: // bin 16
                return readNextLength16();
            case Code.BIN32: // bin 32
                return readNextLength32();
        }
        throw unexpectedHeadByte("Binary", b);
    }

    public void readPayload(ByteBuffer dst) throws IOException {

    }

    public void readPayload(byte[] dst, int off, int len) throws IOException {

    }

    // TODO returns a buffer reference to the payload (zero-copy)
    // public long readPayload(...)



    int readNextLength8() throws IOException {
        if (nextSize >= 0) {
            return nextSize;
        }
        byte u8 = readByte();
        return nextSize = u8 & 0xff;
    }

    int readNextLength16() throws IOException {
        if (nextSize >= 0) {
            return nextSize;
        }
        short u16 = readShort();
        return nextSize = u16 & 0xff;
    }

    int readNextLength32() throws IOException {
        if (nextSize >= 0) {
            return nextSize;
        }
        int u32 = readInt();
        if (u32 < 0) {
            throw overflowU32Size(u32);
        }
        return nextSize = u32;
    }

    @Override
    public void close() throws IOException {
        // TODO
    }

    private static IntegerOverflowException overflowU8(final byte u8) {
        final BigInteger bi = BigInteger.valueOf((long) (u8 & 0xff));
        return new IntegerOverflowException(bi);
    }

    private static IntegerOverflowException overflowU16(final short u16) {
        final BigInteger bi = BigInteger.valueOf((long) (u16 & 0xffff));
        return new IntegerOverflowException(bi);
    }
    private static IntegerOverflowException overflowU32(final int u32) {
        final BigInteger bi = BigInteger.valueOf((long) (u32 & 0x7fffffff) + 0x80000000L);
        return new IntegerOverflowException(bi);
    }

    private static IntegerOverflowException overflowU64(final long u64) {
        final BigInteger bi = BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
        return new IntegerOverflowException(bi);
    }

    private static IntegerOverflowException overflowI16(final short i16) {
        final BigInteger bi = BigInteger.valueOf((long) i16);
        return new IntegerOverflowException(bi);
    }

    private static IntegerOverflowException overflowI32(final int i32) {
        final BigInteger bi = BigInteger.valueOf((long) i32);
        return new IntegerOverflowException(bi);
    }

    private static IntegerOverflowException overflowI64(final long i64) {
        final BigInteger bi = BigInteger.valueOf(i64);
        return new IntegerOverflowException(bi);
    }

    private static MessageSizeLimitException overflowU32Size(final int u32) {
        final long lv = (long) (u32 & 0x7fffffff) + 0x80000000L;
        return new MessageSizeLimitException(lv);
    }


}
