package org.msgpack.value.impl;

import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;
import org.msgpack.value.ValueType;
import org.msgpack.value.ExtendedValue;
import org.msgpack.value.ValueVisitor;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Extended value implementation
 */
public class ExtendedValueImpl extends RawValueImpl implements ExtendedValue {

    private final int type;


    public ExtendedValueImpl(int type, ByteBuffer data) {
        super(data);
        this.type = type;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.EXTENDED;
    }
    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        packer.packExtendedTypeHeader(type, byteBuffer.remaining());
        packer.writePayload(byteBuffer);
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitExtended(this);
    }

    @Override
    public Value toValue() {
        return this;
    }

    @Override
    public int getExtType() {
        return type;
    }


}
