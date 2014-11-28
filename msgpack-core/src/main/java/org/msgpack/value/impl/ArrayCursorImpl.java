package org.msgpack.value.impl;

import org.msgpack.core.*;
import org.msgpack.value.*;
import org.msgpack.value.holder.ValueHolder;
import static org.msgpack.core.MessagePackException.UNSUPPORTED;

import java.io.IOException;
import java.util.Iterator;

/**
 * ArrayCursor implementation
 */
public class ArrayCursorImpl extends AbstractValue implements ArrayCursor {

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
    public Iterator<Value> iterator() {
        return new Iterator<Value>() {
            @Override
            public boolean hasNext() {
                return ArrayCursorImpl.this.hasNext();
            }
            @Override
            public Value next() {
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

    public Value next() {
        try {
            unpacker.unpackValue(valueHolder);
            cursor++;
            return valueHolder.get();
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
    public void writeTo(MessagePacker packer) throws IOException {
        ensureNotTraversed();
        packer.packArrayHeader(arraySize);
        for(Value v : this) {
            packer.packValue(v.toImmutable());
        }
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitArray(toImmutable());
    }

    @Override
    public ArrayValue toImmutable() {
        ensureNotTraversed();
        Value[] arr = new Value[arraySize];
        int i = 0;
        for(Value v : this) {
            arr[i++] = v.toImmutable();
        }
        return ValueFactory.newArray(arr);
    }
}
