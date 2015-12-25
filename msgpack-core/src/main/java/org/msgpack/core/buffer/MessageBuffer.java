//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.core.buffer;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.msgpack.core.Preconditions.checkArgument;
import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * MessageBuffer class is an abstraction of memory for reading/writing message packed data.
 * This MessageBuffers ensures short/int/float/long/double values are written in the big-endian order.
 * <p/>
 * This class is optimized for fast memory access, so many methods are
 * implemented without using any interface method that produces invokeinterface call in JVM.
 * Compared to invokevirtual, invokeinterface is 30% slower in general because it needs to find a target function from the table.
 */
public class MessageBuffer
{
    static final boolean isUniversalBuffer;
    static final Unsafe unsafe;

    /**
     * Reference to MessageBuffer Constructors
     */
    private static final Constructor<?> mbArrConstructor;

    /**
     * The offset from the object memory header to its byte array data
     */
    static final int ARRAY_BYTE_BASE_OFFSET;

    private static final String UNIVERSAL_MESSAGE_BUFFER = "org.msgpack.core.buffer.MessageBufferU";
    private static final String BIGENDIAN_MESSAGE_BUFFER = "org.msgpack.core.buffer.MessageBufferBE";
    private static final String DEFAULT_MESSAGE_BUFFER = "org.msgpack.core.buffer.MessageBuffer";

