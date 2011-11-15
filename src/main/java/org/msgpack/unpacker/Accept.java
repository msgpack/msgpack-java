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
package org.msgpack.unpacker;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.msgpack.io.BufferReferer;
import org.msgpack.MessageTypeException;

abstract class Accept implements BufferReferer {
    void acceptBoolean(boolean v) throws IOException {
        throw new MessageTypeException("Unexpected boolean value");
    }

    void acceptInteger(byte v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptInteger(short v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptInteger(int v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptInteger(long v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptUnsignedInteger(byte v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptUnsignedInteger(short v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptUnsignedInteger(int v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptUnsignedInteger(long v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }

    // void checkRawAcceptable() throws IOException {
    // throw new MessageTypeException("Unexpected raw value");
    // }

    void acceptRaw(byte[] raw) throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }

    void acceptEmptyRaw() throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }

    // void checkArrayAcceptable(int size) throws IOException {
    // throw new MessageTypeException("Unexpected array value");
    // }

    void acceptArray(int size) throws IOException {
        throw new MessageTypeException("Unexpected array value");
    }

    // void checkMapAcceptable(int size) throws IOException {
    // throw new MessageTypeException("Unexpected map value");
    // }

    void acceptMap(int size) throws IOException {
        throw new MessageTypeException("Unexpected map value");
    }

    void acceptNil() throws IOException {
        throw new MessageTypeException("Unexpected nil value");
    }

    void acceptFloat(float v) throws IOException {
        throw new MessageTypeException("Unexpected float value");
    }

    void acceptDouble(double v) throws IOException {
        throw new MessageTypeException("Unexpected float value");
    }

    public void refer(ByteBuffer bb, boolean gift) throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }
}
