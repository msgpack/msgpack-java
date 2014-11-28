package org.msgpack.value.impl;

import org.msgpack.core.MessagePacker;
import org.msgpack.value.ValueType;
import org.msgpack.value.*;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
* Immutable raw string value implementation
*/
public class RawStringValueImpl extends RawValueImpl implements StringValue {

    public RawStringValueImpl(ByteBuffer byteBuffer) {
        super(byteBuffer);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitString(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packRawStringHeader(byteBuffer.remaining());
        pk.writePayload(byteBuffer);
    }

    @Override
    public StringValue toImmutable() {
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
        if (!v.isStringValue()) {
            return false;
        }
        StringValue sv = v.asStringValue();
        return sv.toByteBuffer().equals(byteBuffer);
    }

    @Override
    public int hashCode() {
        return byteBuffer.hashCode();
    }
}
