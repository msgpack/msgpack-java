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
 * Universal MessageBuffer implementation supporting Java6 and Android.
 * This buffer always uses ByteBuffer-based memory access
 */
public class MessageBufferU
        extends MessageBuffer
{
    private final ByteBuffer wrap;

    MessageBufferU(byte[] arr, int offset, int length)
    {
        super(arr, offset, length);
        this.wrap = ByteBuffer.wrap(arr, offset, length).slice();
    }

    MessageBufferU(ByteBuffer bb)
    {
        super(bb);
        this.wrap = bb.slice();
    }

    private MessageBufferU(Object base, long address, int length, ByteBuffer wrap)
    {
        super(base, address, length);
        this.wrap = wrap;
    }

    @Override
    public MessageBufferU slice(int offset, int length)
    {
        if (offset == 0 && length == size()) {
            return this;
        }
        else {
            checkArgument(offset + length <= size());
            try {
                wrap.position(offset);
                wrap.limit(offset + length);
                return new MessageBufferU(base, address + offset, length, wrap.slice());
            }
            finally {
                resetBufferPosition();
            }
        }
    }

    private void resetBufferPosition()
    {
        wrap.position(0);
        wrap.limit(size);
    }

    @Override
    public byte getByte(int index)
    {
        return wrap.get(index);
    }

    @Override
    public boolean getBoolean(int index)
    {
        return wrap.get(index) != 0;
    }

    @Override
    public short getShort(int index)
    {
        return wrap.getShort(index);
    }

    @Override
    public int getInt(int index)
    {
        return wrap.getInt(index);
    }

    @Override
    public float getFloat(int index)
    {
        return wrap.getFloat(index);
    }

    @Override
    public long getLong(int index)
    {
        return wrap.getLong(index);
    }

    @Override
    public double getDouble(int index)
    {
        return wrap.getDouble(index);
    }

    @Override
    public void getBytes(int index, int len, ByteBuffer dst)
    {
        try {
            wrap.position(index);
            wrap.limit(index + len);
            dst.put(wrap);
        }
        finally {
            resetBufferPosition();
        }
    }

    @Override
    public void putByte(int index, byte v)
    {
        wrap.put(index, v);
    }

    @Override
    public void putBoolean(int index, boolean v)
    {
        wrap.put(index, v ? (byte) 1 : (byte) 0);
    }

    @Override
    public void putShort(int index, short v)
    {
        wrap.putShort(index, v);
    }

    @Override
    public void putInt(int index, int v)
    {
        wrap.putInt(index, v);
    }

    @Override
    public void putFloat(int index, float v)
    {
        wrap.putFloat(index, v);
    }

    @Override
    public void putLong(int index, long l)
    {
        wrap.putLong(index, l);
    }

    @Override
    public void putDouble(int index, double v)
    {
        wrap.putDouble(index, v);
    }

    @Override
    public ByteBuffer sliceAsByteBuffer(int index, int length)
    {
        try {
            wrap.position(index);
            wrap.limit(index + length);
            return wrap.slice();
        }
        finally {
            resetBufferPosition();
        }
    }

    @Override
    public ByteBuffer sliceAsByteBuffer()
    {
        return sliceAsByteBuffer(0, size);
    }

    @Override
    public void getBytes(int index, byte[] dst, int dstOffset, int length)
    {
        try {
            wrap.position(index);
            wrap.get(dst, dstOffset, length);
        }
        finally {
            resetBufferPosition();
        }
    }

    @Override
    public void putByteBuffer(int index, ByteBuffer src, int len)
    {
        assert (len <= src.remaining());

        if (src.hasArray()) {
            putBytes(index, src.array(), src.position() + src.arrayOffset(), len);
            src.position(src.position() + len);
        }
        else {
            int prevSrcLimit = src.limit();
            try {
                src.limit(src.position() + len);
                wrap.position(index);
                wrap.put(src);
            }
            finally {
                src.limit(prevSrcLimit);
            }
        }
    }

    @Override
    public void putBytes(int index, byte[] src, int srcOffset, int length)
    {
        try {
            wrap.position(index);
            wrap.put(src, srcOffset, length);
        }
        finally {
            resetBufferPosition();
        }
    }

    @Override
    public void copyTo(int index, MessageBuffer dst, int offset, int length)
    {
        try {
            wrap.position(index);
            dst.putByteBuffer(offset, wrap, length);
        }
        finally {
            resetBufferPosition();
        }
    }

    @Override
    public void putMessageBuffer(int index, MessageBuffer src, int srcOffset, int len)
    {
        putByteBuffer(index, src.sliceAsByteBuffer(srcOffset, len), len);
    }

    @Override
    public byte[] toByteArray()
    {
        byte[] b = new byte[size()];
        getBytes(0, b, 0, b.length);
        return b;
    }
}
