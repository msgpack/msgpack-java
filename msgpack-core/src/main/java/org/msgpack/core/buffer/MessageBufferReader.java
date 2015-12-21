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

public class MessageBufferReader
{
    // TODO add more reader methods for each type

    private MessageBuffer buffer;
    private int position;

    MessageBufferReader(MessageBuffer buffer)
    {
        this.buffer = buffer;
    }

    public MessageBuffer buffer()
    {
        return buffer;
    }

    public int position()
    {
        return position;
    }

    public void position(int newPosition)
    {
        // TODO validation?
        this.position = newPosition;
    }

    public int remaining()
    {
        return buffer.size() - position;
    }

    public byte getByte()
    {
        return buffer.getByte(position);
    }

    public byte readByte()
    {
        byte v = buffer.getByte(position);
        position += 1;
        return v;
    }

    public short getShort()
    {
        return buffer.getShort(position);
    }

    public short readShort()
    {
        short v = buffer.getShort(position);
        position += 1;
        return v;
    }

    public int getInt()
    {
        return buffer.getInt(position);
    }

    public int readInt()
    {
        int v = buffer.getInt(position);
        position += 1;
        return v;
    }

    public long getLong()
    {
        return buffer.getLong(position);
    }

    public long readLong()
    {
        long v = buffer.getLong(position);
        position += 1;
        return v;
    }

    public float getFloat()
    {
        return buffer.getFloat(position);
    }

    public float readFloat()
    {
        float v = buffer.getFloat(position);
        position += 1;
        return v;
    }

    public double getDouble()
    {
        return buffer.getDouble(position);
    }

    public double readDouble()
    {
        double v = buffer.getDouble(position);
        position += 1;
        return v;
    }
}
