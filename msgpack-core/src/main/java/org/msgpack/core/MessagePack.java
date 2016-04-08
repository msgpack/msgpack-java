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

import org.msgpack.core.buffer.ArrayBufferInput;
import org.msgpack.core.buffer.ChannelBufferInput;
import org.msgpack.core.buffer.ChannelBufferOutput;
import org.msgpack.core.buffer.InputStreamBufferInput;
import org.msgpack.core.buffer.MessageBufferInput;
import org.msgpack.core.buffer.MessageBufferOutput;
import org.msgpack.core.buffer.OutputStreamBufferOutput;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

/**
 * This class has MessagePack prefix code definitions and packer/unpacker factory methods.
 */
public class MessagePack
{
    public static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * Default packer/unpacker configurations
     */
    public static final PackerConfig DEFAULT_PACKER_CONFIG = new PackerConfig();
    public static final UnpackerConfig DEFAULT_UNPACKER_CONFIG = new UnpackerConfig();

    /**
     * The prefix code set of MessagePack. See also https://github.com/msgpack/msgpack/blob/master/spec.md for details.
     */
    public static final class Code
    {
        public static final boolean isFixInt(byte b)
        {
            int v = b & 0xFF;
            return v <= 0x7f || v >= 0xe0;
        }

        public static final boolean isPosFixInt(byte b)
        {
            return (b & POSFIXINT_MASK) == 0;
        }

        public static final boolean isNegFixInt(byte b)
        {
            return (b & NEGFIXINT_PREFIX) == NEGFIXINT_PREFIX;
        }

        public static final boolean isFixStr(byte b)
        {
            return (b & (byte) 0xe0) == Code.FIXSTR_PREFIX;
        }

        public static final boolean isFixedArray(byte b)
        {
            return (b & (byte) 0xf0) == Code.FIXARRAY_PREFIX;
        }

        public static final boolean isFixedMap(byte b)
        {
            return (b & (byte) 0xf0) == Code.FIXMAP_PREFIX;
        }

        public static final boolean isFixedRaw(byte b)
        {
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

    private MessagePack()
    {
        // Prohibit instantiation of this class
    }

    /**
     * Create a packer that outputs the packed data to the specified output
     *
     * @param out
     * @return
     */
    public static MessagePacker newDefaultPacker(MessageBufferOutput out)
    {
        return DEFAULT_PACKER_CONFIG.newPacker(out);
    }

    /**
     * Create a packer that outputs the packed data to a target output stream
     *
     * @param out
     * @return
     */
    public static MessagePacker newDefaultPacker(OutputStream out)
    {
        return DEFAULT_PACKER_CONFIG.newPacker(out);
    }

    /**
     * Create a packer that outputs the packed data to a channel
     *
     * @param channel
     * @return
     */
    public static MessagePacker newDefaultPacker(WritableByteChannel channel)
    {
        return DEFAULT_PACKER_CONFIG.newPacker(channel);
    }

    /**
     * Create a packer for storing packed data into a byte array
     *
     * @return
     */
    public static MessageBufferPacker newDefaultBufferPacker()
    {
        return DEFAULT_PACKER_CONFIG.newBufferPacker();
    }

    /**
     * Create an unpacker that reads the data from a given input
     *
     * @param in
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(MessageBufferInput in)
    {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(in);
    }

    /**
     * Create an unpacker that reads the data from a given input stream
     *
     * @param in
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(InputStream in)
    {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(in);
    }

    /**
     * Create an unpacker that reads the data from a given channel
     *
     * @param channel
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(ReadableByteChannel channel)
    {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(channel);
    }

    /**
     * Create an unpacker that reads the data from a given byte array
     *
     * @param contents
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(byte[] contents)
    {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(contents);
    }

    /**
     * Create an unpacker that reads the data from a given byte array [offset, offset+length)
     *
     * @param contents
     * @param offset
     * @param length
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(byte[] contents, int offset, int length)
    {
        return DEFAULT_UNPACKER_CONFIG.newUnpacker(contents, offset, length);
    }

    /**
     * MessagePacker configuration.
     */
    public static class PackerConfig
            implements Cloneable
    {
        private int smallStringOptimizationThreshold = 512;

        private int bufferFlushThreshold = 8192;

        private int bufferSize = 8192;

        private boolean str8FormatSupport = true;

        public PackerConfig()
        {
        }

        private PackerConfig(PackerConfig copy)
        {
            this.smallStringOptimizationThreshold = copy.smallStringOptimizationThreshold;
            this.bufferFlushThreshold = copy.bufferFlushThreshold;
            this.bufferSize = copy.bufferSize;
            this.str8FormatSupport = copy.str8FormatSupport;
        }

        @Override
        public PackerConfig clone()
        {
            return new PackerConfig(this);
        }

        @Override
        public int hashCode()
        {
            int result = smallStringOptimizationThreshold;
            result = 31 * result + bufferFlushThreshold;
            result = 31 * result + bufferSize;
            result = 31 * result + (str8FormatSupport ? 1 : 0);
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof PackerConfig)) {
                return false;
            }
            PackerConfig o = (PackerConfig) obj;
            return this.smallStringOptimizationThreshold == o.smallStringOptimizationThreshold
                    && this.bufferFlushThreshold == o.bufferFlushThreshold
                    && this.bufferSize == o.bufferSize
                    && this.str8FormatSupport == o.str8FormatSupport;
        }

