package org.msgpack.core.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * {@link MessageBufferOutput} adapter for {@link java.nio.channels.WritableByteChannel}
 */
public class ChannelBufferOutput implements MessageBufferOutput {

    private final WritableByteChannel channel;

    public ChannelBufferOutput(WritableByteChannel channel) {
        this.channel = channel;
    }

    @Override
    public void flush(MessageBuffer buf, int offset, int len) throws IOException {
        assert(offset + len < buf.size());
        ByteBuffer bb = buf.toByteBuffer(offset, len);
        channel.write(bb);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
