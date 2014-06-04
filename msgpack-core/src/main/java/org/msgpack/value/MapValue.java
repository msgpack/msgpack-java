package org.msgpack.value;

import java.util.Map;

/**
* Created on 5/30/14.
*/
public interface MapValue extends Value, MapCursor {
    public Value[] toKeyValueSeq();
    public Map<Value, Value> toMap();
}
