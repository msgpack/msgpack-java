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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;

import org.msgpack.core.MessagePack.Code;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.value.Value;
import org.msgpack.value.ImmutableValue;
import org.msgpack.value.Variable;
import org.msgpack.value.ValueFactory;

import static org.msgpack.core.Preconditions.*;


/**
 * MessageUnpacker lets an application read message-packed values from a data stream.
 * The application needs to call {@link #getNextFormat()} followed by an appropriate unpackXXX method according to the the returned format type.
 *
 * <pre>
 * <code>
 *     MessageUnpacker unpacker = MessagePackFactory.DEFAULT.newUnpacker(...);
 *     while(unpacker.hasNext()) {
 *         MessageFormat f = unpacker.getNextFormat();
 *         switch(f) {
 *             case MessageFormat.POSFIXINT:
 *             case MessageFormat.INT8:
 *             case MessageFormat.UINT8: {
 *                int v = unpacker.unpackInt();
 *                break;
 *             }
 *             case MessageFormat.STRING: {
 *                String v = unpacker.unpackString();
 *                break;
 *             }
 *             // ...
  *       }
 *     }
 *
 * </code>
 * </pre>
 */
public class MessageUnpacker implements Closeable {

    private final static MessageBuffer EMPTY_BUFFER = MessageBuffer.wrap(new byte[0]);

    private final MessagePack.Config config;

    private MessageBufferInput in;

    /**
     * Points to the current buffer to read
     */
    private MessageBuffer buffer = EMPTY_BUFFER;

    /**
     * Cursor position in the current buffer
     */
    private int position;

    /**
     * Total read byte size
     */
    private long totalReadBytes;

    /**
     * For preserving the next buffer to use
     */
    private MessageBuffer secondaryBuffer = null;

    /**
     * Extra buffer for string data at the buffer boundary. Using 17-byte buffer (for FIXEXT16) is sufficient.
     */
    private final MessageBuffer extraBuffer = MessageBuffer.wrap(new byte[24]);

    /**
     * True if no more data is available from the MessageBufferInput
     */
    private boolean reachedEOF = false;

    /**
     * For decoding String in unpackString.
     */
    private CharsetDecoder decoder;

    /**
     * Buffer for decoding strings
     */
    private CharBuffer decodeBuffer;


    /**
     * Create an MessageUnpacker that reads data from the given MessageBufferInput
     *
     * @param in
     */
    public MessageUnpacker(MessageBufferInput in) {
        this(in, MessagePack.DEFAULT_CONFIG);
    }


    /**
     * Create an MessageUnpacker
     * @param in
     * @param config configuration
     */
    public MessageUnpacker(MessageBufferInput in, MessagePack.Config config) {
        // Root constructor. All of the constructors must call this constructor.
        this.in = checkNotNull(in, "MessageBufferInput is null");
        this.config = checkNotNull(config, "Config");
    }

    /**
     * Reset input. This method doesn't close the old resource.
     * @param in new input
     * @return the old resource
     */
    public MessageBufferInput reset(MessageBufferInput in) throws IOException {
        MessageBufferInput newIn = checkNotNull(in, "MessageBufferInput is null");

        // Reset the internal states
        MessageBufferInput old = this.in;
        this.in = newIn;
        this.buffer = EMPTY_BUFFER;
        this.position = 0;
        this.totalReadBytes = 0;
        this.secondaryBuffer = null;
        this.reachedEOF = false;
        // No need to initialize the already allocated string decoder here since we can reuse it.
        return old;
    }

    public long getTotalReadBytes() {
        return totalReadBytes + position;
    }

    private void prepareDecoder() {
        if(decoder == null) {
            decodeBuffer = CharBuffer.allocate(config.getStringDecoderBufferSize());
            decoder = MessagePack.UTF8.newDecoder()
                    .onMalformedInput(config.getActionOnMalFormedInput())
                    .onUnmappableCharacter(config.getActionOnUnmappableCharacter());
        }
    }


