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
package org.msgpack.util.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.BufferUnpacker;

public class JSON extends MessagePack {
    public JSON() {
        super();
    }

    public JSON(MessagePack msgpack) {
        super(msgpack);
    }

    @Override
    public Packer createPacker(OutputStream stream) {
        return new JSONPacker(this, stream);
    }

    @Override
    public BufferPacker createBufferPacker() {
        return new JSONBufferPacker(this);
    }

    @Override
    public BufferPacker createBufferPacker(int bufferSize) {
        return new JSONBufferPacker(this, bufferSize);
    }

    @Override
    public Unpacker createUnpacker(InputStream stream) {
        return new JSONUnpacker(this, stream);
    }

    @Override
    public BufferUnpacker createBufferUnpacker() {
        return new JSONBufferUnpacker();
    }

    @Override
    public BufferUnpacker createBufferUnpacker(byte[] b) {
        return createBufferUnpacker().wrap(b);
    }

    @Override
    public BufferUnpacker createBufferUnpacker(byte[] b, int off, int len) {
        return createBufferUnpacker().wrap(b, off, len);
    }

    @Override
    public BufferUnpacker createBufferUnpacker(ByteBuffer bb) {
        return createBufferUnpacker().wrap(bb);
    }
}
