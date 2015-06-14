package org.msgpack.core.buffer;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.msgpack.core.Preconditions.*;

/**
 * Universal MessageBuffer implementation supporting Java6 and Android.
 * This buffer always uses ByteBuffer-based memory access
 */
public class MessageBufferU extends MessageBuffer {

    public MessageBufferU(ByteBuffer bb) {
        super(null, 0L, bb.capacity(), bb.order(ByteOrder.BIG_ENDIAN));
        checkNotNull(reference);
    }

    MessageBufferU(byte[] arr) {
        this(ByteBuffer.wrap(arr));
    }

    @Override
    public MessageBufferU slice(int offset, int length) {
        if(offset == 0 && length == size())
            return this;
        else {
            checkArgument(offset + length <= size());
            try {
                reference.position(offset);
                reference.limit(offset + length);
                return new MessageBufferU(reference.slice());
            }
            finally {
                resetBufferPosition();
            }
        }
    }

    private void resetBufferPosition() {
        reference.position(0);
        reference.limit(size);
    }

    @Override
    public byte getByte(int index) {
        return reference.get(index);
    }
    @Override
    public boolean getBoolean(int index) {
        return reference.get(index) != 0;
    }
    @Override
    public short getShort(int index) {
        return reference.getShort(index);
    }
    @Override
    public int getInt(int index) {
        return reference.getInt(index);
    }
    @Override
    public float getFloat(int index) {
        return reference.getFloat(index);
    }
    @Override
    public long getLong(int index) {
        return reference.getLong(index);
    }
    @Override
    public double getDouble(int index) {
        return reference.getDouble(index);
    }
    @Override
    public void getBytes(int index, int len, ByteBuffer dst) {
        try {
            reference.position(index);
            reference.limit(index + len);
            dst.put(reference);
        }
        finally {
            resetBufferPosition();
        }
    }
    @Override
    public void putByte(int index, byte v) {
        reference.put(index, v);
    }
    @Override
    public void putBoolean(int index, boolean v) {
        reference.put(index, v ? (byte) 1 : (byte) 0);
    }
    @Override
    public void putShort(int index, short v) {
        reference.putShort(index, v);
    }
    @Override
    public void putInt(int index, int v) {
        reference.putInt(index, v);
    }
    @Override
    public void putFloat(int index, float v) {
        reference.putFloat(index, v);
    }
    @Override
    public void putLong(int index, long l) {
        reference.putLong(index, l);
    }
    @Override
    public void putDouble(int index, double v) {
        reference.putDouble(index, v);
    }
    @Override
    public ByteBuffer toByteBuffer(int index, int length) {
        try {
            reference.position(index);
            reference.limit(index + length);
            return reference.slice();
        }
        finally {
            resetBufferPosition();
        }
    }
    @Override
    public ByteBuffer toByteBuffer() {
        return toByteBuffer(0, size);
    }

    @Override
    public void getBytes(int index, byte[] dst, int dstOffset, int length) {
        try {
            reference.position(index);
            reference.get(dst, dstOffset, length);
        }
        finally {
            resetBufferPosition();
        }
    }

    @Override
    public void putByteBuffer(int index, ByteBuffer src, int len) {
        assert (len <= src.remaining());

        if(src.hasArray()) {
            putBytes(index, src.array(), src.position(), len);
            src.position(src.position() + len);
        }
        else {
            int prevSrcLimit = src.limit();
            try {
                src.limit(src.position() + len);
                reference.position(index);
                reference.put(src);
            }
            finally {
                src.limit(prevSrcLimit);
            }
        }
    }

    @Override
    public void putBytes(int index, byte[] src, int srcOffset, int length) {
        try {
            reference.position(index);
            reference.put(src, srcOffset, length);
        }
        finally {
            resetBufferPosition();
        }
    }

    @Override
    public void copyTo(int index, MessageBuffer dst, int offset, int length) {
        try {
            reference.position(index);
            dst.putByteBuffer(offset, reference, length);
        }
        finally {
            resetBufferPosition();
        }
    }
    @Override
    public byte[] toByteArray() {
        byte[] b = new byte[size()];
        getBytes(0, b, 0, b.length);
        return b;
    }
}