    /**
     * Relocate the cursor position so that it points to the real buffer
     *
     * @return true if it succeeds to move the cursor, or false if there is no more buffer to read
     * @throws IOException when failed to retrieve next buffer
     */
    private boolean ensureBuffer() throws IOException {
        while(buffer != null && position >= buffer.size()) {
            // Fetch the next buffer
            int bufferSize = buffer.size();
            position -= bufferSize;
            totalReadBytes += bufferSize;
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
     * Ensure that the buffer has the data of at least the specified size.
     *
     * @param byteSizeToRead the data size to be read
     * @return if the buffer can have the data of the specified size returns true, or if the input source reached an EOF, it returns false.
     * @throws IOException
     */
    private boolean ensure(int byteSizeToRead) throws IOException {
        if(byteSizeToRead == 0)
            return true;

        if(!ensureBuffer())
            return false;

        // The buffer contains the data
        if(position + byteSizeToRead <= buffer.size()) {
            // OK
            return true;
        }

        // When the data is at the boundary
        /*
             |---(byte size to read) ----|
             -- current buffer --|
             |--- extra buffer (slice) --|----|
                                 |-------|---------- secondary buffer (slice) ----------------|

             */

        // If the byte size to read fits within the extra buffer, use the extraBuffer
        MessageBuffer newBuffer = byteSizeToRead <= extraBuffer.size() ? extraBuffer : MessageBuffer.newBuffer(byteSizeToRead);

        // Copy the remaining buffer contents to the new buffer
        int firstHalfSize = buffer.size() - position;
        if(firstHalfSize > 0)
            buffer.copyTo(position, newBuffer, 0, firstHalfSize);

        // Read the last half contents from the next buffers
        int cursor = firstHalfSize;
        while(cursor < byteSizeToRead) {
            secondaryBuffer = takeNextBuffer();
            if(secondaryBuffer == null)
                return false; // No more buffer to read

            // Copy the contents from the secondary buffer to the new buffer
            int copyLen = Math.min(byteSizeToRead - cursor, secondaryBuffer.size());
            secondaryBuffer.copyTo(0, newBuffer, cursor, copyLen);

            // Truncate the copied part from the secondaryBuffer
            secondaryBuffer = copyLen == secondaryBuffer.size() ? null : secondaryBuffer.slice(copyLen, secondaryBuffer.size()-copyLen);
            cursor += copyLen;
        }

        // Replace the current buffer with the new buffer
        totalReadBytes += position;
        buffer = byteSizeToRead == newBuffer.size() ? newBuffer : newBuffer.slice(0, byteSizeToRead);
        position = 0;

        return true;
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

    /**
     * Returns the next MessageFormat type. This method should be called after {@link #hasNext()} returns true.
     * If {@link #hasNext()} returns false, calling this method throws {@link java.io.EOFException}.
     *
     * This method does not proceed the internal cursor.
     * @return the next MessageFormat
     * @throws IOException when failed to read the input data.
     * @throws EOFException when the end of file reached, i.e. {@link #hasNext()} == false.
     */
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
    private byte lookAhead() throws IOException {
        if(ensure(1))
            return buffer.getByte(position);
        else
            throw new EOFException();
    }


    /**
     * Get the head byte value and proceeds the cursor by 1
     */
    private byte consume() throws IOException {
        byte b = lookAhead();
        position += 1;
        return b;
    }

    /**
     * Proceeds the cursor by the specified byte length
     */
    private void consume(int numBytes) throws IOException {
        assert(numBytes >= 0);
        // If position + numBytes becomes negative, it indicates an overflow from Integer.MAX_VALUE.
        if(position + numBytes < 0) {
            ensureBuffer();
        }
        position += numBytes;
     }

    /**
     * Read a byte value at the cursor and proceed the cursor.
     *
     * @return
     * @throws IOException
     */
    private byte readByte() throws IOException {
        if(!ensure(1)) {
            throw new EOFException("insufficient data length for reading byte value");
        }
        byte b = buffer.getByte(position);
        consume(1);
        return b;
    }

    private short readShort() throws IOException {
        if(!ensure(2)) {
            throw new EOFException("insufficient data length for reading short value");
        }
        short s = buffer.getShort(position);
        consume(2);
        return s;
    }

    private int readInt() throws IOException {
        if(!ensure(4)) {
            throw new EOFException("insufficient data length for reading int value");
        }
        int i = buffer.getInt(position);
        consume(4);
        return i;
    }

    private float readFloat() throws IOException {
        if(!ensure(4)) {
            throw new EOFException("insufficient data length for reading float value");
        }
        float f = buffer.getFloat(position);
        consume(4);
        return f;
    }

    private long readLong() throws IOException {
        if(!ensure(8)) {
            throw new EOFException("insufficient data length for reading long value");
        }
        long l = buffer.getLong(position);
        consume(8);
        return l;
    }

    private double readDouble() throws IOException {
        if(!ensure(8)) {
            throw new EOFException("insufficient data length for reading double value");
        }
        double d = buffer.getDouble(position);
        consume(8);
        return d;
    }


    /**
     * Skip reading the specified number of bytes. Use this method only if you know skipping data is safe.
     * For simply skipping the next value, use {@link #skipValue()}.
     *
     * @param numBytes
     * @throws IOException
     */
    public void skipBytes(int numBytes) throws IOException {
        checkArgument(numBytes >= 0, "skip length must be >= 0: " + numBytes);
        consume(numBytes);
    }

    /**
     * Skip the next value, then move the cursor at the end of the value
     *
     * @throws IOException
     */
    public void skipValue() throws IOException {
        int remainingValues = 1;
        while(remainingValues > 0) {
            if(reachedEOF) {
                throw new EOFException();
            }

            MessageFormat f = getNextFormat();
            byte b = consume();
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
                    break;
                case ARRAY32:
                    remainingValues += readNextLength32();
                    break;
                case MAP16:
                    remainingValues += readNextLength16() * 2;
                    break;
                case MAP32:
                    remainingValues += readNextLength32() * 2; // TODO check int overflow
                    break;
                case NEVER_USED:
                    throw new MessageFormatException(String.format("unknown code: %02x is found", b));
            }

            remainingValues--;
        }
    }

    /**
     * Create an exception for the case when an unexpected byte value is read
     *
     * @param expected
     * @param b
     * @return
     * @throws MessageFormatException
     */
    private static MessageTypeException unexpected(String expected, byte b)
            throws MessageTypeException {
        MessageFormat format = MessageFormat.valueOf(b);
        String typeName;
        if (format == MessageFormat.NEVER_USED) {
            typeName = "NeverUsed";
        } else {
            String name = format.getValueType().name();
            typeName = name.substring(0, 1) + name.substring(1).toLowerCase();
        }
        return new MessageTypeException(String.format("Expected %s, but got %s (%02x)", expected, typeName, b));
    }

    public ImmutableValue unpackValue() throws IOException {
        MessageFormat mf = getNextFormat();
        switch(mf.getValueType()) {
            case NIL:
                unpackNil();
                return ValueFactory.newNil();
            case BOOLEAN:
                return ValueFactory.newBoolean(unpackBoolean());
            case INTEGER:
                switch (mf) {
                    case UINT64:
                        return ValueFactory.newInteger(unpackBigInteger());
                    default:
                        return ValueFactory.newInteger(unpackLong());
                }
            case FLOAT:
                return ValueFactory.newFloat(unpackDouble());
            case STRING: {
                int length = unpackRawStringHeader();
                return ValueFactory.newString(readPayload(length));
            }
            case BINARY: {
                int length = unpackBinaryHeader();
                return ValueFactory.newBinary(readPayload(length));
            }
            case ARRAY: {
                int size = unpackArrayHeader();
                Value[] array = new Value[size];
                for (int i=0; i < size; i++) {
                    array[i] = unpackValue();
                }
                return ValueFactory.newArray(array);
            }
            case MAP: {
                int size = unpackMapHeader();
                Value[] kvs = new Value[size * 2];
                for (int i=0; i < size * 2; ) {
                    kvs[i] = unpackValue();
                    i++;
                    kvs[i] = unpackValue();
                    i++;
                }
                return ValueFactory.newMap(kvs);
            }
            case EXTENSION: {
                ExtensionTypeHeader extHeader = unpackExtensionTypeHeader();
                return ValueFactory.newExtension(extHeader.getType(), readPayload(extHeader.getLength()));
            }
            default:
                throw new MessageFormatException("Unknown value type");
        }
    }

    public Variable unpackValue(Variable var) throws IOException {
        MessageFormat mf = getNextFormat();
        switch(mf.getValueType()) {
            case NIL:
                unpackNil();
                var.setNilValue();
                return var;
            case BOOLEAN:
                var.setBooleanValue(unpackBoolean());
                return var;
            case INTEGER:
                switch (mf) {
                    case UINT64:
                        var.setIntegerValue(unpackBigInteger());
                        return var;
                    default:
                        var.setIntegerValue(unpackLong());
                        return var;
                }
            case FLOAT:
                var.setFloatValue(unpackDouble());
                return var;
            case STRING: {
                int length = unpackRawStringHeader();
                var.setStringValue(readPayload(length));
                return var;
            }
            case BINARY: {
                int length = unpackBinaryHeader();
                var.setBinaryValue(readPayload(length));
                return var;
            }
            case ARRAY: {
                int size = unpackArrayHeader();
                List<Value> list = new ArrayList<Value>(size);
                for (int i=0; i < size; i++) {
                    //Variable e = new Variable();
                    //unpackValue(e);
                    //list.add(e);
                    list.add(unpackValue());
                }
                var.setArrayValue(list);
                return var;
            }
            case MAP: {
                int size = unpackMapHeader();
                Map<Value,Value> map = new HashMap<Value,Value>();
                for (int i=0; i < size; i++) {
                    //Variable k = new Variable();
                    //unpackValue(k);
                    //Variable v = new Variable();
                    //unpackValue(v);
                    Value k = unpackValue();
                    Value v = unpackValue();
                    map.put(k, v);
                }
                var.setMapValue(map);
                return var;
            }
            case EXTENSION: {
                ExtensionTypeHeader extHeader = unpackExtensionTypeHeader();
                var.setExtensionValue(extHeader.getType(), readPayload(extHeader.getLength()));
                return var;
            }
            default:
                throw new MessageFormatException("Unknown value type");
        }
    }

    public void unpackNil() throws IOException {
        byte b = consume();
        if(b == Code.NIL) {
            return;
        }
        throw unexpected("Nil", b);
    }


    public boolean unpackBoolean() throws IOException {
        byte b = consume();
        if(b == Code.FALSE) {
            return false;
        } else if(b == Code.TRUE) {
            return true;
        }

        throw unexpected("boolean", b);
    }

    public byte unpackByte() throws IOException {
        byte b = consume();
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
        byte b = consume();
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
        byte b = consume();
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
        byte b = consume();
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
        byte b = consume();
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
        byte b = consume();
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
        byte b = consume();
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


    private final static String EMPTY_STRING = "";

    public String unpackString() throws IOException {
        int strLen = unpackRawStringHeader();
        if(strLen > 0) {
            if(strLen > config.getMaxUnpackStringSize()) {
                throw new MessageSizeException(String.format("cannot unpack a String of size larger than %,d: %,d", config.getMaxUnpackStringSize(), strLen), strLen);
            }

            prepareDecoder();
            assert(decoder != null);

            decoder.reset();

            try {
                int cursor = 0;
                decodeBuffer.clear();
                StringBuilder sb = new StringBuilder();

                while(cursor < strLen) {
                    int readLen = Math.min(position < buffer.size() ? buffer.size() - position : buffer.size(), strLen-cursor);
                    if (!ensure(readLen))
                        throw new EOFException();

                    ByteBuffer bb = buffer.toByteBuffer(position, readLen);

                    while(bb.hasRemaining()) {
                        boolean endOfInput = (cursor + readLen) >= strLen;
                        CoderResult cr = decoder.decode(bb, decodeBuffer, endOfInput);

                        if(endOfInput && cr.isUnderflow())
                            cr = decoder.flush(decodeBuffer);

                        if(cr.isOverflow()) {
                            // The output CharBuffer has insufficient space
                            decoder.reset();
                        }

                        if(cr.isUnderflow() && bb.hasRemaining()) {
                            // input buffer doesn't have enough bytes for multi bytes characters
                            if(config.getActionOnMalFormedInput() == CodingErrorAction.REPORT) {
                                throw new MalformedInputException(strLen);
                            }
                            // trash truncated bytes
                            while (bb.hasRemaining())
                                bb.get();
                        }

                        if(cr.isError()) {
                            if((cr.isMalformed() && config.getActionOnMalFormedInput() == CodingErrorAction.REPORT) ||
                               (cr.isUnmappable() && config.getActionOnUnmappableCharacter() == CodingErrorAction.REPORT)) {
                                cr.throwException();
                            }
                        }

                        decodeBuffer.flip();
                        sb.append(decodeBuffer);

                        decodeBuffer.clear();
                    }

                    cursor += readLen;
                    consume(readLen);
                }

                return sb.toString();
            }
            catch(CharacterCodingException e) {
                throw new MessageStringCodingException(e);
            }
        } else
            return EMPTY_STRING;
    }


    public int unpackArrayHeader() throws IOException {
        byte b = consume();
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
        byte b = consume();
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

    public ExtensionTypeHeader unpackExtensionTypeHeader() throws IOException {
        byte b = consume();
        switch(b) {
            case Code.FIXEXT1:
                return new ExtensionTypeHeader(readByte(), 1);
            case Code.FIXEXT2:
                return new ExtensionTypeHeader(readByte(), 2);
            case Code.FIXEXT4:
                return new ExtensionTypeHeader(readByte(), 4);
            case Code.FIXEXT8:
                return new ExtensionTypeHeader(readByte(), 8);
            case Code.FIXEXT16:
                return new ExtensionTypeHeader(readByte(), 16);
            case Code.EXT8: {
                int length = readNextLength8();
                byte type = readByte();
                return new ExtensionTypeHeader(type, length);
            }
            case Code.EXT16: {
                int length = readNextLength16();
                byte type = readByte();
                return new ExtensionTypeHeader(type, length);
            }
            case Code.EXT32: {
                int length = readNextLength32();
                byte type = readByte();
                return new ExtensionTypeHeader(type, length);
            }
        }

        throw unexpected("Ext", b);
    }

    private int readStringHeader(byte b) throws IOException {
        switch(b) {
            case Code.STR8: // str 8
                return readNextLength8();
            case Code.STR16: // str 16
                return readNextLength16();
            case Code.STR32: // str 32
                return readNextLength32();
            default:
                return -1;
        }
    }

    private int readBinaryHeader(byte b) throws IOException {
        switch(b) {
            case Code.BIN8: // bin 8
                return readNextLength8();
            case Code.BIN16: // bin 16
                return readNextLength16();
            case Code.BIN32: // bin 32
                return readNextLength32();
            default:
                return -1;
        }
    }


    public int unpackRawStringHeader() throws IOException {
        byte b = consume();
        if(Code.isFixedRaw(b)) { // FixRaw
            return b & 0x1f;
        }
        int len = readStringHeader(b);
        if(len >= 0)
            return len;

        if(config.isReadBinaryAsString()){
            len = readBinaryHeader(b);
            if(len >= 0)
                return len;
        }
        throw unexpected("String", b);
    }


    public int unpackBinaryHeader() throws IOException {
        byte b = consume();
        if(Code.isFixedRaw(b)) { // FixRaw
            return b & 0x1f;
        }
        int len = readBinaryHeader(b);
        if(len >= 0)
            return len;

        if(config.isReadStringAsBinary()) {
            len = readStringHeader(b);
            if(len >= 0)
                return len;
        }
        throw unexpected("Binary", b);
    }

    // TODO returns a buffer reference to the payload (zero-copy)


    public void readPayload(ByteBuffer dst) throws IOException {
        while(dst.remaining() > 0) {
            if(!ensureBuffer())
                throw new EOFException();
            int l = Math.min(buffer.size() - position, dst.remaining());
            buffer.getBytes(position, l, dst);
            consume(l);
        }
    }

    public void readPayload(byte[] dst) throws IOException {
        readPayload(dst, 0, dst.length);
    }

    public byte[] readPayload(int length) throws IOException {
        byte[] newArray = new byte[length];
        readPayload(newArray);
        return newArray;
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
            if(!ensureBuffer())
                throw new EOFException();
            int l = Math.min(buffer.size() - position, len - writtenLen);
            buffer.getBytes(position, dst, off + writtenLen, l);
            consume(l);
            writtenLen += l;
        }
    }

    public MessageBuffer readPayloadAsReference(int length) throws IOException {
        checkArgument(length >= 0);
        if(!ensure(length))
            throw new EOFException();

        MessageBuffer ref = buffer.slice(position, length);
        position += length;
        return ref;
    }


    private int readNextLength8() throws IOException {
        byte u8 = readByte();
        return u8 & 0xff;
    }

    private int readNextLength16() throws IOException {
        short u16 = readShort();
        return u16 & 0xffff;
    }

    private int readNextLength32() throws IOException {
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

    private static MessageIntegerOverflowException overflowU8(byte u8) {
        BigInteger bi = BigInteger.valueOf((long) (u8 & 0xff));
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU16(short u16) {
        BigInteger bi = BigInteger.valueOf((long) (u16 & 0xffff));
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU32(int u32) {
        BigInteger bi = BigInteger.valueOf((long) (u32 & 0x7fffffff) + 0x80000000L);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU64(long u64) {
        BigInteger bi = BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI16(short i16) {
        BigInteger bi = BigInteger.valueOf((long) i16);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI32(int i32) {
        BigInteger bi = BigInteger.valueOf((long) i32);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI64(long i64) {
        BigInteger bi = BigInteger.valueOf(i64);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageSizeException overflowU32Size(int u32) {
        long lv = (long) (u32 & 0x7fffffff) + 0x80000000L;
        return new MessageSizeException(lv);
    }


}
