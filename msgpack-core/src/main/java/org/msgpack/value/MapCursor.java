package org.msgpack.value;

import java.util.Iterator;
import java.util.Map;

/**
 * Cursor for traversing map value entries. This cursor reports a sequence of key and value pairs.
 */
public interface MapCursor extends ValueRef {
    public int size();

    /**
     * Test whether this cursor can point to a next key or value.
     * @return
     */
    public boolean hasNext();

    /**
     * Retrieves a reference to the next key or value.
     * @return
     */
    public ValueRef nextKeyOrValue();

    /**
     * Skips a next key or value
     */
    public void skipKeyOrValue();

    /**
     * Skips all of the remaining keys and values.
     */
    public void skipAll();

    MapValue toValue();
}
