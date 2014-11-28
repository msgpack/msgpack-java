package org.msgpack.value.holder;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageStringCodingException;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.value.*;
import org.msgpack.value.impl.AbstractValue;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * ExtendedValue holder
 */
public class ExtHolder extends AbstractValue implements ExtendedValue {

    private int extType;
    private MessageBuffer buffer;


    public void setExtType(int extType, MessageBuffer buffer) {
        this.extType = extType;
        this.buffer = buffer;
    }

    @Override
    public int getExtType() {
        return extType;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.EXTENDED;
    }

    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        packer.packExtendedTypeHeader(extType, buffer.size()).writePayload(buffer.toByteBuffer());
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitExtended(this);
    }
    @Override
    public ExtendedValue toImmutable() {
        // clone the buffer contents
        return ValueFactory.newExtendedValue(extType, buffer.toByteArray());
    }

    @Override
    public byte[] toByteArray() {
        return buffer.toByteArray();
    }
    @Override
    public ByteBuffer toByteBuffer() {
        return buffer.toByteBuffer();
    }
    @Override
    public MessageBuffer toMessageBuffer() {
        return buffer;
    }

    @Override
    public String toString() throws MessageStringCodingException {
        return new String(buffer.toByteArray(), MessagePack.UTF8);
    }
}
