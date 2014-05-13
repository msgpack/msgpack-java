package org.msgpack.core;

/**
 * Header of extended type
 */
public class ExtendedTypeHeader {
    private final int type;
    private final int length;
    ExtendedTypeHeader(int type, int length) {
        this.type = type;
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public int getLength() {
        return length;
    }
}
