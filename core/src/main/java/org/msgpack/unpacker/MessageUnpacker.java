//
// MessagePack for Java
//
// Copyright (C) 2009-2013 FURUHASHI Sadayuki
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
package org.msgpack.unpacker;

import java.io.IOException;
import java.io.EOFException;
import java.math.BigInteger;
import org.msgpack.value.ValueType;
import org.msgpack.unpacker.accept.Accept;
import org.msgpack.unpacker.accept.ByteAccept;
import org.msgpack.unpacker.accept.ShortAccept;
import org.msgpack.unpacker.accept.IntAccept;
import org.msgpack.unpacker.accept.LongAccept;
import org.msgpack.unpacker.accept.BigIntegerAccept;
import org.msgpack.unpacker.accept.FloatAccept;
import org.msgpack.unpacker.accept.DoubleAccept;
import org.msgpack.unpacker.accept.BooleanAccept;
import org.msgpack.unpacker.accept.NilAccept;
import org.msgpack.unpacker.accept.ByteArrayAccept;
import org.msgpack.unpacker.accept.StringAccept;
import org.msgpack.unpacker.accept.ArrayAccept;
import org.msgpack.unpacker.accept.MapAccept;
import org.msgpack.unpacker.accept.ExtAccept;

public class MessageUnpacker implements Unpacker {
    private UnpackerChannel in;

    protected int rawSizeLimit = 134217728;
    protected int arraySizeLimit = 4194304;
    protected int mapSizeLimit = 2097152;

    private byte headByte = REQUIRE_TO_READ_HEAD;

    private byte[] raw;
    private int rawFilled;

    // TODO FIXME Wow, use of this class may not be thread-safe because these Accept instances are statefull.
    private static final ByteAccept byteAccept = new ByteAccept();
    private static final ShortAccept shortAccept = new ShortAccept();
    private static final IntAccept intAccept = new IntAccept();
    private static final LongAccept longAccept = new LongAccept();
    private static final BigIntegerAccept bigIntegerAccept = new BigIntegerAccept();
    private static final FloatAccept floatAccept = new FloatAccept();
    private static final DoubleAccept doubleAccept = new DoubleAccept();
    private static final BooleanAccept booleanAccept = new BooleanAccept();
    private static final NilAccept nilAccept = new NilAccept();
    private static final ByteArrayAccept byteArrayAccept = new ByteArrayAccept();
    private static final StringAccept stringAccept = new StringAccept();
    private static final ArrayAccept arrayAccept = new ArrayAccept();
    private static final MapAccept mapAccept = new MapAccept();
    private static final ExtAccept extAccept = new ExtAccept();

    public MessageUnpacker(UnpackerChannel in) {
        this.in = in;
    }

    // TODO
    //public MessageUnpacker(ReadableByteChannel in) {
    //    this(new ReadableByteChannelUnpackerChannel(in));
    //}

    // TODO
    //public MessageUnpacker(InputStream in) {
    //    this(new InputStreamChannelUnpackerChannel(in));
    //}

    private static final byte REQUIRE_TO_READ_HEAD = (byte) 0xc6;

    private void resetHeadByte() {
        headByte = REQUIRE_TO_READ_HEAD;
    }

    private byte getHeadByte() throws IOException {
        byte b = headByte;
        if(b == REQUIRE_TO_READ_HEAD) {
            b = headByte = in.readByte();
        }
        return b;
    }

    public void readToken(Accept a) throws IOException {
        if(raw != null) {
            readRawBodyCont();
            a.acceptByteArray(raw);
            raw = null;
            resetHeadByte();
            return;
        }

        final int b = (int) getHeadByte();

        if((b & 0x80) == 0) { // Positive Fixnum
            // System.out.println("positive fixnum "+b);
            a.acceptInt(b);
            resetHeadByte();
            return;
        }

        if((b & 0xe0) == 0xe0) { // Negative Fixnum
            // System.out.println("negative fixnum "+b);
            a.acceptInt(b);
            resetHeadByte();
            return;
        }

        if((b & 0xe0) == 0xa0) { // FixRaw
            int size = b & 0x1f;
            if(size == 0) {
                a.acceptEmptyByteArray();
                resetHeadByte();
                return;
            }
            readRawBody(size);
            a.acceptByteArray(raw);
            resetHeadByte();
            return;
        }

        if((b & 0xf0) == 0x90) { // FixArray
            int size = b & 0x0f;
            // System.out.println("fixarray size:"+size);
            checkArraySize(size);
            a.acceptArrayHeader(size);
            resetHeadByte();
            return;
        }

        if((b & 0xf0) == 0x80) { // FixMap
            int size = b & 0x0f;
            // System.out.println("fixmap size:"+size/2);
            checkMapSize(size);
            a.acceptMapHeader(size);
            resetHeadByte();
            return;
        }

        readTokenSwitch(a, b);
    }

