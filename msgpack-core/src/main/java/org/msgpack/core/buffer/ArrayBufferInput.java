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

import java.io.IOException;

import static org.msgpack.core.Preconditions.checkArgument;
import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * MessageBufferInput adapter for byte arrays
 */
public class ArrayBufferInput
        implements MessageBufferInput
{
    private MessageBuffer buffer;
    private boolean isRead = false;

    public ArrayBufferInput(MessageBuffer buf)
    {
        this.buffer = checkNotNull(buf, "input buffer is null");
    }

    public ArrayBufferInput(byte[] arr)
    {
        this(arr, 0, arr.length);
    }

    public ArrayBufferInput(byte[] arr, int offset, int length)
    {
        checkArgument(offset + length <= arr.length);
        this.buffer = MessageBuffer.wrap(checkNotNull(arr, "input array is null")).slice(offset, length);
    }

    /**
     * Reset buffer. This method doesn't close the old resource.
     *
     * @param buf new buffer
     * @return the old resource
     */
    public MessageBuffer reset(MessageBuffer buf)
    {
        MessageBuffer old = this.buffer;
        this.buffer = buf;
        this.isRead = false;
        return old;
    }

    public void reset(byte[] arr)
    {
        reset(MessageBuffer.wrap(checkNotNull(arr, "input array is null")));
    }

    public void reset(byte[] arr, int offset, int len)
    {
        reset(MessageBuffer.wrap(checkNotNull(arr, "input array is null")).slice(offset, len));
    }

    @Override
    public MessageBuffer next()
            throws IOException
    {
        if (isRead) {
            return null;
        }
        isRead = true;
        return buffer;
    }

    @Override
    public void close()
            throws IOException
    {
        buffer = null;
        isRead = false;
    }

}
