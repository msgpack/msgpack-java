package org.msgpack.value.holder;

import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.value.impl.ArrayCursorImpl;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;
import org.msgpack.value.ValueType;
import org.msgpack.value.impl.MapCursorImpl;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.msgpack.core.MessagePackException.UNREACHABLE;

/**
 * This class can hold any message packed value.
 */
public class ValueHolder {

    private ValueType vt;
    private IntegerHolder integerHolder = new IntegerHolder();
    private FloatHolder floatHolder = new FloatHolder();
    private RawHolder rawHolder = new RawHolder();
    private ExtHolder extHolder = new ExtHolder();
    private ArrayCursorImpl arrayCursor;
    private MapCursorImpl mapCursor;
    private Value current;

    public Value get() {
        switch(vt) {
            case NIL:
            case BOOLEAN:
            case INTEGER:
            case FLOAT:
            case ARRAY:
            case MAP:
            case EXTENDED:
            case STRING:
            case BINARY:
                return current;
//                return ValueFactory.newRawString(cloneBuffer(rawHolder.getBuffer()));
//                return ValueFactory.newBinary(cloneBuffer(rawHolder.getBuffer()));
            default:
                throw UNREACHABLE;
        }
    }

    private static ByteBuffer cloneBuffer(MessageBuffer buffer) {
        return ByteBuffer.wrap(buffer.toByteArray());
    }

    public IntegerHolder getIntegerHolder() {
        return integerHolder;
    }

    public FloatHolder getFloatHolder() {
        return floatHolder;
    }

    public void setBoolean(boolean v) {
        vt = ValueType.BOOLEAN;
        current = ValueFactory.newBoolean(v);
    }

    public void setNil() {
        vt = ValueType.NIL;
        current = ValueFactory.nilValue();
    }

    public void setString(MessageBuffer rawString) {
        vt = ValueType.STRING;
        rawHolder.setString(rawString);
        current = rawHolder.asStringValue();
    }

    public void setBinary(MessageBuffer b) {
        vt = ValueType.BINARY;
        rawHolder.setBinary(b);
        current = rawHolder.asBinaryValue();
    }

    public void setToInteger() {
        vt = ValueType.INTEGER;
        current = integerHolder;
    }

    public void setToFloat() {
        vt = ValueType.FLOAT;
        current = floatHolder;
    }

    public void setExt(int extType, MessageBuffer b) {
        vt = ValueType.EXTENDED;
        extHolder.setExtType(extType, b);
        current = extHolder;
    }

    public void prepareArrayCursor(MessageUnpacker unpacker) throws IOException {
        vt = ValueType.ARRAY;

        // TODO reusing cursor instances
        arrayCursor = new ArrayCursorImpl(new ValueHolder());
        arrayCursor.reset(unpacker);
        current = arrayCursor;
    }

    public void prepareMapCursor(MessageUnpacker unpacker) throws IOException {
        vt = ValueType.MAP;

        // TODO reusing cursor instances
        mapCursor = new MapCursorImpl(new ValueHolder());
        mapCursor.reset(unpacker);
        current = mapCursor;
    }

}
