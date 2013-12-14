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
package org.msgpack.value.impl;

import org.msgpack.value.ImmutableValue;
import org.msgpack.value.ImmutableNilValue;
import org.msgpack.value.ImmutableBooleanValue;
import org.msgpack.value.ImmutableNumberValue;
import org.msgpack.value.ImmutableIntegerValue;
import org.msgpack.value.ImmutableFloatValue;
import org.msgpack.value.ImmutableRawValue;
import org.msgpack.value.ImmutableBinaryValue;
import org.msgpack.value.ImmutableStringValue;
import org.msgpack.value.ImmutableArrayValue;
import org.msgpack.value.ImmutableMapValue;
import org.msgpack.value.ImmutableExtendedValue;
import org.msgpack.value.MessageTypeCastException;

public abstract class AbstractImmutableValue
        extends AbstractValue implements ImmutableValue {
    @Override
    public ImmutableValue immutableValue() {
        return this;
    }

    @Override
    public ImmutableNilValue asNilValue() {
        if (!isNilValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableNilValue) this;
    }

    @Override
    public ImmutableBooleanValue asBooleanValue() {
        if (!isBooleanValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableBooleanValue) this;
    }

    @Override
    public ImmutableNumberValue asNumberValue() {
        if (!isNumberValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableNumberValue) this;
    }

    @Override
    public ImmutableIntegerValue asIntegerValue() {
        if (!isIntegerValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableIntegerValue) this;
    }

    @Override
    public ImmutableFloatValue asFloatValue() {
        if (!isFloatValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableFloatValue) this;
    }

    @Override
    public ImmutableRawValue asRawValue() {
        if (!isRawValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableRawValue) this;
    }

    @Override
    public ImmutableBinaryValue asBinaryValue() {
        if (!isBinaryValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableBinaryValue) this;
    }

    @Override
    public ImmutableStringValue asStringValue() {
        if (!isStringValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableStringValue) this;
    }

    @Override
    public ImmutableArrayValue asArrayValue() {
        if (!isArrayValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableArrayValue) this;
    }

    @Override
    public ImmutableMapValue asMapValue() {
        if (!isMapValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableMapValue) this;
    }

    @Override
    public ImmutableExtendedValue asExtendedValue() {
        if (!isExtendedValue()) {
            throw new MessageTypeCastException();
        }
        return (ImmutableExtendedValue) this;
    }
}
