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

import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.core.buffer.MessageBufferOutput;
import org.msgpack.value.Value;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

import static org.msgpack.core.MessagePack.Code.ARRAY16;
import static org.msgpack.core.MessagePack.Code.ARRAY32;
import static org.msgpack.core.MessagePack.Code.BIN16;
import static org.msgpack.core.MessagePack.Code.BIN32;
import static org.msgpack.core.MessagePack.Code.BIN8;
import static org.msgpack.core.MessagePack.Code.EXT16;
import static org.msgpack.core.MessagePack.Code.EXT32;
import static org.msgpack.core.MessagePack.Code.EXT8;
import static org.msgpack.core.MessagePack.Code.FALSE;
import static org.msgpack.core.MessagePack.Code.FIXARRAY_PREFIX;
import static org.msgpack.core.MessagePack.Code.FIXEXT1;
import static org.msgpack.core.MessagePack.Code.FIXEXT16;
import static org.msgpack.core.MessagePack.Code.FIXEXT2;
import static org.msgpack.core.MessagePack.Code.FIXEXT4;
import static org.msgpack.core.MessagePack.Code.FIXEXT8;
import static org.msgpack.core.MessagePack.Code.FIXMAP_PREFIX;
import static org.msgpack.core.MessagePack.Code.FIXSTR_PREFIX;
import static org.msgpack.core.MessagePack.Code.FLOAT32;
import static org.msgpack.core.MessagePack.Code.FLOAT64;
import static org.msgpack.core.MessagePack.Code.INT16;
import static org.msgpack.core.MessagePack.Code.INT32;
import static org.msgpack.core.MessagePack.Code.INT64;
import static org.msgpack.core.MessagePack.Code.INT8;
import static org.msgpack.core.MessagePack.Code.MAP16;
import static org.msgpack.core.MessagePack.Code.MAP32;
import static org.msgpack.core.MessagePack.Code.NIL;
import static org.msgpack.core.MessagePack.Code.STR16;
import static org.msgpack.core.MessagePack.Code.STR32;
import static org.msgpack.core.MessagePack.Code.STR8;
import static org.msgpack.core.MessagePack.Code.TRUE;
import static org.msgpack.core.MessagePack.Code.UINT16;
import static org.msgpack.core.MessagePack.Code.UINT32;
import static org.msgpack.core.MessagePack.Code.UINT64;
import static org.msgpack.core.MessagePack.Code.UINT8;
import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * Writer of message packed data.
 * <p/>
 * <p>
 * MessagePacker provides packXXX methods for writing values in the message pack format.
 * To write raw string or binary data, first use packRawStringHeader or packBinaryHeader to specify the data length,
 * then call writePayload(...) method.
 * </p>
 * <p/>
 * <p>
 * MessagePacker class has no guarantee to produce the correct message-pack format data if it is not used correctly:
 * packXXX methods of primitive values always produce the correct format, but
 * packXXXHeader (e.g. array, map, ext) must be followed by correct number of array/map/ext type values.
 * packRawStringHeader(length) and packBinaryHeader(length) must be followed by writePayload( ... length) to supply
 * the binary data of the specified length in the header.
 * </p>
 */
