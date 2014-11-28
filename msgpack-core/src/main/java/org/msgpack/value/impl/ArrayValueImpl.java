package org.msgpack.value.impl;

import org.msgpack.core.MessagePacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueType;
import org.msgpack.value.ValueType;
import org.msgpack.value.ValueVisitor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

/**
* Immutable ArrayValue implementation
*/
public class ArrayValueImpl extends AbstractValue implements ArrayValue {
    private static ArrayValueImpl EMPTY = new ArrayValueImpl(new Value[0]);

    public static ArrayValue empty() {
        return EMPTY;
    }

    private int cursor = 0;
    private final Value[] array;

    public ArrayValueImpl(Value[] array) {
        this.array = array;
    }

    public Value get(int index) {
        return array[index];
    }

    public Value apply(int index) {
        return array[index];
    }

    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packArrayHeader(array.length);
        for(int i = 0; i < array.length; i++) {
            array[i].writeTo(pk);
        }
    }
    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitArray(this);
    }
    @Override
    public ArrayValue toImmutable() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if(!v.isArrayValue()) {
            return false;
        }
        Value[] other = v.asArrayValue().toArray();
        if(array.length != other.length)
            return false;

        for(int i = 0; i < array.length; i++) {
            if(!array[i].equals(other[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for(int i = 0; i < array.length; i++) {
            Value obj = array[i];
            h = 31 * h + obj.hashCode();
        }
        return h;
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    private StringBuilder toString(StringBuilder sb) {
        if(array.length == 0) {
            return sb.append("[]");
        }
        sb.append("[");
        sb.append(array[0]);
        for(int i = 1; i < array.length; i++) {
            sb.append(",");
            sb.append(array[i].toString());
        }
        sb.append("]");
        return sb;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public boolean hasNext() {
        return cursor < array.length;
    }

    @Override
    public Value next() {
        return array[cursor++];
    }

    @Override
    public void skip() {
        cursor++;
    }
    @Override
    public void skipAll() {
        while(hasNext()) {
            skip();
        }
    }

    public Value[] toArray() {
        return Arrays.copyOf(array, array.length);
    }

    @Override
    public Iterator<Value> iterator() {
        return new Iterator<Value>() {
            int cursor = 0;
            @Override
            public boolean hasNext() {
                return cursor < array.length;
            }
            @Override
            public Value next() {
                return array[cursor++];
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }
}
