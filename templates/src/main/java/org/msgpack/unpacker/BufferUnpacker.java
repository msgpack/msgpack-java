//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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
package org.msgpack.unpacker;

import java.nio.ByteBuffer;

/**
 * This class is buffer-specific deserializer.
 * 
 * @version 0.6.0
 * @see {@link org.msgpack.packer.Unpacker}
 */
public interface BufferUnpacker extends Unpacker {
    public BufferUnpacker wrap(byte[] b);

    public BufferUnpacker wrap(byte[] b, int off, int len);

    public BufferUnpacker wrap(ByteBuffer buf);

    public BufferUnpacker feed(byte[] b);

    public BufferUnpacker feed(byte[] b, boolean reference);

    public BufferUnpacker feed(byte[] b, int off, int len);

    public BufferUnpacker feed(byte[] b, int off, int len, boolean reference);

    public BufferUnpacker feed(ByteBuffer b);

    public BufferUnpacker feed(ByteBuffer buf, boolean reference);

    public int getBufferSize();

    public void copyReferencedBuffer();

    public void clear();
}
