package org.msgpack.core;

/**
 * This error is thrown when the user tries to read a value that has decimal component as byte, short, int and long.
 *
 */
public class MessageFloatOverflowException extends MessageOverflowException {

    private final double value;

    public MessageFloatOverflowException(double value) {
        super();
        this.value = value;
    }

    public double getValue() {
        return value;
    }

}
