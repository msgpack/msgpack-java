package org.msgpack.core.buffer;

import java.io.Closeable;
import java.io.IOException;

/**
 * Provides a sequence of MessageBuffers that contains message packed data.
 */
public interface MessageBufferInput extends Closeable {

    /**
     * Get a next buffer to read
     * @return
     * @throws IOException
     */
    public MessageBuffer next() throws IOException;


}


