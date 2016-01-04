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
 * Provides a sequence of MessageBuffers that contains message packed data.
 */
public interface MessageBufferInput
        extends Closeable
{
    /**
     * Get a next buffer to read.
     * <p>
     * When this method is called, the formally allocated buffer can be safely discarded.
     *
     * @return the next MessageBuffer, or return null if no more buffer is available.
     * @throws IOException when error occurred when reading the data
     */
    MessageBuffer next()
            throws IOException;
}
