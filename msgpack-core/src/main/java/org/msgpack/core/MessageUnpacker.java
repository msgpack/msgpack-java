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
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;

import org.msgpack.core.MessagePack.Code;
import org.msgpack.core.buffer.*;

import static org.msgpack.core.Preconditions.*;


/**
 * MessageUnpacker lets an application read message-packed values from a data stream.
 * The application needs to call {@link #getNextFormat()} then call an appropriate unpackXXX method.
 */
public class MessageUnpacker implements Closeable {

    public static class Options {
        // allow unpackBinaryHeader to read str format family  // default:true
        // allow unpackRawStringHeader and unpackString to read bin format family // default: true
        // string decode malformed input action  // default:report
        // string decode unmappable character action  // default:report

        // unpackString size limit // default: Integer.MAX_VALUE
    }

    private final MessageBufferInput in;

    /**
     * Points to the current buffer to read
     */
    private MessageBuffer buffer;
    /**
     * Cursor position in the current buffer
     */
    private int position;

    /**
     * For preserving the next buffer to use
     */
    private MessageBuffer secondaryBuffer = null;

    /**
     * Extra buffer for string data at the buffer boundary. For most of the case having 8 byte buffer is sufficient.
     */
    private final MessageBuffer extraBuffer = MessageBuffer.wrap(new byte[8]);

    /**
     * True if no more data is available from the MessageBufferInput
     */
    private boolean reachedEOF = false;

    /**
     * For decoding String in unpackString.
     * TODO enable options for handling malformed input and unmappable characters
     */
    private final CharsetDecoder decoder = MessagePack.UTF8.newDecoder();

    /**
     * Create an MessageUnpacker that reads data from the given byte array.
     *
     * @param arr
     */
    public MessageUnpacker(byte[] arr) {
        this(new ArrayBufferInput(arr));
    }

    /**
     * Create an MessageUnpacker that reads data from the given InputStream.
     * @param in
     */
    public MessageUnpacker(InputStream in) {
        this(new InputStreamBufferInput(in));
    }

    /**
     * Create an MessageUnpacker that reads data from the given ReadableByteChannel.
     * @param in
     */
    public MessageUnpacker(ReadableByteChannel in) {
        this(new ChannelBufferInput(in));
    }

    /**
     * Create an MessageUnpacker that reads data from the given MessageBufferInput
     *
     * @param in
     */
    public MessageUnpacker(MessageBufferInput in) {
        this.in = checkNotNull(in, "MessageBufferInput");
    }


    /**
     * Relocate the cursor position so that it points to the real buffer
     *
     * @return true if it succeeds to move the cursor, or false if there is no more buffer to read
     * @throws IOException when failed to retrieve next buffer
     */
    private boolean adjustCursorPosition() throws IOException {
        if(buffer == null)
            buffer = takeNextBuffer();

        while(buffer != null && position >= buffer.size()) {
            // Fetch the next buffer
            position -= buffer.size();
            buffer = takeNextBuffer();
        }
        return buffer != null;
    }

    private MessageBuffer takeNextBuffer() throws IOException {
        if(reachedEOF)
            return null;

        MessageBuffer nextBuffer = null;
        if(secondaryBuffer == null) {
            nextBuffer = in.next();
        }
        else {
            nextBuffer = secondaryBuffer;
            secondaryBuffer = null;
        }

        if(nextBuffer == null) {
            reachedEOF = true;
        }
        return nextBuffer;
    }


