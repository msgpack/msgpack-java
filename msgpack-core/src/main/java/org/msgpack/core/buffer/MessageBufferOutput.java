package org.msgpack.core.buffer;

import java.io.Closeable;
import java.io.IOException;

/**
 * Provides a sequence of MessageBuffers for packing the input data
 */
public interface MessageBufferOutput
        extends Closeable
{
    /**
     * Retrieves the next buffer for writing message packed data
     *
     * @param bufferSize the buffer size to retrieve
     * @return
     * @throws IOException
     */
    public MessageBuffer next(int bufferSize)
            throws IOException;

    /**
     * Output the buffer contents. If you need to output a part of the
     * buffer use {@link MessageBuffer#slice(int, int)}
     *
     * @param buf
     * @throws IOException
     */
    public void flush(MessageBuffer buf)
            throws IOException;
}
