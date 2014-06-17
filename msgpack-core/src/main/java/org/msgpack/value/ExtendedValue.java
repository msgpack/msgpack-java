package org.msgpack.value;

/**
* Created on 5/30/14.
*/
public interface ExtendedValue extends RawValue {
    public int getExtType();
    public ExtendedValue toValue();
}
