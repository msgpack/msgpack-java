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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

public class MessageUnpackerImpl /*implements Unpacker */{
    public static class Options {
        //// upcast raw type  // default:true : getNextType returns RAW?
        // allow readBinary and readBinaryLength to read str types  // default:true
        // string decode malformed input action  // default:report
        // string decode unmappable character action  // default:report        // byte buffer bulk allocation cache size
        // byte buffer allocator
        // allow byte buffer reference : advanced option
        // raw size limit
    }

    private static final byte HEAD_BYTE_NEVER_USED_TYPE = (byte) 0xc1;
    private static final byte REQUIRE_TO_READ_HEAD_BYTE = HEAD_BYTE_NEVER_USED_TYPE;
    private static final int REQUIRE_TO_READ_SIZE = -1;

    private static Charset UTF_8 = Charset.forName("UTF-8");


    //
    // options
    //

    private final MessageUnpackerChannel in;

    //private final int maxRawStringSize = Integer.MAX_VALUE;
    //private final int maxBinarySize = Integer.MAX_VALUE;
    //private final int maxArraySize = Integer.MAX_VALUE;
    //private final int maxMapSize = Integer.MAX_VALUE;

    //
    // state
    //

    private byte headByte = REQUIRE_TO_READ_HEAD_BYTE;

    private int nextSize = REQUIRE_TO_READ_SIZE;

    private CharsetDecoder decoder;

    public MessageUnpackerImpl(MessageUnpackerChannel in) {
        this.in = in;
    }

    private CharsetDecoder getCharsetDecoder() {
        // TODO options
        CharsetDecoder d = decoder;
        if (d == null) {
            d = decoder = UTF_8.newDecoder();
        }
        return d;
    }

    private byte getHeadByte() throws IOException {
        byte b = headByte;
        if (b == REQUIRE_TO_READ_HEAD_BYTE) {
            b = headByte = in.readByte();
            if (b == HEAD_BYTE_NEVER_USED_TYPE) {
                throw new MessageMalformedFormatException("Invalid format byte: " + b);
            }
        }
        return b;
    }

    //private byte readHeadByte() throws IOException {
    //    if (headByte == REQUIRE_TO_READ_HEAD_BYTE) {
    //        return in.readByte();
    //    }
    //    final byte b = in.readByte();
    //    headByte = REQUIRE_TO_READ_HEAD_BYTE;
    //    return b;
    //}

    private static ValueType getTypeFromHeadByte(final byte b) throws MessageMalformedFormatException {
        if ((b & 0x80) == 0) { // positive fixint
            return ValueType.INTEGER;
        }
        if ((b & 0xe0) == 0xe0) { // negative fixint
            return ValueType.INTEGER;
        }
        if ((b & 0xe0) == 0xa0) { // fixstr
            return ValueType.STRING;
        }
        if ((b & 0xf0) == 0x90) { // fixarray
            return ValueType.ARRAY;
        }
        if ((b & 0xf0) == 0x80) { // fixmap
            return ValueType.MAP;
        }
        switch (b & 0xff) {
        case 0xc0: // nil
            return ValueType.NIL;
        case 0xc2: // false
        case 0xc3: // true
            return ValueType.BOOLEAN;
        case 0xc4: // bin 8
        case 0xc5: // bin 16
        case 0xc6: // bin 32
            return ValueType.BINARY;
        case 0xc7: // ext 8
        case 0xc8: // ext 16
        case 0xc9: // ext 32
            return ValueType.EXTENDED;
        case 0xca: // float 32
        case 0xcb: // float 64
            return ValueType.FLOAT;
        case 0xcc: // unsigned int 8
        case 0xcd: // unsigned int 16
        case 0xce: // unsigned int 32
        case 0xcf: // unsigned int 64
        case 0xd0: // signed int 8
        case 0xd1: // signed int 16
        case 0xd2: // signed int 32
        case 0xd3: // signed int 64
            return ValueType.INTEGER;
        case 0xd4: // fixext 1
        case 0xd5: // fixext 2
        case 0xd6: // fixext 4
        case 0xd7: // fixext 8
        case 0xd8: // fixext 16
            return ValueType.EXTENDED;
        case 0xd9: // str 8
        case 0xda: // str 16
        case 0xdb: // str 32
            return ValueType.STRING;
        case 0xdc: // array 16
        case 0xdd: // array 32
            return ValueType.ARRAY;
        case 0xde: // map 16
        case 0xdf: // map 32
            return ValueType.MAP;
        default:
            throw new MessageMalformedFormatException("Invalid format byte: " + b);
        }
    }

