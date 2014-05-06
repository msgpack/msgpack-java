package org.msgpack.core;

import java.io.IOException;

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


    public void flush(MessageBuffer buf, int offset, int len);

    /**
     * Flush and close this buffer.
     * @throws IOException
     */
    public void close() throws IOException;




}
