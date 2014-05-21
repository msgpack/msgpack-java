package org.msgpack.core;

/**
 * Header of the extended types
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
}