    private void readTokenSwitch(Accept a, final int b) throws IOException {
        switch (b & 0xff) {
        case 0xc0: // nil
            a.acceptNil();
            resetHeadByte();
            return;
        case 0xc2: // false
            a.acceptBoolean(false);
            resetHeadByte();
            return;
        case 0xc3: // true
            a.acceptBoolean(true);
            resetHeadByte();
            return;
        case 0xca: // float
            a.acceptFloat(in.readFloat());
            resetHeadByte();
            return;
        case 0xcb: // double
            a.acceptDouble(in.readDouble());
            resetHeadByte();
            return;
        case 0xcc: // unsigned int 8
            a.acceptInt((int) (in.readByte() & 0xff));
            resetHeadByte();
            return;
        case 0xcd: // unsigned int 16
            a.acceptInt((int) (in.readShort() & 0xffff));
            resetHeadByte();
            return;
        case 0xce: // unsigned int 32
            {
                int v = in.readInt();
                if(v < 0) {
                    a.acceptLong((long) (v & 0x7fffffff) + 0x80000000L);
                } else {
                    a.acceptInt(v);
                }
            }
            return;
        case 0xcf: // unsigned int 64
            {
                long v = in.readLong();
                if(v < 0) {
                    a.acceptUnsignedLong(v);
                } else {
                    a.acceptLong(v);
                }
            }
            resetHeadByte();
            return;
        case 0xd0: // signed int 8
            a.acceptInt((int) in.readByte());
            resetHeadByte();
            return;
        case 0xd1: // signed int 16
            a.acceptInt((int) in.readShort());
            resetHeadByte();
            return;
        case 0xd2: // signed int 32
            a.acceptInt(in.readInt());
            resetHeadByte();
            return;
        case 0xd3: // signed int 64
            a.acceptLong(in.readLong());
            resetHeadByte();
            return;
        case 0xda: // raw 16
            {
                int size = in.readShort() & 0xffff;
                if(size == 0) {
                    a.acceptEmptyByteArray();
                    resetHeadByte();
                    return;
                }
                readRawBody(size);
            }
            a.acceptByteArray(raw);
            resetHeadByte();
            return;
        case 0xdb: // raw 32
            {
                int size = in.readInt();
                if(size == 0) {
                    a.acceptEmptyByteArray();
                    resetHeadByte();
                    return;
                }
                readRawBody(size);
            }
            a.acceptByteArray(raw);
            raw = null;
            resetHeadByte();
            return;
        case 0xdc: // array 16
            {
                int size = in.readShort() & 0xffff;
                checkArraySize(size);
                a.acceptArrayHeader(size);
                resetHeadByte();
                return;
            }
        case 0xdd: // array 32
            {
                int size = in.readInt();
                checkArraySize(size);
                a.acceptArrayHeader(size);
                resetHeadByte();
                return;
            }
        case 0xde: // map 16
            {
                int size = in.readShort() & 0xffff;
                checkMapSize(size);
                a.acceptMapHeader(size);
                resetHeadByte();
                return;
            }
        case 0xdf: // map 32
            {
                int size = in.readInt();
                checkMapSize(size);
                a.acceptMapHeader(size);
                resetHeadByte();
                return;
            }
        case 0xd4: // fixext 1
            {
                byte type = in.readByte();
                readRawBody(1);
                a.acceptExt(type, raw);
                resetHeadByte();
                return;
            }
        case 0xd5: // fixext 2
            {
                byte type = in.readByte();
                readRawBody(2);
                a.acceptExt(type, raw);
                resetHeadByte();
                return;
            }
        case 0xd6: // fixext 4
            {
                byte type = in.readByte();
                readRawBody(4);
                a.acceptExt(type, raw);
                resetHeadByte();
                return;
            }
        case 0xd7: // fixext 8
            {
                byte type = in.readByte();
                readRawBody(8);
                a.acceptExt(type, raw);
                resetHeadByte();
                return;
            }
        case 0xd8: // fixext 16
            {
                byte type = in.readByte();
                readRawBody(16);
                a.acceptExt(type, raw);
                resetHeadByte();
                return;
            }
        case 0xc7: // ext 8
            {
                int length = in.readByte() & 0xFF;
                byte type = in.readByte();
                readRawBody(length);
                a.acceptExt(type, raw);
                resetHeadByte();
                return;
            }
        case 0xc8: // ext 16
            {
                int length = in.readShort() & 0xFFFF;
                byte type = in.readByte();
                readRawBody(length);
                a.acceptExt(type, raw);
                resetHeadByte();
                return;
            }
        case 0xc9: // ext 32
            {
                // TODO Array length is limited upto 0x7FFFFFFF in Java
                int length = in.readInt();
                byte type = in.readByte();
                readRawBody(length);
                a.acceptExt(type, raw);
                resetHeadByte();
                return;
            }
        default:
            // System.out.println("unknown b "+(b&0xff));
            // headByte = CS_INVALID
            //resetHeadByte();
            throw new IOException("Invalid MessagePack format: " + b);
        }
    }

