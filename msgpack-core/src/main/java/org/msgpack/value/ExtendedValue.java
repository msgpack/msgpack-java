package org.msgpack.value;

/**
* ExtendedValue interface
*/
public interface ExtendedValue extends RawValue {
    public int getExtType();
    public ExtendedValue toImmutable();
}
