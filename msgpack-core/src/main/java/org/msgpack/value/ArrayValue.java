package org.msgpack.value;

/**
 * Value interface for array type data.
 *
 * Implementation note: We do not implement List<Value> interface here, because
 * we cannot reuse AbstractList and AbstractValue implementations simultaneously since
 * Java does not support mixin of classes. Instead, it provides {@link #iterator} or
 * {@link #toValueArray()} methods to traverse the array contents.
 */
public interface ArrayValue extends Value, ArrayCursor {

    public Value[] toValueArray();

    public Value get(int index);
    public Value apply(int index);

    public ArrayValue toValue();

}
