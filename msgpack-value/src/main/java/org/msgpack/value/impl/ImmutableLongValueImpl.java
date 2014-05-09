//
// MessagePack for Java
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
package org.msgpack.value.impl;

import java.io.IOException;
import java.math.BigInteger;

import org.msgpack.value.Value;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.ImmutableIntegerValue;
import org.msgpack.value.MessageTypeIntegerOverflowException;
import org.msgpack.core.ValueType;
import org.msgpack.core.MessagePacker;

public class ImmutableLongValueImpl
        extends AbstractImmutableValue implements ImmutableIntegerValue {

    private final long value;

    public ImmutableLongValueImpl(long value) {
        this.value = value;
    }

    private static final long BYTE_MIN = (long) Byte.MIN_VALUE;
    private static final long BYTE_MAX = (long) Byte.MAX_VALUE;
    private static final long SHORT_MIN = (long) Short.MIN_VALUE;
    private static final long SHORT_MAX = (long) Short.MAX_VALUE;
    private static final long INT_MIN = (long) Integer.MIN_VALUE;
    private static final long INT_MAX = (long) Integer.MAX_VALUE;

    @Override
    public ValueType getType() {
        return ValueType.INTEGER;
    }

    @Override
    public ImmutableIntegerValue asIntegerValue() {
        return this;
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
    public byte getByte() throws MessageTypeIntegerOverflowException {
        if (!isInByteRange()) {
            throw new MessageTypeIntegerOverflowException(true);
        }
        return (byte) value;
    }

    @Override
    public short getShort() throws MessageTypeIntegerOverflowException {
        if (!isInByteRange()) {
            throw new MessageTypeIntegerOverflowException(true);
        }
        return (short) value;
    }

    @Override
    public int getInt() throws MessageTypeIntegerOverflowException {
        if (!isInIntRange()) {
            throw new MessageTypeIntegerOverflowException(true);
        }
        return (int) value;
    }

    @Override
    public long getLong() throws MessageTypeIntegerOverflowException {
        return value;
    }

    @Override
    public BigInteger getBigInteger() throws MessageTypeIntegerOverflowException {
        return BigInteger.valueOf((long) value);
    }

    @Override
    public boolean isInByteRange() {
        return BYTE_MIN <= value && value <= BYTE_MAX;
    }

    @Override
    public boolean isInShortRange() {
        return SHORT_MIN <= value && value <= SHORT_MAX;
    }

    @Override
    public boolean isInIntRange() {
        return INT_MIN <= value && value <= INT_MAX;
    }

    @Override
    public boolean isInLongRange() {
        return true;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packLong(value);
    }

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
        IntegerValue iv = v.asIntegerValue();
        if (!iv.isInLongRange()) {
            return false;
        }
        return iv.longValue() == value;
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
}
