package org.msgpack.core;

import static org.msgpack.core.Preconditions.*;

/**
 * Header of the extended types
 */
public class ExtendedTypeHeader {
    private final int length;
    private final int type;

    ExtendedTypeHeader(int length, int type) {
        checkArgument(length >= 0, String.format("length must be >= 0: %,d", length));
        this.length = length;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    @Override
    public int hashCode() {
        return (type + 31) * 31 + length;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ExtendedTypeHeader) {
            ExtendedTypeHeader other = (ExtendedTypeHeader) obj;
            return this.type == other.type && this.length == other.length;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ExtendedTypeHeader(type:%d, length:%,d)", type, length);
    }

}
