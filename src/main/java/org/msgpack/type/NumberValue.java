//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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

@SuppressWarnings("serial")
public abstract class NumberValue extends Number implements Value {
    @Override
    public boolean isNilValue() {
        return false;
    }

    @Override
    public boolean isBooleanValue() {
        return false;
    }

    @Override
    public boolean isIntegerValue() {
        return false;
    }

    @Override
    public boolean isFloatValue() {
        return false;
    }

    @Override
    public boolean isArrayValue() {
        return false;
    }

    @Override
    public boolean isMapValue() {
        return false;
    }

    @Override
    public boolean isRawValue() {
        return false;
    }
    
    @Override
    public boolean isExtValue() {
    	return false;
    }

    @Override
    public NilValue asNilValue() {
        throw new MessageTypeException();
    }

    @Override
    public BooleanValue asBooleanValue() {
        throw new MessageTypeException();
    }

    @Override
    public IntegerValue asIntegerValue() {
        throw new MessageTypeException();
    }

    @Override
    public FloatValue asFloatValue() {
        throw new MessageTypeException();
    }

    @Override
    public ArrayValue asArrayValue() {
        throw new MessageTypeException();
    }

    @Override
    public MapValue asMapValue() {
        throw new MessageTypeException();
    }

    @Override
    public RawValue asRawValue() {
        throw new MessageTypeException();
    }
    
    @Override
    public ExtValue asExtValue() {
    	throw new MessageTypeException();
    }

    public abstract BigInteger bigIntegerValue();
}
