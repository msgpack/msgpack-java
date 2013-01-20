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

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.AbstractCollection;
import java.util.NoSuchElementException;
import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.util.android.PortedImmutableEntry;

class SequentialMapValueImpl extends AbstractMapValue {
    private static SequentialMapValueImpl emptyInstance = new SequentialMapValueImpl(new Value[0], true);

    public static MapValue getEmptyInstance() {
        return emptyInstance;
    }

    private Value[] array;

    @Override
    public Value[] getKeyValueArray() {
        return array;
    }

    SequentialMapValueImpl(Value[] array, boolean gift) {
        if (array.length % 2 != 0) {
            throw new IllegalArgumentException(); // TODO message
        }
        if (gift) {
            this.array = array;
        } else {
            this.array = new Value[array.length];
            System.arraycopy(array, 0, this.array, 0, array.length);
        }
    }

    @Override
    public Value get(Object key) {
        if (key == null) {
            return null;
        }
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
        private static final boolean hasDefaultImmutableEntry;
        static {
            boolean hasIt = true;
            try {
                Class.forName("java.util.AbstractMap.SimpleImmutableEntry");
            } catch (ClassNotFoundException e) {
                hasIt = false;
            } finally {
                hasDefaultImmutableEntry = hasIt;
            }
        }

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
                throw new NoSuchElementException(); // TODO message
            }

            Value key = array[pos];
            Value value = array[pos + 1];
            /**
             * @see https://github.com/msgpack/msgpack-java/pull/27
             *
             * msgpack-java was crashed on Android 2.2 or below because
             * the method calls java.util.AbstractMap$SimpleImmutableEntry
             * that doesn't exist in Android 2.2 or below.
             */
            Map.Entry<Value, Value> pair = hasDefaultImmutableEntry ?
                new AbstractMap.SimpleImmutableEntry<Value, Value>(key, value) :
                new PortedImmutableEntry<Value, Value>(key, value);

            pos += 2;
            return pair;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(); // TODO message
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
                throw new NoSuchElementException(); // TODO message
            }
            Value v = array[pos];
            pos += 2;
            return v;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(); // TODO message
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
    public void writeTo(Packer pk) throws IOException {
        pk.writeMapBegin(array.length / 2);
        for (int i = 0; i < array.length; i++) {
            array[i].writeTo(pk);
        }
        pk.writeMapEnd();
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

        Map<Value, Value> om = v.asMapValue();
        if (om.size() != array.length / 2) {
            return false;
        }

        try {
            for (int i = 0; i < array.length; i += 2) {
                Value key = array[i];
                Value value = array[i + 1];
                if (!value.equals(om.get(key))) {
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

    // private boolean equals(SequentialMapValueImpl o) {
    //   if(array.length != o.array.length) {
    //     return false;
    //   }
    //   for(int i=0; i < array.length; i+=2) {
    //     if(!equalsValue(o.array, array[i], array[i+1], i)) {
    //     return false;
    //     }
    //   }
    //   return true;
    // }

    // private boolean equalsValue(Value[] oarray, Value key, Value val, int hint) {
    //   for(int j=hint; j < array.length; j+=2) {
    //     if(key.equals(oarray[j])) {
    //       if(val.equals(oarray[j+1])) {
    //         return true;
    //       } else {
    //         return false;
    //       }
    //     }
    //   }
    //   for(int j=0; j < hint; j+=2) {
    //     if(key.equals(oarray[j])) {
    //       if(val.equals(oarray[j+1])) {
    //         return true;
    //       } else {
    //         return false;
    //       }
    //     }
    //   }
    //   return false;
    // }

    // TODO compareTo?

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

    @Override
    public StringBuilder toString(StringBuilder sb) {
        if (array.length == 0) {
            return sb.append("{}");
        }
        sb.append("{");
        sb.append(array[0]);
        sb.append(":");
        sb.append(array[1]);
        for (int i = 2; i < array.length; i += 2) {
            sb.append(",");
            array[i].toString(sb);
            sb.append(":");
            array[i + 1].toString(sb);
        }
        sb.append("}");
        return sb;
    }
}