    public ValueType getNextType() throws IOException {
        return getTypeFromHeadByte(getHeadByte());
    }

    public boolean trySkipNil() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0xff) == 0xc0) {
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return true;
        }
        return false;
    }

    public void readNil() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0xff) == 0xc0) {
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return;
        }
        throw unexpectedHeadByte("Nil", b);
    }

    private static MessageTypeCastException unexpectedHeadByte(final String expectedTypeName, final byte b)
                throws MessageMalformedFormatException {
        ValueType type = getTypeFromHeadByte(b);
        String name = type.name();
        return new MessageTypeCastException(
                "Expected " + expectedTypeName + " type but got " +
                name.substring(0, 1) + name.substring(1).toLowerCase() + " type");
    }

    private final byte readByteAndResetHeadByte() throws IOException {
        byte v = in.readByte();
        headByte = REQUIRE_TO_READ_HEAD_BYTE;
        return v;
    }

    private final short readShortAndResetHeadByte() throws IOException {
        short v = in.readShort();
        headByte = REQUIRE_TO_READ_HEAD_BYTE;
        return v;
    }

    private final int readIntAndResetHeadByte() throws IOException {
        int v = in.readInt();
        headByte = REQUIRE_TO_READ_HEAD_BYTE;
        return v;
    }

    private final long readLongAndResetHeadByte() throws IOException {
        long v = in.readLong();
        headByte = REQUIRE_TO_READ_HEAD_BYTE;
        return v;
    }

    private final float readFloatAndResetHeadByte() throws IOException {
        float v = in.readFloat();
        headByte = REQUIRE_TO_READ_HEAD_BYTE;
        return v;
    }

    private final double readDoubleAndResetHeadByte() throws IOException {
        double v = in.readDouble();
        headByte = REQUIRE_TO_READ_HEAD_BYTE;
        return v;
    }

    public byte readByte() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0x80) == 0) {
            // positive fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return b;
        }
        if ((b & 0xe0) == 0xe0) {
            // negative fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return b;
        }
        switch (b & 0xff) {
        case 0xcc: // unsigned int 8
            byte u8 = readByteAndResetHeadByte();
            if (u8 < (byte) 0) {
                throw overflowU8(u8);
            }
            return u8;
        case 0xcd: // unsigned int 16
            short u16 = readShortAndResetHeadByte();
            if (u16 < (short) 0 || u16 > (short) Byte.MAX_VALUE) {
                throw overflowU16(u16);
            }
            return (byte) u16;
        case 0xce: // unsigned int 32
            int u32 = readIntAndResetHeadByte();
            if (u32 < 0 || u32 > (int) Byte.MAX_VALUE) {
                throw overflowU32(u32);
            }
            return (byte) u32;
        case 0xcf: // unsigned int 64
            long u64 = readLongAndResetHeadByte();
            if (u64 < 0L || u64 > (long) Byte.MAX_VALUE) {
                throw overflowU64(u64);
            }
            return (byte) u64;
        case 0xd0: // signed int 8
            byte i8 = readByteAndResetHeadByte();
            return i8;
        case 0xd1: // signed int 16
            short i16 = readShortAndResetHeadByte();
            if (i16 < (short) Byte.MIN_VALUE || i16 > (short) Byte.MAX_VALUE) {
                throw overflowI16(i16);
            }
            return (byte) i16;
        case 0xd2: // signed int 32
            int i32 = readIntAndResetHeadByte();
            if (i32 < (int) Byte.MIN_VALUE || i32 > (int) Byte.MAX_VALUE) {
                throw overflowI32(i32);
            }
            return (byte) i32;
        case 0xd3: // signed int 64
            long i64 = readLongAndResetHeadByte();
            if (i64 < (long) Byte.MIN_VALUE || i64 > (long) Byte.MAX_VALUE) {
                throw overflowI64(i64);
            }
            return (byte) i64;
        }
        throw unexpectedHeadByte("Integer", b);
    }

    public short readShort() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0x80) == 0) {
            // positive fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return (short) b;
        }
        if ((b & 0xe0) == 0xe0) {
            // negative fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return (short) b;
        }
        switch (b & 0xff) {
        case 0xcc: // unsigned int 8
            byte u8 = readByteAndResetHeadByte();
            return (short) (u8 & 0xff);
        case 0xcd: // unsigned int 16
            short u16 = readShortAndResetHeadByte();
            if (u16 < (short) 0) {
                throw overflowU16(u16);
            }
            return u16;
        case 0xce: // unsigned int 32
            int u32 = readIntAndResetHeadByte();
            if (u32 < 0 || u32 > (int) Short.MAX_VALUE) {
                throw overflowU32(u32);
            }
            return (short) u32;
        case 0xcf: // unsigned int 64
            long u64 = readLongAndResetHeadByte();
            if (u64 < 0L || u64 > (long) Short.MAX_VALUE) {
                throw overflowU64(u64);
            }
            return (short) u64;
        case 0xd0: // signed int 8
            byte i8 = readByteAndResetHeadByte();
            return (short) i8;
        case 0xd1: // signed int 16
            short i16 = readShortAndResetHeadByte();
            return i16;
        case 0xd2: // signed int 32
            int i32 = readIntAndResetHeadByte();
            if (i32 < (int) Short.MIN_VALUE || i32 > (int) Short.MAX_VALUE) {
                throw overflowI32(i32);
            }
            return (short) i32;
        case 0xd3: // signed int 64
            long i64 = readLongAndResetHeadByte();
            if (i64 < (long) Short.MIN_VALUE || i64 > (long) Short.MAX_VALUE) {
                throw overflowI64(i64);
            }
            return (short) i64;
        }
        throw unexpectedHeadByte("Integer", b);
    }

    public int readInt() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0x80) == 0) {
            // positive fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return (int) b;
        }
        if ((b & 0xe0) == 0xe0) {
            // negative fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return (int) b;
        }
        switch (b & 0xff) {
        case 0xcc: // unsigned int 8
            byte u8 = readByteAndResetHeadByte();
            return u8 & 0xff;
        case 0xcd: // unsigned int 16
            short u16 = readShortAndResetHeadByte();
            return u16 & 0xffff;
        case 0xce: // unsigned int 32
            int u32 = readIntAndResetHeadByte();
            if (u32 < 0) {
                throw overflowU32(u32);
            }
            return u32;
        case 0xcf: // unsigned int 64
            long u64 = readLongAndResetHeadByte();
            if (u64 < 0L || u64 > (long) Integer.MAX_VALUE) {
                throw overflowU64(u64);
            }
            return (int) u64;
        case 0xd0: // signed int 8
            byte i8 = readByteAndResetHeadByte();
            return i8;
        case 0xd1: // signed int 16
            short i16 = readShortAndResetHeadByte();
            return i16;
        case 0xd2: // signed int 32
            int i32 = readIntAndResetHeadByte();
            return i32;
        case 0xd3: // signed int 64
            long i64 = readLongAndResetHeadByte();
            if (i64 < (long) Integer.MIN_VALUE || i64 > (long) Integer.MAX_VALUE) {
                throw overflowI64(i64);
            }
            return (int) i64;
        }
        throw unexpectedHeadByte("Integer", b);
    }

    public long readLong() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0x80) == 0) {
            // positive fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return (long) b;
        }
        if ((b & 0xe0) == 0xe0) {
            // negative fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return (long) b;
        }
        switch (b & 0xff) {
        case 0xcc: // unsigned int 8
            byte u8 = readByteAndResetHeadByte();
            return (long) (u8 & 0xff);
        case 0xcd: // unsigned int 16
            short u16 = readShortAndResetHeadByte();
            return (long) (u16 & 0xffff);
        case 0xce: // unsigned int 32
            int u32 = readIntAndResetHeadByte();
            if (u32 < 0) {
                return (long) (u32 & 0x7fffffff) + 0x80000000L;
            } else {
                return (long) u32;
            }
        case 0xcf: // unsigned int 64
            long u64 = readLongAndResetHeadByte();
            if (u64 < 0L) {
                throw overflowU64(u64);
            }
            return u64;
        case 0xd0: // signed int 8
            byte i8 = readByteAndResetHeadByte();
            return (long) i8;
        case 0xd1: // signed int 16
            short i16 = readShortAndResetHeadByte();
            return (long) i16;
        case 0xd2: // signed int 32
            int i32 = readIntAndResetHeadByte();
            return (long) i32;
        case 0xd3: // signed int 64
            long i64 = readLongAndResetHeadByte();
            return i64;
        }
        throw unexpectedHeadByte("Integer", b);
    }

    public BigInteger readBigInteger() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0x80) == 0) {
            // positive fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return BigInteger.valueOf((long) b);
        }
        if ((b & 0xe0) == 0xe0) {
            // negative fixint
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return BigInteger.valueOf((long) b);
        }
        switch (b & 0xff) {
        case 0xcc: // unsigned int 8
            byte u8 = readByteAndResetHeadByte();
            return BigInteger.valueOf((long) (u8 & 0xff));
        case 0xcd: // unsigned int 16
            short u16 = readShortAndResetHeadByte();
            return BigInteger.valueOf((long) (u16 & 0xffff));
        case 0xce: // unsigned int 32
            int u32 = readIntAndResetHeadByte();
            if (u32 < 0) {
                return BigInteger.valueOf((long) (u32 & 0x7fffffff) + 0x80000000L);
            } else {
                return BigInteger.valueOf((long) u32);
            }
        case 0xcf: // unsigned int 64
            long u64 = readLongAndResetHeadByte();
            if (u64 < 0L) {
                BigInteger bi = BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
                return bi;
            } else {
                return BigInteger.valueOf(u64);
            }
        case 0xd0: // signed int 8
            byte i8 = readByteAndResetHeadByte();
            return BigInteger.valueOf((long) i8);
        case 0xd1: // signed int 16
            short i16 = readShortAndResetHeadByte();
            return BigInteger.valueOf((long) i16);
        case 0xd2: // signed int 32
            int i32 = readIntAndResetHeadByte();
            return BigInteger.valueOf((long) i32);
        case 0xd3: // signed int 64
            long i64 = readLongAndResetHeadByte();
            return BigInteger.valueOf(i64);
        }
        throw unexpectedHeadByte("Integer", b);
    }

    public float readFloat() throws IOException {
        final byte b = getHeadByte();
        switch (b & 0xff) {
        case 0xca: // float
            float fv = readFloatAndResetHeadByte();
            return fv;
        case 0xcb: // double
            double dv = readFloatAndResetHeadByte();
            return (float) dv;
        }
        throw unexpectedHeadByte("Float", b);
    }

    public double readDouble() throws IOException {
        final byte b = getHeadByte();
        switch (b & 0xff) {
        case 0xca: // float
            float fv = readFloatAndResetHeadByte();
            return (double) fv;
        case 0xcb: // double
            double dv = readFloatAndResetHeadByte();
            return dv;
        }
        throw unexpectedHeadByte("Float", b);
    }


    private static MessageTypeIntegerOverflowException overflowU8(final byte u8) {
        final BigInteger bi = BigInteger.valueOf((long) (u8 & 0xff));
        return new MessageTypeIntegerOverflowException(bi);
    }

    private static MessageTypeIntegerOverflowException overflowU16(final short u16) {
        final BigInteger bi = BigInteger.valueOf((long) (u16 & 0xffff));
        return new MessageTypeIntegerOverflowException(bi);
    }

    private static MessageTypeIntegerOverflowException overflowU32(final int u32) {
        final BigInteger bi = BigInteger.valueOf((long) (u32 & 0x7fffffff) + 0x80000000L);
        return new MessageTypeIntegerOverflowException(bi);
    }

    private static MessageTypeIntegerOverflowException overflowU64(final long u64) {
        final BigInteger bi = BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
        return new MessageTypeIntegerOverflowException(bi);
    }

    private static MessageTypeIntegerOverflowException overflowI16(final short i16) {
        final BigInteger bi = BigInteger.valueOf((long) i16);
        return new MessageTypeIntegerOverflowException(bi);
    }

    private static MessageTypeIntegerOverflowException overflowI32(final int i32) {
        final BigInteger bi = BigInteger.valueOf((long) i32);
        return new MessageTypeIntegerOverflowException(bi);
    }

    private static MessageTypeIntegerOverflowException overflowI64(final long i64) {
        final BigInteger bi = BigInteger.valueOf(i64);
        return new MessageTypeIntegerOverflowException(bi);
    }

    private static MessageSizeLimitException overflowU32Size(final int u32) {
        final long lv = (long) (u32 & 0x7fffffff) + 0x80000000L;
        return new MessageSizeLimitException(lv);
    }


    private int getNextLength8() throws IOException {
        if (nextSize >= 0) {
            return nextSize;
        }
        byte u8 = readByteAndResetHeadByte();
        return nextSize = u8 & 0xff;
    }

    private int getNextLength16() throws IOException {
        if (nextSize >= 0) {
            return nextSize;
        }
        short u16 = readShortAndResetHeadByte();
        return nextSize = u16 & 0xff;
    }

    private int getNextLength32() throws IOException {
        if (nextSize >= 0) {
            return nextSize;
        }
        int u32 = readIntAndResetHeadByte();
        if (u32 < 0) {
            throw overflowU32Size(u32);
        }
        return nextSize = u32;
    }

    private int getRawStringLength() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0xe0) == 0xa0) { // FixRaw
            return b & 0x1f;
        }
        switch (b & 0xff) {
        case 0xd9: // str 8
            return getNextLength8();
        case 0xda: // str 16
            return getNextLength16();
        case 0xdb: // str 32
            return getNextLength32();
        }
        throw unexpectedHeadByte("String", b);
    }

    public int readRawStringLength() throws IOException {
        int size = getRawStringLength();
        nextSize = REQUIRE_TO_READ_SIZE;
        return size;
    }

    private int getBinaryLength() throws IOException {
        // TODO option to allow str format family
        final byte b = getHeadByte();
        switch (b & 0xff) {
        case 0xc4: // bin 8
            return getNextLength8();
        case 0xc5: // bin 16
            return getNextLength16();
        case 0xc6: // bin 32
            return getNextLength32();
        }
        throw unexpectedHeadByte("Binary", b);
    }

    public int readBinaryLength() throws IOException {
        int size = getBinaryLength();
        nextSize = REQUIRE_TO_READ_SIZE;
        return size;
    }

    private int getArrayHeader() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0xf0) == 0x90) { // fixarray
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return b & 0x0f;
        }
        switch (b & 0xff) {
        case 0xdc: // array 16
            return getNextLength16();
        case 0xdd: // array 32
            return getNextLength32();
        }
        throw unexpectedHeadByte("Array", b);
    }

    public int readArrayHeader() throws IOException {
        int size = getArrayHeader();
        nextSize = REQUIRE_TO_READ_SIZE;
        return size;
    }

    private int getMapHeader() throws IOException {
        final byte b = getHeadByte();
        if ((b & 0xf0) == 0x80) { // fixmap
            headByte = REQUIRE_TO_READ_HEAD_BYTE;
            return b & 0x0f;
        }
        switch (b & 0xff) {
        case 0xde: // map 16
            return getNextLength16();
        case 0xdf: // map 32
            return getNextLength32();
        }
        throw unexpectedHeadByte("Map", b);
    }

    public int readMapHeader() throws IOException {
        int size = getMapHeader();
        nextSize = REQUIRE_TO_READ_SIZE;
        return size;
    }

    public String readString() throws IOException {
        // TODO cache CharacterBuffer
        ByteBuffer bb = readRawString();
        return getCharsetDecoder().decode(bb).toString();
    }

    public ByteBuffer readRawString() throws IOException {
        final int size = getRawStringLength();
        ByteBuffer bb =  in.readAll(size);
        nextSize = REQUIRE_TO_READ_SIZE;
        return bb;
    }

    public ByteBuffer readBinary() throws IOException {
        final int size = getBinaryLength();
        ByteBuffer bb = in.readAll(size);
        nextSize = REQUIRE_TO_READ_SIZE;
        return bb;
    }
}
