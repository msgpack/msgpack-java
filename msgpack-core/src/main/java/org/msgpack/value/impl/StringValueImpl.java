package org.msgpack.value.impl;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageStringCodingException;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.value.ValueType;
import org.msgpack.value.*;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
* Immutable StringValue implementation
*/
public class StringValueImpl extends AbstractValue implements StringValue {

    private final String value;

    public StringValueImpl(String value) {
        this.value = value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

    @Override
    public byte[] toByteArray() {
        return value.getBytes(MessagePack.UTF8);
    }

    @Override
    public MessageBuffer toMessageBuffer() {
        return MessageBuffer.wrap(toByteArray());
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        return toMessageBuffer().toByteBuffer();
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packString(value);
    }
    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitString(this);
    }
    @Override
    public StringValue toImmutable() {
        return ValueFactory.newString(value);
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
        try {
            return v.asStringValue().toString().equals(value);
        } catch (MessageStringCodingException ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
