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
