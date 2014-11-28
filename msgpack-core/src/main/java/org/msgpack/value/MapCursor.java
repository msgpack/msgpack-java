package org.msgpack.value;


/**
 * Cursor for traversing map value entries. This cursor reports a sequence of key and value pairs.
 */
public interface MapCursor extends Value {
    public int size();

    /**
     * Test whether this cursor can point to a next key or value.
     * @return
     */
    public boolean hasNext();

    /**
     * Retrieves the next key and value.
     * @return
     */
    public KeyValuePair next();

    /**
     * Skips a next key-value pair
     */
    public void skip();

    /**
     * Skips all of the remaining keys and values.
     */
    public void skipAll();

    public MapValue toImmutable();
}
