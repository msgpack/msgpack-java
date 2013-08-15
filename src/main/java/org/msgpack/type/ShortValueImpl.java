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
package org.msgpack.type;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

import java.io.IOException;
import java.math.BigInteger;

@SuppressWarnings("serial")
class ShortValueImpl extends IntegerClassValue {
    private short value;

    ShortValueImpl(short value) {
        this.value = value;
    }

    private static int BYTE_MAX = (int) Byte.MAX_VALUE;
    private static int BYTE_MIN = (int) Byte.MIN_VALUE;

    @Override
    public ValueType getType() {
        return ValueType.SHORT;
    }

    @Override
    public byte getByte() {
        if (value > BYTE_MAX || value < BYTE_MIN) {
            throw new MessageTypeException(); // TODO message
        }
        return (byte) value;
    }

    @Override
    public short getShort() {
        return value;
    }

    @Override
    public int getInt() {
        return value;
    }

    @Override
    public long getLong() {
        return value;
    }

    @Override
    public BigInteger getBigInteger() {
        return BigInteger.valueOf((long) value);
    }

    @Override
    public byte byteValue() {
        return (byte) value;
    }

    @Override
    public short shortValue() {
        return (short) value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf((long) value);
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return (double) value;
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
        pk.write(value);
    }

    // TODO compareTo

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if (!v.isIntegerClassValue()) {
            return false;
        }

        try {
            // TODO
            return value == v.asIntegerClassValue().getShort();
        } catch (MessageTypeException ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(Integer.toString(value));
    }
}
