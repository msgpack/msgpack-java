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

import java.math.BigInteger;
import org.msgpack.MessageTypeException;

public abstract class NumberValue extends Number implements Value {
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

    public boolean isMap() {
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

    public MapValue asMapValue() {
        throw new MessageTypeException();
    }

    public RawValue asRawValue() {
        throw new MessageTypeException();
    }
}

