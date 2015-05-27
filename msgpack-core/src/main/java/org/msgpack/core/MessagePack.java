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

import org.msgpack.core.buffer.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

import static org.msgpack.core.Preconditions.checkArgument;

/**
 * This class has MessagePack prefix code definitions and packer/unpacker factory methods.
 *
 */
public class MessagePack {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * Message packer/unpacker configuration object
     */
    public static class Config {
        private final boolean readStringAsBinary;
        private final boolean readBinaryAsString;
        private final CodingErrorAction onMalFormedInput;
        private final CodingErrorAction onUnmappableCharacter;
        private final int maxUnpackStringSize;
        private final int stringEncoderBufferSize;
        private final int stringDecoderBufferSize;
        private final int packerBufferSize;
        private final int packerRawDataCopyingThreshold;

        public Config(
                boolean readStringAsBinary,
                boolean readBinaryAsString,
                CodingErrorAction onMalFormedInput,
                CodingErrorAction onUnmappableCharacter,
                int maxUnpackStringSize,
                int stringEncoderBufferSize,
                int stringDecoderBufferSize,
                int packerBufferSize,
                int packerRawDataCopyingThreshold) {

            checkArgument(packerBufferSize > 0, "packer buffer size must be larger than 0: " + packerBufferSize);
            checkArgument(stringEncoderBufferSize > 0, "string encoder buffer size must be larger than 0: " + stringEncoderBufferSize);
            checkArgument(stringDecoderBufferSize > 0, "string decoder buffer size must be larger than 0: " + stringDecoderBufferSize);

            this.readStringAsBinary = readStringAsBinary;
            this.readBinaryAsString = readBinaryAsString;
            this.onMalFormedInput = onMalFormedInput;
            this.onUnmappableCharacter = onUnmappableCharacter;
            this.maxUnpackStringSize = maxUnpackStringSize;
            this.stringEncoderBufferSize = stringEncoderBufferSize;
            this.stringDecoderBufferSize = stringDecoderBufferSize;
            this.packerBufferSize = packerBufferSize;
            this.packerRawDataCopyingThreshold = packerRawDataCopyingThreshold;
        }

        /**
         * allow unpackBinaryHeader to read str format family  (default:true)
         */
        public boolean isReadStringAsBinary() { return readStringAsBinary; }

        /**
         * allow unpackRawStringHeader and unpackString to read bin format family (default: true)
         */
        public boolean isReadBinaryAsString() { return readBinaryAsString; }
        /**
         * Action when encountered a malformed input
         */
        public CodingErrorAction getActionOnMalFormedInput() { return onMalFormedInput; }
        /**
         * Action when an unmappable character is found
         */
        public CodingErrorAction getActionOnUnmappableCharacter() { return onUnmappableCharacter; }

        /**
         * unpackString size limit. (default: Integer.MAX_VALUE)
         */
        public int getMaxUnpackStringSize() { return maxUnpackStringSize; }

        public int getStringEncoderBufferSize() { return stringEncoderBufferSize; }
        public int getStringDecoderBufferSize() { return stringDecoderBufferSize; }

        public int getPackerBufferSize() { return packerBufferSize; }
        public int getPackerRawDataCopyingThreshold() { return packerRawDataCopyingThreshold; }
    }

    /**
     * Builder of the configuration object
     */
    public static class ConfigBuilder {

        private boolean readStringAsBinary = true;
        private boolean readBinaryAsString = true;

        private CodingErrorAction onMalFormedInput = CodingErrorAction.REPLACE;
        private CodingErrorAction onUnmappableCharacter = CodingErrorAction.REPLACE;

        private int maxUnpackStringSize = Integer.MAX_VALUE;
        private int stringEncoderBufferSize = 8192;
        private int stringDecoderBufferSize = 8192;
        private int packerBufferSize = 8192;
        private int packerRawDataCopyingThreshold = 512;

        public Config build() {
            return new Config(
                    readStringAsBinary,
                    readBinaryAsString,
                    onMalFormedInput,
                    onUnmappableCharacter,
                    maxUnpackStringSize,
                    stringEncoderBufferSize,
                    stringDecoderBufferSize,
                    packerBufferSize,
                    packerRawDataCopyingThreshold
            );
        }

