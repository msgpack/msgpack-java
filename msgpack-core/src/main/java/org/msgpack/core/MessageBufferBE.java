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
    public short getShort(int index) {
        return unsafe.getShort(base, address + index);
    }

    @Override
    public int getInt(int index) {
        // We can simply return the integer value as big-endian value
        return unsafe.getInt(base, address + index);
    }

    public long getLong(int index) {
        return unsafe.getLong(base, address + index);
    }

    @Override
    public float getFloat(int index) {
        return unsafe.getFloat(base, address + index);
    }



    @Override
    public void putShort(int index, short v) {
        unsafe.putShort(base, address + index, v);
    }

    @Override
    public void putInt(int index, int v) {
        unsafe.putInt(base, address + index, v);
    }

    public void putLong(int index, int v) {
        unsafe.putLong(base, address + index, v);
    }


}
