package org.msgpack.value.impl;

import org.msgpack.core.MessageFormatException;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageTypeException;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.*;
import org.msgpack.value.holder.ValueHolder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.msgpack.core.MessagePackException.UNSUPPORTED;

/**
 * Created on 6/16/14.
 */
public class MapCursorImpl extends AbstractValueRef implements MapCursor {

    private final ValueHolder valueHolder;
    private MessageUnpacker unpacker;
    private int cursor = 0;
    private int mapSize;

    public MapCursorImpl(ValueHolder valueHolder) {
        this.valueHolder = valueHolder;
    }

    public void reset(MessageUnpacker unpacker) throws IOException {
        this.unpacker = unpacker;
        cursor = 0;
        this.mapSize = unpacker.unpackMapHeader();
    }

    @Override
    public int size() {
        return mapSize;
    }
    @Override
    public boolean hasNext() {
        try {
            return cursor < (mapSize * 2) && unpacker.hasNext();
        }
        catch(IOException e) {
            return false;
        }
    }

    @Override
    public ValueRef nextKeyOrValue() {
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
    public void skipKeyOrValue() {
        try {
            unpacker.skipValue();
        }
        catch(IOException e) {
            throw new MessageFormatException(e);
        }
    }

    @Override
    public void skipAll() {
        while(hasNext()) {
            skipKeyOrValue();
        }
    }

    private void ensureNotTraversed() {
        if(cursor != 0)
            throw UNSUPPORTED("ArrayCursor is already traversed");
    }


    @Override
    public MapCursor getMapCursor() throws MessageTypeException {
        return this;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.MAP;
    }

    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        ensureNotTraversed();
        packer.packMapHeader(mapSize);
        while(hasNext()) {
            packer.pack(nextKeyOrValue().toValue());
        }
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitMap(this.toValue());
    }

    @Override
    public MapValue toValue() {
        ensureNotTraversed();
        Value[] keyValueArray = new Value[mapSize];
        int i = 0;
        while(hasNext()) {
            keyValueArray[i++] = nextKeyOrValue().toValue();
        }
        return ValueFactory.newMap(keyValueArray);
    }


}