        public ConfigBuilder readStringAsBinary(boolean enable) {
            this.readStringAsBinary = enable;
            return this;
        }
        public ConfigBuilder readBinaryAsString(boolean enable) {
            this.readBinaryAsString = enable;
            return this;
        }
        public ConfigBuilder onMalFormedInput(CodingErrorAction action) {
            this.onMalFormedInput = action;
            return this;
        }
        public ConfigBuilder onUnmappableCharacter(CodingErrorAction action) {
            this.onUnmappableCharacter = action;
            return this;
        }
        public ConfigBuilder maxUnpackStringSize(int size){
            this.maxUnpackStringSize = size;
            return this;
        }
        public ConfigBuilder stringEncoderBufferSize(int size) {
            this.stringEncoderBufferSize = size;
            return this;
        }
        public ConfigBuilder stringDecoderBufferSize(int size) {
            this.stringDecoderBufferSize = size;
            return this;
        }
        public ConfigBuilder packerBufferSize(int size) {
            this.packerBufferSize = size;
            return this;
        }
        public ConfigBuilder packerRawDataCopyingThreshold(int threshold) {
            this.packerRawDataCopyingThreshold = threshold;
            return this;
        }
    }



    /**
     * Default configuration, which is visible only from classes in the core package.
     */
    static final Config DEFAULT_CONFIG = new ConfigBuilder().build();


    /**
     * The prefix code set of MessagePack. See also https://github.com/msgpack/msgpack/blob/master/spec.md for details.
     */
    public static final class Code {

        public static final boolean isFixInt(byte b) {
            int v = b & 0xFF;
            return v <= 0x7f || v >= 0xe0;
        }

        public static final boolean isPosFixInt(byte b) {
            return (b & POSFIXINT_MASK) == 0;
        }
        public static final boolean isNegFixInt(byte b) {
            return (b & NEGFIXINT_PREFIX) == NEGFIXINT_PREFIX;
        }
        public static final boolean isFixStr(byte b) {
            return (b & (byte) 0xe0) == Code.FIXSTR_PREFIX;
        }
        public static final boolean isFixedArray(byte b) {
            return (b & (byte) 0xf0) == Code.FIXARRAY_PREFIX;
        }
        public static final boolean isFixedMap(byte b) {
            return (b & (byte) 0xe0) == Code.FIXMAP_PREFIX;
        }

        public static final boolean isFixedRaw(byte b) {
            return (b & (byte) 0xe0) == Code.FIXSTR_PREFIX;
        }

        public static final byte POSFIXINT_MASK = (byte) 0x80;

        public static final byte FIXMAP_PREFIX = (byte) 0x80;
        public static final byte FIXARRAY_PREFIX = (byte) 0x90;
        public static final byte FIXSTR_PREFIX = (byte) 0xa0;

        public static final byte NIL = (byte) 0xc0;
        public static final byte NEVER_USED = (byte) 0xc1;
        public static final byte FALSE = (byte) 0xc2;
        public static final byte TRUE = (byte) 0xc3;
        public static final byte BIN8 = (byte) 0xc4;
        public static final byte BIN16 = (byte) 0xc5;
        public static final byte BIN32 = (byte) 0xc6;
        public static final byte EXT8 = (byte) 0xc7;
        public static final byte EXT16 = (byte) 0xc8;
        public static final byte EXT32 = (byte) 0xc9;
        public static final byte FLOAT32 = (byte) 0xca;
        public static final byte FLOAT64 = (byte) 0xcb;
        public static final byte UINT8 = (byte) 0xcc;
        public static final byte UINT16 = (byte) 0xcd;
        public static final byte UINT32 = (byte) 0xce;
        public static final byte UINT64 = (byte) 0xcf;

        public static final byte INT8 = (byte) 0xd0;
        public static final byte INT16 = (byte) 0xd1;
        public static final byte INT32 = (byte) 0xd2;
        public static final byte INT64 = (byte) 0xd3;

        public static final byte FIXEXT1 = (byte) 0xd4;
        public static final byte FIXEXT2 = (byte) 0xd5;
        public static final byte FIXEXT4 = (byte) 0xd6;
        public static final byte FIXEXT8 = (byte) 0xd7;
        public static final byte FIXEXT16 = (byte) 0xd8;

