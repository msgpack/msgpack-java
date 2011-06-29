//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
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
package org.msgpack;

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessagePack {
    public <T> T unpack(InputStream in, T v) {
        // TODO
        return null;
    }

    public <T> T unpack(InputStream in, Class<T> c) {
        // TODO
        return null;
    }

    public <T> T unpack(byte[] b, T v) {
        // TODO
        return null;
    }

    public <T> T unpack(byte[] b, Class<T> c) {
        // TODO
        return null;
    }

    public <T> T unpack(ByteBuffer b, T v) {
        // TODO
        return null;
    }

    public <T> T unpack(ByteBuffer b, Class<T> c) {
        // TODO
        return null;
    }


    // TODO
    private static final MessagePack globalMessagePack;

    @Deprecated
    public static <T> T unpack(InputStream in, T v) {
        return globalMessagePack.unpack(in, v);
    }

    @Deprecated
    public static <T> T unpack(InputStream in, Class<T> c) {
        return globalMessagePack.unpack(in, c);
    }

    @Deprecated
    public static <T> T unpack(byte[] b, T v) {
        return globalMessagePack.unpack(b, v);
    }

    @Deprecated
    public static <T> T unpack(byte[] b, Class<T> c) {
        return globalMessagePack.unpack(b, c);
    }

    @Deprecated
    public static <T> T unpack(ByteBuffer b, T v) {
        return globalMessagePack.unpack(b, v);
    }

    @Deprecated
    public static <T> T unpack(ByteBuffer b, Class<T> c) {
        return globalMessagePack.unpack(b, c);
    }
}

