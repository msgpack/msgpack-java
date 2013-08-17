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
class BigIntegerValueImpl extends IntegerValue {
    private BigInteger value;

    BigIntegerValueImpl(BigInteger value) {
        this.value = value;
    }

    private static BigInteger BYTE_MAX = BigInteger.valueOf((long) Byte.MAX_VALUE);
    private static BigInteger SHORT_MAX = BigInteger.valueOf((long) Short.MAX_VALUE);
    private static BigInteger INT_MAX = BigInteger.valueOf((long) Integer.MAX_VALUE);
    private static BigInteger LONG_MAX = BigInteger.valueOf((long) Long.MAX_VALUE);
    private static BigInteger BYTE_MIN = BigInteger.valueOf((long) Byte.MIN_VALUE);
    private static BigInteger SHORT_MIN = BigInteger.valueOf((long) Short.MIN_VALUE);
    private static BigInteger INT_MIN = BigInteger.valueOf((long) Integer.MIN_VALUE);
    private static BigInteger LONG_MIN = BigInteger.valueOf((long) Long.MIN_VALUE);

    @Override
    public byte getByte() {
        if (value.compareTo(BYTE_MAX) > 0 || value.compareTo(BYTE_MIN) < 0) {
            throw new MessageTypeException(); // TODO message
        }
        return value.byteValue();
    }

    @Override
    public short getShort() {
        if (value.compareTo(SHORT_MAX) > 0 || value.compareTo(SHORT_MIN) < 0) {
            throw new MessageTypeException(); // TODO message
        }
        return value.shortValue();
    }

    @Override
    public int getInt() {
        if (value.compareTo(INT_MAX) > 0 || value.compareTo(INT_MIN) < 0) {
            throw new MessageTypeException(); // TODO message
        }
        return value.intValue();
    }

    @Override
    public long getLong() {
        if (value.compareTo(LONG_MAX) > 0 || value.compareTo(LONG_MIN) < 0) {
            throw new MessageTypeException(); // TODO message
        }
        return value.longValue();
    }

    @Override
    public BigInteger getBigInteger() {
        return value;
    }

    @Override
    public byte byteValue() {
        return value.byteValue();
    }

    @Override
    public short shortValue() {
        return value.shortValue();
    }

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
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

        return value.equals(v.asIntegerValue().bigIntegerValue());
    }

    @Override
    public int hashCode() {
        if (INT_MIN.compareTo(value) <= 0 && value.compareTo(INT_MAX) <= 0) {
            return (int) value.longValue();
        } else if (LONG_MIN.compareTo(value) <= 0
                && value.compareTo(LONG_MAX) <= 0) {
            long v = value.longValue();
            return (int) (v ^ (v >>> 32));
        }
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(value.toString());
    }
}
