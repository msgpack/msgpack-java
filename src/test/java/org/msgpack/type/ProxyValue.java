package org.msgpack.type;

import java.io.IOException;

import org.msgpack.packer.Packer;

public abstract class ProxyValue implements Value {
    public ProxyValue() { }

    protected abstract Value getValue();

    public ValueType getType() {
        return getValue().getType();
    }

    public boolean isNil() {
        return getValue().isNil();
    }

    public boolean isBoolean() {
        return getValue().isBoolean();
    }

    public boolean isInteger() {
        return getValue().isInteger();
    }

    public boolean isFloat() {
        return getValue().isFloat();
    }

    public boolean isArray() {
        return getValue().isArray();
    }

    public boolean isMap() {
        return getValue().isMap();
    }

    public boolean isRaw() {
        return getValue().isRaw();
    }

    public NilValue asNilValue() {
        return getValue().asNilValue();
    }

    public BooleanValue asBooleanValue() {
        return getValue().asBooleanValue();
    }

    public IntegerValue asIntegerValue() {
        return getValue().asIntegerValue();
    }

    public FloatValue asFloatValue() {
        return getValue().asFloatValue();
    }

    public ArrayValue asArrayValue() {
        return getValue().asArrayValue();
    }

    public MapValue asMapValue() {
        return getValue().asMapValue();
    }

    public RawValue asRawValue() {
        return getValue().asRawValue();
    }

    public void writeTo(Packer pk) throws IOException {
        getValue().writeTo(pk);
    }

    public StringBuilder toString(StringBuilder sb) {
        return getValue().toString(sb);
    }

    public String toString() {
        return getValue().toString();
    }

    public int hashCode() {
        return getValue().hashCode();
    }

    public boolean equals(Object o) {
        return getValue().equals(o);
    }
}

