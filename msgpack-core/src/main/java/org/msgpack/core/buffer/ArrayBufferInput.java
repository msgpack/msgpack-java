package org.msgpack.core.buffer;

import java.io.IOException;

/**
 * MessageBufferInput adapter for byte arrays
 */
public class ArrayBufferInput implements MessageBufferInput {

    private MessageBuffer buffer;
    private boolean isRead = false;

    public ArrayBufferInput(byte[] arr) {
        this.buffer = MessageBuffer.wrap(arr);
    }

    @Override
    public MessageBuffer next() throws IOException {
        if(isRead) {
            return null;
        } else {
            isRead = true;
            return buffer;
        }
    }

    @Override
    public void close() throws IOException {
        buffer = null;
    }
}
