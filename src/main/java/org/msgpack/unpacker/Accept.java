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

import org.msgpack.MessageTypeException;

abstract class Accept {
    void acceptBoolean(boolean v) {
        throw new MessageTypeException("Unexpected boolean value");
    }

    void acceptInteger(byte v) {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptInteger(short v) {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptInteger(int v) {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptInteger(long v) {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptUnsignedInteger(byte v) {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptUnsignedInteger(short v) {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptUnsignedInteger(int v) {
        throw new MessageTypeException("Unexpected integer value");
    }

    void acceptUnsignedInteger(long v) {
        throw new MessageTypeException("Unexpected integer value");
    }

    //void checkRawAcceptable() {
    //    throw new MessageTypeException("Unexpected raw value");
    //}

    void acceptRaw(byte[] raw) {
        throw new MessageTypeException("Unexpected raw value");
    }

    void acceptEmptyRaw() {
        throw new MessageTypeException("Unexpected raw value");
    }

    //void checkArrayAcceptable(int size) {
    //    throw new MessageTypeException("Unexpected array value");
    //}

    void acceptArray(int size) {
        throw new MessageTypeException("Unexpected array value");
    }

    //void checkMapAcceptable(int size) {
    //    throw new MessageTypeException("Unexpected map value");
    //}

    void acceptMap(int size) {
        throw new MessageTypeException("Unexpected map value");
    }

    void acceptNil() {
        throw new MessageTypeException("Unexpected nil value");
    }

    void acceptFloat(float v) {
        throw new MessageTypeException("Unexpected float value");
    }

    void acceptDouble(double v) {
        throw new MessageTypeException("Unexpected float value");
    }
}

