package org.msgpack.core.buffer;

import java.io.Closeable;
import java.io.IOException;

/**
 * Provides a sequence of MessageBuffers that contains message packed data.
 */
public interface MessageBufferInput
        extends Closeable
{
    /**
     * Get a next buffer to read.
     *
     * @return the next MessageBuffer, or null if no more buffer is available.
     * @throws IOException when error occurred when reading the data
     */
    public MessageBuffer next()
            throws IOException;
}
