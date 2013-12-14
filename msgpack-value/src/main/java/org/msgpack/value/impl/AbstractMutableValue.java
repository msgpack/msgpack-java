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

import org.msgpack.value.MutableValue;
import org.msgpack.value.MutableNilValue;
import org.msgpack.value.MutableBooleanValue;
import org.msgpack.value.MutableNumberValue;
import org.msgpack.value.MutableIntegerValue;
import org.msgpack.value.MutableFloatValue;
import org.msgpack.value.MutableRawValue;
import org.msgpack.value.MutableBinaryValue;
import org.msgpack.value.MutableStringValue;
import org.msgpack.value.MutableArrayValue;
import org.msgpack.value.MutableMapValue;
import org.msgpack.value.MutableExtendedValue;
import org.msgpack.value.MessageTypeCastException;

abstract class AbstractMutableValue
        extends AbstractValue implements MutableValue {
    @Override
    public MutableNilValue asNilValue() {
        if (!isNilValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableNilValue) this;
    }

    @Override
    public MutableBooleanValue asBooleanValue() {
        if (!isBooleanValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableBooleanValue) this;
    }

    @Override
    public MutableNumberValue asNumberValue() {
        if (!isNumberValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableNumberValue) this;
    }

    @Override
    public MutableIntegerValue asIntegerValue() {
        if (!isIntegerValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableIntegerValue) this;
    }

    @Override
    public MutableFloatValue asFloatValue() {
        if (!isFloatValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableFloatValue) this;
    }

    @Override
    public MutableRawValue asRawValue() {
        if (!isRawValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableRawValue) this;
    }

    @Override
    public MutableBinaryValue asBinaryValue() {
        if (!isBinaryValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableBinaryValue) this;
    }

    @Override
    public MutableStringValue asStringValue() {
        if (!isStringValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableStringValue) this;
    }

    @Override
    public MutableArrayValue asArrayValue() {
        if (!isArrayValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableArrayValue) this;
    }

    @Override
    public MutableMapValue asMapValue() {
        if (!isMapValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableMapValue) this;
    }

    @Override
    public MutableExtendedValue asExtendedValue() {
        if (!isExtendedValue()) {
            throw new MessageTypeCastException();
        }
        return (MutableExtendedValue) this;
    }
}
