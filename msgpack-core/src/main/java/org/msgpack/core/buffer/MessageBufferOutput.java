package org.msgpack.core.buffer;


import java.io.Closeable;
import java.io.IOException;

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

}




