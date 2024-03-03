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

import org.msgpack.core.MessagePacker;
import org.msgpack.value.ImmutableMapValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueType;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * {@code ImmutableMapValueImpl} Implements {@code ImmutableMapValue} using a {@code Value[]} field.
 *
 * @see org.msgpack.value.MapValue
 */
public class ImmutableMapValueImpl
        extends AbstractImmutableValue
        implements ImmutableMapValue
{
    private static final ImmutableMapValueImpl EMPTY = new ImmutableMapValueImpl(new Value[0]);

    public static ImmutableMapValue empty()
    {
        return EMPTY;
    }

    private final Value[] kvs;

    public ImmutableMapValueImpl(Value[] kvs)
    {
        this.kvs = kvs;
    }

    @Override
    public ValueType getValueType()
    {
        return ValueType.MAP;
    }

    @Override
    public ImmutableMapValue immutableValue()
    {
        return this;
    }

    @Override
    public ImmutableMapValue asMapValue()
    {
        return this;
    }

    @Override
    public Value[] getKeyValueArray()
    {
        return Arrays.copyOf(kvs, kvs.length);
    }

    @Override
    public int size()
    {
        return kvs.length / 2;
    }

    @Override
    public Set<Value> keySet()
    {
        return new KeySet(kvs);
    }

    @Override
    public Set<Map.Entry<Value, Value>> entrySet()
    {
        return new EntrySet(kvs);
    }

    @Override
    public Collection<Value> values()
    {
        return new ValueCollection(kvs);
    }

    @Override
    public Map<Value, Value> map()
    {
        return new ImmutableMapValueMap(kvs);
    }

    @Override
    public void writeTo(MessagePacker pk)
            throws IOException
    {
        pk.packMapHeader(kvs.length / 2);
        for (int i = 0; i < kvs.length; i++) {
            kvs[i].writeTo(pk);
        }
    }

    @Override
    public boolean equals(Object o)
    {
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
        return map().equals(mv.map());
    }

    @Override
    public int hashCode()
    {
        int h = 0;
        for (int i = 0; i < kvs.length; i += 2) {
            h += kvs[i].hashCode() ^ kvs[i + 1].hashCode();
        }
        return h;
    }

    @Override
    public String toJson()
    {
        if (kvs.length == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        appendJsonKey(sb, kvs[0]);
        sb.append(":");
        sb.append(kvs[1].toJson());
        for (int i = 2; i < kvs.length; i += 2) {
            sb.append(",");
            appendJsonKey(sb, kvs[i]);
            sb.append(":");
            sb.append(kvs[i + 1].toJson());
        }
        sb.append("}");
        return sb.toString();
    }

    private static void appendJsonKey(StringBuilder sb, Value key)
    {
        if (key.isRawValue()) {
            sb.append(key.toJson());
        }
        else {
            ImmutableStringValueImpl.appendJsonString(sb, key.toString());
        }
    }

    @Override
    public String toString()
    {
        if (kvs.length == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        appendString(sb, kvs[0]);
        sb.append(":");
        appendString(sb, kvs[1]);
        for (int i = 2; i < kvs.length; i += 2) {
            sb.append(",");
            appendString(sb, kvs[i]);
            sb.append(":");
            appendString(sb, kvs[i + 1]);
        }
        sb.append("}");
        return sb.toString();
    }

    private static void appendString(StringBuilder sb, Value value)
    {
        if (value.isRawValue()) {
            sb.append(value.toJson());
        }
        else {
            sb.append(value.toString());
        }
    }

    private static class ImmutableMapValueMap
            extends AbstractMap<Value, Value>
    {
        private final Value[] kvs;

        public ImmutableMapValueMap(Value[] kvs)
        {
            this.kvs = kvs;
        }

        @Override
        public Value get(Object key)
        {
            if (key == null) {
                // handle this case in the exact same way as the previously used AbstractMap.get()
                for (int i = 0; i < kvs.length; i += 2) {
                    if (kvs[i] == null) {
                        return kvs[i + 1];
                    }
                }
            }
            else {
                for (int i = 0; i < kvs.length; i += 2) {
                    if (key.equals(kvs[i])) {
                        return kvs[i + 1];
                    }
                }
            }
            return null;
        }

        @Override
        public Set<Map.Entry<Value, Value>> entrySet()
        {
            return new EntrySet(kvs);
        }

        @Override
        public void forEach(BiConsumer<? super Value, ? super Value> action)
        {
            for (int i = 0; i < kvs.length; i += 2) {
                action.accept(kvs[i], kvs[i + 1]);
            }
        }
    }

    private static class EntrySet
            extends AbstractSet<Map.Entry<Value, Value>>
    {
        private final Value[] kvs;

        EntrySet(Value[] kvs)
        {
            this.kvs = kvs;
        }

        @Override
        public int size()
        {
            return kvs.length / 2;
        }

        @Override
        public Iterator<Map.Entry<Value, Value>> iterator()
        {
            return new EntrySetIterator(kvs);
        }
    }

    private static class EntrySetIterator
            implements Iterator<Map.Entry<Value, Value>>
    {
        private final Value[] kvs;
        private int index;

        EntrySetIterator(Value[] kvs)
        {
            this.kvs = kvs;
            this.index = 0;
        }

        @Override
        public boolean hasNext()
        {
            return index < kvs.length;
        }

        @Override
        public Map.Entry<Value, Value> next()
        {
            if (index >= kvs.length) {
                throw new NoSuchElementException(); // TODO message
            }

            Value key = kvs[index];
            Value value = kvs[index + 1];
            Map.Entry<Value, Value> pair = new AbstractMap.SimpleImmutableEntry<Value, Value>(key, value);

            index += 2;
            return pair;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException(); // TODO message
        }
    }

    private static class KeySet
            extends AbstractSet<Value>
    {
        private Value[] kvs;

        KeySet(Value[] kvs)
        {
            this.kvs = kvs;
        }

        @Override
        public int size()
        {
            return kvs.length / 2;
        }

        @Override
        public Iterator<Value> iterator()
        {
            return new EntryIterator(kvs, 0);
        }
    }

    private static class ValueCollection
            extends AbstractCollection<Value>
    {
        private Value[] kvs;

        ValueCollection(Value[] kvs)
        {
            this.kvs = kvs;
        }

        @Override
        public int size()
        {
            return kvs.length / 2;
        }

        @Override
        public Iterator<Value> iterator()
        {
            return new EntryIterator(kvs, 1);
        }
    }

    private static class EntryIterator
            implements Iterator<Value>
    {
        private Value[] kvs;
        private int index;

        public EntryIterator(Value[] kvs, int offset)
        {
            this.kvs = kvs;
            this.index = offset;
        }

        @Override
        public boolean hasNext()
        {
            return index < kvs.length;
        }

        @Override
        public Value next()
        {
            int i = index;
            if (i >= kvs.length) {
                throw new NoSuchElementException();
            }
            index = i + 2;
            return kvs[i];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
