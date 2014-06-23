package org.msgpack.value.holder;

import org.msgpack.core.MessageIntegerOverflowException;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageTypeException;
import org.msgpack.value.*;
import org.msgpack.value.impl.AbstractValue;
import org.msgpack.value.impl.AbstractValueRef;

import java.io.IOException;
import java.math.BigInteger;
import static org.msgpack.core.NumberUtil.*;

/**
 * Union of integer values
 */
public class IntegerHolder extends AbstractValueRef implements IntegerValue {

    @Override
    public ValueType getValueType() {
        return ValueType.INTEGER;
    }
    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        switch(type) {
            case BIG_INTEGER:
                packer.packBigInteger(biValue);
                break;
            default:
                packer.packLong(longValue);
                break;
        }
    }

    @Override
    public IntegerValue asInteger() throws MessageTypeException {
        return this;
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitInteger(this);
    }

    @Override
    public IntegerValue toValue() {
        switch(type){
            case BYTE:
                return ValueFactory.newByte(toByte());
            case SHORT:
                return ValueFactory.newShort(toShort());
            case INT:
                return ValueFactory.newInt(toInt());
            case LONG:
                return ValueFactory.newLong(toLong());
            case BIG_INTEGER:
                return ValueFactory.newBigInteger(toBigInteger());
            default:
                throw new IllegalStateException("cannot reach here");
        }
    }

    public static enum Type {
        BYTE,
        SHORT,
        INT,
        LONG,
        BIG_INTEGER
    }

    private Type type;
    private long longValue;
    private BigInteger biValue;

    public Type getType() {
        return type;
    }

    public void setByte(byte v){
        type = Type.BYTE;
        longValue = v;
    }
    public void setShort(short v) {
        type = Type.SHORT;
        longValue = v;
    }
    public void setInt(int v) {
        type = Type.INT;
        longValue = v;
    }
    public void setLong(long v) {
        type = Type.LONG;
        longValue = v;
    }
    public void setBigInteger(BigInteger v) {
        type = Type.BIG_INTEGER;
        biValue = v;
    }

    private RuntimeException failure() {
        return new IllegalStateException();
    }

    public boolean isBigInteger() {
        return type == Type.BIG_INTEGER;
    }

    @Override
    public boolean isValidByte() {
        return type == Type.BYTE;
    }
    @Override
    public boolean isValidShort() {
        return type.ordinal() <= Type.SHORT.ordinal();
    }
    @Override
    public boolean isValidInt() {
        return type.ordinal() <= Type.INT.ordinal();
    }
    @Override
    public boolean isValidLong() {
        return type.ordinal() <= Type.LONG.ordinal();
    }

    @Override
    public boolean isWhole() {
        return true;
    }

    @Override
    public byte toByte() {
        return isBigInteger() ? biValue.byteValue() : (byte) longValue;
    }

    @Override
    public short toShort() {
        return isBigInteger() ? biValue.shortValue() : (short) longValue;
    }

    @Override
    public int toInt() {
        return isBigInteger() ? biValue.intValue() : (int) longValue;
    }

    @Override
    public long toLong(){
        return isBigInteger() ? biValue.longValue() : longValue;
    }

    @Override
    public BigInteger toBigInteger() {
        return isBigInteger() ? biValue : BigInteger.valueOf(longValue);
    }
    @Override
    public float toFloat() {
        return isBigInteger() ? biValue.floatValue() : (float) longValue;
    }
    @Override
    public double toDouble() {
        return isBigInteger() ? biValue.doubleValue() : (double) longValue;
    }


    @Override
    public byte asByte() throws MessageIntegerOverflowException {
        switch(type) {
            case BYTE:
                return (byte) longValue;
            case SHORT:
            case INT:
            case LONG:
                if(LongUtil.isValidByte(longValue)) {
                    return (byte) longValue;
                }
                else {
                    throw new MessageIntegerOverflowException(longValue);
                }
            case BIG_INTEGER:
                if(LongUtil.isValidByte(biValue)) {
                    return biValue.byteValue();
                }
                else {
                    throw new MessageIntegerOverflowException(biValue);
                }
            default:
                throw failure();
        }
    }


    @Override
    public short asShort() throws MessageIntegerOverflowException {
        switch(type) {
            case BYTE:
            case SHORT:
                return (short) longValue;
            case INT:
            case LONG:
                if(LongUtil.isValidShort(longValue)) {
                    return (short) longValue;
                }
                else {
                    throw new MessageIntegerOverflowException(longValue);
                }
            case BIG_INTEGER:
                if(LongUtil.isValidShort(biValue)) {
                    return biValue.shortValue();
                }
                else {
                    throw new MessageIntegerOverflowException(biValue);
                }
            default:
                throw failure();
        }
    }


    @Override
    public int asInt() throws MessageIntegerOverflowException {
        switch(type) {
            case BYTE:
            case SHORT:
            case INT:
                return (int) longValue;
            case LONG:
                if(LongUtil.isValidInt(longValue)) {
                    return (int) longValue;
                }
                else {
                    throw new MessageIntegerOverflowException(longValue);
                }
            case BIG_INTEGER:
                if(LongUtil.isValidInt(biValue)) {
                    return biValue.intValue();
                }
                else {
                    throw new MessageIntegerOverflowException(biValue);
                }
            default:
                throw failure();
        }
    }

    @Override
    public long asLong() throws MessageIntegerOverflowException {
        if(isBigInteger()){
            if(LongUtil.isValidLong(biValue)) {
                return biValue.longValue();
            } else {
                throw new MessageIntegerOverflowException(biValue);
            }
        }
        return longValue;
    }

    @Override
    public BigInteger asBigInteger() {
        return toBigInteger();
    }


    @Override
    public int hashCode() {
        int hash = 0;
        if(isBigInteger()) {
            hash = biValue.hashCode();
        }
        else {
            hash = (int)((longValue >>> 32) * 31 + longValue & 0xFFFFFFFF);
        }
        return hash;
    }

    @Override
    public String toString() {
        return isBigInteger() ? biValue.toString() : Long.toString(longValue);
    }



}
