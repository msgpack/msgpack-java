package org.msgpack.value.impl;

import org.msgpack.core.MessageIntegerOverflowException;
import org.msgpack.core.MessageOverflowException;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.*;

import java.io.IOException;
import java.math.BigInteger;

/**
* Created on 5/30/14.
*/
public class IntegerValueImpl extends AbstractValue implements IntegerValue {

    private final int value;

    public IntegerValueImpl(int value) {
        this.value = value;
    }

    private static int BYTE_MIN = (int) Byte.MIN_VALUE;
    private static int BYTE_MAX = (int) Byte.MAX_VALUE;
    private static int SHORT_MIN = (int) Short.MIN_VALUE;
    private static int SHORT_MAX = (int) Short.MAX_VALUE;

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
        return value;
    }

    @Override
    public long toLong() {
        return value;
    }

    @Override
    public BigInteger toBigInteger() {
        return BigInteger.valueOf((long) value);
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
    public byte asByte() throws MessageOverflowException {
        if (!isValidByte()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (byte) value;
    }

    @Override
    public short asShort() throws MessageOverflowException {
        if (!isValidShort()) {
            throw new MessageIntegerOverflowException(value);
        }
        return (short) value;
    }

    @Override
    public int asInt() throws MessageOverflowException {
        return value;
    }

    @Override
    public long asLong() throws MessageOverflowException {
        return value;
    }

    @Override
    public BigInteger asBigInteger() throws MessageOverflowException {
        return BigInteger.valueOf((long) value);
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
        return true;
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
        pk.packInt(value);
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
        if (!iv.isValidInt()) {
            return false;
        }
        return iv.toInt() == value;
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
