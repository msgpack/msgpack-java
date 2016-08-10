//
// MessagePack for Java
//
// Copyright (C) 2014 Takatoshi Kondo
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

import java.math.BigInteger;
import org.msgpack.type.ValueFactory;
import org.msgpack.type.NumberValue;

final class NumberAccept extends Accept {
    NumberValue value;

    NumberAccept() {
        super("number");
    }

    @Override
    void acceptInteger(byte v) {
        this.value = ValueFactory.createIntegerValue(v);
    }

    @Override
    void acceptInteger(short v) {
        this.value = ValueFactory.createIntegerValue(v);
    }

    @Override
    void acceptInteger(int v) {
        this.value = ValueFactory.createIntegerValue(v);
    }

    @Override
    void acceptInteger(long v) {
        this.value = ValueFactory.createIntegerValue(v);
    }

    @Override
    void acceptUnsignedInteger(byte v) {
        this.value = ValueFactory.createIntegerValue(v & 0xff);
    }

    @Override
    void acceptUnsignedInteger(short v) {
        this.value = ValueFactory.createIntegerValue(v & 0xffff);
    }

    @Override
    void acceptUnsignedInteger(int v) {
        if (v < 0) {
            this.value = ValueFactory.createIntegerValue((long) (v & 0x7fffffff) + 0x80000000L);
        } else {
            this.value = ValueFactory.createIntegerValue(v);
        }
    }

    @Override
    void acceptUnsignedInteger(long v) {
        if (v < 0L) {
            this.value = ValueFactory.createIntegerValue(
                BigInteger.valueOf(v + Long.MAX_VALUE + 1L).setBit(63));
        } else {
            this.value = ValueFactory.createIntegerValue(v);
        }
    }

    @Override
    void acceptFloat(float v) {
        this.value = ValueFactory.createFloatValue(v);
    }

    @Override
    void acceptDouble(double v) {
        this.value = ValueFactory.createFloatValue(v);
    }
}
