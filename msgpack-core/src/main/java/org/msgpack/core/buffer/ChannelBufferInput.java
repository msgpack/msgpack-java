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
package org.msgpack.core.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import static org.msgpack.core.Preconditions.checkArgument;
import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferInput} adapter for {@link java.nio.channels.ReadableByteChannel}
 */
public class ChannelBufferInput
        implements MessageBufferInput
{
    private ReadableByteChannel channel;
    private final MessageBuffer buffer;

    public ChannelBufferInput(ReadableByteChannel channel)
    {
        this(channel, 8192);
    }

    public ChannelBufferInput(ReadableByteChannel channel, int bufferSize)
    {
        this.channel = checkNotNull(channel, "input channel is null");
        checkArgument(bufferSize > 0, "buffer size must be > 0: " + bufferSize);
        this.buffer = MessageBuffer.allocate(bufferSize);
    }

    /**
     * Reset channel. This method doesn't close the old resource.
     *
     * @param channel new channel
     * @return the old resource
     */
    public ReadableByteChannel reset(ReadableByteChannel channel)
            throws IOException
    {
        ReadableByteChannel old = this.channel;
        this.channel = channel;
        return old;
    }

    @Override
    public MessageBuffer next()
            throws IOException
    {
        ByteBuffer b = buffer.sliceAsByteBuffer();
        int ret = channel.read(b);
        if (ret == -1) {
            return null;
        }
        b.flip();
        return buffer.slice(0, b.limit());
    }

    @Override
    public void close()
            throws IOException
    {
        channel.close();
    }
}