    /**
     * Ensure the buffer has the data of at least the specified size.
     *
     * @param byteSizeToRead the data size to be read
     * @return if the buffer can have the data of the specified size returns true, or if the input source reached an EOF, it returns false.
     * @throws IOException
     */
    private boolean ensure(int byteSizeToRead) throws IOException {
        if(!adjustCursorPosition())
            return false;


        // The buffer contains the data
        if(position + byteSizeToRead <= buffer.size()) {
            // OK
            return true;
        }

        if(byteSizeToRead <= extraBuffer.size()) {
            // When the data is at the boundary and can fit to the extra buffer
            /*
             -- current buffer --|
             |------ extra buffer -----|
                                 |-----|---------- secondary buffer (slice) ----------------|

             */

            // Copy the remaining buffer contents to the extra buffer
            int firstHalfSize = buffer.size() - position;
            if(firstHalfSize > 0)
                buffer.copyTo(position, extraBuffer, 0, firstHalfSize);

            // Read the last half contents from the next buffers
            int cursor = firstHalfSize;
            while(cursor < byteSizeToRead) {
                secondaryBuffer = takeNextBuffer();
                if(secondaryBuffer == null)
                    return false; // No more buffer to read

                // Copy the contents from the secondary buffer to the extra buffer
                int copyLen = Math.min(byteSizeToRead - cursor, secondaryBuffer.size());
                secondaryBuffer.copyTo(0, extraBuffer, cursor, copyLen);

                // Truncate the copied part from the secondaryBuffer
                secondaryBuffer = copyLen == secondaryBuffer.size() ? null : secondaryBuffer.slice(copyLen, secondaryBuffer.size()-copyLen);
                cursor += copyLen;
            }

            // Replace the current buffer to the extra buffer
            buffer = byteSizeToRead == extraBuffer.size() ? extraBuffer : extraBuffer.slice(0, byteSizeToRead);
            position = 0;

            return true;
        } else {
            // When the data at the boundary exceeds the size of the extra buffer
            int remaining = buffer.size() - position;
            int bufferTotal = remaining;
            // Read next buffers
            ArrayList<MessageBuffer> bufferList = new ArrayList<MessageBuffer>();
            while(bufferTotal < byteSizeToRead) {
                MessageBuffer next = takeNextBuffer();
                if(next == null)
                    return false;

                bufferTotal += next.size();
                bufferList.add(next);
            }

            // create a new buffer that is large enough to hold all entries
            MessageBuffer newBuffer = MessageBuffer.newBuffer(bufferTotal);

            // Copy the buffer contents to the new buffer
            int p = 0;
            buffer.copyTo(position, newBuffer, p, remaining);
            p += remaining;
            for(MessageBuffer m : bufferList) {
                m.copyTo(0, newBuffer, p, m.size());
                p += m.size();
            }

            buffer = newBuffer;
            position = 0;
            return true;
        }
    }


    private static ValueType getTypeFromHead(final byte b) throws MessageFormatException {
        MessageFormat mf = MessageFormat.valueOf(b);
        ValueType vt = MessageFormat.valueOf(b).getValueType();
        return vt;
    }

    /**
     * Returns true true if this unpacker has more elements.
     * When this returns true, subsequent call to {@link #getNextFormat()} returns an
     * MessageFormat instance. If false, {@link #getNextFormat()} will throw an EOFException.
     *
     * @return true if this unpacker has more elements to read
     */
    public boolean hasNext() throws IOException {
        return ensure(1);
    }

    public ValueType getNextValueType() throws IOException {
        return getNextFormat().getValueType();
    }

    public MessageFormat getNextFormat() throws IOException {
        byte b = lookAhead();
        return MessageFormat.valueOf(b);
    }

    /**
     * Look-ahead a byte value at the current cursor position.
     * This method does not proceed the cursor.
     *
     * @return
     * @throws IOException
     */
    protected byte lookAhead() throws IOException {
        if(ensure(1))
            return buffer.getByte(position);
        else {
            throw new EOFException();
        }
    }


    /**
     * Get the head byte value and proceeds the cursor by 1
     */
    protected byte consume() throws IOException {
        byte b = lookAhead();
        consume(1);
        return b;
    }

    /**
     * Proceeds the cursor by the specified byte length
     */
    protected void consume(int numBytes) throws IOException {
        assert (numBytes >= 0);

        // If position + numBytes becomes negative, it indicates an overflow from Integer.MAX_VALUE.
        // So we need to relocate the position
        if(position + numBytes < 0) {
            adjustCursorPosition();
        }
        position += numBytes;
    }

