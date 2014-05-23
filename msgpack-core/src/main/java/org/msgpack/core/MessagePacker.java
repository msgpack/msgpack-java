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
import org.msgpack.core.buffer.OutputStreamBufferOutput;

import java.io.Closeable;
import java.io.OutputStream;
import java.math.BigInteger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

import static org.msgpack.core.MessagePack.Code.*;
import static org.msgpack.core.Preconditions.*;

/**
 * Writer of message packed data.
 *
 * <p>
 * MessagePacker provides packXXX methods for writing values in the message pack format.
 * To write raw string or binary data, first use packRawStringHeader or packBinaryHeader to specify the data length,
 * then call writePayload(...) method.
 * </p>
 *
 * <p>
 * MessagePacker class has no guarantee to produce the correct message-pack format data if it is not used correctly:
 * packXXX methods of primitive values always produce the correct format, but
 * packXXXHeader (e.g. array, map, ext) must be followed by correct number of array/map/ext type values.
 * packRawStringHeader(length) and packBinaryHeader(length) must be followed by writePayload( ... length) to supply
 * the binary data of the specified length in the header.
 * </p>
 *
 */
public class MessagePacker implements Closeable {

    private final MessagePack.Config config;

    private final MessageBufferOutput out;
    private final MessageBuffer buffer;
    private int position;


    /**
     * Buffer for encoding UTF8 strings
     */
    private ByteBuffer encodeBuffer;

    private final ByteBuffer defaultEncodeBuffer;

    /**
     * String encoder
     */
    private final CharsetEncoder encoder;

    public MessagePacker(OutputStream out) {
        this(new OutputStreamBufferOutput(out));
    }


    public MessagePacker(MessageBufferOutput out) {
        this(out, MessagePack.DEFAULT_CONFIG);
    }

    public MessagePacker(MessageBufferOutput out, MessagePack.Config config) {
        this.config = checkNotNull(config, "config is null");
        checkArgument(config.PACKER_BUFFER_SIZE > 0, "packer buffer size must be larger than 0: " + config.PACKER_BUFFER_SIZE);
        checkArgument(config.STRING_ENCODER_BUFFER_SIZE > 0, "string encoder buffer size must be larger than 0: " + config.STRING_ENCODER_BUFFER_SIZE);
        this.out = checkNotNull(out, "MessageBufferOutput is null");
        this.buffer = MessageBuffer.newDirectBuffer(config.PACKER_BUFFER_SIZE);
        this.position = 0;
        this.defaultEncodeBuffer = ByteBuffer.allocate(config.STRING_ENCODER_BUFFER_SIZE);
        this.encodeBuffer = this.defaultEncodeBuffer;
        this.encoder = MessagePack.UTF8.newEncoder().onMalformedInput(config.MALFORMED_INPUT_ACTION).onUnmappableCharacter(config.UNMAPPABLE_CHARACTER_ACTION);
    }


    public void flush() throws IOException {
        out.flush(buffer, 0, position);
        position = 0;
    }

    private void flushBuffer(MessageBuffer b) throws IOException {
        out.flush(b, 0, b.size());
    }

    public void close() throws IOException {
        try {
            flush();
        }
        finally {
            out.close();
        }
    }

    private void ensureCapacity(int numBytesToWrite) throws IOException {
        if(position + numBytesToWrite < buffer.size())
            return;

        flush();
    }


    private void writeByte(byte b) throws IOException {
        ensureCapacity(1);
        buffer.putByte(position++, b);
    }


    private void writeByteAndByte(byte b, byte v) throws IOException {
        ensureCapacity(2);
        buffer.putByte(position++, b);
        buffer.putByte(position++, v);
    }

    private void writeByteAndShort(byte b, short v) throws IOException {
        ensureCapacity(3);
        buffer.putByte(position++, b);
        buffer.putShort(position, v);
        position += 2;
    }

    private void writeByteAndInt(byte b, int v) throws IOException {
        ensureCapacity(5);
        buffer.putByte(position++, b);
        buffer.putInt(position, v);
        position += 4;
    }

    private void writeByteAndFloat(byte b, float v) throws IOException {
        ensureCapacity(5);
        buffer.putByte(position++, b);
        buffer.putFloat(position, v);
        position += 4;
    }

