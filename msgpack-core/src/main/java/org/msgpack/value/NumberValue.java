package org.msgpack.value;

import org.msgpack.core.MessageOverflowException;

import java.math.BigInteger;

/**
* Created on 5/30/14.
*/
public interface NumberValue extends Value {

    /**
     * Check whether this value is a valid byte value.
     * @return true if this value has no fractional part, and is within the range of {@link Byte#MIN_VALUE} and {@link Byte#MAX_VALUE}; otherwise returns false
     */
    public boolean isValidByte();

    /**
     * Check whether this value is a valid short value.
     * @return true if this value has no fractional part, and is within the range of {@link Short#MIN_VALUE} and {@link Short#MAX_VALUE}; otherwise returns false
     */
    public boolean isValidShort();

    /**
     * Check whether this value is a valid integer value.
     * @return true if this value has no fractional part, and is within the range of {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}; otherwise returns false
     */
    public boolean isValidInt();

    /**
     * Check whether this value is a valid long value.
     * @return true if this value has no fractional part, and is within the range of {@link Long#MIN_VALUE} and {@link Long#MAX_VALUE}; otherwise returns false
     */
    public boolean isValidLong();

    /**
     * Returns true if this number has no decimal component
     * @return true if this number has no decimal component, otherwise false (float, double values);
     */
    public boolean isWhole();

    /**
     * Convert this value into a byte value. If this value is not within the range of Byte value, it will truncate or round the value.
     */
    public byte toByte();
    /**
     * Convert this value into a short value. If this value is not within the range of Short value, it will truncate or round the value.
     */
    public short toShort();
    /**
     * Convert this value into an int value. If this value is not within the range of Int value, it will truncate or round the value.
     */
    public int toInt();
    /**
     * Convert this value into a long value. If this value is not within the range of Long value, it will truncate or round the value.
     */
    public long toLong();
    /**
     * Convert this value into a BigInteger
     */
    public BigInteger toBigInteger();
    /**
     * Convert this value into a float value
     */
    public float toFloat();
    /**
     * Convert this value into a double value
     */
    public double toDouble();

    /**
     * Convert this value into a byte value. If this value is not within the range of Byte value, it throws an exception.
     * @return
     * @throws org.msgpack.core.MessageOverflowException when the value is not within the range of {@link Byte#MIN_VALUE} and {@link Byte#MAX_VALUE};
     */
    public byte asByte() throws MessageOverflowException;

    /**
     * Convert this value into a short value. If this value is not within the range of Short value, it throws an exception.
     * @return
     * @throws org.msgpack.core.MessageOverflowException when the value is not within the range of {@link Short#MIN_VALUE} and {@link Short#MAX_VALUE}
     */
    public short asShort() throws MessageOverflowException;

    /**
     * Convert this value into an int value. If this value is not within the range of Integer value, it throws an exception.
     * @return
     * @throws org.msgpack.core.MessageOverflowException when the value is not within the range of {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}
     */
    public int asInt() throws MessageOverflowException;

    /**
     * Convert this value into a long value. If this value is not within the range of Long value, it throws an exception.
     * @return
     * @throws org.msgpack.core.MessageOverflowException when the value is not within the range of {@link Long#MIN_VALUE} and {@link Long#MAX_VALUE}
     */
    public long asLong() throws MessageOverflowException;

    /**
     * Convert this value into a BigInteger value.
     * @return
     * @throws org.msgpack.core.MessageOverflowException
     */
    public BigInteger asBigInteger() throws MessageOverflowException;

}