        /**
         * Create a packer that outputs the packed data to a given output
         *
         * @param out
         * @return
         */
        public MessagePacker newPacker(MessageBufferOutput out)
        {
            return new MessagePacker(out, this);
        }

        /**
         * Create a packer that outputs the packed data to a given output stream
         *
         * @param out
         * @return
         */
        public MessagePacker newPacker(OutputStream out)
        {
            return newPacker(new OutputStreamBufferOutput(out, bufferSize));
        }

        /**
         * Create a packer that outputs the packed data to a given output channel
         *
         * @param channel
         * @return
         */
        public MessagePacker newPacker(WritableByteChannel channel)
        {
            return newPacker(new ChannelBufferOutput(channel, bufferSize));
        }

        /**
         * Create a packer for storing packed data into a byte array
         *
         * @return
         */
        public MessageBufferPacker newBufferPacker()
        {
            return new MessageBufferPacker(this);
        }

        /**
         * Use String.getBytes() for converting Java Strings that are smaller than this threshold into UTF8.
         * Note that this parameter is subject to change.
         */
        public PackerConfig withSmallStringOptimizationThreshold(int bytes)
        {
            PackerConfig copy = clone();
            copy.smallStringOptimizationThreshold = bytes;
            return copy;
        }

        public int getSmallStringOptimizationThreshold()
        {
            return smallStringOptimizationThreshold;
        }

        /**
         * When the next payload size exceeds this threshold, MessagePacker will call MessageBufferOutput.flush() before
         * packing the data (default: 8192).
         */
        public PackerConfig withBufferFlushThreshold(int bytes)
        {
            PackerConfig copy = clone();
            copy.bufferFlushThreshold = bytes;
            return copy;
        }

        public int getBufferFlushThreshold()
        {
            return bufferFlushThreshold;
        }

        /**
         * When a packer is created with newPacker(OutputStream) or newPacker(WritableByteChannel), the stream will be
         * buffered with this size of buffer (default: 8192).
         */
        public PackerConfig withBufferSize(int bytes)
        {
            PackerConfig copy = clone();
            copy.bufferSize = bytes;
            return copy;
        }

        public int getBufferSize()
        {
            return bufferSize;
        }

        /**
         * Disable str8 format when needed backward compatibility between
         * different msgpack serializer versions.
         * default true (str8 supported enabled)
         */
        public PackerConfig withStr8FormatSupport(boolean str8FormatSupport)
        {
            PackerConfig copy = clone();
            copy.str8FormatSupport = str8FormatSupport;
            return copy;
        }

        public boolean isStr8FormatSupport()
        {
            return str8FormatSupport;
        }
    }

