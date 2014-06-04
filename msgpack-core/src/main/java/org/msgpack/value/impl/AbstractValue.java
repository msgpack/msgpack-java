package org.msgpack.value.impl;

import org.msgpack.core.MessageTypeException;
import org.msgpack.value.ValueType;
import org.msgpack.value.*;

/**
* Base implementation of MessagePackValue
*/
public abstract class AbstractValue extends AbstractValueRef implements Value {

    @Override
    public boolean isRef() { return false; }

}
