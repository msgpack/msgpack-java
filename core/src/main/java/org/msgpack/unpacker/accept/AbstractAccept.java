//
// MessagePack for Java
//
// Copyright (C) 2009-2013 FURUHASHI Sadayuki
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
package org.msgpack.unpacker.accept;

import java.io.IOException;
import org.msgpack.MessageTypeException;

public abstract class AbstractAccept implements Accept {
    private static final byte[] EMPTY = new byte[0];

    public void acceptNil() throws IOException {
        throw new MessageTypeException("Unexpected nil value");
    }

    public void acceptBoolean(boolean v) throws IOException {
        throw new MessageTypeException("Unexpected boolean value");
    }

    public void acceptInt(int v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptLong(long v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptUnsignedLong(long v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    public void acceptByteArray(byte[] raw) throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }

    public void acceptEmptyByteArray() throws IOException {
        acceptByteArray(EMPTY);
    }

    public void acceptFloat(float v) throws IOException {
        throw new MessageTypeException("Unexpected float value");
    }

    public void acceptDouble(double v) throws IOException {
        throw new MessageTypeException("Unexpected float value");
    }

    public void acceptArrayHeader(int size) throws IOException {
        throw new MessageTypeException("Unexpected array value");
    }

    public void acceptMapHeader(int size) throws IOException {
        throw new MessageTypeException("Unexpected map value");
    }

    public void acceptExt(byte type, byte[] value) throws IOException {
        throw new MessageTypeException("Unexpected ext value");
    }
}
