package org.msgpack.value.holder;

import org.msgpack.core.MessageFloatOverflowException;
import org.msgpack.core.MessageOverflowException;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.*;
import org.msgpack.value.impl.AbstractValueRef;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created on 6/3/14.
 */
public class FloatHolder extends AbstractValueRef implements FloatValue {

    public static enum Type {
        FLOAT,
        DOUBLE
    }

    private Type tpe;
    private double value;

    @Override
    public boolean isValidByte() {
        return ((double) ((byte) value)) == value;
    }
    @Override
    public boolean isValidShort() {
        return ((double) ((short) value)) == value;
    }
    @Override
    public boolean isValidInt() {
        return ((double) ((int) value)) == value;
    }
    @Override
    public boolean isValidLong() {
        long l = (long) value;
        return ((double) l) == value && l != Long.MAX_VALUE;
    }
    @Override
    public boolean isWhole() {
        long l = (long) value;
        return ((double) l == value) || l == Long.MAX_VALUE && value < Double.POSITIVE_INFINITY || l == Long.MIN_VALUE && value > Double.NEGATIVE_INFINITY;
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
        return new BigDecimal(value).toBigInteger();
    }

    @Override
    public float toFloat() {
        return (float) value;
    }

    @Override
    public double toDouble() {
        return value;
    }
    @Override
    public byte asByte() throws MessageOverflowException {
        if(!isValidByte())
            throw new MessageFloatOverflowException(value);
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
        return new BigDecimal(value).toBigInteger();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.FLOAT;
    }
    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        switch(tpe) {
            case FLOAT:
                pk.packFloat(toFloat());
                break;
            case DOUBLE:
                pk.packDouble(value);
                break;
        }
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitFloat(this);
    }
    @Override
    public FloatValue toValue() {
        switch(tpe) {
            case FLOAT:
                return ValueFactory.newFloat(toFloat());
            case DOUBLE:
                return ValueFactory.newDouble(toDouble());
            default:
                throw new IllegalStateException("cannot reach here");
        }
    }

    public Type getType() {
        return tpe;
    }

    public void setFloat(float v) {
        tpe = Type.FLOAT;
        value = v;
    }

    public void setDouble(double v) {
        tpe = Type.DOUBLE;
        value = v;
    }

    @Override
    public int hashCode() {
        long v = Double.doubleToLongBits(value);
        return (int) (v ^ (v >>> 32));
    }

    @Override
    public String toString() {
        switch(tpe) {
            case FLOAT:
                return Float.toString((float) value);
            case DOUBLE:
                return Double.toString(value);
            default:
                throw new IllegalStateException("cannot reach here");
        }
    }


}
