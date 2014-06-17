package org.msgpack.value;

import org.msgpack.core.MessageStringCodingException;
import org.msgpack.core.buffer.MessageBuffer;

import java.nio.ByteBuffer;

/**
 * Base type of StringValue, BinaryValue and ExtendedValue
 */
public interface RawValue extends Value {
    public byte[] toByteArray();
    public ByteBuffer toByteBuffer();
    public MessageBuffer toMessageBuffer();

    @Override
    public String toString();

    public RawValue toValue();
}
