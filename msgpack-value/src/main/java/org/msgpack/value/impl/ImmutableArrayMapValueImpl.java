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

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.AbstractCollection;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.io.IOException;

import org.msgpack.value.Value;
import org.msgpack.value.MapValue;
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
import org.msgpack.core.ValueType;
import org.msgpack.core.MessagePacker;

public class ImmutableArrayMapValueImpl
        extends AbstractMap<Value, Value> implements ImmutableMapValue {
    private static ImmutableArrayMapValueImpl emptyMapInstance = new ImmutableArrayMapValueImpl(new Value[0]);

    public static ImmutableArrayMapValueImpl getEmptyMapInstance() {
        return emptyMapInstance;
    }

    private final Value[] array;

    public ImmutableArrayMapValueImpl(Value[] array) {
        this.array = array;
    }

    @Override
    public ValueType getType() {
        return ValueType.MAP;
    }

    @Override
    public Value[] getKeyValueArray() {
        return Arrays.copyOf(array, array.length);
    }

    @Override
    public Value get(Object key) {
        for (int i = array.length - 2; i >= 0; i -= 2) {
            if (array[i].equals(key)) {
                return array[i + 1];
            }
        }
        return null;
    }

    private static class EntrySet extends AbstractSet<Map.Entry<Value, Value>> {
        private Value[] array;

        EntrySet(Value[] array) {
            this.array = array;
        }

        @Override
        public int size() {
            return array.length / 2;
        }

        @Override
        public Iterator<Map.Entry<Value, Value>> iterator() {
            return new EntrySetIterator(array);
        }
    }

    private static class EntrySetIterator implements
            Iterator<Map.Entry<Value, Value>> {
        private Value[] array;
        private int pos;

        EntrySetIterator(Value[] array) {
            this.array = array;
            this.pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < array.length;
        }

        @Override
        public Map.Entry<Value, Value> next() {
            if (pos >= array.length) {
                throw new NoSuchElementException();
            }

            Value key = array[pos];
            Value value = array[pos + 1];
            Map.Entry<Value, Value> pair = new AbstractMap.SimpleImmutableEntry<Value, Value>(key, value);

            pos += 2;
            return pair;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class KeySet extends AbstractSet<Value> {
        private Value[] array;

        KeySet(Value[] array) {
            this.array = array;
        }

        @Override
        public int size() {
            return array.length / 2;
        }

        @Override
        public Iterator<Value> iterator() {
            return new ValueIterator(array, 0);
        }
    }

    private static class ValueCollection extends AbstractCollection<Value> {
        private Value[] array;

        ValueCollection(Value[] array) {
            this.array = array;
        }

        @Override
        public int size() {
            return array.length / 2;
        }

        @Override
        public Iterator<Value> iterator() {
            return new ValueIterator(array, 1);
        }
    }

    private static class ValueIterator implements Iterator<Value> {
        private Value[] array;
        private int pos;

        ValueIterator(Value[] array, int offset) {
            this.array = array;
            this.pos = offset;
        }

        @Override
        public boolean hasNext() {
            return pos < array.length;
        }

        @Override
        public Value next() {
            if (pos >= array.length) {
                throw new NoSuchElementException();
            }
            Value v = array[pos];
            pos += 2;
            return v;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Set<Map.Entry<Value, Value>> entrySet() {
        return new EntrySet(array);
    }

    @Override
    public Set<Value> keySet() {
        return new KeySet(array);
    }

    @Override
    public Collection<Value> values() {
        return new ValueCollection(array);
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packMapHeader(array.length / 2);
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
        if (!v.isMapValue()) {
            return false;
        }

        MapValue mv = v.asMapValue();
        if (mv.size() != array.length / 2) {
            return false;
        }

        try {
            for (int i = 0; i < array.length; i += 2) {
                Value key = array[i];
                Value value = array[i + 1];
                if (!value.equals(mv.get(key))) {
                    return false;
                }
            }
        } catch (ClassCastException ex) {
            return false;
        } catch (NullPointerException ex) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < array.length; i += 2) {
            h += array[i].hashCode() ^ array[i + 1].hashCode();
        }
        return h;
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    private StringBuilder toString(StringBuilder sb) {
        if (array.length == 0) {
            return sb.append("{}");
        }
        sb.append("{");
        sb.append(array[0]);
        sb.append(":");
        sb.append(array[1]);
        for (int i = 2; i < array.length; i += 2) {
            sb.append(",");
            sb.append(array[i].toString());
            sb.append(":");
            sb.append(array[i + 1].toString());
        }
        sb.append("}");
        return sb;
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
