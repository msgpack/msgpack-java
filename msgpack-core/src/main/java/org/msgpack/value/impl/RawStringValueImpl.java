package org.msgpack.value.impl;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageStringCodingException;
import org.msgpack.value.ValueType;
import org.msgpack.value.*;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
* Created on 5/30/14.
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
    public StringValue toValue() {
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
        if (!v.isString()) {
            return false;
        }
        try {
            return toString().equals(v.asString().toString());
        } catch (MessageStringCodingException ex) {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
