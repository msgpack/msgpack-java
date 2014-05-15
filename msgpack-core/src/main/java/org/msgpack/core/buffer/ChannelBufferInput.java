package org.msgpack.core.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferInput} adapter for {@link java.nio.channels.ReadableByteChannel}
 */
public class ChannelBufferInput implements MessageBufferInput {

    private final ReadableByteChannel channel;

    public ChannelBufferInput(ReadableByteChannel channel) {
        this.channel = checkNotNull(channel, "input channel is null");
    }

    @Override
    public MessageBuffer next() throws IOException {
        MessageBuffer m = MessageBuffer.newBuffer(8192);
        ByteBuffer b = m.toByteBuffer(0, m.size);
        for(int ret = 0; (ret = channel.read(b)) != -1; ) {
        }
        b.flip();
        if(b.remaining() < m.size)
            return m.slice(0, b.remaining());
        else
            return m;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
