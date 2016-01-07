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

import org.msgpack.core.buffer.ArrayBufferOutput;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.core.buffer.MessageBufferOutput;

import java.io.IOException;
import java.util.List;

/**
 * MessagePacker that is useful to produce byte array output
 */
public class MessageBufferPacker
        extends MessagePacker
{
    protected MessageBufferPacker(MessagePack.PackerConfig config)
    {
        this(new ArrayBufferOutput(), config);
    }

    protected MessageBufferPacker(ArrayBufferOutput out, MessagePack.PackerConfig config)
    {
        super(out, config);
    }

    public MessageBufferOutput reset(MessageBufferOutput out)
            throws IOException
    {
        if (!(out instanceof ArrayBufferOutput)) {
            throw new IllegalArgumentException("MessageBufferPacker accepts only ArrayBufferOutput");
        }
        return super.reset(out);
    }

    private ArrayBufferOutput getArrayBufferOut()
    {
        return (ArrayBufferOutput) out;
    }

    public void clear()
    {
        getArrayBufferOut().clear();
    }

    public byte[] toByteArray()
    {
        try {
            flush();
        }
        catch (IOException ex) {
            // IOException must not happen because underlaying ArrayBufferOutput never throws IOException
            throw new RuntimeException(ex);
        }
        return getArrayBufferOut().toByteArray();
    }

    public MessageBuffer toMessageBuffer()
    {
        try {
            flush();
        }
        catch (IOException ex) {
            // IOException must not happen because underlaying ArrayBufferOutput never throws IOException
            throw new RuntimeException(ex);
        }
        return getArrayBufferOut().toMessageBuffer();
    }

    public List<MessageBuffer> toBufferList()
    {
        try {
            flush();
        }
        catch (IOException ex) {
            // IOException must not happen because underlaying ArrayBufferOutput never throws IOException
            throw new RuntimeException(ex);
        }
        return getArrayBufferOut().toBufferList();
    }
}
