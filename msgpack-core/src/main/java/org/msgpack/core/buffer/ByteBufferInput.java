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

import java.nio.ByteBuffer;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferInput} adapter for {@link java.nio.ByteBuffer}
 */
public class ByteBufferInput
        implements MessageBufferInput
{
    private ByteBuffer input;
    private boolean isRead = false;

    public ByteBufferInput(ByteBuffer input)
    {
        this.input = checkNotNull(input, "input ByteBuffer is null").slice();
    }

    /**
     * Reset buffer.
     *
     * @param input new buffer
     * @return the old buffer
     */
    public ByteBuffer reset(ByteBuffer input)
    {
        ByteBuffer old = this.input;
        this.input = checkNotNull(input, "input ByteBuffer is null").slice();
        isRead = false;
        return old;
    }

    @Override
    public MessageBuffer next()
    {
        if (isRead) {
            return null;
        }

        MessageBuffer b = MessageBuffer.wrap(input);
        isRead = true;
        return b;
    }

    @Override
    public void close()
    {
        // Nothing to do
    }

    /**
     * Create a ByteBufferInput on off heap memory
     * @param address the address
     * @param offset the offset
     * @param length the length
     * @return a new ByteBufferInput on the specified address
     */
    public static ByteBuffer directBuffer(long address, int offset, int length)
    {
        return DirectBufferAccess.newByteBuffer(address, offset, length, null);
    }
}
