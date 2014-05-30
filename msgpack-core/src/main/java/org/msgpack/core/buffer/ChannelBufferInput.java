package org.msgpack.core.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import static org.msgpack.core.Preconditions.*;

/**
 * {@link MessageBufferInput} adapter for {@link java.nio.channels.ReadableByteChannel}
 */
public class ChannelBufferInput implements MessageBufferInput {

    private final ReadableByteChannel channel;
    private boolean reachedEOF = false;
    private final int bufferSize;

    public ChannelBufferInput(ReadableByteChannel channel) {
        this(channel, 8192);
    }

    public ChannelBufferInput(ReadableByteChannel channel, int bufferSize) {
        this.channel = checkNotNull(channel, "input channel is null");
        checkArgument(bufferSize > 0, "buffer size must be > 0: " + bufferSize);
        this.bufferSize = bufferSize;
    }

    @Override
    public MessageBuffer next() throws IOException {

        if(reachedEOF)
            return null;

        MessageBuffer m = MessageBuffer.newBuffer(bufferSize);
        ByteBuffer b = m.toByteBuffer(0, m.size);
        while(b.remaining() > 0) {
            int ret = 0;
            ret = channel.read(b);
            if(ret == -1) {
                reachedEOF = true;
                break;
            }
        }
        b.flip();
        if(b.remaining() == 0) {
            return null;
        }
        else if(b.limit() < m.size) {
            return m.slice(0, b.limit());
        }
        else {
            return m;
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
