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

import java.nio.ByteBuffer;

import static org.msgpack.core.Preconditions.checkArgument;

/**
 * MessageBufferBE is a {@link MessageBuffer} implementation tailored to big-endian machines.
 * The specification of Message Pack demands writing short/int/float/long/double values in the big-endian format.
 * In the big-endian machine, we need to swap the byte order.
 */
public class MessageBufferBE
        extends MessageBuffer
{
    MessageBufferBE(byte[] arr, int offset, int length)
    {
        super(arr, offset, length);
    }

    MessageBufferBE(ByteBuffer bb)
    {
        super(bb);
    }

    private MessageBufferBE(Object base, long address, int length)
    {
        super(base, address, length);
    }

    @Override
    public MessageBufferBE slice(int offset, int length)
    {
        if (offset == 0 && length == size()) {
            return this;
        }
        else {
            checkArgument(offset + length <= size());
            return new MessageBufferBE(base, address + offset, length);
        }
    }

    @Override
    public short getShort(int index)
    {
    	// EEN change: Numbers should be read in little endian style, so reversing endianness.
		short v = unsafe.getShort(base, address + index);
		return Short.reverseBytes(v);
    }

    @Override
    public int getInt(int index)
    {
		// EEN change: Numbers should be read in little endian style, so reversing endianness.
		int i = unsafe.getInt(base, address + index);
		// Reversing the endian
		return Integer.reverseBytes(i);
    }

    public long getLong(int index)
    {
		// EEN change: Numbers should be read in little endian style, so reversing endianness.
		long l = unsafe.getLong(base, address + index);
		return Long.reverseBytes(l);
    }

    @Override
    public float getFloat(int index)
    {
        return unsafe.getFloat(base, address + index);
    }

    @Override
    public double getDouble(int index)
    {
        return unsafe.getDouble(base, address + index);
    }

    @Override
    public void putShort(int index, short v)
    {
		// EEN change: Numbers should be written in little endian style, so reversing endianness.
		v = Short.reverseBytes(v);
		unsafe.putShort(base, address + index, v);
    }

    @Override
    public void putInt(int index, int v)
    {
		// EEN change: Numbers should be written in little endian style, so reversing endianness.
		v = Integer.reverseBytes(v);
		unsafe.putInt(base, address + index, v);
    }

    @Override
    public void putLong(int index, long l)
    {
		// EEN change: Numbers should be written in little endian style, so reversing endianness.
		l = Long.reverseBytes(l);
		unsafe.putLong(base, address + index, l);
    }

    @Override
    public void putDouble(int index, double v)
    {
        unsafe.putDouble(base, address + index, v);
    }
}
