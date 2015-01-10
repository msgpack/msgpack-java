package org.msgpack.core.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferOutput} adapter for {@link java.nio.channels.WritableByteChannel}
 */
public class ChannelBufferOutput implements MessageBufferOutput {

    private WritableByteChannel channel;
    private MessageBuffer buffer;

    public ChannelBufferOutput(WritableByteChannel channel) {
        this.channel = checkNotNull(channel, "output channel is null");
    }

    public void reset(WritableByteChannel channel) throws IOException {
        this.channel = channel;
    }


    @Override
    public MessageBuffer next(int bufferSize) throws IOException {
        if(buffer == null || buffer.size() != bufferSize) {
            buffer = MessageBuffer.newBuffer(bufferSize);
        }
        return buffer;
    }

    @Override
    public void flush(MessageBuffer buf) throws IOException {
        ByteBuffer bb = buf.toByteBuffer();
        channel.write(bb);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
