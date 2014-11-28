package org.msgpack.value;

/**
*
*/
public class KeyValuePair {
    public final Value key;
    public final Value value;

    public KeyValuePair(Value key, Value value) {
        this.key = key;
        this.value = value;
    }
}