    /**
     * MessageUnpacker configuration.
     */
    public static class UnpackerConfig
            implements Cloneable
    {
        private boolean allowReadingStringAsBinary = true;

        private boolean allowReadingBinaryAsString = true;

        private CodingErrorAction actionOnMalformedString = CodingErrorAction.REPLACE;

        private CodingErrorAction actionOnUnmappableString = CodingErrorAction.REPLACE;

        private int stringSizeLimit = Integer.MAX_VALUE;

        private int bufferSize = 8192;

        private int stringDecoderBufferSize = 8192;

        public UnpackerConfig()
        {
        }

        private UnpackerConfig(UnpackerConfig copy)
        {
            this.allowReadingStringAsBinary = copy.allowReadingStringAsBinary;
            this.allowReadingBinaryAsString = copy.allowReadingBinaryAsString;
            this.actionOnMalformedString = copy.actionOnMalformedString;
            this.actionOnUnmappableString = copy.actionOnUnmappableString;
            this.stringSizeLimit = copy.stringSizeLimit;
            this.bufferSize = copy.bufferSize;
        }

        @Override
        public UnpackerConfig clone()
        {
            return new UnpackerConfig(this);
        }

        @Override
        public int hashCode()
        {
            int result = (allowReadingStringAsBinary ? 1 : 0);
            result = 31 * result + (allowReadingBinaryAsString ? 1 : 0);
            result = 31 * result + (actionOnMalformedString != null ? actionOnMalformedString.hashCode() : 0);
            result = 31 * result + (actionOnUnmappableString != null ? actionOnUnmappableString.hashCode() : 0);
            result = 31 * result + stringSizeLimit;
            result = 31 * result + bufferSize;
            result = 31 * result + stringDecoderBufferSize;
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof UnpackerConfig)) {
                return false;
            }
            UnpackerConfig o = (UnpackerConfig) obj;
            return this.allowReadingStringAsBinary == o.allowReadingStringAsBinary
                    && this.allowReadingBinaryAsString == o.allowReadingBinaryAsString
                    && this.actionOnMalformedString == o.actionOnMalformedString
                    && this.actionOnUnmappableString == o.actionOnUnmappableString
                    && this.stringSizeLimit == o.stringSizeLimit
                    && this.stringDecoderBufferSize == o.stringDecoderBufferSize
                    && this.bufferSize == o.bufferSize;
        }

        /**
         * Create an unpacker that reads the data from a given input
         *
         * @param in
         * @return
         */
        public MessageUnpacker newUnpacker(MessageBufferInput in)
        {
            return new MessageUnpacker(in, this);
        }

        /**
         * Create an unpacker that reads the data from a given input stream
         *
         * @param in
         * @return
         */
        public MessageUnpacker newUnpacker(InputStream in)
        {
            return newUnpacker(new InputStreamBufferInput(in, bufferSize));
        }

        /**
         * Create an unpacker that reads the data from a given channel
         *
         * @param channel
         * @return
         */
        public MessageUnpacker newUnpacker(ReadableByteChannel channel)
        {
            return newUnpacker(new ChannelBufferInput(channel, bufferSize));
        }

        /**
         * Create an unpacker that reads the data from a given byte array
         *
         * @param contents
         * @return
         */
        public MessageUnpacker newUnpacker(byte[] contents)
        {
            return newUnpacker(new ArrayBufferInput(contents));
        }

        /**
         * Create an unpacker that reads the data from a given byte array [offset, offset+size)
         *
         * @param contents
         * @return
         */
        public MessageUnpacker newUnpacker(byte[] contents, int offset, int length)
        {
            return newUnpacker(new ArrayBufferInput(contents, offset, length));
        }

        /**
         * Allow unpackBinaryHeader to read str format family  (default: true)
         */
        public UnpackerConfig withAllowReadingStringAsBinary(boolean enable)
        {
            UnpackerConfig copy = clone();
            copy.allowReadingStringAsBinary = enable;
            return copy;
        }

        public boolean getAllowReadingStringAsBinary()
        {
            return allowReadingStringAsBinary;
        }

        /**
         * Allow unpackString and unpackRawStringHeader and unpackString to read bin format family (default: true)
         */
        public UnpackerConfig withAllowReadingBinaryAsString(boolean enable)
        {
            UnpackerConfig copy = clone();
            copy.allowReadingBinaryAsString = enable;
            return copy;
        }

        public boolean getAllowReadingBinaryAsString()
        {
            return allowReadingBinaryAsString;
        }

        /**
         * Action when encountered a malformed input (default: REPLACE)
         */
        public UnpackerConfig withActionOnMalformedString(CodingErrorAction action)
        {
            UnpackerConfig copy = clone();
            copy.actionOnMalformedString = action;
            return copy;
        }

        public CodingErrorAction getActionOnMalformedString()
        {
            return actionOnMalformedString;
        }

        /**
         * Action when an unmappable character is found (default: REPLACE)
         */
        public UnpackerConfig withActionOnUnmappableString(CodingErrorAction action)
        {
            UnpackerConfig copy = clone();
            copy.actionOnUnmappableString = action;
            return copy;
        }

        public CodingErrorAction getActionOnUnmappableString()
        {
            return actionOnUnmappableString;
        }

        /**
         * unpackString size limit (default: Integer.MAX_VALUE).
         */
        public UnpackerConfig withStringSizeLimit(int bytes)
        {
            UnpackerConfig copy = clone();
            copy.stringSizeLimit = bytes;
            return copy;
        }

        public int getStringSizeLimit()
        {
            return stringSizeLimit;
        }

        /**
         *
         */
        public UnpackerConfig withStringDecoderBufferSize(int bytes)
        {
            UnpackerConfig copy = clone();
            copy.stringDecoderBufferSize = bytes;
            return copy;
        }

        public int getStringDecoderBufferSize()
        {
            return stringDecoderBufferSize;
        }

        /**
         * When a packer is created with newUnpacker(OutputStream) or newUnpacker(WritableByteChannel), the stream will be
         * buffered with this size of buffer (default: 8192).
         */
        public UnpackerConfig withBufferSize(int bytes)
        {
            UnpackerConfig copy = clone();
            copy.bufferSize = bytes;
            return copy;
        }

        public int getBufferSize()
        {
            return bufferSize;
        }
    }
}