    static {
        boolean useUniversalBuffer = false;
        Unsafe unsafeInstance = null;
        int arrayByteBaseOffset = 16;

        try {
            // Check java version
            String javaVersion = System.getProperty("java.specification.version", "");
            int dotPos = javaVersion.indexOf('.');
            boolean isJavaAtLeast7 = false;
            if (dotPos != -1) {
                try {
                    int major = Integer.parseInt(javaVersion.substring(0, dotPos));
                    int minor = Integer.parseInt(javaVersion.substring(dotPos + 1));
                    isJavaAtLeast7 = major > 1 || (major == 1 && minor >= 7);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace(System.err);
                }
            }

            boolean hasUnsafe = false;
            try {
                hasUnsafe = Class.forName("sun.misc.Unsafe") != null;
            }
            catch (Exception e) {
            }

            // Detect android VM
            boolean isAndroid = System.getProperty("java.runtime.name", "").toLowerCase().contains("android");

            // Is Google App Engine?
            boolean isGAE = System.getProperty("com.google.appengine.runtime.version") != null;

            // For Java6, android and JVM that has no Unsafe class, use Universal MessageBuffer
            useUniversalBuffer =
                    Boolean.parseBoolean(System.getProperty("msgpack.universal-buffer", "false"))
                            || isAndroid
                            || isGAE
                            || !isJavaAtLeast7
                            || !hasUnsafe;

            if (!useUniversalBuffer) {
                // Fetch theUnsafe object for Oracle and OpenJDK
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafeInstance = (Unsafe) field.get(null);
                if (unsafeInstance == null) {
                    throw new RuntimeException("Unsafe is unavailable");
                }
                arrayByteBaseOffset = unsafeInstance.arrayBaseOffset(byte[].class);
                int arrayByteIndexScale = unsafeInstance.arrayIndexScale(byte[].class);

                // Make sure the VM thinks bytes are only one byte wide
                if (arrayByteIndexScale != 1) {
                    throw new IllegalStateException("Byte array index scale must be 1, but is " + arrayByteIndexScale);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            // Use MessageBufferU
            useUniversalBuffer = true;
        }
        finally {
            // Initialize the static fields
            unsafe = unsafeInstance;
            ARRAY_BYTE_BASE_OFFSET = arrayByteBaseOffset;

            // Switch MessageBuffer implementation according to the environment
            isUniversalBuffer = useUniversalBuffer;
            String bufferClsName;
            if (isUniversalBuffer) {
                bufferClsName = UNIVERSAL_MESSAGE_BUFFER;
            }
            else {
                // Check the endian of this CPU
                boolean isLittleEndian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
                bufferClsName = isLittleEndian ? DEFAULT_MESSAGE_BUFFER : BIGENDIAN_MESSAGE_BUFFER;
            }

            try {
                // We need to use reflection here to find MessageBuffer implementation classes because
                // importing these classes creates TypeProfile and adds some overhead to method calls.

                // MessageBufferX (default, BE or U) class
                Class<?> bufferCls = Class.forName(bufferClsName);

                // MessageBufferX(byte[]) constructor
                Constructor<?> mbArrCstr = bufferCls.getDeclaredConstructor(byte[].class, int.class, int.class);
                mbArrCstr.setAccessible(true);
                mbArrConstructor = mbArrCstr;
            }
            catch (Exception e) {
                e.printStackTrace(System.err);
                throw new RuntimeException(e); // No more fallback exists if MessageBuffer constructors are inaccessible
            }
        }
    }

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

    public static MessageBuffer allocate(int length)
    {
        return wrap(new byte[length]);
    }

    public static MessageBuffer wrap(byte[] array)
    {
        return newMessageBuffer(array, 0, array.length);
    }

    public static MessageBuffer wrap(byte[] array, int offset, int length)
    {
        return newMessageBuffer(array, offset, length);
    }

    /**
     * Creates a new MessageBuffer instance backed by a java heap array
     *
     * @param arr
     * @return
     */
    private static MessageBuffer newMessageBuffer(byte[] arr, int off, int len)
    {
        checkNotNull(arr);
        try {
            return (MessageBuffer) mbArrConstructor.newInstance(arr, off, len);
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void releaseBuffer(MessageBuffer buffer)
    {
        if (isUniversalBuffer || buffer.base instanceof byte[]) {
            // We have nothing to do. Wait until the garbage-collector collects this array object
        }
        else {
            // Maybe cannot reach here
            unsafe.freeMemory(buffer.address);
        }
    }

    /**
     * Create a MessageBuffer instance from an java heap array
     *
     * @param arr
     * @param offset
     * @param length
     */
    MessageBuffer(byte[] arr, int offset, int length)
    {
        this.base = arr;
        this.address = ARRAY_BYTE_BASE_OFFSET + offset;
        this.size = length;
    }

    protected MessageBuffer(Object base, long address, int length)
    {
        this.base = base;
        this.address = address;
        this.size = length;
    }

    /**
     * byte size of the buffer
     *
     * @return
     */
    public int size()
    {
        return size;
    }

    public MessageBuffer slice(int offset, int length)
    {
        // TODO ensure deleting this slice does not collapse this MessageBuffer
        if (offset == 0 && length == size()) {
            return this;
        }
        else {
            checkArgument(offset + length <= size());
            return new MessageBuffer(base, address + offset, length);
        }
    }

    public byte getByte(int index)
    {
        return unsafe.getByte(base, address + index);
    }

    public boolean getBoolean(int index)
    {
        return unsafe.getBoolean(base, address + index);
    }

    public short getShort(int index)
    {
        short v = unsafe.getShort(base, address + index);
        return Short.reverseBytes(v);
    }

    /**
     * Read a big-endian int value at the specified index
     *
     * @param index
     * @return
     */
    public int getInt(int index)
    {
        // Reading little-endian value
        int i = unsafe.getInt(base, address + index);
        // Reversing the endian
        return Integer.reverseBytes(i);
    }

    public float getFloat(int index)
    {
        return Float.intBitsToFloat(getInt(index));
    }

    public long getLong(int index)
    {
        long l = unsafe.getLong(base, address + index);
        return Long.reverseBytes(l);
    }

    public double getDouble(int index)
    {
        return Double.longBitsToDouble(getLong(index));
    }

    public void getBytes(int index, byte[] dst, int dstOffset, int length)
    {
        unsafe.copyMemory(base, address + index, dst, ARRAY_BYTE_BASE_OFFSET + dstOffset, length);
    }

    public void getBytes(int index, int len, ByteBuffer dst)
    {
        if (dst.remaining() < len) {
            throw new BufferOverflowException();
        }
        ByteBuffer src = sliceAsByteBuffer(index, len);
        dst.put(src);
    }

    public void putByte(int index, byte v)
    {
        unsafe.putByte(base, address + index, v);
    }

    public void putBoolean(int index, boolean v)
    {
        unsafe.putBoolean(base, address + index, v);
    }

    public void putShort(int index, short v)
    {
        v = Short.reverseBytes(v);
        unsafe.putShort(base, address + index, v);
    }

    /**
     * Write a big-endian integer value to the memory
     *
     * @param index
     * @param v
     */
    public void putInt(int index, int v)
    {
        // Reversing the endian
        v = Integer.reverseBytes(v);
        unsafe.putInt(base, address + index, v);
    }

    public void putFloat(int index, float v)
    {
        putInt(index, Float.floatToRawIntBits(v));
    }

    public void putLong(int index, long l)
    {
        // Reversing the endian
        l = Long.reverseBytes(l);
        unsafe.putLong(base, address + index, l);
    }

    public void putDouble(int index, double v)
    {
        putLong(index, Double.doubleToRawLongBits(v));
    }

    public void putBytes(int index, byte[] src, int srcOffset, int length)
    {
        unsafe.copyMemory(src, ARRAY_BYTE_BASE_OFFSET + srcOffset, base, address + index, length);
    }

    public void putByteBuffer(int index, ByteBuffer src, int len)
    {
        assert (len <= src.remaining());
        assert (!isUniversalBuffer);

        if (src.isDirect()) {
            unsafe.copyMemory(null, DirectBufferAccess.getAddress(src) + src.position(), base, address + index, len);
            src.position(src.position() + len);
        }
        else if (src.hasArray()) {
            byte[] srcArray = src.array();
            unsafe.copyMemory(srcArray, ARRAY_BYTE_BASE_OFFSET + src.position(), base, address + index, len);
            src.position(src.position() + len);
        }
        else {
            if (base != null) {
                src.get((byte[]) base, index, len);
            }
            else {
                for (int i = 0; i < len; ++i) {
                    unsafe.putByte(base, address + index, src.get());
                }
            }
        }
    }

    /**
     * Create a ByteBuffer view of the range [index, index+length) of this memory
     *
     * @param index
     * @param length
     * @return
     */
    public ByteBuffer sliceAsByteBuffer(int index, int length)
    {
        return ByteBuffer.wrap((byte[]) base, (int) ((address - ARRAY_BYTE_BASE_OFFSET) + index), length);
    }

    /**
     * Get a ByteBuffer view of this buffer
     *
     * @return
     */
    public ByteBuffer sliceAsByteBuffer()
    {
        return sliceAsByteBuffer(0, size());
    }

    /**
     * Get a copy of this buffer
     *
     * @return
     */
    public byte[] toByteArray()
    {
        byte[] b = new byte[size()];
        unsafe.copyMemory(base, address, b, ARRAY_BYTE_BASE_OFFSET, size());
        return b;
    }

    public byte[] array()
    {
        return (byte[]) base;
    }

    public int arrayOffset()
    {
        return (int) address - ARRAY_BYTE_BASE_OFFSET;
    }

    /**
     * Copy this buffer contents to another MessageBuffer
     *
     * @param index
     * @param dst
     * @param offset
     * @param length
     */
    public void copyTo(int index, MessageBuffer dst, int offset, int length)
    {
        unsafe.copyMemory(base, address + index, dst.base, dst.address + offset, length);
    }

    public String toHexString(int offset, int length)
    {
        StringBuilder s = new StringBuilder();
        for (int i = offset; i < length; ++i) {
            if (i != offset) {
                s.append(" ");
            }
            s.append(String.format("%02x", getByte(i)));
        }
        return s.toString();
    }
}
