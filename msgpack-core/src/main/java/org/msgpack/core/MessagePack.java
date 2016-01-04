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

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * This class has MessagePack prefix code definitions and packer/unpacker factory methods.
 */
public class MessagePack
{
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private static MessagePackFactory defaultFactory = new MessagePackFactory();

    /**
     * Sets the default configuration used for the static constructor methods of this MessagePack class.
     */
    public static void setDefaultFactory(MessagePackFactory newDefaultFactory)
    {
        defaultFactory = newDefaultFactory;
    }

    public static MessagePackFactory getDefaultFactory()
    {
        return defaultFactory;
    }

    private MessagePack()
    {
    }

    /**
     * Equivalent to getDefaultFactory().newPacker(out).
     *
     * @param out
     * @return
     */
    public static MessagePacker newDefaultPacker(OutputStream out)
    {
        return defaultFactory.newPacker(out);
    }

    /**
     * Equivalent to getDefaultFactory().newPacker(channel).
     *
     * @param channel
     * @return
     */
    public static MessagePacker newDefaultPacker(WritableByteChannel channel)
    {
        return defaultFactory.newPacker(channel);
    }

    /**
     * Equivalent to getDefaultFactory().newBufferPacker()
     *
     * @return
     */
    public static MessageBufferPacker newDefaultBufferPacker()
    {
        return defaultFactory.newBufferPacker();
    }

    /**
     * Equivalent to getDefaultFactory().newUnpacker(in).
     *
     * @param in
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(InputStream in)
    {
        return defaultFactory.newUnpacker(in);
    }

    /**
     * Equivalent to getDefaultFactory().newUnpacker(channel).
     *
     * @param channel
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(ReadableByteChannel channel)
    {
        return defaultFactory.newUnpacker(channel);
    }

    /**
     * Equivalent to getDefaultFactory().newUnpacker(contents).
     *
     * @param contents
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(byte[] contents)
    {
        return defaultFactory.newUnpacker(contents);
    }

    /**
     * Equivalent to getDefaultFactory().newUnpacker(contents, offset, length).
     *
     * @param contents
     * @param offset
     * @param length
     * @return
     */
    public static MessageUnpacker newDefaultUnpacker(byte[] contents, int offset, int length)
    {
        return defaultFactory.newUnpacker(contents, offset, length);
    }
}
