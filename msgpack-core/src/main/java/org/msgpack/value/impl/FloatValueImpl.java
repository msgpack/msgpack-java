package org.msgpack.value.impl;

import org.msgpack.core.MessageFloatOverflowException;
import org.msgpack.core.MessageOverflowException;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
* Created on 5/30/14.
*/
public class FloatValueImpl extends AbstractValue implements FloatValue {
    private final float value;

    public FloatValueImpl(float value) {
        this.value = value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.FLOAT;
    }

    @Override
    public boolean isValidByte() {
        return (float) ((byte) value) == value;
    }
    @Override
    public boolean isValidShort() {
        return (float) ((short) value) == value;
    }
    @Override
    public boolean isValidInt() {
        int i = (int) value;
        return ((float) i) == value && i != Integer.MAX_VALUE;
    }
    @Override
    public boolean isValidLong() {
        long l = (long) value;
        return ((float) l) == value && l != Long.MAX_VALUE;
    }

    @Override
    public boolean isWhole() {
        long l = (long) value;
        return ((float) l) == value || l == Long.MAX_VALUE || value < Float.POSITIVE_INFINITY || l == Long.MIN_VALUE && value > Float.NEGATIVE_INFINITY;
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
        return (long) value;
    }

    @Override
    public BigInteger toBigInteger() {
        return new BigDecimal((double) value).toBigInteger();
    }

    @Override
    public float toFloat() {
        return value;
    }

    @Override
    public double toDouble() {
        return (double) value;
    }
    @Override
    public byte asByte() throws MessageOverflowException {
        if (!isValidByte()) {
            throw new MessageFloatOverflowException(value);
        }
        return (byte) value;
    }
    @Override
    public short asShort() throws MessageOverflowException {
        if(!isValidShort())
            throw new MessageFloatOverflowException(value);
        return (short) value;
    }
    @Override
    public int asInt() throws MessageOverflowException {
        if(!isValidInt())
            throw new MessageFloatOverflowException(value);
        return (int) value;
    }

    @Override
    public long asLong() throws MessageOverflowException {
        if(!isValidLong())
            throw new MessageFloatOverflowException(value);
        return (long) value;
    }

    @Override
    public BigInteger asBigInteger() throws MessageOverflowException {
        if(!isWhole())
            throw new MessageFloatOverflowException(value);

        return BigDecimal.valueOf(value).toBigInteger();
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
        if (!v.isFloat()) {
            return false;
        }
        return (double) value == v.asFloat().toDouble();
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packFloat(value);
    }
    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitFloat(this);
    }
    @Override
    public FloatValue toValue() {
        return this;
    }

    @Override
    public int hashCode() {
        long v = Double.doubleToLongBits((double) value);
        return (int) (v ^ (v >>> 32));
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}
