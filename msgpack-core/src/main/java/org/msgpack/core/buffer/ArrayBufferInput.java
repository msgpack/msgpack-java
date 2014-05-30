package org.msgpack.core.buffer;

import java.io.IOException;
import static org.msgpack.core.Preconditions.*;

/**
 * MessageBufferInput adapter for byte arrays
 */
public class ArrayBufferInput implements MessageBufferInput {

    private MessageBuffer buffer;
    private int cursor;
    private final int length;

    public ArrayBufferInput(byte[] arr) {
        this(arr, 0, arr.length);
    }

    public ArrayBufferInput(byte[] arr, int offset, int length) {
        this.buffer = MessageBuffer.wrap(checkNotNull(arr, "input array is null"));
        this.cursor = offset;
        checkArgument(length <= arr.length);
        this.length = length;
    }


    @Override
    public MessageBuffer next() throws IOException {
        if(cursor < length) {
            int c = cursor;
            cursor = length;
            if(c == 0 && length == buffer.size)
                return buffer;
            else
                return buffer.slice(c, length);
        }
        else {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        buffer = null;
    }
}
