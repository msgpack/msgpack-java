package org.msgpack.core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import static org.msgpack.core.Preconditions.*;

/**
 * Provides a sequence of MessageBuffers for packing the input data
 */
public interface MessageBufferOutput {

//    /**
//     * Retrieves the next buffer for writing message packed data
//     * @return
//     * @throws IOException
//     */
//    public MessageBuffer next() throws IOException;


    public void flush(MessageBuffer buf, int offset, int len) throws IOException;

    /**
     * Flush and close this buffer.
     * @throws IOException
     */
    public void close() throws IOException;


}


class MessageBufferOutputStream implements MessageBufferOutput {

    private final OutputStream out;

    MessageBufferOutputStream(OutputStream out) {
        this.out = checkNotNull(out, "output is null");
    }

    @Override
    public void flush(MessageBuffer buf, int offset, int len) throws IOException {
        assert(offset + len < buf.size());

        // TODO reuse the allocated buffer
        byte[] in = new byte[len];
        buf.getBytes(offset, in, 0, len);
        out.write(in, 0, len);
    }

    @Override
    public void close() throws IOException {
        try {
            out.flush();
        }
        finally {
            out.close();
        }
    }
}


class MessageBufferOutputChannel implements MessageBufferOutput {

    private final WritableByteChannel channel;

    MessageBufferOutputChannel(WritableByteChannel channel) {
        this.channel = checkNotNull(channel, "output channel is null");
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