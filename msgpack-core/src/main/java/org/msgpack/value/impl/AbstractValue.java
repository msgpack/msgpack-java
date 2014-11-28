package org.msgpack.value.impl;

import org.msgpack.core.MessageTypeException;
import org.msgpack.value.ValueType;
import org.msgpack.value.*;

/**
* Base implementation of MessagePackValue
*/
public abstract class AbstractValue implements Value {

    public boolean isImmutable() { return true; }

    protected static int NUMBER_TYPE_MASK = (1 << ValueType.INTEGER.ordinal())
        | (1 << ValueType.FLOAT.ordinal());
    protected static int RAW_TYPE_MASK = (1 << ValueType.STRING.ordinal())
        | (1 << ValueType.BINARY.ordinal());

    protected <E extends Value> E as(Class<E> valueClass, ValueType vt) {
        return as(valueClass, 1 << vt.ordinal());
    }
    protected <E extends Value> E as(Class<E> valueClass, int bitMask) {
        if(this.getValueType() == null)
            throw new MessageTypeException("This value points to nothing");
        if(!this.getValueType().isTypeOf(bitMask))
            throw new MessageTypeException(String.format("Expected %s, but %s", valueClass.getSimpleName(), this.getValueType()));
        return valueClass.cast(this);
    }

    public NilValue asNilValue() throws MessageTypeException { return as(NilValue.class, ValueType.NIL); }
    public BooleanValue asBooleanValue() throws MessageTypeException{ return as(BooleanValue.class, ValueType.BOOLEAN); }
    public NumberValue asNumberValue() throws MessageTypeException { return as(NumberValue.class, NUMBER_TYPE_MASK); }
    public IntegerValue asIntegerValue() throws MessageTypeException { return as(IntegerValue.class, ValueType.INTEGER); }
    public FloatValue asFloatValue() throws MessageTypeException { return as(FloatValue.class, ValueType.FLOAT); }
    public BinaryValue asBinaryValue() throws MessageTypeException { return as(BinaryValue.class, ValueType.BINARY); }
    public StringValue asStringValue() throws MessageTypeException { return as(StringValue.class, ValueType.STRING); }
    public RawValue asRawValue() throws MessageTypeException { return as(RawValue.class, RAW_TYPE_MASK); }
    public ArrayValue asArrayValue() throws MessageTypeException { return as(ArrayValue.class, ValueType.ARRAY); }
    public MapValue asMapValue() throws MessageTypeException { return as(MapValue.class, ValueType.MAP); }
    public ExtendedValue asExtendedValue() throws MessageTypeException { return as(ExtendedValue.class, ValueType.EXTENDED); }

    public boolean isNilValue() { return getValueType().isNilType(); }
    public boolean isBooleanValue() { return getValueType().isBooleanType(); }
    public boolean isNumberValue() { return getValueType().isNumberType(); }
    public boolean isIntegerValue() { return getValueType().isIntegerType(); }
    public boolean isFloatValue() { return getValueType().isFloatType(); }
    public boolean isBinaryValue() { return getValueType().isBinaryType(); }
    public boolean isStringValue() { return getValueType().isStringType(); }
    public boolean isRawValue() { return getValueType().isRawType(); }
    public boolean isArrayValue() { return getValueType().isArrayType(); }
    public boolean isMapValue() { return getValueType().isMapType(); }
    public boolean isExtendedValue() { return getValueType().isExtendedType(); }

}
