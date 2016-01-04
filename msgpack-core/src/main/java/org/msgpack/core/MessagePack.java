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
        return new PackerConfig().newPacker(out);
    }

    /**
     * Create a packer that outputs the packed data to a target output stream
     *
     * @param out
     * @return
     */
    public static MessagePacker newDefaultPacker(OutputStream out)
    {
        return new PackerConfig().newPacker(out);
    }

    /**
     * Create a packer that outputs the packed data to a channel
     *
     * @param channel
     * @return
     */
    public static MessagePacker newDefaultPacker(WritableByteChannel channel)
    {
        return new PackerConfig().newPacker(channel);
    }

    /**
     * Create a packer for storing packed data into a byte array
     *
     * @return
     */
    public static MessageBufferPacker newDefaultBufferPacker()
    {
        return new PackerConfig().newBufferPacker();
    }

    /**
     * Create an unpacker that reads the data from a given input
     *
     * @param in
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(MessageBufferInput in)
    {
        return new UnpackerConfig().newUnpacker(in);
    }

    /**
     * Create an unpacker that reads the data from a given input stream
     *
     * @param in
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(InputStream in)
    {
        return new UnpackerConfig().newUnpacker(in);
    }

    /**
     * Create an unpacker that reads the data from a given channel
     *
     * @param channel
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(ReadableByteChannel channel)
    {
        return new UnpackerConfig().newUnpacker(channel);
    }

    /**
     * Create an unpacker that reads the data from a given byte array
     *
     * @param contents
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(byte[] contents)
    {
        return new UnpackerConfig().newUnpacker(contents);
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
        return new UnpackerConfig().newUnpacker(contents, offset, length);
    }

    /**
     * MessagePacker configuration.
     */
    public static class PackerConfig
    {
        /**
         * Use String.getBytes() for converting Java Strings that are smaller than this threshold into UTF8.
         * Note that this parameter is subject to change.
         */
        public int smallStringOptimizationThreshold = 512;

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
            return newPacker(new OutputStreamBufferOutput(out));
        }

        /**
         * Create a packer that outputs the packed data to a given output channel
         *
         * @param channel
         * @return
         */
        public MessagePacker newPacker(WritableByteChannel channel)
        {
            return newPacker(new ChannelBufferOutput(channel));
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
    }

    /**
     * MessageUnpacker configuration.
     */
    public static class UnpackerConfig
    {
        /**
         * Allow unpackBinaryHeader to read str format family  (default:true)
         */
        public boolean allowReadingStringAsBinary = true;

        /**
         * Allow unpackRawStringHeader and unpackString to read bin format family (default: true)
         */
        public boolean allowReadingBinaryAsString = true;

        /**
         * Action when encountered a malformed input
         */
        public CodingErrorAction actionOnMalformedString = CodingErrorAction.REPLACE;

        /**
         * Action when an unmappable character is found
         */
        public CodingErrorAction actionOnUnmappableString = CodingErrorAction.REPLACE;

        /**
         * unpackString size limit. (default: Integer.MAX_VALUE)
         */
        public int stringSizeLimit = Integer.MAX_VALUE;

        /**
         *
         */
        public int stringDecoderBufferSize = 8192;

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
            return newUnpacker(new InputStreamBufferInput(in));
        }

        /**
         * Create an unpacker that reads the data from a given channel
         *
         * @param channel
         * @return
         */
        public MessageUnpacker newUnpacker(ReadableByteChannel channel)
        {
            return newUnpacker(new ChannelBufferInput(channel));
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
    }
}
