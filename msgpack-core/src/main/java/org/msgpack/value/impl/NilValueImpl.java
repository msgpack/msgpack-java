package org.msgpack.value.impl;

import org.msgpack.core.MessagePacker;
import org.msgpack.value.ValueType;
import org.msgpack.value.NilValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueVisitor;

import java.io.IOException;

/**
* Created on 5/30/14.
*/
public class NilValueImpl extends AbstractValue implements NilValue {

    private static NilValue instance = new NilValueImpl();

    public static NilValue getInstance() {
        return instance;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.NIL;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        return ((Value) o).isNil();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        packer.packNil();
    }
    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitNil();
    }
    @Override
    public NilValue toValue() {
        return instance;
    }
}
