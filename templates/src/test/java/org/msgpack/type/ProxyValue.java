package org.msgpack.type;

import java.io.IOException;

import org.msgpack.packer.Packer;

public abstract class ProxyValue implements Value {
    public ProxyValue() {
    }

    protected abstract Value getValue();

    public ValueType getType() {
        return getValue().getType();
    }

    @Override
    public boolean isNilValue() {
        return getValue().isNilValue();
    }

    @Override
    public boolean isBooleanValue() {
        return getValue().isBooleanValue();
    }

    @Override
    public boolean isIntegerValue() {
        return getValue().isIntegerValue();
    }

    @Override
    public boolean isFloatValue() {
        return getValue().isFloatValue();
    }

    @Override
    public boolean isArrayValue() {
        return getValue().isArrayValue();
    }

    @Override
    public boolean isMapValue() {
        return getValue().isMapValue();
    }

    @Override
    public boolean isRawValue() {
        return getValue().isRawValue();
    }

    @Override
    public NilValue asNilValue() {
        return getValue().asNilValue();
    }

    @Override
    public BooleanValue asBooleanValue() {
        return getValue().asBooleanValue();
    }

    @Override
    public IntegerValue asIntegerValue() {
        return getValue().asIntegerValue();
    }

    @Override
    public FloatValue asFloatValue() {
        return getValue().asFloatValue();
    }

    @Override
    public ArrayValue asArrayValue() {
        return getValue().asArrayValue();
    }

    @Override
    public MapValue asMapValue() {
        return getValue().asMapValue();
    }

    @Override
    public RawValue asRawValue() {
        return getValue().asRawValue();
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
        getValue().writeTo(pk);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return getValue().toString(sb);
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return getValue().equals(o);
    }
}
