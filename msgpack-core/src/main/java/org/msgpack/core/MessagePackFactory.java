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
import java.nio.charset.CodingErrorAction;

public class MessagePackFactory
{
    private int packerSmallStringOptimizationThreshold = 512;

    private boolean unpackAllowStringAsBinary = true;
    private boolean unpackAllowBinaryAsString = true;
    private CodingErrorAction unpackActionOnMalformedString = CodingErrorAction.REPLACE;
    private CodingErrorAction unpackActionOnUnmappableString = CodingErrorAction.REPLACE;
    private int unpackStringSizeLimit = Integer.MAX_VALUE;
    private int unpackStringDecoderBufferSize = 8192;

    private int inputBufferSize = 16 * 1024;
    private int outputBufferSize = 16 * 1024;

    public MessagePacker newPacker(OutputStream out)
    {
        return newPacker(new OutputStreamBufferOutput(out));
    }

    public MessagePacker newPacker(WritableByteChannel channel)
    {
        return newPacker(new ChannelBufferOutput(channel));
    }

    public MessagePacker newPacker(MessageBufferOutput output)
    {
        return new MessagePacker(output)
                .setSmallStringOptimizationThreshold(packerSmallStringOptimizationThreshold);
    }

    public MessageBufferPacker newBufferPacker()
    {
        return new MessageBufferPacker()
                .setSmallStringOptimizationThreshold(packerSmallStringOptimizationThreshold);
    }

    public MessageUnpacker newUnpacker(byte[] contents)
    {
        return newUnpacker(contents, 0, contents.length);
    }

    public MessageUnpacker newUnpacker(byte[] contents, int offset, int length)
    {
        return newUnpacker(new ArrayBufferInput(contents, offset, length));
    }

    public MessageUnpacker newUnpacker(InputStream in)
    {
        return newUnpacker(new InputStreamBufferInput(in));
    }

    public MessageUnpacker newUnpacker(ReadableByteChannel channel)
    {
        return newUnpacker(new ChannelBufferInput(channel));
    }

    public MessageUnpacker newUnpacker(MessageBufferInput input)
    {
        return new MessageUnpacker(input)
                .setAllowStringAsBinary(unpackAllowStringAsBinary)
                .setAllowBinaryAsString(unpackAllowBinaryAsString)
                .setActionOnMalformedString(unpackActionOnMalformedString)
                .setActionOnUnmappableString(unpackActionOnUnmappableString)
                .setStringSizeLimit(unpackStringSizeLimit)
                .setStringDecoderBufferSize(unpackStringDecoderBufferSize);
    }

    /**
     * Use String.getBytes() for strings smaller than this threshold.
     * Note that this parameter is subject to change.
     */
    public MessagePackFactory packerSmallStringOptimizationThreshold(int bytes)
    {
        this.packerSmallStringOptimizationThreshold = bytes;
        return this;
    }

    public MessagePackFactory unpackAllowStringAsBinary(boolean enabled)
    {
        this.unpackAllowStringAsBinary = enabled;
        return this;
    }

    public MessagePackFactory unpackAllowBinaryAsString(boolean enabled)
    {
        this.unpackAllowBinaryAsString = enabled;
        return this;
    }

    public MessagePackFactory unpackActionOnMalformedString(CodingErrorAction action)
    {
        this.unpackActionOnMalformedString = action;
        return this;
    }

    public MessagePackFactory unpackActionOnUnmappableString(CodingErrorAction action)
    {
        this.unpackActionOnUnmappableString = action;
        return this;
    }

    public MessagePackFactory unpackStringSizeLimit(int bytes)
    {
        this.unpackStringSizeLimit = bytes;
        return this;
    }

    public MessagePackFactory unpackStringDecoderBufferSize(int bytes)
    {
        this.unpackStringDecoderBufferSize = bytes;
        return this;
    }

    public MessagePackFactory inputBufferSize(int bytes)
    {
        this.inputBufferSize = bytes;
        return this;
    }

    public MessagePackFactory outputBufferSize(int bytes)
    {
        this.inputBufferSize = bytes;
        return this;
    }

    private int getPackerSmallStringOptimizationThreshold()
    {
        return packerSmallStringOptimizationThreshold;
    }

    private boolean getUnpackAllowStringAsBinary()
    {
        return unpackAllowStringAsBinary;
    }

    private boolean getUnpackAllowBinaryAsString()
    {
        return unpackAllowBinaryAsString;
    }

    private CodingErrorAction getUnpackActionOnMalformedString()
    {
        return unpackActionOnMalformedString;
    }

    private CodingErrorAction getUnpackActionOnUnmappableString()
    {
        return unpackActionOnUnmappableString;
    }

    private int getUnpackStringSizeLimit()
    {
        return unpackStringSizeLimit;
    }

    private int getUnpackStringDecoderBufferSize()
    {
        return unpackStringDecoderBufferSize;
    }

    private int getInputBufferSize()
    {
        return inputBufferSize;
    }

    private int getOutputBufferSize()
    {
        return outputBufferSize;
    }
}
