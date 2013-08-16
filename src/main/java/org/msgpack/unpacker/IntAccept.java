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

import org.msgpack.MessageTypeException;

final class IntAccept extends Accept {
    int value;

    IntAccept() {
        super("integer");
    }

    @Override
    void acceptInteger(byte v) {
        this.value = (int) v;
    }

    @Override
    void acceptInteger(short v) {
        this.value = (int) v;
    }

    @Override
    void acceptInteger(int v) {
        this.value = v;
    }

    @Override
    void acceptInteger(long v) {
        if (v < (long) Integer.MIN_VALUE || v > (long) Integer.MAX_VALUE) {
            throw new MessageTypeException(); // TODO message
        }
        this.value = (int) v;
    }

    @Override
    void acceptUnsignedInteger(byte v) {
        this.value = v & 0xff;
    }

    @Override
    void acceptUnsignedInteger(short v) {
        this.value = v & 0xffff;
    }

    @Override
    void acceptUnsignedInteger(int v) {
        if (v < 0) {
            throw new MessageTypeException(); // TODO message
        }
        this.value = v;
    }

    @Override
    void acceptUnsignedInteger(long v) {
        if (v < 0 || v > (long) Integer.MAX_VALUE) {
            throw new MessageTypeException(); // TODO message
        }
        this.value = (int) v;
    }
}
