package org.msgpack.value;

import java.util.Iterator;

/**
 * Created on 6/16/14.
 */
public interface ArrayCursor extends ValueRef, Iterable<ValueRef> {
    public int size();

    public boolean hasNext();
    public ValueRef next();
    public void skip();

    /**
     * Skips all of the remaining values
     */
    public void skipAll();

    public Iterator<ValueRef> iterator();
}
