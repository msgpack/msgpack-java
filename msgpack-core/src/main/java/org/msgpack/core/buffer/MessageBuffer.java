package org.msgpack.core.buffer;

import org.msgpack.core.annotations.Insecure;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.msgpack.core.Preconditions.*;

/**
 * MessageBuffer class is an abstraction of memory for reading/writing message packed data.
 * This MessageBuffers ensures short/int/float/long/double values are written in the big-endian order.
 * <p/>
 * This class is optimized for fast memory access, so many methods are
 * implemented without using any interface method that produces invokeinterface call in JVM.
 * Compared to invokevirtual, invokeinterface is 30% slower in general because it needs to find a target function from the table.
 */
public class MessageBuffer {

    static final boolean isUniversalBuffer;
    static final Unsafe unsafe;

    static final int ARRAY_BYTE_BASE_OFFSET;
    static final int ARRAY_BYTE_INDEX_SCALE;

    static {
        try {
            // Check java version
            String javaVersion = System.getProperty("java.specification.version", "");
            int dotPos = javaVersion.indexOf('.');
            boolean isJavaAtLeast7 = false;
            if(dotPos != -1) {
                try {
                    int major = Integer.parseInt(javaVersion.substring(0, dotPos));
                    int minor = Integer.parseInt(javaVersion.substring(dotPos + 1));
                    isJavaAtLeast7 = major > 1 || (major == 1 && minor >= 7);
                }
                catch(NumberFormatException e) {
                    e.printStackTrace(System.err);
                }
            }

            boolean hasUnsafe = false;
            try {
                hasUnsafe = Class.forName("sun.misc.Unsafe") != null;
            }
            catch(Exception e) {
            }

            // Detect android VM
            boolean isAndroid = System.getProperty("java.runtime.name", "").toLowerCase().contains("android");

            // Is Google App Engine?
            boolean isGAE = System.getProperty("com.google.appengine.runtime.version") != null;

            // For Java6, android and JVM that has no Unsafe class, use Universal MessageBuffer
            isUniversalBuffer =
                Boolean.parseBoolean(System.getProperty("msgpack.universal-buffer", "false"))
                    || isAndroid
                    || isGAE
                    || !isJavaAtLeast7
                    || !hasUnsafe;

            // We need to use reflection to find MessageBuffer implementation classes because
            // importing these classes creates TypeProfile and adds some overhead to method calls.
            String bufferClsName;
            if(isUniversalBuffer) {
                bufferClsName = "org.msgpack.core.buffer.MessageBufferU";
                unsafe = null;
                ARRAY_BYTE_BASE_OFFSET = 16; // dummy value
                ARRAY_BYTE_INDEX_SCALE = 1;
            }
            else {
                // Check the endian of this CPU
                boolean isLittleEndian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
                if(isLittleEndian) {
                    bufferClsName = "org.msgpack.core.buffer.MessageBuffer";
                }
                else {
                    bufferClsName = "org.msgpack.core.buffer.MessageBufferBE";
                }

                // Fetch theUnsafe object for Oracle and OpenJDK
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafe = (Unsafe) field.get(null);
                if(unsafe == null) {
                    throw new RuntimeException("Unsafe is unavailable");
                }
                ARRAY_BYTE_BASE_OFFSET = unsafe.arrayBaseOffset(byte[].class);
                ARRAY_BYTE_INDEX_SCALE = unsafe.arrayIndexScale(byte[].class);

                // Make sure the VM thinks bytes are only one byte wide
                if(ARRAY_BYTE_INDEX_SCALE != 1) {
                    throw new IllegalStateException("Byte array index scale must be 1, but is " + ARRAY_BYTE_INDEX_SCALE);
                }
            }
            Class<?> bufferCls = Class.forName(bufferClsName);
            msgBufferClass = bufferCls;

            Constructor<?> mbArrCstr = bufferCls.getDeclaredConstructor(byte[].class);
            mbArrCstr.setAccessible(true);
            mbArrConstructor = mbArrCstr;

            Constructor<?> mbBBCstr = bufferCls.getDeclaredConstructor(ByteBuffer.class);
            mbBBCstr.setAccessible(true);
            mbBBConstructor = mbBBCstr;

            // Requires Java7
            //newMsgBuffer = MethodHandles.lookup().unreflectConstructor(mbArrCstr).asType(
            //    MethodType.methodType(bufferCls, byte[].class)
            //);

        }
        catch(Exception e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    /**
     * MessageBuffer class to use. If this machine is big-endian, it uses MessageBufferBE, which overrides some methods in this class that translate endians. If not, uses MessageBuffer.
     */
    private final static Class<?> msgBufferClass;

    private final static Constructor<?> mbArrConstructor;
    private final static Constructor<?> mbBBConstructor;

    // Requires Java7
    //private final static MethodHandle newMsgBuffer;

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
    protected final ByteBuffer reference;

    static MessageBuffer newOffHeapBuffer(int length) {
        // This method is not available in Android OS
        if(!isUniversalBuffer) {
            long address = unsafe.allocateMemory(length);
            return new MessageBuffer(address, length);
        }
        else {
            return newDirectBuffer(length);
        }
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
        return newMessageBuffer(bb).slice(bb.position(), bb.remaining());
    }

    /**
     * Creates a new MessageBuffer instance backed by ByteBuffer
     *
     * @param bb
     * @return
     */
    private static MessageBuffer newMessageBuffer(ByteBuffer bb) {
        checkNotNull(bb);
        try {
            // We need to use reflection to create MessageBuffer instances in order to prevent TypeProfile generation for getInt method. TypeProfile will be
            // generated to resolve one of the method references when two or more classes overrides the method.
            return (MessageBuffer) mbBBConstructor.newInstance(bb);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new MessageBuffer instance backed by a java heap array
     *
     * @param arr
     * @return
     */
    private static MessageBuffer newMessageBuffer(byte[] arr) {
        checkNotNull(arr);
        try {
            return (MessageBuffer) mbArrConstructor.newInstance(arr);
        }
        catch(Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void releaseBuffer(MessageBuffer buffer) {
        if(isUniversalBuffer || buffer.base instanceof byte[]) {
            // We have nothing to do. Wait until the garbage-collector collects this array object
        }
        else if(DirectBufferAccess.isDirectByteBufferInstance(buffer.base)) {
            DirectBufferAccess.clean(buffer.base);
        }
        else {
            // Maybe cannot reach here
            unsafe.freeMemory(buffer.address);
        }
    }

    /**
     * Create a MessageBuffer instance from a given memory address and length
     *
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
     *
     * @param bb
     */
    MessageBuffer(ByteBuffer bb) {
        if(bb.isDirect()) {
            if(isUniversalBuffer) {
                throw new IllegalStateException("Cannot create MessageBuffer from DirectBuffer");
            }
            // Direct buffer or off-heap memory
            this.base = null;
            this.address = DirectBufferAccess.getAddress(bb);
            this.size = bb.capacity();
            this.reference = bb;
        }
        else if(bb.hasArray()) {
            this.base = bb.array();
            this.address = ARRAY_BYTE_BASE_OFFSET;
            this.size = bb.array().length;
            this.reference = null;
        }
        else {
            throw new IllegalArgumentException("Only the array-backed ByteBuffer or DirectBuffer are supported");
        }
    }


    /**
     * Create a MessageBuffer instance from an java heap array
     *
     * @param arr
     */
    MessageBuffer(byte[] arr) {
        this.base = arr;
        this.address = ARRAY_BYTE_BASE_OFFSET;
        this.size = arr.length;
        this.reference = null;
    }

    MessageBuffer(Object base, long address, int length, ByteBuffer reference) {
        this.base = base;
        this.address = address;
        this.size = length;
        this.reference = reference;
    }

    /**
     * byte size of the buffer
     *
     * @return
     */
    public int size() {
        return size;
    }

    public MessageBuffer slice(int offset, int length) {
        // TODO ensure deleting this slice does not collapse this MessageBuffer
        if(offset == 0 && length == size())
            return this;
        else {
            checkArgument(offset + length <= size());
            return new MessageBuffer(base, address + offset, length, reference);
        }
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
     *
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
        unsafe.copyMemory(base, address + index, dst, ARRAY_BYTE_BASE_OFFSET + dstOffset, length);
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
     *
     * @param index
     * @param v
     */
    public void putInt(int index, int v) {
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
        unsafe.copyMemory(src, ARRAY_BYTE_BASE_OFFSET + srcOffset, base, address + index, length);
    }

    public void putByteBuffer(int index, ByteBuffer src, int len) {
        assert (len <= src.remaining());
        assert (!isUniversalBuffer);

        if(src.isDirect()) {
            unsafe.copyMemory(null, DirectBufferAccess.getAddress(src) + src.position(), base, address + index, len);
        }
        else if(src.hasArray()) {
            byte[] srcArray = src.array();
            unsafe.copyMemory(srcArray, ARRAY_BYTE_BASE_OFFSET + src.position(), base, address + index, len);
        }
        else {
            if(base != null) {
                src.get((byte[]) base, index, len);
            }
            else {
                for(int i = 0; i < len; ++i) {
                    unsafe.putByte(base, address + index, src.get());
                }
            }
        }
        src.position(src.position() + len);
    }

    /**
     * Create a ByteBuffer view of the range [index, index+length) of this memory
     *
     * @param index
     * @param length
     * @return
     */
    public ByteBuffer toByteBuffer(int index, int length) {
        if(hasArray()) {
            return ByteBuffer.wrap((byte[]) base, (int) ((address - ARRAY_BYTE_BASE_OFFSET) + index), length);
        }
        else {
            assert (!isUniversalBuffer);
            return DirectBufferAccess.newByteBuffer(address, index, length, reference);
        }
    }

    /**
     * Get a ByteBuffer view of this buffer
     *
     * @return
     */
    public ByteBuffer toByteBuffer() {
        return toByteBuffer(0, size());
    }

    /**
     * Get a copy of this buffer
     *
     * @return
     */
    public byte[] toByteArray() {
        byte[] b = new byte[size()];
        unsafe.copyMemory(base, address, b, ARRAY_BYTE_BASE_OFFSET, size());
        return b;
    }

    @Insecure
    public boolean hasArray() {
        return base instanceof byte[];
    }

    @Insecure
    public byte[] getArray() {
        return (byte[]) base;
    }

    @Insecure
    public Object getBase() {
        return base;
    }

    @Insecure
    public long getAddress() {
        return address;
    }

    @Insecure
    public int offset() {
        if(hasArray()) {
            return (int) address - ARRAY_BYTE_BASE_OFFSET;
        }
        else {
            return 0;
        }
    }

    @Insecure
    public ByteBuffer getReference() {
        return reference;
    }

    /**
     * Copy this buffer contents to another MessageBuffer
     *
     * @param index
     * @param dst
     * @param offset
     * @param length
     */
    public void copyTo(int index, MessageBuffer dst, int offset, int length) {
        unsafe.copyMemory(base, address + index, dst.base, dst.address + offset, length);
    }

    public String toHexString(int offset, int length) {
        StringBuilder s = new StringBuilder();
        for(int i = offset; i < length; ++i) {
            if(i != offset)
                s.append(" ");
            s.append(String.format("%02x", getByte(i)));
        }
        return s.toString();
    }

}
