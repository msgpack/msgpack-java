package org.msgpack.core;

import java.nio.ByteBuffer;

/**
 * MessageBuffer class tailored to the big-endian machine. Message pack specification demands writing short/int/float/long/double values in big-endian format.
 * In the big-endian machine, we do not need to swap the byte order.
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
    public double getDouble(int index) {
        return unsafe.getDouble(base, address + index);
    }

    @Override
    public void putShort(int index, short v) {
        unsafe.putShort(base, address + index, v);
    }

    @Override
    public void putInt(int index, int v) {
        unsafe.putInt(base, address + index, v);
    }

    @Override
    public void putLong(int index, long v) {
        unsafe.putLong(base, address + index, v);
    }

    @Override
    public void putDouble(int index, double v) {
        unsafe.putDouble(base, address + index, v);
    }


}
