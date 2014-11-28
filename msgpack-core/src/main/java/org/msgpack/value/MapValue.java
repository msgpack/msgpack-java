package org.msgpack.value;

import java.util.Map;

/**
* MapValue interface
*/
public interface MapValue extends Value, MapCursor {
    public Value[] toKeyValueArray();
    public KeyValuePair[] toArray();
    public Map<Value, Value> toMap();

    public MapValue toImmutable();
}