    /**
     * Read a byte value at the cursor and proceed the cursor.
     * It also rests the head value to READ_NEXT.
     *
     * @return
     * @throws IOException
     */
    private byte readByte() throws IOException {
        if(!ensure(1)) {
            throw new MessageFormatException("insufficient data length for reading byte value");
        }
        byte b = buffer.getByte(position);
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

    /**
     * Skip reading a value
     *
     * @return true if there are remaining values to read, false if no more values to skip
     * @throws IOException
     */
    public boolean skipValue() throws IOException {
        try {
            int remainingValues = 1;
            while(!reachedEOF && remainingValues > 0) {
                MessageFormat f = getNextFormat();
                byte b = lookAhead();
                consume();
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
                    case NEVER_USED:
                        throw new MessageFormatException(String.format("unknown code: %02x is found", b));
                }

                remainingValues--;
            }
        } catch(EOFException e) {
            return false;
        }
        return true;
    }

    /**
     * An exception when an unexpected byte value is read
     *
     * @param expectedTypeName
     * @param b
     * @return
     * @throws MessageFormatException
     */
    private static MessageTypeException unexpected(final String expectedTypeName, final byte b)
            throws MessageFormatException {
        ValueType type = getTypeFromHead(b);
        String name = type.name();
        return new MessageTypeException(
                "Expected " + expectedTypeName + " type but got " +
                        name.substring(0, 1) + name.substring(1).toLowerCase() + " type"
        );
    }

    public Object unpackNil() throws IOException {
        final byte b = lookAhead();
        if(b == Code.NIL) {
            consume();
            return null;
        }
        throw unexpected("Nil", b);
    }


    public boolean unpackBoolean() throws IOException {
        final byte b = lookAhead();
        if(b == Code.FALSE) {
            consume();
            return false;
        } else if(b == Code.TRUE) {
            consume();
            return true;
        }

        throw unexpected("boolean", b);
    }

    public byte unpackByte() throws IOException {
        final byte b = consume();
        if(Code.isFixInt(b)) {
            return b;
        }
        switch(b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                if(u8 < (byte) 0) {
                    throw overflowU8(u8);
                }
                return u8;
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                if(u16 < 0 || u16 > Byte.MAX_VALUE) {
                    throw overflowU16(u16);
                }
                return (byte) u16;
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if(u32 < 0 || u32 > Byte.MAX_VALUE) {
                    throw overflowU32(u32);
                }
                return (byte) u32;
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if(u64 < 0L || u64 > Byte.MAX_VALUE) {
                    throw overflowU64(u64);
                }
                return (byte) u64;
            case Code.INT8: // signed int 8
                byte i8 = readByte();
                return i8;
            case Code.INT16: // signed int 16
                short i16 = readShort();
                if(i16 < Byte.MIN_VALUE || i16 > Byte.MAX_VALUE) {
                    throw overflowI16(i16);
                }
                return (byte) i16;
            case Code.INT32: // signed int 32
                int i32 = readInt();
                if(i32 < Byte.MIN_VALUE || i32 > Byte.MAX_VALUE) {
                    throw overflowI32(i32);
                }
                return (byte) i32;
            case Code.INT64: // signed int 64
                long i64 = readLong();
                if(i64 < Byte.MIN_VALUE || i64 > Byte.MAX_VALUE) {
                    throw overflowI64(i64);
                }
                return (byte) i64;
        }
        throw unexpected("Integer", b);
    }

