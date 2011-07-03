//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.value;

import java.util.AbstractMap;
import org.msgpack.MessageTypeException;

abstract class AbstractMapValue extends AbstractMap<Value,Value> implements MapValue {
    public ValueType getType() {
        return ValueType.MAP;
    }

    public boolean isMap() {
        return true;
    }

    public MapValue asMapValue() {
        return this;
    }

    public boolean isNil() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isInteger() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isRaw() {
        return false;
    }

    public NilValue asNilValue() {
        throw new MessageTypeException();
    }

    public BooleanValue asBooleanValue() {
        throw new MessageTypeException();
    }

    public IntegerValue asIntegerValue() {
        throw new MessageTypeException();
    }

    public FloatValue asFloatValue() {
        throw new MessageTypeException();
    }

    public ArrayValue asArrayValue() {
        throw new MessageTypeException();
    }

    public RawValue asRawValue() {
        throw new MessageTypeException();
    }
}

