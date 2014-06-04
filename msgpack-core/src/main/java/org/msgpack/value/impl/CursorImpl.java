package org.msgpack.value.impl;

import org.msgpack.core.*;
import org.msgpack.value.*;
import org.msgpack.value.holder.ValueHolder;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Cursor implementation
 */
public class CursorImpl implements Cursor {

    private final MessageUnpacker unpacker;
    private MessageFormat currentFormat;
    private ValueHolder valueHolder;

    public CursorImpl(MessageUnpacker unpacker) {
        this.unpacker = unpacker;
        this.currentFormat = MessageFormat.NIL;
        this.valueHolder = new ValueHolder();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }


    @Override
    public boolean hasNext()  {
        try {
            return unpacker.hasNext();
        }
        catch(IOException e) {
            throw new MessageFormatException(e);
        }
    }

    @Override
    public void skip() {
        try {
            unpacker.skipValue();
        }
        catch(IOException e){
            throw new MessageFormatException(e);
        }
    }
    @Override
    public long getReadBytes() {
        return unpacker.getTotalReadBytes();
    }


    private final void readNext() {
        try {
            currentFormat = unpacker.unpackValue(valueHolder);
         }
        catch(IOException e) {
            throw new MessageFormatException(e);
        }
    }

    @Override
    public Value next() {
        readNext();
        return valueHolder.get();
     }

    @Override
    public ValueRef nextRef() {
        readNext();
        return valueHolder.getRef();
    }

    private ValueType getValueType() {
        return currentFormat.getValueType();
    }


    @Override
    public <Out> Out apply(Function<Out> f) {
        return null;
    }

    @Override
    public boolean isNilValue() {
        return getValueType().isNilType();
    }
    @Override
    public boolean isBooleanValue() {
        return getValueType().isBooleanType();
    }
    @Override
    public boolean isNumberValue() {
        return getValueType().isNumberType();
    }
    @Override
    public boolean isIntegerValue() {
        return getValueType().isIntegerType();
    }
    @Override
    public boolean isFloatValue() {
        return getValueType().isFloatType();
    }
    @Override
    public boolean isBinaryValue() {
        return getValueType().isBinaryType();
    }
    @Override
    public boolean isStringValue() {
        return getValueType().isStringType();
    }
    @Override
    public boolean isRawValue() {
        return getValueType().isRawType();
    }
    @Override
    public boolean isArrayValue() {
        return getValueType().isArrayType();
    }
    @Override
    public boolean isMapValue() {
        return getValueType().isMapType();
    }
    @Override
    public boolean isExtendedValue() {
        return getValueType().isExtendedType();
    }

    @Override
    public void close() throws IOException {
        unpacker.close();
    }
}
