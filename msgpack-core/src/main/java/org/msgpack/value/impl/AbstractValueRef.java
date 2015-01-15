package org.msgpack.value.impl;

import org.msgpack.core.MessageTypeException;
import org.msgpack.value.*;

/**
 * Base implementation of message pack values
 */
public abstract class AbstractValueRef implements ValueRef {

    public boolean isRef() { return true; }

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

    public NilValue asNil() throws MessageTypeException { return as(NilValue.class, ValueType.NIL); }
    public BooleanValue asBoolean() throws MessageTypeException{ return as(BooleanValue.class, ValueType.BOOLEAN); }
    public NumberValue asNumber() throws MessageTypeException { return as(NumberValue.class, NUMBER_TYPE_MASK); }
    public IntegerValue asInteger() throws MessageTypeException { return as(IntegerValue.class, ValueType.INTEGER); }
    public FloatValue asFloat() throws MessageTypeException { return as(FloatValue.class, ValueType.FLOAT); }
    public BinaryValue asBinary() throws MessageTypeException { return as(BinaryValue.class, ValueType.BINARY); }
    public StringValue asString() throws MessageTypeException { return as(StringValue.class, ValueType.STRING); }
    public RawValue asRaw() throws MessageTypeException { return as(RawValue.class, RAW_TYPE_MASK); }
    public ArrayValue asArrayValue() throws MessageTypeException { return as(ArrayValue.class, ValueType.ARRAY); }
    public MapValue asMapValue() throws MessageTypeException { return as(MapValue.class, ValueType.MAP); }
    public ExtendedValue asExtended() throws MessageTypeException { return as(ExtendedValue.class, ValueType.EXTENDED); }

    @Override
    public ArrayCursor getArrayCursor() throws MessageTypeException {
        throw new MessageTypeException("This value is not an array type");
    }
    @Override
    public MapCursor getMapCursor() throws MessageTypeException {
        throw new MessageTypeException("This value is not a map type");
    }

    public boolean isNil() { return getValueType().isNilType(); }
    public boolean isBoolean() { return getValueType().isBooleanType(); }
    public boolean isNumber() { return getValueType().isNumberType(); }
    public boolean isInteger() { return getValueType().isIntegerType(); }
    public boolean isFloat() { return getValueType().isFloatType(); }
    public boolean isBinary() { return getValueType().isBinaryType(); }
    public boolean isString() { return getValueType().isStringType(); }
    public boolean isRaw() { return getValueType().isRawType(); }
    public boolean isArray() { return getValueType().isArrayType(); }
    public boolean isMap() { return getValueType().isMapType(); }
    public boolean isExtended() { return getValueType().isExtendedType(); }

}