    public short unpackShort() throws IOException {
        final byte b = consume();
        if(Code.isFixInt(b)) {
            return (short) b;
        }
        switch(b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                return (short) (u8 & 0xff);
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                if(u16 < (short) 0) {
                    throw overflowU16(u16);
                }
                return u16;
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if(u32 < 0 || u32 > Short.MAX_VALUE) {
                    throw overflowU32(u32);
                }
                return (short) u32;
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if(u64 < 0L || u64 > Short.MAX_VALUE) {
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
                if(i32 < Short.MIN_VALUE || i32 > Short.MAX_VALUE) {
                    throw overflowI32(i32);
                }
                return (short) i32;
            case Code.INT64: // signed int 64
                long i64 = readLong();
                if(i64 < Short.MIN_VALUE || i64 > Short.MAX_VALUE) {
                    throw overflowI64(i64);
                }
                return (short) i64;
        }
        throw unexpected("Integer", b);

    }

    public int unpackInt() throws IOException {
        final byte b = consume();
        if(Code.isFixInt(b)) {
            return (int) b;
        }
        switch(b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                return u8 & 0xff;
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                return u16 & 0xffff;
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if(u32 < 0) {
                    throw overflowU32(u32);
                }
                return u32;
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if(u64 < 0L || u64 > (long) Integer.MAX_VALUE) {
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
                if(i64 < (long) Integer.MIN_VALUE || i64 > (long) Integer.MAX_VALUE) {
                    throw overflowI64(i64);
                }
                return (int) i64;
        }
        throw unexpected("Integer", b);

    }

    public long unpackLong() throws IOException {
        final byte b = consume();
        if(Code.isFixInt(b)) {
            return (long) b;
        }
        switch(b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                return (long) (u8 & 0xff);
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                return (long) (u16 & 0xffff);
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if(u32 < 0) {
                    return (long) (u32 & 0x7fffffff) + 0x80000000L;
                } else {
                    return (long) u32;
                }
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if(u64 < 0L) {
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
        throw unexpected("Integer", b);

    }

    public BigInteger unpackBigInteger() throws IOException {
        final byte b = consume();
        if(Code.isFixInt(b)) {
            return BigInteger.valueOf((long) b);
        }
        switch(b) {
            case Code.UINT8: // unsigned int 8
                byte u8 = readByte();
                return BigInteger.valueOf((long) (u8 & 0xff));
            case Code.UINT16: // unsigned int 16
                short u16 = readShort();
                return BigInteger.valueOf((long) (u16 & 0xffff));
            case Code.UINT32: // unsigned int 32
                int u32 = readInt();
                if(u32 < 0) {
                    return BigInteger.valueOf((long) (u32 & 0x7fffffff) + 0x80000000L);
                } else {
                    return BigInteger.valueOf((long) u32);
                }
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if(u64 < 0L) {
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
        throw unexpected("Integer", b);
    }

    public float unpackFloat() throws IOException {
        final byte b = consume();
        switch(b) {
            case Code.FLOAT32: // float
                float fv = readFloat();
                return fv;
            case Code.FLOAT64: // double
                double dv = readDouble();
                return (float) dv;
        }
        throw unexpected("Float", b);
    }

    public double unpackDouble() throws IOException {
        final byte b = consume();
        switch(b) {
            case Code.FLOAT32: // float
                float fv = readFloat();
                return (double) fv;
            case Code.FLOAT64: // double
                double dv = readDouble();
                return dv;
        }
        throw unexpected("Float", b);
    }

    private static String EMPTY_STRING = "";

    public String unpackString() throws IOException {
        int strLen = unpackRawStringHeader();
        if(strLen > 0) {
            ensure(strLen);
            ByteBuffer bb = buffer.toByteBuffer(position, strLen);
            String ret = decoder.decode(bb).toString();
            consume(strLen);
            return ret;
        } else
            return EMPTY_STRING;
    }


    public int unpackArrayHeader() throws IOException {
        final byte b = consume();
        if(Code.isFixedArray(b)) { // fixarray
            return b & 0x0f;
        }
        switch(b) {
            case Code.ARRAY16: // array 16
                return readNextLength16();
            case Code.ARRAY32: // array 32
                return readNextLength32();
        }
        throw unexpected("Array", b);
    }

    public int unpackMapHeader() throws IOException {
        final byte b = consume();
        if(Code.isFixedMap(b)) { // fixmap
            return b & 0x0f;
        }
        switch(b) {
            case Code.MAP16: // map 16
                return readNextLength16();
            case Code.MAP32: // map 32
                return readNextLength32();
        }
        throw unexpected("Map", b);
    }

    public ExtendedTypeHeader unpackExtendedTypeHeader() throws IOException {
        byte b = consume();
        switch(b) {
            case Code.FIXEXT1:
                return new ExtendedTypeHeader(1, readByte());
            case Code.FIXEXT2:
                return new ExtendedTypeHeader(2, readByte());
            case Code.FIXEXT4:
                return new ExtendedTypeHeader(4, readByte());
            case Code.FIXEXT8:
                return new ExtendedTypeHeader(8, readByte());
            case Code.FIXEXT16:
                return new ExtendedTypeHeader(16, readByte());
            case Code.EXT8: {
                int len = readNextLength8();
                int t = readByte();
                return new ExtendedTypeHeader(len, t);
            }
            case Code.EXT16: {
                int len = readNextLength16();
                int t = readByte();
                return new ExtendedTypeHeader(len, t);
            }
            case Code.EXT32: {
                int len = readNextLength32();
                int t = readByte();
                return new ExtendedTypeHeader(len, t);
            }
        }

        throw unexpected("Ext", b);
    }

    public int unpackRawStringHeader() throws IOException {
        final byte b = consume();
        if(Code.isFixedRaw(b)) { // FixRaw
            return b & 0x1f;
        }
        switch(b) {
            case Code.STR8: // str 8
                return readNextLength8();
            case Code.STR16: // str 16
                return readNextLength16();
            case Code.STR32: // str 32
                return readNextLength32();
        }
        throw unexpected("String", b);
    }


    public int unpackBinaryHeader() throws IOException {
        // TODO option to allow str format family
        final byte b = consume();
        switch(b) {
            case Code.BIN8: // bin 8
                return readNextLength8();
            case Code.BIN16: // bin 16
                return readNextLength16();
            case Code.BIN32: // bin 32
                return readNextLength32();
        }
        throw unexpected("Binary", b);
    }

    // TODO returns a buffer reference to the payload (zero-copy)


    public void readPayload(ByteBuffer dst) throws IOException {
        while(dst.remaining() > 0) {
            adjustCursorPosition();
            if(buffer == null)
                throw new EOFException();
            int l = Math.min(buffer.size() - position, dst.remaining());
            buffer.getBytes(position, l, dst);
            consume(l);
        }
    }

    /**
     * Read up to len bytes of data into the destination array
     *
     * @param dst the buffer into which the data is read
     * @param off the offset in the dst array
     * @param len the number of bytes to read
     * @throws IOException
     */
    public void readPayload(byte[] dst, int off, int len) throws IOException {
        int writtenLen = 0;
        while(writtenLen < len) {
            adjustCursorPosition();
            if(buffer == null)
                throw new EOFException();
            int l = Math.min(buffer.size() - position, len - writtenLen);
            buffer.getBytes(position, dst, off + writtenLen, l);
            consume(position);
            writtenLen += l;
        }
    }


    int readNextLength8() throws IOException {
        byte u8 = readByte();
        return u8 & 0xff;
    }

    int readNextLength16() throws IOException {
        short u16 = readShort();
        return u16 & 0xffff;
    }

    int readNextLength32() throws IOException {
        int u32 = readInt();
        if(u32 < 0) {
            throw overflowU32Size(u32);
        }
        return u32;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    private static MessageIntegerOverflowException overflowU8(final byte u8) {
        final BigInteger bi = BigInteger.valueOf((long) (u8 & 0xff));
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU16(final short u16) {
        final BigInteger bi = BigInteger.valueOf((long) (u16 & 0xffff));
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU32(final int u32) {
        final BigInteger bi = BigInteger.valueOf((long) (u32 & 0x7fffffff) + 0x80000000L);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU64(final long u64) {
        final BigInteger bi = BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI16(final short i16) {
        final BigInteger bi = BigInteger.valueOf((long) i16);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI32(final int i32) {
        final BigInteger bi = BigInteger.valueOf((long) i32);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI64(final long i64) {
        final BigInteger bi = BigInteger.valueOf(i64);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageSizeException overflowU32Size(final int u32) {
        final long lv = (long) (u32 & 0x7fffffff) + 0x80000000L;
        return new MessageSizeException(lv);
    }


}
