package org.msgpack.core.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import static org.msgpack.core.Preconditions.*;

/**
 * {@link MessageBufferInput} adapter for {@link java.nio.ByteBuffer}
 */
public class ByteBufferInput implements MessageBufferInput {

    private final ByteBuffer input;
    private boolean isRead = false;

    public ByteBufferInput(ByteBuffer input) {
        this.input = checkNotNull(input, "input ByteBuffer is null");
    }


    @Override
    public MessageBuffer next() throws IOException {
        if(isRead)
            return null;

        isRead = true;
        return MessageBuffer.wrap(input);
    }


    @Override
    public void close() throws IOException {
        // Nothing to do
    }
}
