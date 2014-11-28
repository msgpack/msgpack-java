package org.msgpack.value;

import java.util.Iterator;

/**
 * Created on 6/16/14.
 */
public interface ArrayCursor extends Value, Iterable<Value> {
    public int size();

    public boolean hasNext();
    public Value next();
    public void skip();

    /**
     * Skips all of the remaining values
     */
    public void skipAll();

    public Iterator<Value> iterator();

    public ArrayValue toImmutable();

}
