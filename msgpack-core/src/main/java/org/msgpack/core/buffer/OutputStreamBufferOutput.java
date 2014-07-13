package org.msgpack.core.buffer;

import java.io.IOException;
import java.io.OutputStream;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * MessageBufferOutput adapter for {@link java.io.OutputStream}.
 */
public class OutputStreamBufferOutput implements MessageBufferOutput {

    private final OutputStream out;

    public OutputStreamBufferOutput(OutputStream out) {
        this.out = checkNotNull(out, "output is null");
    }

    @Override
    public void flush(MessageBuffer buf, int offset, int len) throws IOException {
        assert(offset + len <= buf.size());

        // TODO reuse the allocated buffer
        byte[] in = new byte[len];
        buf.getBytes(offset, in, 0, len);
        out.write(in, 0, len);
    }

    @Override
    public void close() throws IOException {
        try {
            out.flush();
        }
        finally {
            out.close();
        }
    }
}
