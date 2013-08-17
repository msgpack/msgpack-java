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

import java.io.IOException;
import java.nio.ByteBuffer;

final class SkipAccept extends Accept {
    SkipAccept() {
        super(null);
    }

    @Override
    void acceptBoolean(boolean v) {
    }

    @Override
    void acceptInteger(byte v) {
    }

    @Override
    void acceptInteger(short v) {
    }

    @Override
    void acceptInteger(int v) {
    }

    @Override
    void acceptInteger(long v) {
    }

    @Override
    void acceptUnsignedInteger(byte v) {
    }

    @Override
    void acceptUnsignedInteger(short v) {
    }

    @Override
    void acceptUnsignedInteger(int v) {
    }

    @Override
    void acceptUnsignedInteger(long v) {
    }

    @Override
    void acceptRaw(byte[] raw) {
    }

    @Override
    void acceptEmptyRaw() {
    }

    @Override
    public void refer(ByteBuffer bb, boolean gift) throws IOException {
    }

    @Override
    void acceptArray(int size) {
    }

    @Override
    void acceptMap(int size) {
    }

    @Override
    void acceptNil() {
    }

    @Override
    void acceptFloat(float v) {
    }

    @Override
    void acceptDouble(double v) {
    }
}
