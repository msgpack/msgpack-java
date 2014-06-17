package org.msgpack.value.impl;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageTypeException;
import org.msgpack.value.ValueType;
import org.msgpack.value.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

/**
* Created on 5/30/14.
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
    public ArrayCursor getArrayCursor() throws MessageTypeException {
        return this;
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
    public ArrayValue toValue() {
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
        if(!v.isArray()) {
            return false;
        }
        Value[] other = v.asArrayValue().toValueArray();
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
    public ValueRef next() {
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

    public Value[] toValueArray() {
        return Arrays.copyOf(array, array.length);
    }

    @Override
    public Iterator<ValueRef> iterator() {
        return new Iterator<ValueRef>() {
            int cursor = 0;
            @Override
            public boolean hasNext() {
                return cursor < array.length;
            }
            @Override
            public ValueRef next() {
                return array[cursor++];
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }
}
