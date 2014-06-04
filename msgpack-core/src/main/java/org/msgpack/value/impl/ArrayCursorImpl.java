package org.msgpack.value.impl;

import org.msgpack.core.*;
import org.msgpack.value.*;
import org.msgpack.value.holder.ValueHolder;
import static org.msgpack.core.MessagePackException.UNSUPPORTED;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created on 6/16/14.
 */
public class ArrayCursorImpl extends AbstractValueRef implements ArrayCursor {

    private final ValueHolder valueHolder;
    private MessageUnpacker unpacker;
    private int cursor = 0;
    private int arraySize;

    public ArrayCursorImpl(ValueHolder valueHolder)  {
        this.valueHolder = valueHolder;
    }

    public void reset(MessageUnpacker unpacker) throws IOException {
        this.unpacker = unpacker;
        this.arraySize = unpacker.unpackArrayHeader();
        this.cursor = 0;
    }

    @Override
    public int size() {
        return arraySize;
    }

    @Override
    public Iterator<ValueRef> iterator() {
        return new Iterator<ValueRef>() {
            @Override
            public boolean hasNext() {
                return ArrayCursorImpl.this.hasNext();
            }
            @Override
            public ValueRef next() {
                return ArrayCursorImpl.this.next();
            }
            @Override
            public void remove() {
                throw UNSUPPORTED("remove");
            }
        };
    }

    public boolean hasNext() {
        return cursor < arraySize;
    }

    public ValueRef next() {
        try {
            unpacker.unpackValue(valueHolder);
            cursor++;
            return valueHolder.getRef();
        }
        catch(IOException e) {
            throw new MessageFormatException(e);
        }
    }

    @Override
    public void skip() {
        try {
            unpacker.skipValue();
            cursor++;
        }
        catch(IOException e) {
            throw new MessageFormatException(e);
        }
    }

    @Override
    public void skipAll() {
        while(hasNext()) {
            skip();
        }
    }


    private void ensureNotTraversed() {
        if(cursor != 0)
            throw UNSUPPORTED("ArrayCursor is already traversed");
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
    public void writeTo(MessagePacker packer) throws IOException {
        ensureNotTraversed();
        packer.packArrayHeader(arraySize);
        for(ValueRef v : this) {
            packer.pack(v.toValue());
        }
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitArray(toValue());
    }

    @Override
    public ArrayValue toValue() {
        Value[] arr = new Value[arraySize];
        int i = 0;
        for(ValueRef v : this) {
            arr[i++] = v.toValue();
        }
        return ValueFactory.newArray(arr);
    }
}
