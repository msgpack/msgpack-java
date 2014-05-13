package org.msgpack.core.buffer;

import java.io.IOException;
import java.io.InputStream;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * {@link MessageBufferInput} adapter for {@link InputStream}
 */
public class InputStreamBufferInput implements MessageBufferInput {

    private final InputStream in;
    private byte[] buffer = new byte[8192];

    public InputStreamBufferInput(InputStream in) {
        this.in = checkNotNull(in, "input is null");
    }

    @Override
    public MessageBuffer next() throws IOException {
        // Manage the allocated buffers
        MessageBuffer m = MessageBuffer.newBuffer(buffer.length);

        // TODO reduce the number of memory copy
        int cursor = 0;
        while(cursor < buffer.length) {
            int readLen = in.read(buffer, cursor, buffer.length - cursor);
            if(readLen == -1) {
                break;
            }
            cursor += readLen;
        }
        m.putBytes(0, buffer, 0, cursor);
        return m;
    }

    @Override
    public void close() throws IOException {
        try {
            in.close();
        }
        finally {
            buffer = null;
        }
    }
}
