package org.msgpack.core.buffer;

import java.io.IOException;
import static org.msgpack.core.Preconditions.*;

/**
 * MessageBufferInput adapter for byte arrays
 */
public class ArrayBufferInput implements MessageBufferInput {

    private MessageBuffer buffer;
    private boolean isRead = false;

    public ArrayBufferInput(byte[] arr) {
        this(arr, 0, arr.length);
    }

    public ArrayBufferInput(byte[] arr, int offset, int length) {
        checkArgument(offset + length <= arr.length);
        this.buffer = MessageBuffer.wrap(checkNotNull(arr, "input array is null")).slice(offset, length);
    }

    @Override
    public MessageBuffer next() throws IOException {
        if(isRead)
            return null;
        isRead = true;
        return buffer;
    }

    @Override
    public void close() throws IOException {
        buffer = null;
    }
}
