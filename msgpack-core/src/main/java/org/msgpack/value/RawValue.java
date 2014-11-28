package org.msgpack.value;

import org.msgpack.core.buffer.MessageBuffer;

import java.nio.ByteBuffer;

/**
 * Base type of StringValue, BinaryValue and ExtendedValue
 */
public interface RawValue extends Value {

    /**
     * Returns byte array representation of this value
     * @return
     */
    public byte[] toByteArray();

    /**
     * Returns ByteBuffer representation of this value
     * @return
     */
    public ByteBuffer toByteBuffer();

    /**
     * Returns MessageBuffer representation of this value
     * @return
     */
    public MessageBuffer toMessageBuffer();

    @Override
    public String toString();

    public RawValue toImmutable();
}
