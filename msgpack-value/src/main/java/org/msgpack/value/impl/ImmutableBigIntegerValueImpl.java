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

import org.msgpack.core.MessageTypeFamily;
import org.msgpack.value.Value;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.ImmutableIntegerValue;
import org.msgpack.value.MessageTypeIntegerOverflowException;
import org.msgpack.core.MessagePacker;

public class ImmutableBigIntegerValueImpl
        extends AbstractImmutableValue implements ImmutableIntegerValue {

    private final BigInteger value;

    public ImmutableBigIntegerValueImpl(BigInteger value) {
        // TODO check null
        this.value = value;
    }

    private static final BigInteger BYTE_MIN = BigInteger.valueOf((long) Byte.MIN_VALUE);
    private static final BigInteger BYTE_MAX = BigInteger.valueOf((long) Byte.MAX_VALUE);
    private static final BigInteger SHORT_MIN = BigInteger.valueOf((long) Short.MIN_VALUE);
    private static final BigInteger SHORT_MAX = BigInteger.valueOf((long) Short.MAX_VALUE);
    private static final BigInteger INT_MIN = BigInteger.valueOf((long) Integer.MIN_VALUE);
    private static final BigInteger INT_MAX = BigInteger.valueOf((long) Integer.MAX_VALUE);
    private static final BigInteger LONG_MIN = BigInteger.valueOf((long) Long.MIN_VALUE);
    private static final BigInteger LONG_MAX = BigInteger.valueOf((long) Long.MAX_VALUE);

    @Override
    public MessageTypeFamily getType() {
        return MessageTypeFamily.INTEGER;
    }

    @Override
    public ImmutableIntegerValue asIntegerValue() {
        return this;
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
    public byte getByte() throws MessageTypeIntegerOverflowException {
        if (!isInByteRange()) {
            throw new MessageTypeIntegerOverflowException(isInLongRange());
        }
        return value.byteValue();
    }

    @Override
    public short getShort() throws MessageTypeIntegerOverflowException {
        if (!isInShortRange()) {
            throw new MessageTypeIntegerOverflowException(isInLongRange());
        }
        return value.shortValue();
    }

    @Override
    public int getInt() throws MessageTypeIntegerOverflowException {
        if (!isInIntRange()) {
            throw new MessageTypeIntegerOverflowException(isInLongRange());
        }
        return value.intValue();
    }

    @Override
    public long getLong() throws MessageTypeIntegerOverflowException {
        if (!isInLongRange()) {
            throw new MessageTypeIntegerOverflowException(false);
        }
        return value.longValue();
    }

    @Override
    public BigInteger getBigInteger() throws MessageTypeIntegerOverflowException {
        return value;
    }

    @Override
    public boolean isInByteRange() {
        return 0 <= value.compareTo(BYTE_MIN) && value.compareTo(BYTE_MAX) <= 0;
    }

    @Override
    public boolean isInShortRange() {
        return 0 <= value.compareTo(SHORT_MIN) && value.compareTo(SHORT_MAX) <= 0;
    }

    @Override
    public boolean isInIntRange() {
        return 0 <= value.compareTo(INT_MIN) && value.compareTo(INT_MAX) <= 0;
    }

    @Override
    public boolean isInLongRange() {
        return 0 <= value.compareTo(LONG_MIN) && value.compareTo(LONG_MAX) <= 0;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packBigInteger(value);
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
        return value.equals(iv.bigIntegerValue());
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
}
