package org.msgpack.core;

/**
 * Created on 5/28/14.
 */
public class MessageOverflowException extends MessageTypeException {

    public MessageOverflowException() {
        super();
    }

    public MessageOverflowException(String message) {
        super(message);
    }

}
