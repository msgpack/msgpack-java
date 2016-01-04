//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.core.buffer;

import java.io.Closeable;
import java.io.IOException;
import java.io.Flushable;

/**
 * Provides a buffered output stream for packing objects
 */
public interface MessageBufferOutput
        extends Closeable, Flushable
{
    /**
     * Allocates the next buffer for writing message packed data.
     * If the previously allocated buffer is not flushed yet, this next method should discard
     * it without writing it.
     *
     * @param minimumSize the mimium required buffer size to allocate
     * @return
     * @throws IOException
     */
    MessageBuffer next(int minimumSize)
            throws IOException;

    /**
     * Flushes the previously allocated buffer.
     * This method is not always called because next method also flushes previously allocated buffer.
     * This method is called when write method is called or application wants to control the timing of flush.
     *
     * @param length the size of buffer to flush
     * @throws IOException
     */
    void writeBuffer(int length)
            throws IOException;

    /**
     * Writes an external payload data.
     * This method should follow semantics of OutputStream.
     *
     * @param buffer the data to write
     * @param offset the start offset in the data
     * @param length the number of bytes to write
     * @return
     * @throws IOException
     */
    void write(byte[] buffer, int offset, int length)
            throws IOException;

    /**
     * Writes an external payload data.
     * This buffer is given - this MessageBufferOutput owns the buffer and may modify contents of the buffer. Contents of this buffer won't be modified by the caller.
     *
     * @param buffer the data to add
     * @param offset the start offset in the data
     * @param length the number of bytes to add
     * @return
     * @throws IOException
     */
    void add(byte[] buffer, int offset, int length)
            throws IOException;
}
