package org.msgpack.core;

import static org.msgpack.core.Preconditions.*;

/**
 * Header of the Extension types
 */
public class ExtensionTypeHeader {
    private final byte type;
    private final int length;

    /**
     * Create an extension type header
     * @param type extension type (byte). You can check the valid byte range with {@link #checkedCastToByte(int)} method.
     * @param length extension type data length
     */
    public ExtensionTypeHeader(byte type, int length) {
        checkArgument(length >= 0, "length must be >= 0");
        this.type = type;
        this.length = length;
    }

    public static byte checkedCastToByte(int code) {
        checkArgument(Byte.MIN_VALUE <= code && code <= Byte.MAX_VALUE, "Extension type code must be within the range of byte");
        return (byte) code;
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
