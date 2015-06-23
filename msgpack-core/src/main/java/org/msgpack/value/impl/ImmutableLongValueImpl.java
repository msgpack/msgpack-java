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

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageIntegerOverflowException;
import org.msgpack.value.*;

import java.io.IOException;
import java.math.BigInteger;


/**
 * {@code ImmutableLongValueImpl} Implements {@code ImmutableIntegerValue} using a {@code long} field.
 *
 * @see  org.msgpack.value.IntegerValue
 */
public class ImmutableLongValueImpl extends AbstractImmutableValue implements ImmutableIntegerValue {
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
    public ValueType getValueType() {
        return ValueType.INTEGER;
    }

    @Override
    public ImmutableIntegerValue immutableValue() {
        return this;
    }

    @Override
    public ImmutableNumberValue asNumberValue() {
        return this;
    }

    @Override
    public ImmutableIntegerValue asIntegerValue() {
        return this;
    }

    @Override
    public byte castAsByte() {
        return (byte) value;
    }

    @Override
    public short castAsShort() {
        return (short) value;
    }

    @Override
    public int castAsInt() {
        return (int) value;
    }

    @Override
    public long castAsLong() {
        return value;
    }

    @Override
    public BigInteger castAsBigInteger() {
        return BigInteger.valueOf(value);
    }

    @Override
    public float castAsFloat() {
        return (float) value;
    }

    @Override
    public double castAsDouble() {
        return (double) value;
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
    public byte getByte() {
        if (!isInByteRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (byte) value;
    }

    @Override
    public short getShort() {
        if (!isInByteRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (short) value;
    }

    @Override
    public int getInt() {
        if (!isInIntRange()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (int) value;
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
        return value == iv.castAsLong();
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
