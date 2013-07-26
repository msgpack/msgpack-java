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
import java.math.BigInteger;
import org.msgpack.type.ValueFactory;
import org.msgpack.packer.Unconverter;

final class ValueAccept extends Accept {
    private Unconverter uc = null;

    ValueAccept() {
        super(null);
    }

    void setUnconverter(Unconverter uc) throws IOException {
        this.uc = uc;
    }

    @Override
    void acceptBoolean(boolean v) throws IOException {
        uc.write(ValueFactory.createBooleanValue(v));
    }

    @Override
    void acceptInteger(byte v) throws IOException {
        uc.write(ValueFactory.createIntegerValue(v));
    }

    @Override
    void acceptInteger(short v) throws IOException {
        uc.write(ValueFactory.createIntegerValue(v));
    }

    @Override
    void acceptInteger(int v) throws IOException {
        uc.write(ValueFactory.createIntegerValue(v));
    }

    @Override
    void acceptInteger(long v) throws IOException {
        uc.write(ValueFactory.createIntegerValue(v));
    }

    @Override
    void acceptUnsignedInteger(byte v) throws IOException {
        uc.write(ValueFactory.createIntegerValue(v & 0xff));
    }

    @Override
    void acceptUnsignedInteger(short v) throws IOException {
        uc.write(ValueFactory.createIntegerValue(v & 0xffff));
    }

    @Override
    void acceptUnsignedInteger(int v) throws IOException {
        if (v < 0) {
            long value = (long) (v & 0x7fffffff) + 0x80000000L;
            uc.write(ValueFactory.createIntegerValue(value));
        } else {
            uc.write(ValueFactory.createIntegerValue(v));
        }
    }

    @Override
    void acceptUnsignedInteger(long v) throws IOException {
        if (v < 0L) {
            BigInteger value = BigInteger.valueOf(v + Long.MAX_VALUE + 1L)
                    .setBit(63);
            uc.write(ValueFactory.createIntegerValue(value));
        } else {
            uc.write(ValueFactory.createIntegerValue(v));
        }
    }

    @Override
    void acceptRaw(byte[] raw) throws IOException {
        uc.write(ValueFactory.createRawValue(raw));
    }

    @Override
    void acceptEmptyRaw() throws IOException {
        uc.write(ValueFactory.createRawValue());
    }

    @Override
    public void refer(ByteBuffer bb, boolean gift) throws IOException {
        // TODO gift
        byte[] raw = new byte[bb.remaining()];
        bb.get(raw);
        uc.write(ValueFactory.createRawValue(raw, true));
    }

    @Override
    void acceptArray(int size) throws IOException {
        uc.writeArrayBegin(size);
    }

    @Override
    void acceptMap(int size) throws IOException {
        uc.writeMapBegin(size);
    }

    @Override
    void acceptNil() throws IOException {
        uc.write(ValueFactory.createNilValue());
    }

    @Override
    void acceptFloat(float v) throws IOException {
        uc.write(ValueFactory.createFloatValue(v));
    }

    @Override
    void acceptDouble(double v) throws IOException {
        uc.write(ValueFactory.createFloatValue(v));
    }
}
