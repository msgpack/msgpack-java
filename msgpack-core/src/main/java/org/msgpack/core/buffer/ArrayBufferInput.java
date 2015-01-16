package org.msgpack.core.buffer;

import java.io.IOException;
import static org.msgpack.core.Preconditions.*;

/**
 * MessageBufferInput adapter for byte arrays
 */
public class ArrayBufferInput implements MessageBufferInput {

    private MessageBuffer buffer;
    private boolean isRead = false;

    public ArrayBufferInput(MessageBuffer buf) {
        this.buffer = checkNotNull(buf, "input buffer is null");
    }

    public ArrayBufferInput(byte[] arr) {
        this(arr, 0, arr.length);
    }

    public ArrayBufferInput(byte[] arr, int offset, int length) {
        checkArgument(offset + length <= arr.length);
        this.buffer = MessageBuffer.wrap(checkNotNull(arr, "input array is null")).slice(offset, length);
    }

    /**
     * Reset buffer. This method doesn't close the old resource.
     * @param buf new buffer
     * @return the old resource
     */
    public MessageBuffer reset(MessageBuffer buf) {
        MessageBuffer old = this.buffer;
        this.buffer = buf;
        this.isRead = false;
        return old;
    }

    public void reset(byte[] arr) {
        reset(MessageBuffer.wrap(checkNotNull(arr, "input array is null")));
    }

    public void reset(byte[] arr, int offset, int len) {
        reset(MessageBuffer.wrap(checkNotNull(arr, "input array is null")).slice(offset, len));
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
        isRead = false;
    }
}
