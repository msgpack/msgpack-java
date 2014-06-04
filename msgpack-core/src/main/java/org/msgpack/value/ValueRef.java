package org.msgpack.value;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageTypeException;

import java.io.IOException;

/**
 * Reference to the value
 */
public interface ValueRef {
    public ValueType getValueType();

    public NilValue asNil() throws MessageTypeException;
    public BooleanValue asBoolean() throws MessageTypeException;
    public NumberValue asNumber() throws MessageTypeException;
    public IntegerValue asInteger() throws MessageTypeException;
    public FloatValue asFloat() throws MessageTypeException;
    public BinaryValue asBinary() throws MessageTypeException;
    public StringValue asString() throws MessageTypeException;
    public RawValue asRaw() throws MessageTypeException;
    public ExtendedValue asExtended() throws MessageTypeException;

    public ArrayCursor getArrayCursor() throws MessageTypeException;
    public MapCursor getMapCursor() throws MessageTypeException;

    public boolean isNil();
    public boolean isBoolean();
    public boolean isNumber();
    public boolean isInteger();
    public boolean isFloat();
    public boolean isBinary();
    public boolean isString();
    public boolean isRaw();
    public boolean isArray();
    public boolean isMap();
    public boolean isExtended();

    public void writeTo(MessagePacker packer) throws IOException;

    public void accept(ValueVisitor visitor);

    /**
     * Create an immutable value from this reference
     * @return
     */
    public Value toValue();

    /**
     * Test whether this value is a reference of not.
     * @return true if this value is reference, otherwise false.
     */
    public boolean isRef();
}
