//
// MessagePack for Java
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
package org.msgpack.type.impl;

import org.msgpack.type.Value;
import org.msgpack.type.NilValue;
import org.msgpack.type.BooleanValue;
import org.msgpack.type.NumberValue;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.FloatValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.BinaryValue;
import org.msgpack.type.StringValue;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.ExtendedValue;
import org.msgpack.type.MessageTypeCastException;

public abstract class AbstractValue
        implements Value {
    @Override
    public boolean isNilValue() {
        return getType().isNilType();
    }

    @Override
    public boolean isBooleanValue() {
        return getType().isBooleanType();
    }

    @Override
    public boolean isNumberValue() {
        return getType().isNumberType();
    }

    @Override
    public boolean isIntegerValue() {
        return getType().isIntegerType();
    }

    @Override
    public boolean isFloatValue() {
        return getType().isFloatType();
    }

    @Override
    public boolean isRawValue() {
        return getType().isRawType();
    }

    @Override
    public boolean isBinaryValue() {
        return getType().isBinaryType();
    }

    @Override
    public boolean isStringValue() {
        return getType().isStringType();
    }

    @Override
    public boolean isArrayValue() {
        return getType().isArrayType();
    }

    @Override
    public boolean isMapValue() {
        return getType().isMapType();
    }

    @Override
    public boolean isExtendedValue() {
        return getType().isExtendedType();
    }

    @Override
    public NilValue asNilValue() {
        if (!isNilValue()) {
            throw new MessageTypeCastException();
        }
        return (NilValue) this;
    }

    @Override
    public BooleanValue asBooleanValue() {
        if (!isBooleanValue()) {
            throw new MessageTypeCastException();
        }
        return (BooleanValue) this;
    }

    @Override
    public NumberValue asNumberValue() {
        if (!isIntegerValue()) {
            throw new MessageTypeCastException();
        }
        return (NumberValue) this;
    }

    @Override
    public IntegerValue asIntegerValue() {
        if (!isIntegerValue()) {
            throw new MessageTypeCastException();
        }
        return (IntegerValue) this;
    }

    @Override
    public FloatValue asFloatValue() {
        if (!isFloatValue()) {
            throw new MessageTypeCastException();
        }
        return (FloatValue) this;
    }

    @Override
    public RawValue asRawValue() {
        if (!isRawValue()) {
            throw new MessageTypeCastException();
        }
        return (RawValue) this;
    }

    @Override
    public BinaryValue asBinaryValue() {
        if (!isBinaryValue()) {
            throw new MessageTypeCastException();
        }
        return (BinaryValue) this;
    }

    @Override
    public StringValue asStringValue() {
        if (!isStringValue()) {
            throw new MessageTypeCastException();
        }
        return (StringValue) this;
    }

    @Override
    public ArrayValue asArrayValue() {
        if (!isArrayValue()) {
            throw new MessageTypeCastException();
        }
        return (ArrayValue) this;
    }

    @Override
    public MapValue asMapValue() {
        if (!isMapValue()) {
            throw new MessageTypeCastException();
        }
        return (MapValue) this;
    }

    @Override
    public ExtendedValue asExtendedValue() {
        if (!isExtendedValue()) {
            throw new MessageTypeCastException();
        }
        return (ExtendedValue) this;
    }
}
