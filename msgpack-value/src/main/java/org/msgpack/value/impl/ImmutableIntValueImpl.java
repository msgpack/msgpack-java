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
import org.msgpack.core.Packer;

public class ImmutableIntValueImpl
        extends AbstractImmutableValue implements ImmutableIntegerValue {

    private final int value;

    public ImmutableIntValueImpl(int value) {
        this.value = value;
    }

    private static int BYTE_MIN = (int) Byte.MIN_VALUE;
    private static int BYTE_MAX = (int) Byte.MAX_VALUE;
    private static int SHORT_MIN = (int) Short.MIN_VALUE;
    private static int SHORT_MAX = (int) Short.MAX_VALUE;

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
        return value;
    }

    @Override
    public long longValue() {
        return value;
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
        return value;
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
        return true;
    }

    @Override
    public boolean isInLongRange() {
        return true;
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
        pk.writeInt(value);
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
        if (!iv.isInIntRange()) {
            return false;
        }
        return iv.intValue() == value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
