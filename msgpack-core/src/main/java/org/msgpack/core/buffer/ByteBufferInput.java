package org.msgpack.core.buffer;

import java.io.IOException;
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
        this.input = checkNotNull(input, "input ByteBuffer is null");
    }

    /**
     * Reset buffer. This method doesn't close the old resource.
     *
     * @param input new buffer
     * @return the old resource
     */
    public ByteBuffer reset(ByteBuffer input)
    {
        ByteBuffer old = this.input;
        this.input = input;
        isRead = false;
        return old;
    }

    @Override
    public MessageBuffer next()
            throws IOException
    {
        if (isRead) {
            return null;
        }

        isRead = true;
        return MessageBuffer.wrap(input);
    }

    @Override
    public void close()
            throws IOException
    {
        // Nothing to do
    }
}
