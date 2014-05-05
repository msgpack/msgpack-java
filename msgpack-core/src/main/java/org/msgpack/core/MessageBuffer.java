package org.msgpack.core;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static sun.misc.Unsafe.ARRAY_BYTE_BASE_OFFSET;
import static sun.misc.Unsafe.ARRAY_BYTE_INDEX_SCALE;

/**
 *
 */
public class MessageBuffer {

    static final Unsafe unsafe;
    static final MethodHandle newByteBuffer;

    static {
        try {
            // Fetch theUnsafe object for Orackle JDK and OpenJDK
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            if (unsafe == null) {
                throw new RuntimeException("Unsafe is unavailable");
            }
            // TODO Finding Unsafe instance for Android JVM

            // Make sure the VM thinks bytes are only one byte wide
            if (sun.misc.Unsafe.ARRAY_BYTE_INDEX_SCALE != 1) {
                throw new IllegalStateException("Byte array index scale must be 1, but is " + ARRAY_BYTE_INDEX_SCALE);
            }

            // Fetch a method handle for the hidden constructor for DirectByteBuffer
            Class<?> directByteBufferClass = ClassLoader.getSystemClassLoader().loadClass("java.nio.DirectByteBuffer");
            Constructor<?> constructor = directByteBufferClass.getDeclaredConstructor(long.class, int.class, Object.class);
            constructor.setAccessible(true);
            newByteBuffer = MethodHandles.lookup().unreflectConstructor(constructor).asType(MethodType.methodType(ByteBuffer.class, long.class, int.class, Object.class));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Base object for resolving the relative address of the raw byte array.
     * If base == null, the address value is a raw memory address
     */
    private final Object base;

    /**
     * Head address of the underlying memory. If base is null, the address is a direct memory address, and if not,
     * it is the relative address within an array object (base)
     */
    private final long address;

    /**
     * Size of the underlying memory
     */
    private final int size;

    /**
     * Reference is used to hold a reference to an object that holds the underlying memory so that it cannot be
     * released by the garbage collector.
     */
    private final ByteBuffer reference;

    private AtomicInteger referenceCounter;


    static MessageBuffer newOffHeapBuffer(int length) {
        long address = unsafe.allocateMemory(length);
        return new MessageBuffer(address, length);
    }

    public static MessageBuffer newDirectBuffer(int length) {
        return new MessageBuffer(ByteBuffer.allocateDirect(length));
    }

    public static MessageBuffer newBuffer(int length) {
        return new MessageBuffer(ByteBuffer.allocate(length));
    }

    public static void releaseBuffer(MessageBuffer buffer) {
        if(buffer.base instanceof byte[]) {
            // We have nothing to do. Wait until the garbage-collector collects this array object
        }
        else {
            unsafe.freeMemory(buffer.address);
        }
    }


    MessageBuffer(long address, int length) {
        this.base = null;
        this.address = address;
        this.size = length;
        this.reference = null;
    }

    public MessageBuffer(ByteBuffer bb) {
        if(bb.isDirect()) {
            // Direct buffer or off-heap memory
            DirectBuffer db = DirectBuffer.class.cast(bb);
            this.base = null;
            this.address = db.address();
            this.size = bb.capacity();
            this.reference = bb;
        }
        else if(bb.hasArray()) {
            this.base = bb.array();
            this.address = ARRAY_BYTE_BASE_OFFSET;
            this.size = bb.array().length;
            this.reference = null;
        } else {
            throw new IllegalArgumentException("Only the array-backed ByteBuffer or DirectBuffer are supported");
        }
    }

    public MessageBuffer(byte[] arr) {
        this.base = arr;
        this.address = ARRAY_BYTE_BASE_OFFSET;
        this.size = arr.length;
        this.reference = null;
    }

    public int size() { return size; }


    public byte getByte(int index) {
        return unsafe.getByte(base, address + index);
    }

    public boolean getBoolean(int index) {
        return unsafe.getBoolean(base, address + index);
    }

    public short getShort(int index) {
        return unsafe.getShort(base, address + index);
    }

    /**
     * Read a big-endian value in the memory
     * @param index
     * @return
     */
    public int getInt(int index) {
        // Reading little-endian value
        int i = unsafe.getInt(base, address + index);
        // Reversing the endian
        return ((i >>> 24)           ) |
                ((i >>   8) &   0xFF00) |
                ((i <<   8) & 0xFF0000) |
                ((i << 24));
    }

    public float getFloat(int index) {
        return unsafe.getFloat(base, address + index);
    }

    public long getLong(int index) {
        return unsafe.getLong(base, address + index);
    }

    public double getDouble(int index) {
        return unsafe.getDouble(base, address + index);
    }

    public void getBytes(int index, byte[] dst, int dstIndex, int length) {
        unsafe.copyMemory(base, address+index, dst, ARRAY_BYTE_BASE_OFFSET + dstIndex, length);
    }

    public void putByte(int index, byte v) {
        unsafe.putByte(base, address + index, v);
    }

    public void putBoolean(int index, boolean v) {
        unsafe.putBoolean(base, address + index, v);
    }

    public void putShort(int index, short v) {
        unsafe.putShort(base, address + index, v);
    }

    /**
     * Write big-endian integer to the memory
     * @param index
     * @param v
     */
    public void putInt(int index, int v){
        // Reversing the endian
        v = ((v >>> 24)           ) |
                ((v >>   8) &   0xFF00) |
                ((v <<   8) & 0xFF0000) |
                ((v << 24));
        unsafe.putInt(base, address + index, v);
    }

    public void putFloat(int index, float v) {
        unsafe.putFloat(base, address + index, v);
    }

    public void putLong(int index, long v) {
        unsafe.putLong(base, address + index, v);
    }

    public void putDouble(int index, double v) {
        unsafe.putDouble(base, address + index, v);
    }

    public ByteBuffer toByteBuffer(int index, int length) {
        if(base instanceof byte[]) {
            return ByteBuffer.wrap((byte[]) base, (int) ((address-ARRAY_BYTE_BASE_OFFSET) + index), length);
        }
        try {
            return (ByteBuffer) newByteBuffer.invokeExact(address + index, length, reference);
        } catch(Throwable e) {
            // Convert checked exception to unchecked exception
            throw new RuntimeException(e);
        }
    }


}