    private void writeByteAndDouble(byte b, double v) throws IOException {
        ensureCapacity(9);
        buffer.putByte(position++, b);
        buffer.putDouble(position, v);
        position += 8;
    }

    private void writeByteAndLong(byte b, long v) throws IOException {
        ensureCapacity(9);
        buffer.putByte(position++, b);
        buffer.putLong(position, v);
        position += 8;
    }

    private void writeShort(short v) throws IOException {
        ensureCapacity(2);
        buffer.putShort(position, v);
        position += 2;
    }

    private void writeInt(int v) throws IOException {
        ensureCapacity(4);
        buffer.putInt(position, v);
        position += 4;
    }

    private void writeLong(long v) throws IOException {
        ensureCapacity(8);
        buffer.putLong(position, v);
        position += 8;
    }

    public MessagePacker packNil() throws IOException {
        writeByte(NIL);
        return this;
    }

    public MessagePacker packBoolean(boolean b) throws IOException {
        writeByte(b ? TRUE : FALSE);
        return this;
    }


    public MessagePacker packByte(byte b) throws IOException {
        if(b < -(1 << 5)) {
            writeByteAndByte(INT8, b);
        } else {
            writeByte(b);
        }
        return this;
    }

    public MessagePacker packShort(short v) throws IOException {
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
        return this;
    }

    public MessagePacker packInt(int r) throws IOException {
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
        return this;
    }

    public MessagePacker packLong(long v) throws IOException {
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
        return this;
    }

    public MessagePacker packBigInteger(BigInteger bi) throws IOException {
        if(bi.bitLength() <= 63) {
            packLong(bi.longValue());
        } else if(bi.bitLength() == 64 && bi.signum() == 1) {
            writeByteAndLong(UINT64, bi.longValue());
        } else {
            throw new IllegalArgumentException("Messagepack cannot serialize BigInteger larger than 2^64-1");
        }
        return this;
    }
    
    public MessagePacker packFloat(float v) throws IOException {
        writeByteAndFloat(FLOAT32, v);
        return this;
    }
 
    public MessagePacker packDouble(double v) throws IOException {
        writeByteAndDouble(FLOAT64, v);
        return this;
    }

    /**
     * Pack the input String in UTF-8 encoding
     *
     * @param s
     * @return
     * @throws IOException
     */
    public MessagePacker packString(String s) throws IOException {
        if(s.length() > 0) {
            CharBuffer in = CharBuffer.wrap(s);
            encodeBuffer.clear();
            encoder.reset();
            while(in.hasRemaining()) {
                try {
                    CoderResult cr = encoder.encode(in, encodeBuffer, true);

                    if(cr.isUnderflow()) {
                        cr = encoder.flush(encodeBuffer);
                    }

                    if(cr.isOverflow()) {
                        // Allocate a larger buffer
                        int estimatedRemainingSize = Math.max(1, (int) (in.remaining() * encoder.averageBytesPerChar()));
                        encodeBuffer.flip();
                        ByteBuffer newBuffer = ByteBuffer.allocate(encodeBuffer.remaining() + estimatedRemainingSize);
                        newBuffer.put(encodeBuffer);
                        encodeBuffer = newBuffer;
                        continue;
                    }

                    if(cr.isError()) {
                        if(cr.isMalformed() && config.MALFORMED_INPUT_ACTION == CodingErrorAction.REPORT) {
                            cr.throwException();
                        } else if(cr.isUnderflow() && config.MALFORMED_INPUT_ACTION == CodingErrorAction.REPORT) {
                            cr.throwException();
                        }
                    }
                }
                catch(CharacterCodingException e) {
                    throw new MessageStringCodingException(e);
                }
            }
            encodeBuffer.flip();
            packRawStringHeader(encodeBuffer.remaining());
            writePayload(encodeBuffer);
        }
        else {
            packRawStringHeader(0);
        }
        encodeBuffer = defaultEncodeBuffer;
        return this;
    }

    public MessagePacker packArrayHeader(int arraySize) throws IOException {
        if(arraySize < 0)
            throw new IllegalArgumentException("array size must be >= 0");

        if(arraySize < (1 << 4)) {
            writeByte((byte) (FIXARRAY_PREFIX | arraySize));
        } else if(arraySize < (1 << 16)) {
            writeByteAndShort(ARRAY16, (short) arraySize);
        } else {
            writeByteAndInt(ARRAY32, arraySize);
        }
        return this;
    }

