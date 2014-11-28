package org.msgpack.value.impl;

import org.msgpack.core.MessageIntegerOverflowException;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.ValueType;
import org.msgpack.value.IntegerValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueVisitor;

import java.io.IOException;
import java.math.BigInteger;

import static org.msgpack.core.Preconditions.checkNotNull;

/**
 * Immutable BigIntegerValue implementation
 */
public class BigIntegerValueImpl extends AbstractValue implements IntegerValue {

    private final BigInteger value;

    public BigIntegerValueImpl(BigInteger value) {
        this.value = checkNotNull(value, "BigInteger value is null");
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
    public ValueType getValueType() {
        return ValueType.INTEGER;
    }

    @Override
    public byte toByte() {
        return value.byteValue();
    }

    @Override
    public short toShort() {
        return value.shortValue();
    }

    @Override
    public int toInt() {
        return value.intValue();
    }

    @Override
    public long toLong() {
        return value.longValue();
    }

    @Override
    public BigInteger toBigInteger() {
        return value;
    }

    @Override
    public float toFloat() {
        return value.floatValue();
    }

    @Override
    public double toDouble() {
        return value.doubleValue();
    }

    @Override
    public byte asByte() throws MessageIntegerOverflowException {
        if(!isValidByte()) {
            throw new MessageIntegerOverflowException(value);
        }
        return value.byteValue();
    }

    @Override
    public short asShort() throws MessageIntegerOverflowException {
        if(!isValidShort()) {
            throw new MessageIntegerOverflowException(value);
        }
        return value.shortValue();
    }

    @Override
    public int asInt() throws MessageIntegerOverflowException {
        if(!isValidInt()) {
            throw new MessageIntegerOverflowException(value);
        }
        return value.intValue();
    }

    @Override
    public long asLong() throws MessageIntegerOverflowException {
        if(!isValidLong()) {
            throw new MessageIntegerOverflowException(value);
        }
        return value.longValue();
    }

    @Override
    public BigInteger asBigInteger() throws MessageIntegerOverflowException {
        return value;
    }

    @Override
    public boolean isValidByte() {
        return 0 <= value.compareTo(BYTE_MIN) && value.compareTo(BYTE_MAX) <= 0;
    }

    @Override
    public boolean isValidShort() {
        return 0 <= value.compareTo(SHORT_MIN) && value.compareTo(SHORT_MAX) <= 0;
    }

    @Override
    public boolean isValidInt() {
        return 0 <= value.compareTo(INT_MIN) && value.compareTo(INT_MAX) <= 0;
    }

    @Override
    public boolean isValidLong() {
        return 0 <= value.compareTo(LONG_MIN) && value.compareTo(LONG_MAX) <= 0;
    }
    @Override
    public boolean isWhole() {
        return true;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packBigInteger(value);
    }
    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitInteger(this);
    }
    @Override
    public IntegerValue toImmutable() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if(!v.isIntegerValue()) {
            return false;
        }
        IntegerValue iv = v.asIntegerValue();
        return value.equals(iv.toBigInteger());
    }

    @Override
    public int hashCode() {
        if(INT_MIN.compareTo(value) <= 0 && value.compareTo(INT_MAX) <= 0) {
            return (int) value.longValue();
        } else if(LONG_MIN.compareTo(value) <= 0
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
