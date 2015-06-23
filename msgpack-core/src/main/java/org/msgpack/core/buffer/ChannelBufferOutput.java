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
import java.nio.channels.WritableByteChannel;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferOutput} adapter for {@link java.nio.channels.WritableByteChannel}
 */
public class ChannelBufferOutput
        implements MessageBufferOutput
{
    private WritableByteChannel channel;
    private MessageBuffer buffer;

    public ChannelBufferOutput(WritableByteChannel channel)
    {
        this.channel = checkNotNull(channel, "output channel is null");
    }

    /**
     * Reset channel. This method doesn't close the old resource.
     *
     * @param channel new channel
     * @return the old resource
     */
    public WritableByteChannel reset(WritableByteChannel channel)
            throws IOException
    {
        WritableByteChannel old = this.channel;
        this.channel = channel;
        return old;
    }

    @Override
    public MessageBuffer next(int bufferSize)
            throws IOException
    {
        if (buffer == null || buffer.size() != bufferSize) {
            buffer = MessageBuffer.newBuffer(bufferSize);
        }
        return buffer;
    }

    @Override
    public void flush(MessageBuffer buf)
            throws IOException
    {
        ByteBuffer bb = buf.toByteBuffer();
        channel.write(bb);
    }

    @Override
    public void close()
            throws IOException
    {
        channel.close();
    }
}
