package org.msgpack.core.buffer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Provides a sequence of MessageBuffers for packing the input data
 */
public interface MessageBufferOutput extends Closeable {

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