        public static final byte STR8 = (byte) 0xd9;
        public static final byte STR16 = (byte) 0xda;
        public static final byte STR32 = (byte) 0xdb;

        public static final byte ARRAY16 = (byte) 0xdc;
        public static final byte ARRAY32 = (byte) 0xdd;

        public static final byte MAP16 = (byte) 0xde;
        public static final byte MAP32 = (byte) 0xdf;

        public static final byte NEGFIXINT_PREFIX = (byte) 0xe0;
    }

    // Packer/Unpacker factory methods

    private final MessagePack.Config config;

    public MessagePack() {
        this(MessagePack.DEFAULT_CONFIG);
    }

    public MessagePack(MessagePack.Config config) {
        this.config = config;
    }

    /**
     * Default MessagePack packer/unpacker factory
     */
    public static final MessagePack DEFAULT = new MessagePack(MessagePack.DEFAULT_CONFIG);


    /**
     * Create a MessagePacker that outputs the packed data to the specified stream, using the default configuration
     * @param out
     * @return
     */
    public static MessagePacker newDefaultPacker(OutputStream out) {
        return DEFAULT.newPacker(out);
    }

    /**
     * Create a MessagePacker that outputs the packed data to the specified channel, using the default configuration
     * @param channel
     * @return
     */
    public static MessagePacker newDefaultPacker(WritableByteChannel channel) {
        return DEFAULT.newPacker(channel);
    }

    /**
     * Create a MessageUnpacker that reads data from then given InputStream, using the default configuration
     * @param in
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(InputStream in) {
        return DEFAULT.newUnpacker(in);
    }

    /**
     * Create a MessageUnpacker that reads data from the given channel, using the default configuration
     * @param channel
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(ReadableByteChannel channel) {
        return DEFAULT.newUnpacker(channel);
    }

    /**
     * Create a MessageUnpacker that reads data from the given byte array, using the default configuration
     * @param arr
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(byte[] arr) {
        return DEFAULT.newUnpacker(arr);
    }

    /**
     * Create a MessageUnpacker that reads data form the given byte array [offset, .. offset+length), using the default
     * configuration.
     * @param arr
     * @param offset
     * @param length
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(byte[] arr, int offset, int length) {
        return DEFAULT.newUnpacker(arr, offset, length);
    }


    /**
     * Create a MessagePacker that outputs the packed data to the specified stream
     * @param out
     */
    public MessagePacker newPacker(OutputStream out) {
        return new MessagePacker(new OutputStreamBufferOutput(out), config);
    }

    /**
     * Create a MessagePacker that outputs the packed data to the specified channel
     * @param channel
     */
    public MessagePacker newPacker(WritableByteChannel channel) {
        return new MessagePacker(new ChannelBufferOutput(channel), config);
    }

    /**
     * Create a MessageUnpacker that reads data from the given InputStream.
     * For reading data efficiently from byte[], use {@link MessageUnpacker(byte[])} or {@link MessageUnpacker(byte[], int, int)} instead of this constructor.
     *
     * @param in
     */
    public MessageUnpacker newUnpacker(InputStream in) {
        return new MessageUnpacker(InputStreamBufferInput.newBufferInput(in), config);
    }

    /**
     * Create a MessageUnpacker that reads data from the given ReadableByteChannel.
     * @param in
     */
    public MessageUnpacker newUnpacker(ReadableByteChannel in) {
        return new MessageUnpacker(new ChannelBufferInput(in), config);
    }


    /**
     * Create a MessageUnpacker that reads data from the given byte array.
     *
     * @param arr
     */
    public MessageUnpacker newUnpacker(byte[] arr) {
        return new MessageUnpacker(new ArrayBufferInput(arr), config);
    }

    /**
     * Create a MessageUnpacker that reads data from the given byte array [offset, offset+length)
     * @param arr
     * @param offset
     * @param length
     */
    public MessageUnpacker newUnpacker(byte[] arr, int offset, int length) {
        return new MessageUnpacker(new ArrayBufferInput(arr, offset, length), config);
    }




}
