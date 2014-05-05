package org.msgpack.core;

import java.nio.ByteBuffer;

/**
 * MessageBuffer class for the big-endian machine.
 */
public class MessageBufferBE extends MessageBuffer {

    MessageBufferBE(ByteBuffer bb) {
        super(bb);
    }

    @Override
    public int getInt(int index) {
        // We can simply return the integer value as big-endian value
        return unsafe.getInt(base, address + index);
    }

    @Override
    public void putInt(int index, int v) {
        unsafe.putInt(base, address + index, v);
    }
}
