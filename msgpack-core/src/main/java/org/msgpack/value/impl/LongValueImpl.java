package org.msgpack.value.impl;

import org.msgpack.core.MessageIntegerOverflowException;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.ValueType;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueVisitor;

import java.io.IOException;
import java.math.BigInteger;

/**
* Created on 5/30/14.
*/
public class LongValueImpl extends AbstractValue implements IntegerValue {

    private final long value;

    public LongValueImpl(long value) {
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
    public IntegerValue asInteger() {
        return this;
    }

    @Override
    public byte toByte() {
        return (byte) value;
    }

    @Override
    public short toShort() {
        return (short) value;
    }

    @Override
    public int toInt() {
        return (int) value;
    }

    @Override
    public long toLong() {
        return value;
    }

    @Override
    public BigInteger toBigInteger() {
        return BigInteger.valueOf(value);
    }

    @Override
    public float toFloat() {
        return (float) value;
    }

    @Override
    public double toDouble() {
        return (double) value;
    }

    @Override
    public byte asByte() throws MessageIntegerOverflowException {
        if (!isValidByte()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (byte) value;
    }

    @Override
    public short asShort() throws MessageIntegerOverflowException {
        if (!isValidShort()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (short) value;
    }

    @Override
    public int asInt() throws MessageIntegerOverflowException {
        if (!isValidInt()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (int) value;
    }

    @Override
    public long asLong() throws MessageIntegerOverflowException {
        return value;
    }

    @Override
    public BigInteger asBigInteger() throws MessageIntegerOverflowException {
        return BigInteger.valueOf(value);
    }

    @Override
    public boolean isValidByte() {
        return BYTE_MIN <= value && value <= BYTE_MAX;
    }

    @Override
    public boolean isValidShort() {
        return SHORT_MIN <= value && value <= SHORT_MAX;
    }

    @Override
    public boolean isValidInt() {
        return INT_MIN <= value && value <= INT_MAX;
    }

    @Override
    public boolean isValidLong() {
        return true;
    }
    @Override
    public boolean isWhole() {
        return true;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packLong(value);
    }
    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitInteger(this);
    }
    @Override
    public IntegerValue toValue() {
        return this;
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
        if (!v.isInteger()) {
            return false;
        }
        IntegerValue iv = v.asInteger();
        if (!iv.isValidLong()) {
            return false;
        }
        return value == iv.toLong();
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