public class MessagePacker
        implements Closeable
{
    private final MessagePack.Config config;

    private MessageBufferOutput out;

    private MessageBuffer buffer;

    private int position;

    /**
     * Total written byte size
     */
    private long totalFlushBytes;

    /**
     * String encoder
     */
    private CharsetEncoder encoder;

    /**
     * Create an MessagePacker that outputs the packed data to the given {@link org.msgpack.core.buffer.MessageBufferOutput}
     *
     * @param out MessageBufferOutput. Use {@link org.msgpack.core.buffer.OutputStreamBufferOutput}, {@link org.msgpack.core.buffer.ChannelBufferOutput} or
     * your own implementation of {@link org.msgpack.core.buffer.MessageBufferOutput} interface.
     */
    public MessagePacker(MessageBufferOutput out)
    {
        this(out, MessagePack.DEFAULT_CONFIG);
    }

    public MessagePacker(MessageBufferOutput out, MessagePack.Config config)
    {
        this.config = checkNotNull(config, "config is null");
        this.out = checkNotNull(out, "MessageBufferOutput is null");
        this.position = 0;
        this.totalFlushBytes = 0;
    }

    /**
     * Reset output. This method doesn't close the old resource.
     *
     * @param out new output
     * @return the old resource
     */
    public MessageBufferOutput reset(MessageBufferOutput out)
            throws IOException
    {
        // Validate the argument
        MessageBufferOutput newOut = checkNotNull(out, "MessageBufferOutput is null");

        // Flush before reset
        flush();
        MessageBufferOutput old = this.out;
        this.out = newOut;

        // Reset totalFlushBytes
        this.totalFlushBytes = 0;

        return old;
    }

    public long getTotalWrittenBytes()
    {
        return totalFlushBytes + position;
    }

    public void flush()
            throws IOException
    {
        if (position > 0) {
            out.writeBuffer(position);
            buffer = null;
            totalFlushBytes += position;
            position = 0;
        }
        out.flush();
    }

    public void close()
            throws IOException
    {
        try {
            flush();
        }
        finally {
            out.close();
        }
    }

    private void ensureCapacity(int mimimumSize)
            throws IOException
    {
        if (buffer == null) {
            buffer = out.next(mimimumSize);
        }
        else if (position + mimimumSize >= buffer.size()) {
            out.writeBuffer(position);
            buffer = null;
            totalFlushBytes += position;
            position = 0;
            buffer = out.next(mimimumSize);
        }
    }

    private void writeByte(byte b)
            throws IOException
    {
        ensureCapacity(1);
        buffer.putByte(position++, b);
    }

    private void writeByteAndByte(byte b, byte v)
            throws IOException
    {
        ensureCapacity(2);
        buffer.putByte(position++, b);
        buffer.putByte(position++, v);
    }

    private void writeByteAndShort(byte b, short v)
            throws IOException
    {
        ensureCapacity(3);
        buffer.putByte(position++, b);
        buffer.putShort(position, v);
        position += 2;
    }

    private void writeByteAndInt(byte b, int v)
            throws IOException
    {
        ensureCapacity(5);
        buffer.putByte(position++, b);
        buffer.putInt(position, v);
        position += 4;
    }

    private void writeByteAndFloat(byte b, float v)
            throws IOException
    {
        ensureCapacity(5);
        buffer.putByte(position++, b);
        buffer.putFloat(position, v);
        position += 4;
    }

    private void writeByteAndDouble(byte b, double v)
            throws IOException
    {
        ensureCapacity(9);
        buffer.putByte(position++, b);
        buffer.putDouble(position, v);
        position += 8;
    }

    private void writeByteAndLong(byte b, long v)
            throws IOException
    {
        ensureCapacity(9);
        buffer.putByte(position++, b);
        buffer.putLong(position, v);
        position += 8;
    }

    private void writeShort(short v)
            throws IOException
    {
        ensureCapacity(2);
        buffer.putShort(position, v);
        position += 2;
    }

    private void writeInt(int v)
            throws IOException
    {
        ensureCapacity(4);
        buffer.putInt(position, v);
        position += 4;
    }

    private void writeLong(long v)
            throws IOException
    {
        ensureCapacity(8);
        buffer.putLong(position, v);
        position += 8;
    }

    public MessagePacker packNil()
            throws IOException
    {
        writeByte(NIL);
        return this;
    }

    public MessagePacker packBoolean(boolean b)
            throws IOException
    {
        writeByte(b ? TRUE : FALSE);
        return this;
    }

    public MessagePacker packByte(byte b)
            throws IOException
    {
        if (b < -(1 << 5)) {
            writeByteAndByte(INT8, b);
        }
        else {
            writeByte(b);
        }
        return this;
    }

    public MessagePacker packShort(short v)
            throws IOException
    {
        if (v < -(1 << 5)) {
            if (v < -(1 << 7)) {
                writeByteAndShort(INT16, v);
            }
            else {
                writeByteAndByte(INT8, (byte) v);
            }
        }
        else if (v < (1 << 7)) {
            writeByte((byte) v);
        }
        else {
            if (v < (1 << 8)) {
                writeByteAndByte(UINT8, (byte) v);
            }
            else {
                writeByteAndShort(UINT16, v);
            }
        }
        return this;
    }

    public MessagePacker packInt(int r)
            throws IOException
    {
        if (r < -(1 << 5)) {
            if (r < -(1 << 15)) {
                writeByteAndInt(INT32, r);
            }
            else if (r < -(1 << 7)) {
                writeByteAndShort(INT16, (short) r);
            }
            else {
                writeByteAndByte(INT8, (byte) r);
            }
        }
        else if (r < (1 << 7)) {
            writeByte((byte) r);
        }
        else {
            if (r < (1 << 8)) {
                writeByteAndByte(UINT8, (byte) r);
            }
            else if (r < (1 << 16)) {
                writeByteAndShort(UINT16, (short) r);
            }
            else {
                // unsigned 32
                writeByteAndInt(UINT32, r);
            }
        }
        return this;
    }

    public MessagePacker packLong(long v)
            throws IOException
    {
        if (v < -(1L << 5)) {
            if (v < -(1L << 15)) {
                if (v < -(1L << 31)) {
                    writeByteAndLong(INT64, v);
                }
                else {
                    writeByteAndInt(INT32, (int) v);
                }
            }
            else {
                if (v < -(1 << 7)) {
                    writeByteAndShort(INT16, (short) v);
                }
                else {
                    writeByteAndByte(INT8, (byte) v);
                }
            }
        }
        else if (v < (1 << 7)) {
            // fixnum
            writeByte((byte) v);
        }
        else {
            if (v < (1L << 16)) {
                if (v < (1 << 8)) {
                    writeByteAndByte(UINT8, (byte) v);
                }
                else {
                    writeByteAndShort(UINT16, (short) v);
                }
            }
            else {
                if (v < (1L << 32)) {
                    writeByteAndInt(UINT32, (int) v);
                }
                else {
                    writeByteAndLong(UINT64, v);
                }
            }
        }
        return this;
    }

    public MessagePacker packBigInteger(BigInteger bi)
            throws IOException
    {
        if (bi.bitLength() <= 63) {
            packLong(bi.longValue());
        }
        else if (bi.bitLength() == 64 && bi.signum() == 1) {
            writeByteAndLong(UINT64, bi.longValue());
        }
        else {
            throw new IllegalArgumentException("MessagePack cannot serialize BigInteger larger than 2^64-1");
        }
        return this;
    }

    public MessagePacker packFloat(float v)
            throws IOException
    {
        writeByteAndFloat(FLOAT32, v);
        return this;
    }

    public MessagePacker packDouble(double v)
            throws IOException
    {
        writeByteAndDouble(FLOAT64, v);
        return this;
    }

    private void packStringByGetBytes(String s)
            throws IOException
    {
        byte[] bytes = s.getBytes(MessagePack.UTF8);
        packRawStringHeader(bytes.length);
        addPayload(bytes);
    }

    private void prepareEncoder()
    {
        if (encoder == null) {
            this.encoder = MessagePack.UTF8.newEncoder().onMalformedInput(config.actionOnMalFormedInput).onUnmappableCharacter(config.actionOnMalFormedInput);
        }
    }

    private int encodeStringToBufferAt(int pos, String s)
    {
        prepareEncoder();
        ByteBuffer bb = buffer.toByteBuffer(pos, buffer.size() - pos);
        int startPosition = bb.position();
        CharBuffer in = CharBuffer.wrap(s);
        CoderResult cr = encoder.encode(in, bb, true);
        if (cr.isError()) {
            try {
                cr.throwException();
            }
            catch (CharacterCodingException e) {
                throw new MessageStringCodingException(e);
            }
        }
        if (cr.isUnderflow() || cr.isOverflow()) {
            return -1;
        }
        return bb.position() - startPosition;
    }

    private static final int UTF_8_MAX_CHAR_SIZE = 6;

    /**
     * Pack the input String in UTF-8 encoding
     *
     * @param s
     * @return
     * @throws IOException
     */
    public MessagePacker packString(String s)
            throws IOException
    {
        if (s.length() <= 0) {
            packRawStringHeader(0);
            return this;
        }
        else if (s.length() < config.packerSmallStringOptimizationThreshold) {
            // Write the length and payload of small string to the buffer so that it avoids an extra flush of buffer
            packStringByGetBytes(s);
            return this;
        }
        else if (s.length() < (1 << 8)) {
            // ensure capacity for 2-byte raw string header + the maximum string size (+ 1 byte for falback code)
            ensureCapacity(2 + s.length() * UTF_8_MAX_CHAR_SIZE + 1);
            // keep 2-byte header region and write raw string
            int written = encodeStringToBufferAt(position + 2, s);
            if (written >= 0) {
                if (written < (1 << 8)) {
                    buffer.putByte(position++, STR8);
                    buffer.putByte(position++, (byte) written);
                    position += written;
                }
                else {
                    if (written >= (1 << 16)) {
                        // this must not happen because s.length() is less than 2^8 and (2^8) * UTF_8_MAX_CHAR_SIZE is less than 2^16
                        throw new IllegalArgumentException("Unexpected UTF-8 encoder state");
                    }
                    // move 1 byte backward to expand 3-byte header region to 3 bytes
                    buffer.putBytes(position + 3,
                            buffer.getArray(), buffer.offset() + position + 2, written);
                    // write 3-byte header header
                    buffer.putByte(position++, STR16);
                    buffer.putShort(position, (short) written);
                    position += 2;
                    position += written;
                }
                return this;
            }
        }
        else if (s.length() < (1 << 16)) {
            // ensure capacity for 3-byte raw string header + the maximum string size (+ 2 bytes for falback code)
            ensureCapacity(3 + s.length() * UTF_8_MAX_CHAR_SIZE + 2);
            // keep 3-byte header region and write raw string
            int written = encodeStringToBufferAt(position + 3, s);
            if (written >= 0) {
                if (written < (1 << 16)) {
                    buffer.putByte(position++, STR16);
                    buffer.putShort(position, (short) written);
                    position += 2;
                    position += written;
                }
                else {
                    if (written >= (1 << 32)) {
                        // this must not happen because s.length() is less than 2^16 and (2^16) * UTF_8_MAX_CHAR_SIZE is less than 2^32
                        throw new IllegalArgumentException("Unexpected UTF-8 encoder state");
                    }
                    // move 2 bytes backward to expand 3-byte header region to 5 bytes
                    buffer.putBytes(position + 5,
                            buffer.getArray(), buffer.offset() + position + 3, written);
                    // write 3-byte header header
                    buffer.putByte(position++, STR32);
                    buffer.putInt(position, written);
                    position += 4;
                    position += written;
                }
                return this;
            }
        }

        // Here doesn't use above optimized code for s.length() < (1 << 32) so that
        // ensureCapacity is not called with an integer larger than (3 + ((1 << 16) * UTF_8_MAX_CHAR_SIZE) + 2).
        // This makes it sure that MessageBufferOutput.next won't be called a size larger than
        // 384KB, which is OK size to keep in memory.

        // fallback
        packStringByGetBytes(s);
        return this;
    }

    public MessagePacker packArrayHeader(int arraySize)
            throws IOException
    {
        if (arraySize < 0) {
            throw new IllegalArgumentException("array size must be >= 0");
        }

        if (arraySize < (1 << 4)) {
            writeByte((byte) (FIXARRAY_PREFIX | arraySize));
        }
        else if (arraySize < (1 << 16)) {
            writeByteAndShort(ARRAY16, (short) arraySize);
        }
        else {
            writeByteAndInt(ARRAY32, arraySize);
        }
        return this;
    }

    public MessagePacker packMapHeader(int mapSize)
            throws IOException
    {
        if (mapSize < 0) {
            throw new IllegalArgumentException("map size must be >= 0");
        }

        if (mapSize < (1 << 4)) {
            writeByte((byte) (FIXMAP_PREFIX | mapSize));
        }
        else if (mapSize < (1 << 16)) {
            writeByteAndShort(MAP16, (short) mapSize);
        }
        else {
            writeByteAndInt(MAP32, mapSize);
        }
        return this;
    }

    public MessagePacker packValue(Value v)
            throws IOException
    {
        v.writeTo(this);
        return this;
    }

    public MessagePacker packExtensionTypeHeader(byte extType, int payloadLen)
            throws IOException
    {
        if (payloadLen < (1 << 8)) {
            if (payloadLen > 0 && (payloadLen & (payloadLen - 1)) == 0) { // check whether dataLen == 2^x
                if (payloadLen == 1) {
                    writeByteAndByte(FIXEXT1, extType);
                }
                else if (payloadLen == 2) {
                    writeByteAndByte(FIXEXT2, extType);
                }
                else if (payloadLen == 4) {
                    writeByteAndByte(FIXEXT4, extType);
                }
                else if (payloadLen == 8) {
                    writeByteAndByte(FIXEXT8, extType);
                }
                else if (payloadLen == 16) {
                    writeByteAndByte(FIXEXT16, extType);
                }
                else {
                    writeByteAndByte(EXT8, (byte) payloadLen);
                    writeByte(extType);
                }
            }
            else {
                writeByteAndByte(EXT8, (byte) payloadLen);
                writeByte(extType);
            }
        }
        else if (payloadLen < (1 << 16)) {
            writeByteAndShort(EXT16, (short) payloadLen);
            writeByte(extType);
        }
        else {
            writeByteAndInt(EXT32, payloadLen);
            writeByte(extType);

            // TODO support dataLen > 2^31 - 1
        }
        return this;
    }

    public MessagePacker packBinaryHeader(int len)
            throws IOException
    {
        if (len < (1 << 8)) {
            writeByteAndByte(BIN8, (byte) len);
        }
        else if (len < (1 << 16)) {
            writeByteAndShort(BIN16, (short) len);
        }
        else {
            writeByteAndInt(BIN32, len);
        }
        return this;
    }

    public MessagePacker packRawStringHeader(int len)
            throws IOException
    {
        if (len < (1 << 5)) {
            writeByte((byte) (FIXSTR_PREFIX | len));
        }
        else if (len < (1 << 8)) {
            writeByteAndByte(STR8, (byte) len);
        }
        else if (len < (1 << 16)) {
            writeByteAndShort(STR16, (short) len);
        }
        else {
            writeByteAndInt(STR32, len);
        }
        return this;
    }

    /**
     * Writes buffer to the output.
     * This method is used with packRawStringHeader or packBinaryHeader.
     *
     * @param src the data to add
     * @return this
     * @throws IOException
     */
    public MessagePacker writePayload(byte[] src)
            throws IOException
    {
        return writePayload(src, 0, src.length);
    }

    /**
     * Writes buffer to the output.
     * This method is used with packRawStringHeader or packBinaryHeader.
     *
     * @param src the data to add
     * @param off the start offset in the data
     * @param len the number of bytes to add
     * @return this
     * @throws IOException
     */
    public MessagePacker writePayload(byte[] src, int off, int len)
            throws IOException
    {
        if (buffer.size() - position < len || len > 8192) {
            flush();  // call flush before write
            out.write(src, off, len);
            totalFlushBytes += len;
        }
        else {
            buffer.putBytes(position, src, off, len);
            position += len;
        }
        return this;
    }

    /**
     * Writes buffer to the output.
     * Unlike writePayload method, addPayload method doesn't copy the source data. It means that the caller
     * must not modify the data after calling this method.
     *
     * @param src the data to add
     * @return this
     * @throws IOException
     */
    public MessagePacker addPayload(byte[] src)
            throws IOException
    {
        return addPayload(src, 0, src.length);
    }

    /**
     * Writes buffer to the output.
     * Unlike writePayload method, addPayload method doesn't copy the source data. It means that the caller
     * must not modify the data after calling this method.
     *
     * @param src the data to add
     * @param off the start offset in the data
     * @param len the number of bytes to add
     * @return this
     * @throws IOException
     */
    public MessagePacker addPayload(byte[] src, int off, int len)
            throws IOException
    {
        if (buffer.size() - position < len || len > 8192) {
            flush();  // call flush before add
            out.add(src, off, len);
            totalFlushBytes += len;
        }
        else {
            buffer.putBytes(position, src, off, len);
            position += len;
        }
        return this;
    }
}
