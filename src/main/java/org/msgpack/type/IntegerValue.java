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
package org.msgpack.type;

import java.math.BigInteger;
import org.msgpack.MessageTypeException;

public abstract class IntegerValue extends NumberValue {
    @Override
    public ValueType getType() {
        return ValueType.INTEGER;
    }

    @Override
    public boolean isIntegerValue() {
        return true;
    }

    @Override
    public IntegerValue asIntegerValue() {
        return this;
    }

    public abstract byte getByte();

    public abstract short getShort();

    public abstract int getInt();

    public abstract long getLong();

    public BigInteger getBigInteger() {
        return bigIntegerValue();
    }

    // TODO equals
    // TODO hashCode
}

