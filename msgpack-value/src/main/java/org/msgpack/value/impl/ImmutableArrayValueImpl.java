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

import java.util.Iterator;
import java.util.AbstractList;
import java.io.IOException;

import org.msgpack.core.ValueType;
import org.msgpack.value.Value;
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
import org.msgpack.core.MessagePacker;

public class ImmutableArrayValueImpl
        extends AbstractList<Value> implements ImmutableArrayValue {
    private static ImmutableArrayValueImpl emptyArrayInstance = new ImmutableArrayValueImpl(new Value[0]);

    public static ImmutableArrayValueImpl getEmptyArrayInstance() {
        return emptyArrayInstance;
    }

    private final Value[] array;

    public ImmutableArrayValueImpl(Value[] array) {
        this.array = array;
    }

    @Override
    public ValueType getType() {
        return ValueType.ARRAY;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packArrayHeader(array.length);
        for (int i = 0; i < array.length; i++) {
            array[i].writeTo(pk);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if (!v.isArrayValue()) {
            return false;
        }
        Iterator<Value> ite = v.asArrayValue().iterator();
        for (int i=0; i < array.length; i++) {
            if (!ite.hasNext() || !array[i].equals(ite.next())) {
                return false;
            }
        }
        return !ite.hasNext();
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0; i < array.length; i++) {
            Value obj = array[i];
            h = 31 * h + obj.hashCode();
        }
        return h;
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    private StringBuilder toString(StringBuilder sb) {
        if (array.length == 0) {
            return sb.append("[]");
        }
        sb.append("[");
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(",");
            sb.append(array[i].toString());
        }
        sb.append("]");
        return sb;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public boolean isEmpty() {
        return array.length == 0;
    }

    @Override
    public Value get(int index) {
        // List.get(index) throws IndexOutOfBoundsException
        return array[index];
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    //
    // copied from AbstractValue
    //

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

    //
    // copied from AbstractImmutableValue {
    //

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
