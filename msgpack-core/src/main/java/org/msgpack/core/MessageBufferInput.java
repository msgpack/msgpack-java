package org.msgpack.core;

import java.io.IOException;
import java.io.InputStream;

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



class MessageBufferInputStream implements MessageBufferInput {

    private final InputStream in;

    MessageBufferInputStream(InputStream in) {
        if(in == null)
            throw new NullPointerException("MessageBufferInputStream: input is null");
        this.in = in;
    }

    @Override
    public MessageBuffer next() throws IOException {




        return null;
    }

    @Override
    public void close() throws IOException {

    }
}