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
import java.io.OutputStream;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * MessageBufferOutput adapter for {@link java.io.OutputStream}.
 */
public class OutputStreamBufferOutput
        implements MessageBufferOutput
{
    private OutputStream out;
    private MessageBuffer buffer;

    public OutputStreamBufferOutput(OutputStream out)
    {
        this(out, 8192);
    }

    public OutputStreamBufferOutput(OutputStream out, int bufferSize)
    {
        this.out = checkNotNull(out, "output is null");
        this.buffer = MessageBuffer.allocate(bufferSize);
    }

    /**
     * Reset Stream. This method doesn't close the old stream.
     *
     * @param out new stream
     * @return the old stream
     */
    public OutputStream reset(OutputStream out)
            throws IOException
    {
        OutputStream old = this.out;
        this.out = out;
        return old;
    }

    @Override
    public MessageBuffer next(int minimumSize)
            throws IOException
    {
        if (buffer.size() < minimumSize) {
            buffer = MessageBuffer.allocate(minimumSize);
        }
        return buffer;
    }

    @Override
    public void writeBuffer(int length)
            throws IOException
    {
        write(buffer.array(), buffer.arrayOffset(), length);
    }

    @Override
    public void write(byte[] buffer, int offset, int length)
            throws IOException
    {
        out.write(buffer, offset, length);
    }

    @Override
    public void add(byte[] buffer, int offset, int length)
            throws IOException
    {
        write(buffer, offset, length);
    }

    @Override
    public void close()
            throws IOException
    {
        out.close();
    }

    @Override
    public void flush()
            throws IOException
    {
        out.flush();
    }
}
