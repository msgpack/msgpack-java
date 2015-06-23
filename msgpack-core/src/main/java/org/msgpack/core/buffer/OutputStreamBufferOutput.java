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
    private byte[] tmpBuf;

    public OutputStreamBufferOutput(OutputStream out)
    {
        this.out = checkNotNull(out, "output is null");
    }

    /**
     * Reset Stream. This method doesn't close the old resource.
     *
     * @param out new stream
     * @return the old resource
     */
    public OutputStream reset(OutputStream out)
            throws IOException
    {
        OutputStream old = this.out;
        this.out = out;
        return old;
    }

    @Override
    public MessageBuffer next(int bufferSize)
            throws IOException
    {
        if (buffer == null || buffer.size != bufferSize) {
            buffer = MessageBuffer.newBuffer(bufferSize);
        }
        return buffer;
    }

    @Override
    public void flush(MessageBuffer buf)
            throws IOException
    {
        int writeLen = buf.size();
        if (buf.hasArray()) {
            out.write(buf.getArray(), buf.offset(), writeLen);
        }
        else {
            if (tmpBuf == null || tmpBuf.length < writeLen) {
                tmpBuf = new byte[writeLen];
            }
            buf.getBytes(0, tmpBuf, 0, writeLen);
            out.write(tmpBuf, 0, writeLen);
        }
    }

    @Override
    public void close()
            throws IOException
    {
        try {
            out.flush();
        }
        finally {
            out.close();
        }
    }
}
