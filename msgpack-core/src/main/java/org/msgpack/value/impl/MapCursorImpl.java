package org.msgpack.value.impl;

import org.msgpack.core.MessageFormatException;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.KeyValuePair;
import org.msgpack.value.MapCursor;
import org.msgpack.value.MapValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;
import org.msgpack.value.ValueType;
import org.msgpack.value.ValueVisitor;
import org.msgpack.value.holder.ValueHolder;

import java.io.IOException;

import static org.msgpack.core.MessagePackException.UNSUPPORTED;

/**
 * MapCursor implementation
 */
public class MapCursorImpl extends AbstractValue implements MapCursor {

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
    public KeyValuePair next() {
        try {
            unpacker.unpackValue(valueHolder);
            Value key = valueHolder.get();
            cursor++;

            unpacker.unpackValue(valueHolder);
            Value value = valueHolder.get();
            cursor++;
            return new KeyValuePair(key, value);
        }
        catch(IOException e) {
            throw new MessageFormatException(e);
        }
    }

    @Override
    public void skip() {
        try {
            unpacker.skipValue();
            unpacker.skipValue();
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
            throw UNSUPPORTED("MapCursor is already traversed");
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
            KeyValuePair pair = next();
            packer.packValue(pair.key);
            packer.packValue(pair.value);
        }
    }

    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitMap(this.toImmutable());
    }

    @Override
    public MapValue toImmutable() {
        ensureNotTraversed();
        Value[] keyValueArray = new Value[mapSize * 2];
        int i = 0;
        while(hasNext()) {
            KeyValuePair pair = next();
            keyValueArray[i++] = pair.key.toImmutable();
            keyValueArray[i++] = pair.value.toImmutable();
        }
        return ValueFactory.newMap(keyValueArray);
    }


}
