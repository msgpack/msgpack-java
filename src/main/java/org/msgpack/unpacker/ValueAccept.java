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

import java.math.BigInteger;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.packer.Unconverter;

final class ValueAccept extends Accept {
    private Unconverter uc = null;

    void setUnconverter(Unconverter uc) {
        this.uc = uc;
    }

    @Override
    void acceptBoolean(boolean v) {
        uc.write(ValueFactory.booleanValue(v));
    }

    @Override
    void acceptInteger(byte v) {
        uc.write(ValueFactory.integerValue(v));
    }

    @Override
    void acceptInteger(short v) {
        uc.write(ValueFactory.integerValue(v));
    }

    @Override
    void acceptInteger(int v) {
        uc.write(ValueFactory.integerValue(v));
    }

    @Override
    void acceptInteger(long v) {
        uc.write(ValueFactory.integerValue(v));
    }

    @Override
    void acceptUnsignedInteger(byte v) {
        uc.write(ValueFactory.integerValue(v & 0xff));
    }

    @Override
    void acceptUnsignedInteger(short v) {
        uc.write(ValueFactory.integerValue(v & 0xffff));
    }

    @Override
    void acceptUnsignedInteger(int v) {
        if(v < 0) {
            long value = (long)(v & 0x7fffffff) + 0x80000000L;
            uc.write(ValueFactory.integerValue(value));
        } else {
            uc.write(ValueFactory.integerValue(v));
        }
    }

    @Override
    void acceptUnsignedInteger(long v) {
        if(v < 0L) {
            BigInteger value = BigInteger.valueOf(v+Long.MAX_VALUE+1L).setBit(63);
            uc.write(ValueFactory.integerValue(value));
        } else {
            uc.write(ValueFactory.integerValue(v));
        }
    }

    @Override
    void acceptRaw(byte[] raw) {
        uc.write(ValueFactory.rawValue(raw));
    }

    @Override
    void acceptEmptyRaw() {
        uc.write(ValueFactory.rawValue());
    }

    @Override
    void acceptArray(int size) {
        uc.writeArrayBegin(size);
    }

    @Override
    void acceptMap(int size) {
        uc.writeMapBegin(size);
    }

    @Override
    void acceptNil() {
        uc.write(ValueFactory.nilValue());
    }

    @Override
    void acceptFloat(float v) {
        uc.write(ValueFactory.floatValue(v));
    }

    @Override
    void acceptDouble(double v) {
        uc.write(ValueFactory.floatValue(v));
    }
}

