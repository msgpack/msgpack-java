package org.msgpack.core;

/**
 * Thrown when converting a message into a type that causes truncation or rounding.
 * For example, {@link org.msgpack.value.NumberValue#asInt()} throws this error if
 * it is a long value more than {@link java.lang.Integer#MAX_VALUE}.
 */
public class MessageOverflowException extends MessageTypeException {

    public MessageOverflowException() {
        super();
    }

    public MessageOverflowException(String message) {
        super(message);
    }

}
