package org.msgpack.core;

import static org.msgpack.core.Preconditions.*;

/**
 * Header of the Extension types
 */
public class ExtensionTypeHeader {
    private final byte type;
    private final int length;

    ExtensionTypeHeader(byte type, int length) {
        checkArgument(length >= 0, String.format("length must be >= 0: %,d", length));
        this.length = length;
        this.type = type;
    }

    public byte getType() {
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
        if(obj instanceof ExtensionTypeHeader) {
            ExtensionTypeHeader other = (ExtensionTypeHeader) obj;
            return this.type == other.type && this.length == other.length;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ExtensionTypeHeader(type:%d, length:%,d)", type, length);
    }

}
