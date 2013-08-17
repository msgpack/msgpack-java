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

import java.math.BigInteger;
import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;

@SuppressWarnings("serial")
class LongValueImpl extends IntegerValue {
    private long value;

    LongValueImpl(long value) {
        this.value = value;
    }

    private static long BYTE_MAX = (long) Byte.MAX_VALUE;
    private static long SHORT_MAX = (long) Short.MAX_VALUE;
    private static long INT_MAX = (long) Integer.MAX_VALUE;

    private static long BYTE_MIN = (long) Byte.MIN_VALUE;
    private static long SHORT_MIN = (long) Short.MIN_VALUE;
    private static long INT_MIN = (long) Integer.MIN_VALUE;

    @Override
    public byte getByte() {
        if (value > BYTE_MAX || value < BYTE_MIN) {
            throw new MessageTypeException(); // TODO message
        }
        return (byte) value;
    }

    @Override
    public short getShort() {
        if (value > SHORT_MAX || value < SHORT_MIN) {
            throw new MessageTypeException(); // TODO message
        }
        return (short) value;
    }

    @Override
    public int getInt() {
        if (value > INT_MAX || value < INT_MIN) {
            throw new MessageTypeException(); // TODO message
        }
        return (int) value;
    }

    @Override
    public long getLong() {
        return value;
    }

    @Override
    public BigInteger getBigInteger() {
        return BigInteger.valueOf(value);
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
        return (int) value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf(value);
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
        if (!v.isIntegerValue()) {
            return false;
        }

        try {
            // TODO
            return value == v.asIntegerValue().getLong();
        } catch (MessageTypeException ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (INT_MIN <= value && value <= INT_MAX) {
            return (int) value;
        } else {
            return (int) (value ^ (value >>> 32));
        }
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(Long.toString(value));
    }
}
