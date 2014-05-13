package org.msgpack.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import static org.msgpack.core.Preconditions.*;


/**
 * Provides a sequence of MessageBuffers that contains message packed data.
 */
public interface MessageBufferInput {

    /**
     * Get a next buffer to read
     * @return
     * @throws IOException
     */
    public MessageBuffer next() throws IOException;

    /**
     * Close this buffer input
     * @throws IOException
     */
    public void close() throws IOException;

}

class MessageBufferInputArray implements MessageBufferInput {

    private MessageBuffer buffer;
    private boolean isRead = false;

    MessageBufferInputArray(byte[] arr) {
        this.buffer = MessageBuffer.wrap(checkNotNull(arr, "input array is null"));
    }

    @Override
    public MessageBuffer next() throws IOException {
        if(isRead) {
            return null;
        } else {
            isRead = true;
            return buffer;
        }
    }

    @Override
    public void close() throws IOException {
        buffer = null;
    }
}

class MessageBufferInputStream implements MessageBufferInput {

    private final InputStream in;
    private byte[] buffer = new byte[8192];

    MessageBufferInputStream(InputStream in) {
        this.in = checkNotNull(in, "input is null");
    }

    @Override
    public MessageBuffer next() throws IOException {
        // Manage the allocated buffers
        MessageBuffer m = MessageBuffer.newBuffer(buffer.length);

        // TODO reduce the number of memory copy
        int cursor = 0;
        while(cursor < buffer.length) {
            int readLen = in.read(buffer, cursor, buffer.length - cursor);
            if(readLen == -1) {
                break;
            }
            cursor += readLen;
        }
        m.putBytes(0, buffer, 0, cursor);
        return m;
    }

    @Override
    public void close() throws IOException {
        try {
            in.close();
        }
        finally {
            buffer = null;
        }
    }
}

class MessageBufferInputChannel implements MessageBufferInput {

    private final ReadableByteChannel channel;

    MessageBufferInputChannel(ReadableByteChannel channel) {
        this.channel = checkNotNull(channel, "input channel is null");
    }

    @Override
    public MessageBuffer next() throws IOException {
        MessageBuffer m = MessageBuffer.newBuffer(8192);
        ByteBuffer b = m.toByteBuffer(0, m.size);
        channel.read(b);
        b.flip();
        m.setLimit(b.remaining());
        return m;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
