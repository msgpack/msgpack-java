package org.msgpack.value;


import java.io.Closeable;
import java.util.Iterator;

/**
 * Cursor for traversing a stream of message-packed values.
 * Returned value might be changed for efficiency. To retrieving an immutable value,
 * call {@link org.msgpack.value.Value#toImmutable()} after retrieving a value.
 */
public interface Cursor extends Iterator<Value>, Closeable {

    /**
     * Tests whether there is a next element.
     * @return true if there is a next element, or false if there is no more element.
     */
    public boolean hasNext();

    /**
     * Returns the next value, then proceeds the cursor.
     * @return
     */
    public Value next();

    /**
     * Skip reading the current value.
     */
    public void skip();

    /**
     * Returns the number of the read bytes
     * @return the number of the read bytes
     */
    public long getReadBytes();

    public static interface Function<Out> {
        public Out apply(Value input) throws Exception;
    }

    /**
     * Applies a function f to the referenced value, then returns the result of the function.
     * @param f a function that receives the referenced value.
     * @param <Out> the result type of the function
     * @return the result of the function
     */
    public <Out> Out apply(Function<Out> f);

    public boolean isNilValue();
    public boolean isBooleanValue();
    public boolean isNumberValue();
    public boolean isIntegerValue();
    public boolean isFloatValue();
    public boolean isBinaryValue();
    public boolean isStringValue();
    public boolean isRawValue();
    public boolean isArrayValue();
    public boolean isMapValue();
    public boolean isExtendedValue();

}
