package org.msgpack.core;

import java.io.IOException;

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
