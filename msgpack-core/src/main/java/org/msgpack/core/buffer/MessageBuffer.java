package org.msgpack.core.buffer;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import static org.msgpack.core.Preconditions.*;

/**
 * MessageBuffer class is an abstraction of memory for reading/writing message packed data.
 * This MessageBuffers ensures short/int/float/long/double values are written in the big-endian order.
 *
 * This class is optimized for fast memory access, so many methods are
 * implemented without using any interface method that produces invokeinterface call in JVM.
 * Compared to invokevirtual, invokeinterface is 30% slower in general because it needs to find a target function from the table.
 *
 */
public class MessageBuffer {

    static final Unsafe unsafe;
    static final Constructor byteBufferConstructor;
    static final Class<?> directByteBufferClass;
    static final long ARRAY_BYTE_BASE_OFFSET;
    static final long ARRAY_BYTE_INDEX_SCALE;
    static final boolean isAndroid;

    static {
        try {

            // Detect android VM
            isAndroid = System.getProperty("java.runtime.name", "").toLowerCase().contains("android");

            if(!isAndroid) {
                // Fetch theUnsafe object for Oracle JDK and OpenJDK
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafe = (Unsafe) field.get(null);
            }
            else {
                // Workaround for creating an Unsafe instance for Android OS
                Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
                unsafeConstructor.setAccessible(true);
                unsafe = unsafeConstructor.newInstance();
            }
            if (unsafe == null) {
                throw new RuntimeException("Unsafe is unavailable");
            }

            ARRAY_BYTE_BASE_OFFSET = unsafe.arrayBaseOffset(byte[].class);
            ARRAY_BYTE_INDEX_SCALE = unsafe.arrayIndexScale(byte[].class);

            // Make sure the VM thinks bytes are only one byte wide
            if (ARRAY_BYTE_INDEX_SCALE != 1) {
                throw new IllegalStateException("Byte array index scale must be 1, but is " + ARRAY_BYTE_INDEX_SCALE);
            }

            // Find the hidden constructor for DirectByteBuffer
            directByteBufferClass = ClassLoader.getSystemClassLoader().loadClass("java.nio.DirectByteBuffer");
            if(!isAndroid) {
                byteBufferConstructor = directByteBufferClass.getDeclaredConstructor(long.class, int.class, Object.class);
            }
            else {
                // https://android.googlesource.com/platform/libcore/+/master/luni/src/main/java/java/nio/DirectByteBuffer.java
                // DirectByteBuffer(long address, int capacity)
                byteBufferConstructor = directByteBufferClass.getDeclaredConstructor(long.class, int.class);
            }
            if(byteBufferConstructor == null)
                throw new RuntimeException("Constructor of DirectByteBuffer is not found");

            byteBufferConstructor.setAccessible(true);

            // Check the endian of this CPU
            boolean isLittleEndian = true;
            byte[] a = new byte[8];
            unsafe.putLong(a, ARRAY_BYTE_BASE_OFFSET, 0x0102030405060708L);
            // TODO  Unsafe.getByte is not available in Android
            byte b = unsafe.getByte(a, ARRAY_BYTE_BASE_OFFSET);
            switch (b) {
                case 0x01:
                    isLittleEndian = false;
                    break;
                case 0x08:
                    isLittleEndian = true;
                    break;
                default:
                    assert false;
            }

            String bufferClsName = isLittleEndian ? "org.msgpack.core.buffer.MessageBuffer" : "org.msgpack.core.buffer.MessageBufferBE";
            msgBufferClass = Class.forName(bufferClsName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class MemoryAccess {
        static Method mGetAddress;
        static Method mCleaner;
        static Method mClean;

        static {
            try {
                mGetAddress = directByteBufferClass.getDeclaredMethod("address");
                mGetAddress.setAccessible(true);

                mCleaner = directByteBufferClass.getDeclaredMethod("cleaner");
                mCleaner.setAccessible(true);

                mClean = mCleaner.getReturnType().getDeclaredMethod("clean");
                mClean.setAccessible(true);
            }
            catch(NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        static long getAddress(Object base) {
            try {
                return (Long) mGetAddress.invoke(base);
            } catch(IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch(InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        static void clean(Object base) {
            try {
                Object cleaner = mCleaner.invoke(base);
                mClean.invoke(cleaner);
            }
            catch(Throwable e) {
                throw new RuntimeException(e);
            }
        }

    }




    /**
     * MessageBuffer class to use. If this machine is big-endian, it uses MessageBufferBE, which overrides some methods in this class that translate endians. If not, uses MessageBuffer.
     */
    private final static Class<?> msgBufferClass;

    /**
     * Base object for resolving the relative address of the raw byte array.
     * If base == null, the address value is a raw memory address
     */
    protected final Object base;

    /**
     * Head address of the underlying memory. If base is null, the address is a direct memory address, and if not,
     * it is the relative address within an array object (base)
     */
    protected final long address;

    /**
     * Size of the underlying memory
     */
    protected final int size;

    /**
     * Reference is used to hold a reference to an object that holds the underlying memory so that it cannot be
     * released by the garbage collector.
     */
    private final ByteBuffer reference;

    // TODO life-time managment of this buffer
    private AtomicInteger referenceCounter;


    static MessageBuffer newOffHeapBuffer(int length) {
        // This method is not available in Android OS
        long address = unsafe.allocateMemory(length);
        return new MessageBuffer(address, length);
    }

    public static MessageBuffer newDirectBuffer(int length) {
        ByteBuffer m = ByteBuffer.allocateDirect(length);
        return newMessageBuffer(m);
    }

    public static MessageBuffer newBuffer(int length) {
        return newMessageBuffer(new byte[length]);
    }

    public static MessageBuffer wrap(byte[] array) {
        return newMessageBuffer(array);
    }

    public static MessageBuffer wrap(ByteBuffer bb) {
        return newMessageBuffer(bb);
    }

    /**
     * Creates a new MessageBuffer instance backed by ByteBuffer
     * @param bb
     * @return
     */
    private static MessageBuffer newMessageBuffer(ByteBuffer bb) {
       checkNotNull(bb);
       try {
           // We need to use reflection to create MessageBuffer instances in order to prevent TypeProfile generation for getInt method. TypeProfile will be
           // generated to resolve one of the method references when two or more classes overrides the method.
           Constructor<?> constructor = msgBufferClass.getDeclaredConstructor(ByteBuffer.class);
           return (MessageBuffer) constructor.newInstance(bb);
       }
       catch(NoSuchMethodException e) {
           throw new IllegalStateException(e);
       }
       catch(Exception e) {
           throw new RuntimeException(e);
       }
    }

    /**
     * Creates a new MessageBuffer instance backed by a java heap array
     * @param arr
     * @return
     */
    private static MessageBuffer newMessageBuffer(byte[] arr) {
        checkNotNull(arr);
        try {
            Constructor<?> constructor = msgBufferClass.getDeclaredConstructor(byte[].class);
            return (MessageBuffer) constructor.newInstance(arr);
        }
        catch(NoSuchMethodException e) {
            throw new IllegalStateException(e);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void releaseBuffer(MessageBuffer buffer) {
        if(buffer.base instanceof byte[]) {
            // We have nothing to do. Wait until the garbage-collector collects this array object
        }
        else if(directByteBufferClass.isInstance(buffer.base)) {
            if(!isAndroid) {
                MemoryAccess.clean(buffer.base);
            }
            else {
                // TODO
            }
        }
        else {
            // Maybe cannot reach here
            unsafe.freeMemory(buffer.address);
        }
    }


    /**
     * Create a MessageBuffer instance from a given memory address and length
     * @param address
     * @param length
     */
    MessageBuffer(long address, int length) {
        this.base = null;
        this.address = address;
        this.size = length;
        this.reference = null;
    }

    /**
     * Create a MessageBuffer instance from a given ByteBuffer instance
     * @param bb
     */
    MessageBuffer(ByteBuffer bb) {
        if(bb.isDirect()) {
            // Direct buffer or off-heap memory
            this.base = null;
            if(isAndroid) {
                this.address = -1;
            }
            else {
                this.address = MemoryAccess.getAddress(bb);
            }
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


    /**
     * Create a MessageBuffer instance from an java heap array
     * @param arr
     */
    MessageBuffer(byte[] arr) {
        this.base = arr;
        this.address = ARRAY_BYTE_BASE_OFFSET;
        this.size = arr.length;
        this.reference = null;
    }

    private MessageBuffer(Object base, long address, int length, ByteBuffer reference) {
        this.base = base;
        this.address = address;
        this.size = length;
        this.reference = reference;
    }

    /**
     * byte size of the buffer
     * @return
     */
    public int size() { return size; }


    public MessageBuffer slice(int offset, int length) {
        // TODO ensure deleting this slice does not collapse this MessageBuffer
        return new MessageBuffer(base, address + offset, length, reference);
    }

    public byte getByte(int index) {
        return unsafe.getByte(base, address + index);
    }

    public boolean getBoolean(int index) {
        return unsafe.getBoolean(base, address + index);
    }

    public short getShort(int index) {
        short v = unsafe.getShort(base, address + index);
        return Short.reverseBytes(v);
    }

    /**
     * Read a big-endian int value at the specified index
     * @param index
     * @return
     */
    public int getInt(int index) {
        // Reading little-endian value
        int i = unsafe.getInt(base, address + index);
        // Reversing the endian
        return Integer.reverseBytes(i);
    }

    public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    public long getLong(int index) {
        long l = unsafe.getLong(base, address + index);
        return Long.reverseBytes(l);
    }

    public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    public void getBytes(int index, byte[] dst, int dstOffset, int length) {
        unsafe.copyMemory(base, address+index, dst, ARRAY_BYTE_BASE_OFFSET + dstOffset, length);
    }

    public void getBytes(int index, int len, ByteBuffer dst) {
        if(dst.remaining() > len)
            throw new BufferOverflowException();
        ByteBuffer src = toByteBuffer(index, len);
        dst.put(src);
    }


    public void putByte(int index, byte v) {
        unsafe.putByte(base, address + index, v);
    }

    public void putBoolean(int index, boolean v) {
        unsafe.putBoolean(base, address + index, v);
    }

    public void putShort(int index, short v) {
        v = Short.reverseBytes(v);
        unsafe.putShort(base, address + index, v);
    }

    /**
     * Write a big-endian integer value to the memory
     * @param index
     * @param v
     */
    public void putInt(int index, int v){
        // Reversing the endian
        v = Integer.reverseBytes(v);
        unsafe.putInt(base, address + index, v);
    }

    public void putFloat(int index, float v) {
        putInt(index, Float.floatToRawIntBits(v));
    }

    public void putLong(int index, long l) {
        // Reversing the endian
        l = Long.reverseBytes(l);
        unsafe.putLong(base, address + index, l);
    }

    public void putDouble(int index, double v) {
        putLong(index, Double.doubleToRawLongBits(v));
    }

    public void putBytes(int index, byte[] src, int srcOffset, int length) {
        unsafe.copyMemory(src, ARRAY_BYTE_BASE_OFFSET + srcOffset, base, address+index, length);
    }

    public void putByteBuffer(int index, ByteBuffer src, int len) {
        assert(len <= src.remaining());

        if(src.isDirect()) {
            if(!isAndroid) {
                unsafe.copyMemory(null, MemoryAccess.getAddress(src) + src.position(), base, address + index, len);
            }
            else {
                // TODO
            }
        } else if(src.hasArray()) {
            byte[] srcArray = src.array();
            unsafe.copyMemory(srcArray, ARRAY_BYTE_BASE_OFFSET + src.position(), base, address+index, len);
        } else {
            throw new IllegalArgumentException("Only the array-backed ByteBuffer or DirectBuffer are supported");
        }
        src.position(src.position() + len);
    }


    /**
     * Create a ByteBuffer view of the range [index, index+length) of this memory
     * @param index
     * @param length
     * @return
     */
    public ByteBuffer toByteBuffer(int index, int length) {
        if(base instanceof byte[]) {
            return ByteBuffer.wrap((byte[]) base, (int) ((address-ARRAY_BYTE_BASE_OFFSET) + index), length);
        }
        try {
            if(!isAndroid)
                return (ByteBuffer) byteBufferConstructor.newInstance(address + index, length, reference);
            else {
                // DirectByteBuffer(long address, int offset)
                return (ByteBuffer) byteBufferConstructor.newInstance(address + index, length);
            }
        } catch(Throwable e) {
            // Convert checked exception to unchecked exception
            throw new RuntimeException(e);
        }
    }

    public void relocate(int offset, int length, int dst) {
        unsafe.copyMemory(base, address + offset, base, address+dst, length);
    }

    /**
     * Copy this buffer contents to another MessageBuffer
     * @param index
     * @param dst
     * @param offset
     * @param length
     */
    public void copyTo(int index, MessageBuffer dst, int offset, int length) {
        if(!isAndroid)
            unsafe.copyMemory(base, address + index, dst.base, dst.address + offset, length);
        else {
            // TODO

        }
    }


    public String toHexString(int offset, int length) {
        StringBuilder s = new StringBuilder();
        for(int i=offset; i<length; ++i) {
            if(i != offset)
              s.append(" ");
            s.append(String.format("%02x", getByte(i)));
        }
        return s.toString();
    }

}
