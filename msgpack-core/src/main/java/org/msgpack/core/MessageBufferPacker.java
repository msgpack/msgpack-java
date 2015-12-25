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
package org.msgpack.core;

import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.core.buffer.MessageBufferOutput;
import org.msgpack.core.buffer.ArrayBufferOutput;

import java.io.IOException;
import java.util.List;

public class MessageBufferPacker
        extends MessagePacker
{
    public MessageBufferPacker()
    {
        this(new ArrayBufferOutput());
    }

    public MessageBufferPacker(ArrayBufferOutput out)
    {
        super(out);
    }

    @Override
    public MessageBufferPacker setSmallStringOptimizationThreshold(int bytes)
    {
        super.setSmallStringOptimizationThreshold(bytes);
        return this;
    }

    public MessageBufferOutput reset(MessageBufferOutput out)
            throws IOException
    {
        if (!(out instanceof ArrayBufferOutput)) {
            throw new IllegalArgumentException("MessageBufferPacker accepts only ArrayBufferOutput");
        }
        return super.reset(out);
    }

    public void clear()
    {
        ((ArrayBufferOutput) out).clear();
    }

    public byte[] toByteArray()
    {
        return ((ArrayBufferOutput) out).toByteArray();
    }

    public MessageBuffer toMessageBuffer()
    {
        return ((ArrayBufferOutput) out).toMessageBuffer();
    }

    public List<MessageBuffer> toBufferList()
    {
        return ((ArrayBufferOutput) out).toBufferList();
    }
}