    public MessagePacker packMapHeader(int mapSize) throws IOException {
        if(mapSize < 0)
            throw new IllegalArgumentException("map size must be >= 0");

        if(mapSize < (1 << 4)) {
            writeByte((byte) (FIXMAP_PREFIX | mapSize));
        } else if(mapSize < (1 << 16)) {
            writeByteAndShort(MAP16, (short) mapSize);
        } else {
            writeByteAndInt(MAP32, mapSize);
        }
        return this;
    }

    public MessagePacker packExtendedTypeHeader(int extType, int dataLen) throws IOException {
        if(dataLen < (1 << 8)) {
            if(dataLen > 0 && (dataLen & (dataLen - 1)) == 0) { // check whether dataLen == 2^x
                if(dataLen == 1) {
                    writeByteAndByte(FIXEXT1, (byte) extType);
                } else if(dataLen == 2){
                    writeByteAndByte(FIXEXT2, (byte) extType);
                } else if(dataLen == 4) {
                    writeByteAndByte(FIXEXT4, (byte) extType);
                } else if(dataLen == 8) {
                    writeByteAndByte(FIXEXT8, (byte) extType);
                } else {
                    writeByteAndByte(FIXEXT16, (byte) extType);
                }
            } else {
                writeByteAndByte(EXT8, (byte) dataLen);
                writeByte((byte) extType);
            }
        } else if(dataLen < (1 << 16)) {
            writeByteAndShort(EXT16, (short) dataLen);
            writeByte((byte) extType);
        } else {
            writeByteAndInt(EXT32, dataLen);
            writeByte((byte) extType);

            // TODO support dataLen > 2^31 - 1
        }
        return this;
    }

    public MessagePacker packBinaryHeader(int len) throws IOException {
        if(len < (1 << 8)) {
            writeByteAndByte(BIN8, (byte) len);
        } else if(len < (1 << 16)) {
            writeByteAndShort(BIN16, (short) len);
        } else {
            writeByteAndInt(BIN32, len);
        }
        return this;
    }

    public MessagePacker packRawStringHeader(int len) throws IOException {
        if(len < (1 << 5)) {
            writeByte((byte) (FIXSTR_PREFIX | len));
        } else if(len < (1 << 8)) {
            writeByteAndByte(STR8, (byte) len);
        } else if(len < (1 << 16)) {
            writeByteAndShort(STR16, (short) len);
        } else {
            writeByteAndInt(STR32, len);
        }
        return this;
    }


    public MessagePacker writePayload(ByteBuffer src) throws IOException {
        if(src.remaining() >= config.PACKER_FLUSH_THRESHOLD) {
            // Use the source ByteBuffer directly to avoid memory copy

            // First, flush the current buffer contents
            flush();

            // Wrap the input source as a MessageBuffer
            MessageBuffer wrapped = MessageBuffer.wrap(src).slice(src.position(), src.remaining());
            // Then, dump the source data to the output
            flushBuffer(wrapped);
            src.position(src.limit());
        }
        else {
            // If the input source is small, simply copy the contents to the buffer
            while(src.remaining() > 0) {
                if(position >= buffer.size())
                    flush();
                int writeLen = Math.min(buffer.size() - position, src.remaining());
                buffer.putByteBuffer(position, src, writeLen);
                position += writeLen;
            }
        }
        return this;
    }

    public MessagePacker writePayload(byte[] src) throws IOException {
        return writePayload(src, 0, src.length);
    }

    public MessagePacker writePayload(byte[] src, int off, int len) throws IOException {
        if(len >= config.PACKER_FLUSH_THRESHOLD) {
            // Use the input array directory to avoid memory copy

            // Flush the current buffer contents
            flush();

            // Wrap the input array as a MessageBuffer
            MessageBuffer wrapped = MessageBuffer.wrap(src).slice(off, len);
            // Dump the source data to the output
            flushBuffer(wrapped);
        }
        else {
            int cursor = 0;
            while(cursor < len) {
                if(position >= buffer.size())
                    flush();
                int writeLen = Math.min(buffer.size() - position, len - cursor);
                buffer.putBytes(position, src, off + cursor, writeLen);
                position += writeLen;
                cursor += writeLen;
            }
        }
        return this;
    }


}
