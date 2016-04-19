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

import org.msgpack.core.MessagePack.Code;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.value.ImmutableValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;
import org.msgpack.value.Variable;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * MessageUnpacker lets an application read message-packed values from a data stream.
 * The application needs to call {@link #getNextFormat()} followed by an appropriate unpackXXX method according to the the returned format type.
 * <p/>
 * <pre>
 * <code>
 *     MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(...);
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
public class MessageUnpacker
        implements Closeable
{
    private static final MessageBuffer EMPTY_BUFFER = MessageBuffer.wrap(new byte[0]);

    private final boolean allowReadingStringAsBinary;
    private final boolean allowReadingBinaryAsString;
    private final CodingErrorAction actionOnMalformedString;
    private final CodingErrorAction actionOnUnmappableString;
    private final int stringSizeLimit;
    private final int stringDecoderBufferSize;

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
     * An extra buffer for reading a small number value across the input buffer boundary.
     * At most 8-byte buffer (for readLong used by uint 64 and UTF-8 character decoding) is required.
     */
    private final MessageBuffer numberBuffer = MessageBuffer.allocate(8);

    /**
     * After calling prepareNumberBuffer(), the caller should use this variable to read from the returned MessageBuffer.
     */
    private int nextReadPosition;

    /**
     * For decoding String in unpackString.
     */
    private StringBuilder decodeStringBuffer;

    /**
     * For decoding String in unpackString.
     */
    private CharsetDecoder decoder;

    /**
     * Buffer for decoding strings
     */
    private CharBuffer decodeBuffer;

    /**
     * Create an MessageUnpacker that reads data from the given MessageBufferInput.
     * This method is available for subclasses to override. Use MessagePack.UnpackerConfig.newUnpacker method to instanciate this implementation.
     *
     * @param in
     */
    protected MessageUnpacker(MessageBufferInput in, MessagePack.UnpackerConfig config)
    {
        this.in = checkNotNull(in, "MessageBufferInput is null");
        this.allowReadingStringAsBinary = config.getAllowReadingStringAsBinary();
        this.allowReadingBinaryAsString = config.getAllowReadingBinaryAsString();
        this.actionOnMalformedString = config.getActionOnMalformedString();
        this.actionOnUnmappableString = config.getActionOnUnmappableString();
        this.stringSizeLimit = config.getStringSizeLimit();
        this.stringDecoderBufferSize = config.getStringDecoderBufferSize();
    }

    /**
     * Reset input. This method doesn't close the old resource.
     *
     * @param in new input
     * @return the old resource
     */
    public MessageBufferInput reset(MessageBufferInput in)
            throws IOException
    {
        MessageBufferInput newIn = checkNotNull(in, "MessageBufferInput is null");

        // Reset the internal states
        MessageBufferInput old = this.in;
        this.in = newIn;
        this.buffer = EMPTY_BUFFER;
        this.position = 0;
        this.totalReadBytes = 0;
        // No need to initialize the already allocated string decoder here since we can reuse it.

        return old;
    }

    public long getTotalReadBytes()
    {
        return totalReadBytes + position;
    }

    /**
     * Get the next buffer without changing the position
     *
     * @return
     * @throws IOException
     */
    private MessageBuffer getNextBuffer()
            throws IOException
    {
        MessageBuffer next = in.next();
        if (next == null) {
            throw new MessageInsufficientBufferException();
        }
        assert (buffer != null);
        totalReadBytes += buffer.size();
        return next;
    }

    private void nextBuffer()
            throws IOException
    {
        buffer = getNextBuffer();
        position = 0;
    }

    /**
     * Returns a short size buffer (upto 8 bytes) to read a number value
     *
     * @param readLength
     * @return
     * @throws IOException
     * @throws MessageInsufficientBufferException If no more buffer can be acquired from the input source for reading the specified data length
     */
    private MessageBuffer prepareNumberBuffer(int readLength)
            throws IOException
    {
        int remaining = buffer.size() - position;
        if (remaining >= readLength) {
            // When the data is contained inside the default buffer
            nextReadPosition = position;
            position += readLength;  // here assumes following buffer.getXxx never throws exception
            return buffer; // Return the default buffer
        }
        else {
            // When the default buffer doesn't contain the whole length,
            // fill the temporary buffer from the current data fragment and
            // next fragment(s).

            // TODO buffer.array() doesn't work if MessageBuffer is allocated by
            //      newDirectBuffer. dd copy method to MessageBuffer to solve this issue.

            int off = 0;
            if (remaining > 0) {
                numberBuffer.putBytes(0,
                        buffer.array(), buffer.arrayOffset() + position,
                        remaining);
                readLength -= remaining;
                off += remaining;
            }

            while (true) {
                nextBuffer();
                int nextSize = buffer.size();
                if (nextSize >= readLength) {
                    numberBuffer.putBytes(off,
                            buffer.array(), buffer.arrayOffset(),
                            readLength);
                    position = readLength;
                    break;
                }
                else {
                    numberBuffer.putBytes(off,
                            buffer.array(), buffer.arrayOffset(),
                            nextSize);
                    readLength -= nextSize;
                    off += nextSize;
                }
            }

            nextReadPosition = 0;
            return numberBuffer;
        }
    }

    private static int utf8MultibyteCharacterSize(byte firstByte)
    {
        return Integer.numberOfLeadingZeros(~(firstByte & 0xff) << 24);
    }

    /**
     * Returns true true if this unpacker has more elements.
     * When this returns true, subsequent call to {@link #getNextFormat()} returns an
     * MessageFormat instance. If false, next {@link #getNextFormat()} call will throw an MessageInsufficientBufferException.
     *
     * @return true if this unpacker has more elements to read
     */
    public boolean hasNext()
            throws IOException
    {
        while (buffer.size() <= position) {
            MessageBuffer next = in.next();
            if (next == null) {
                return false;
            }
            totalReadBytes += buffer.size();
            buffer = next;
            position = 0;
        }
        return true;
    }

    /**
     * Returns the next MessageFormat type. This method should be called after {@link #hasNext()} returns true.
     * If {@link #hasNext()} returns false, calling this method throws {@link MessageInsufficientBufferException}.
     * <p/>
     * This method does not proceed the internal cursor.
     *
     * @return the next MessageFormat
     * @throws IOException when failed to read the input data.
     * @throws MessageInsufficientBufferException when the end of file reached, i.e. {@link #hasNext()} == false.
     */
    public MessageFormat getNextFormat()
            throws IOException
    {
        // makes sure that buffer has at leat 1 byte
        if (!hasNext()) {
            throw new MessageInsufficientBufferException();
        }
        byte b = buffer.getByte(position);
        return MessageFormat.valueOf(b);
    }

    /**
     * Read a byte value at the cursor and proceed the cursor.
     *
     * @return
     * @throws IOException
     */
    private byte readByte()
            throws IOException
    {
        if (buffer.size() > position) {
            byte b = buffer.getByte(position);
            position++;
            return b;
        }
        else {
            nextBuffer();
            if (buffer.size() > 0) {
                byte b = buffer.getByte(0);
                position = 1;
                return b;
            }
            return readByte();
        }
    }

    private short readShort()
            throws IOException
    {
        MessageBuffer numberBuffer = prepareNumberBuffer(2);
        return numberBuffer.getShort(nextReadPosition);
    }

    private int readInt()
            throws IOException
    {
        MessageBuffer numberBuffer = prepareNumberBuffer(4);
        return numberBuffer.getInt(nextReadPosition);
    }

    private long readLong()
            throws IOException
    {
        MessageBuffer numberBuffer = prepareNumberBuffer(8);
        return numberBuffer.getLong(nextReadPosition);
    }

    private float readFloat()
            throws IOException
    {
        MessageBuffer numberBuffer = prepareNumberBuffer(4);
        return numberBuffer.getFloat(nextReadPosition);
    }

    private double readDouble()
            throws IOException
    {
        MessageBuffer numberBuffer = prepareNumberBuffer(8);
        return numberBuffer.getDouble(nextReadPosition);
    }

    /**
     * Skip the next value, then move the cursor at the end of the value
     *
     * @throws IOException
     */
    public void skipValue()
            throws IOException
    {
        int remainingValues = 1;
        while (remainingValues > 0) {
            byte b = readByte();
            MessageFormat f = MessageFormat.valueOf(b);
            switch (f) {
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
                    skipPayload(strLen);
                    break;
                }
                case INT8:
                case UINT8:
                    skipPayload(1);
                    break;
                case INT16:
                case UINT16:
                    skipPayload(2);
                    break;
                case INT32:
                case UINT32:
                case FLOAT32:
                    skipPayload(4);
                    break;
                case INT64:
                case UINT64:
                case FLOAT64:
                    skipPayload(8);
                    break;
                case BIN8:
                case STR8:
                    skipPayload(readNextLength8());
                    break;
                case BIN16:
                case STR16:
                    skipPayload(readNextLength16());
                    break;
                case BIN32:
                case STR32:
                    skipPayload(readNextLength32());
                    break;
                case FIXEXT1:
                    skipPayload(2);
                    break;
                case FIXEXT2:
                    skipPayload(3);
                    break;
                case FIXEXT4:
                    skipPayload(5);
                    break;
                case FIXEXT8:
                    skipPayload(9);
                    break;
                case FIXEXT16:
                    skipPayload(17);
                    break;
                case EXT8:
                    skipPayload(readNextLength8() + 1);
                    break;
                case EXT16:
                    skipPayload(readNextLength16() + 1);
                    break;
                case EXT32:
                    skipPayload(readNextLength32() + 1);
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
                    throw new MessageNeverUsedFormatException("Encountered 0xC1 \"NEVER_USED\" byte");
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
    private static MessagePackException unexpected(String expected, byte b)
    {
        MessageFormat format = MessageFormat.valueOf(b);
        if (format == MessageFormat.NEVER_USED) {
            return new MessageNeverUsedFormatException(String.format("Expected %s, but encountered 0xC1 \"NEVER_USED\" byte", expected));
        }
        else {
            String name = format.getValueType().name();
            String typeName = name.substring(0, 1) + name.substring(1).toLowerCase();
            return new MessageTypeException(String.format("Expected %s, but got %s (%02x)", expected, typeName, b));
        }
    }

    public ImmutableValue unpackValue()
            throws IOException
    {
        MessageFormat mf = getNextFormat();
        switch (mf.getValueType()) {
            case NIL:
                readByte();
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
                return ValueFactory.newString(readPayload(length), true);
            }
            case BINARY: {
                int length = unpackBinaryHeader();
                return ValueFactory.newBinary(readPayload(length), true);
            }
            case ARRAY: {
                int size = unpackArrayHeader();
                Value[] array = new Value[size];
                for (int i = 0; i < size; i++) {
                    array[i] = unpackValue();
                }
                return ValueFactory.newArray(array, true);
            }
            case MAP: {
                int size = unpackMapHeader();
                Value[] kvs = new Value[size * 2];
                for (int i = 0; i < size * 2; ) {
                    kvs[i] = unpackValue();
                    i++;
                    kvs[i] = unpackValue();
                    i++;
                }
                return ValueFactory.newMap(kvs, true);
            }
            case EXTENSION: {
                ExtensionTypeHeader extHeader = unpackExtensionTypeHeader();
                return ValueFactory.newExtension(extHeader.getType(), readPayload(extHeader.getLength()));
            }
            default:
                throw new MessageNeverUsedFormatException("Unknown value type");
        }
    }

    public Variable unpackValue(Variable var)
            throws IOException
    {
        MessageFormat mf = getNextFormat();
        switch (mf.getValueType()) {
            case NIL:
                readByte();
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
                for (int i = 0; i < size; i++) {
                    list.add(unpackValue());
                }
                var.setArrayValue(list);
                return var;
            }
            case MAP: {
                int size = unpackMapHeader();
                Map<Value, Value> map = new HashMap<Value, Value>();
                for (int i = 0; i < size; i++) {
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

    public void unpackNil()
            throws IOException
    {
        byte b = readByte();
        if (b == Code.NIL) {
            return;
        }
        throw unexpected("Nil", b);
    }

    public boolean unpackBoolean()
            throws IOException
    {
        byte b = readByte();
        if (b == Code.FALSE) {
            return false;
        }
        else if (b == Code.TRUE) {
            return true;
        }
        throw unexpected("boolean", b);
    }

    public byte unpackByte()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixInt(b)) {
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
        throw unexpected("Integer", b);
    }

    public short unpackShort()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixInt(b)) {
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
                if (u32 < 0 || u32 > Short.MAX_VALUE) {
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
        throw unexpected("Integer", b);
    }

    public int unpackInt()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixInt(b)) {
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
        throw unexpected("Integer", b);
    }

    public long unpackLong()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixInt(b)) {
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
                }
                else {
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
        throw unexpected("Integer", b);
    }

    public BigInteger unpackBigInteger()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixInt(b)) {
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
                }
                else {
                    return BigInteger.valueOf((long) u32);
                }
            case Code.UINT64: // unsigned int 64
                long u64 = readLong();
                if (u64 < 0L) {
                    BigInteger bi = BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
                    return bi;
                }
                else {
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

    public float unpackFloat()
            throws IOException
    {
        byte b = readByte();
        switch (b) {
            case Code.FLOAT32: // float
                float fv = readFloat();
                return fv;
            case Code.FLOAT64: // double
                double dv = readDouble();
                return (float) dv;
        }
        throw unexpected("Float", b);
    }

    public double unpackDouble()
            throws IOException
    {
        byte b = readByte();
        switch (b) {
            case Code.FLOAT32: // float
                float fv = readFloat();
                return (double) fv;
            case Code.FLOAT64: // double
                double dv = readDouble();
                return dv;
        }
        throw unexpected("Float", b);
    }

    private static final String EMPTY_STRING = "";

    private void resetDecoder()
    {
        if (decoder == null) {
            decodeBuffer = CharBuffer.allocate(stringDecoderBufferSize);
            decoder = MessagePack.UTF8.newDecoder()
                    .onMalformedInput(actionOnMalformedString)
                    .onUnmappableCharacter(actionOnUnmappableString);
        }
        else {
            decoder.reset();
        }
        if (decodeStringBuffer == null) {
            decodeStringBuffer = new StringBuilder();
        }
        else {
            decodeStringBuffer.setLength(0);
        }
    }

    public String unpackString()
            throws IOException
    {
        int len = unpackRawStringHeader();
        if (len == 0) {
            return EMPTY_STRING;
        }
        if (len > stringSizeLimit) {
            throw new MessageSizeException(String.format("cannot unpack a String of size larger than %,d: %,d", stringSizeLimit, len), len);
        }
        if (buffer.size() - position >= len) {
            return decodeStringFastPath(len);
        }

        resetDecoder();

        try {
            int rawRemaining = len;
            while (rawRemaining > 0) {
                int bufferRemaining = buffer.size() - position;
                if (bufferRemaining >= rawRemaining) {
                    decodeStringBuffer.append(decodeStringFastPath(rawRemaining));
                    break;
                }
                else if (bufferRemaining == 0) {
                    nextBuffer();
                }
                else {
                    ByteBuffer bb = buffer.sliceAsByteBuffer(position, bufferRemaining);
                    int bbStartPosition = bb.position();
                    decodeBuffer.clear();

                    CoderResult cr = decoder.decode(bb, decodeBuffer, false);
                    int readLen = bb.position() - bbStartPosition;
                    position += readLen;
                    rawRemaining -= readLen;
                    decodeStringBuffer.append(decodeBuffer.flip());

                    if (cr.isError()) {
                        handleCoderError(cr);
                    }
                    if (cr.isUnderflow() && readLen < bufferRemaining) {
                        // handle incomplete multibyte character
                        int incompleteMultiBytes = utf8MultibyteCharacterSize(buffer.getByte(position));
                        ByteBuffer multiByteBuffer = ByteBuffer.allocate(incompleteMultiBytes);
                        buffer.getBytes(position, buffer.size() - position, multiByteBuffer);

                        // read until multiByteBuffer is filled
                        while (true) {
                            nextBuffer();

                            int more = multiByteBuffer.remaining();
                            if (buffer.size() >= more) {
                                buffer.getBytes(0, more, multiByteBuffer);
                                position = more;
                                break;
                            }
                            else {
                                buffer.getBytes(0, buffer.size(), multiByteBuffer);
                                position = buffer.size();
                            }
                        }
                        multiByteBuffer.position(0);
                        decodeBuffer.clear();
                        cr = decoder.decode(multiByteBuffer, decodeBuffer, false);
                        if (cr.isError()) {
                            handleCoderError(cr);
                        }
                        if (cr.isOverflow() || (cr.isUnderflow() && multiByteBuffer.position() < multiByteBuffer.limit())) {
                            // isOverflow or isOverflow must not happen. if happened, throw exception
                            try {
                                cr.throwException();
                                throw new MessageFormatException("Unexpected UTF-8 multibyte sequence");
                            }
                            catch (Exception ex) {
                                throw new MessageFormatException("Unexpected UTF-8 multibyte sequence", ex);
                            }
                        }
                        rawRemaining -= multiByteBuffer.limit();
                        decodeStringBuffer.append(decodeBuffer.flip());
                    }
                }
            }
            return decodeStringBuffer.toString();
        }
        catch (CharacterCodingException e) {
            throw new MessageStringCodingException(e);
        }
    }

    private void handleCoderError(CoderResult cr)
            throws CharacterCodingException
    {
        if ((cr.isMalformed() && actionOnMalformedString == CodingErrorAction.REPORT) ||
                (cr.isUnmappable() && actionOnUnmappableString == CodingErrorAction.REPORT)) {
            cr.throwException();
        }
    }

    private String decodeStringFastPath(int length)
    {
        if (actionOnMalformedString == CodingErrorAction.REPLACE &&
                actionOnUnmappableString == CodingErrorAction.REPLACE) {
            String s = new String(buffer.array(), buffer.arrayOffset() + position, length, MessagePack.UTF8);
            position += length;
            return s;
        }
        else {
            resetDecoder();
            ByteBuffer bb = buffer.sliceAsByteBuffer();
            bb.limit(position + length);
            bb.position(position);
            CharBuffer cb;
            try {
                cb = decoder.decode(bb);
            }
            catch (CharacterCodingException e) {
                throw new MessageStringCodingException(e);
            }
            position += length;
            return cb.toString();
        }
    }

    public int unpackArrayHeader()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixedArray(b)) { // fixarray
            return b & 0x0f;
        }
        switch (b) {
            case Code.ARRAY16: { // array 16
                int len = readNextLength16();
                return len;
            }
            case Code.ARRAY32: { // array 32
                int len = readNextLength32();
                return len;
            }
        }
        throw unexpected("Array", b);
    }

    public int unpackMapHeader()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixedMap(b)) { // fixmap
            return b & 0x0f;
        }
        switch (b) {
            case Code.MAP16: { // map 16
                int len = readNextLength16();
                return len;
            }
            case Code.MAP32: { // map 32
                int len = readNextLength32();
                return len;
            }
        }
        throw unexpected("Map", b);
    }

    public ExtensionTypeHeader unpackExtensionTypeHeader()
            throws IOException
    {
        byte b = readByte();
        switch (b) {
            case Code.FIXEXT1: {
                byte type = readByte();
                return new ExtensionTypeHeader(type, 1);
            }
            case Code.FIXEXT2: {
                byte type = readByte();
                return new ExtensionTypeHeader(type, 2);
            }
            case Code.FIXEXT4: {
                byte type = readByte();
                return new ExtensionTypeHeader(type, 4);
            }
            case Code.FIXEXT8: {
                byte type = readByte();
                return new ExtensionTypeHeader(type, 8);
            }
            case Code.FIXEXT16: {
                byte type = readByte();
                return new ExtensionTypeHeader(type, 16);
            }
            case Code.EXT8: {
                MessageBuffer numberBuffer = prepareNumberBuffer(2);
                int u8 = numberBuffer.getByte(nextReadPosition);
                int length = u8 & 0xff;
                byte type = numberBuffer.getByte(nextReadPosition + 1);
                return new ExtensionTypeHeader(type, length);
            }
            case Code.EXT16: {
                MessageBuffer numberBuffer = prepareNumberBuffer(3);
                int u16 = numberBuffer.getShort(nextReadPosition);
                int length = u16 & 0xffff;
                byte type = numberBuffer.getByte(nextReadPosition + 2);
                return new ExtensionTypeHeader(type, length);
            }
            case Code.EXT32: {
                MessageBuffer numberBuffer = prepareNumberBuffer(5);
                int u32 = numberBuffer.getInt(nextReadPosition);
                if (u32 < 0) {
                    throw overflowU32Size(u32);
                }
                int length = u32;
                byte type = numberBuffer.getByte(nextReadPosition + 4);
                return new ExtensionTypeHeader(type, length);
            }
        }

        throw unexpected("Ext", b);
    }

    private int tryReadStringHeader(byte b)
            throws IOException
    {
        switch (b) {
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

    private int tryReadBinaryHeader(byte b)
            throws IOException
    {
        switch (b) {
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

    public int unpackRawStringHeader()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixedRaw(b)) { // FixRaw
            return b & 0x1f;
        }
        int len = tryReadStringHeader(b);
        if (len >= 0) {
            return len;
        }

        if (allowReadingBinaryAsString) {
            len = tryReadBinaryHeader(b);
            if (len >= 0) {
                return len;
            }
        }
        throw unexpected("String", b);
    }

    public int unpackBinaryHeader()
            throws IOException
    {
        byte b = readByte();
        if (Code.isFixedRaw(b)) { // FixRaw
            return b & 0x1f;
        }
        int len = tryReadBinaryHeader(b);
        if (len >= 0) {
            return len;
        }

        if (allowReadingStringAsBinary) {
            len = tryReadStringHeader(b);
            if (len >= 0) {
                return len;
            }
        }
        throw unexpected("Binary", b);
    }

    /**
     * Skip reading the specified number of bytes. Use this method only if you know skipping data is safe.
     * For simply skipping the next value, use {@link #skipValue()}.
     *
     * @param numBytes
     * @throws IOException
     */
    private void skipPayload(int numBytes)
            throws IOException
    {
        while (true) {
            int bufferRemaining = buffer.size() - position;
            if (bufferRemaining >= numBytes) {
                position += numBytes;
                return;
            }
            else {
                position += bufferRemaining;
                numBytes -= bufferRemaining;
            }
            nextBuffer();
        }
    }

    public void readPayload(ByteBuffer dst)
            throws IOException
    {
        while (true) {
            int dstRemaining = dst.remaining();
            int bufferRemaining = buffer.size() - position;
            if (bufferRemaining >= dstRemaining) {
                buffer.getBytes(position, dstRemaining, dst);
                position += dstRemaining;
                return;
            }
            buffer.getBytes(position, bufferRemaining, dst);
            position += bufferRemaining;

            nextBuffer();
        }
    }

    public void readPayload(byte[] dst)
            throws IOException
    {
        readPayload(dst, 0, dst.length);
    }

    public byte[] readPayload(int length)
            throws IOException
    {
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
    public void readPayload(byte[] dst, int off, int len)
            throws IOException
    {
        // TODO optimize
        readPayload(ByteBuffer.wrap(dst, off, len));
    }

    public MessageBuffer readPayloadAsReference(int length)
            throws IOException
    {
        int bufferRemaining = buffer.size() - position;
        if (bufferRemaining >= length) {
            MessageBuffer slice = buffer.slice(position, length);
            position += length;
            return slice;
        }
        MessageBuffer dst = MessageBuffer.allocate(length);
        readPayload(dst.sliceAsByteBuffer());
        return dst;
    }

    private int readNextLength8()
            throws IOException
    {
        byte u8 = readByte();
        return u8 & 0xff;
    }

    private int readNextLength16()
            throws IOException
    {
        short u16 = readShort();
        return u16 & 0xffff;
    }

    private int readNextLength32()
            throws IOException
    {
        int u32 = readInt();
        if (u32 < 0) {
            throw overflowU32Size(u32);
        }
        return u32;
    }

    @Override
    public void close()
            throws IOException
    {
        buffer = EMPTY_BUFFER;
        position = 0;
        in.close();
    }

    private static MessageIntegerOverflowException overflowU8(byte u8)
    {
        BigInteger bi = BigInteger.valueOf((long) (u8 & 0xff));
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU16(short u16)
    {
        BigInteger bi = BigInteger.valueOf((long) (u16 & 0xffff));
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU32(int u32)
    {
        BigInteger bi = BigInteger.valueOf((long) (u32 & 0x7fffffff) + 0x80000000L);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowU64(long u64)
    {
        BigInteger bi = BigInteger.valueOf(u64 + Long.MAX_VALUE + 1L).setBit(63);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI16(short i16)
    {
        BigInteger bi = BigInteger.valueOf((long) i16);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI32(int i32)
    {
        BigInteger bi = BigInteger.valueOf((long) i32);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageIntegerOverflowException overflowI64(long i64)
    {
        BigInteger bi = BigInteger.valueOf(i64);
        return new MessageIntegerOverflowException(bi);
    }

    private static MessageSizeException overflowU32Size(int u32)
    {
        long lv = (long) (u32 & 0x7fffffff) + 0x80000000L;
        return new MessageSizeException(lv);
    }
}