    private void checkArraySize(int unsignedSize) throws IOException {
        if(unsignedSize < 0 || unsignedSize >= arraySizeLimit) {
            String reason = String.format(
                    "Size of array (%d) over limit at %d",
                    new Object[] { unsignedSize, arraySizeLimit });
            throw new MessageSizeException(reason);
        }
    }

    private void checkMapSize(int unsignedSize) throws IOException {
        if(unsignedSize < 0 || unsignedSize >= mapSizeLimit) {
            String reason = String.format(
                    "Size of map (%d) over limit at %d",
                    new Object[] { unsignedSize, mapSizeLimit });
            throw new MessageSizeException(reason);
        }
    }

    private void checkRawSize(int unsignedSize) throws IOException {
        if(unsignedSize < 0 || unsignedSize >= rawSizeLimit) {
            String reason = String.format(
                    "Size of raw (%d) over limit at %d",
                    new Object[] { unsignedSize, rawSizeLimit });
            throw new MessageSizeException(reason);
        }
    }

    private void readRawBody(int size) throws IOException {
        checkRawSize(size);
        this.raw = new byte[size];
        this.rawFilled = 0;
        readRawBodyCont();
    }

    private void readRawBodyCont() throws IOException {
        int len = in.read(raw, rawFilled, raw.length - rawFilled);
        rawFilled += len;
        if (rawFilled < raw.length) {
            throw new EOFException();
        }
    }

    public boolean tryReadNil() throws IOException {
        int b = getHeadByte();
        if(b == 0xc0) {
            resetHeadByte();
            return true;
        }
        return false;
    }

    public byte readByte() throws IOException {
        readToken(byteAccept);
        return byteAccept.getValue();
    }

    public short readShort() throws IOException {
        readToken(shortAccept);
        return shortAccept.getValue();
    }

    public int readInt() throws IOException {
        readToken(intAccept);
        return intAccept.getValue();
    }

    public long readLong() throws IOException {
        readToken(longAccept);
        return longAccept.getValue();
    }

    public BigInteger readBigInteger() throws IOException {
        readToken(bigIntegerAccept);
        return bigIntegerAccept.getValue();
    }

    public float readFloat() throws IOException {
        readToken(floatAccept);
        return floatAccept.getValue();
    }

    public double readDouble() throws IOException {
        readToken(doubleAccept);
        return doubleAccept.getValue();
    }

    public boolean readBoolean() throws IOException {
        readToken(booleanAccept);
        return booleanAccept.getValue();
    }

    public void readNil() throws IOException {
        readToken(nilAccept);
    }

    public byte[] readByteArray() throws IOException {
        readToken(byteArrayAccept);
        return byteArrayAccept.getValue();
    }

    public String readString() throws IOException {
        readToken(stringAccept);
        return stringAccept.getValue();
    }

    public int readArrayHeader() throws IOException {
        readToken(arrayAccept);
        return arrayAccept.getSize();
    }

    public int readMapHeader() throws IOException {
        readToken(mapAccept);
        return mapAccept.getSize();
    }

    public Ext readExt() throws IOException {
      readToken(extAccept);
      return extAccept.getValue();
    }

    public ValueType getNextType() throws IOException {
        final int b = (int) getHeadByte();
        if ((b & 0x80) == 0) { // Positive Fixnum
            return ValueType.INTEGER;
        }
        if ((b & 0xe0) == 0xe0) { // Negative Fixnum
            return ValueType.INTEGER;
        }
        if ((b & 0xe0) == 0xa0) { // FixRaw
            return ValueType.RAW;
        }
        if ((b & 0xf0) == 0x90) { // FixArray
            return ValueType.ARRAY;
        }
        if ((b & 0xf0) == 0x80) { // FixMap
            return ValueType.MAP;
        }
        switch (b & 0xff) {
        case 0xc0: // nil
            return ValueType.NIL;
        case 0xc2: // false
        case 0xc3: // true
            return ValueType.BOOLEAN;
        case 0xca: // float
        case 0xcb: // double
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
        case 0xda: // raw 16
        case 0xdb: // raw 32
            return ValueType.RAW;
        case 0xdc: // array 16
        case 0xdd: // array 32
            return ValueType.ARRAY;
        case 0xde: // map 16
        case 0xdf: // map 32
            return ValueType.MAP;
        case 0xd4: // fixext 1
        case 0xd5: // fixext 2
        case 0xd6: // fixext 4
        case 0xd7: // fixext 8
        case 0xd8: // fixext 16
        case 0xc7: // ext 8
        case 0xc8: // ext 16
        case 0xc9: // ext 32
            return ValueType.EXT;
        default:
            throw new MessageFormatException("Invalid MessagePack format: " + b);
        }
    }

    public void close() throws IOException {
        in.close();
    }
}

