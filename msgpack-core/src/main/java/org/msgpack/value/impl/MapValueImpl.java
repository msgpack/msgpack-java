package org.msgpack.value.impl;

import org.msgpack.core.MessagePacker;
import org.msgpack.value.ValueType;
import org.msgpack.value.*;

import java.io.IOException;
import java.util.*;

/**
* Created on 5/30/14.
*/
public class MapValueImpl extends AbstractValue implements MapValue {
    private static MapValueImpl EMPTY = new MapValueImpl(new Value[0]);

    public static MapValue empty() {
        return EMPTY;
    }

    private final Value[] array;
    private transient int cursor = 0;

    public MapValueImpl(Value[] array) {
        this.array = array;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.MAP;
    }

    @Override
    public Value[] toKeyValueSeq() {
        return Arrays.copyOf(array, array.length);
    }
    @Override
    public int size() {
        return array.length / 2;
    }

    @Override
    public boolean hasNext() {
        return cursor < array.length;
    }
    @Override
    public ValueRef nextKeyOrValue() {
        return array[cursor++];
    }

    @Override
    public void skipKeyOrValue() {
        cursor++;
    }
    @Override
    public void skipAll() {
        while(hasNext()) {
            skipKeyOrValue();
        }
    }


    private class MapImpl extends AbstractMap<Value, Value> {

        private class EntrySet extends AbstractSet<Map.Entry<Value, Value>> {

            @Override
            public int size() {
                return array.length / 2;
            }

            @Override
            public Iterator<Map.Entry<Value, Value>> iterator() {
                return new EntrySetIterator();
            }
        }

        private class EntrySetIterator implements
                Iterator<Map.Entry<Value, Value>> {
            private int pos = 0;

            @Override
            public boolean hasNext() {
                return pos < array.length;
            }

            @Override
            public Entry<Value, Value> next() {
                if (pos >= array.length) {
                    throw new NoSuchElementException();
                }

                Value key = array[pos];
                Value value = array[pos + 1];
                Entry<Value, Value> pair = new SimpleImmutableEntry<Value, Value>(key, value);

                pos += 2;
                return pair;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        private class KeySet extends AbstractSet<Value> {
            @Override
            public int size() {
                return array.length / 2;
            }

            @Override
            public Iterator<Value> iterator() {
                return new ValueIterator(0);
            }
        }

        private class ValueCollection extends AbstractCollection<Value> {

            @Override
            public int size() {
                return array.length / 2;
            }

            @Override
            public Iterator<Value> iterator() {
                return new ValueIterator(1);
            }
        }

        private class ValueIterator implements Iterator<Value> {
            private int pos;

            ValueIterator(int offset) {
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
        public Set<Entry<Value, Value>> entrySet() {
            return new EntrySet();
        }

        @Override
        public Set<Value> keySet() {
            return new KeySet();
        }

        @Override
        public Collection<Value> values() {
            return new ValueCollection();
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
    }

    @Override
    public Map<Value, Value> toMap() {
        return new MapImpl();
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packMapHeader(array.length / 2);
        for (int i = 0; i < array.length; i++) {
            array[i].writeTo(pk);
        }
    }
    @Override
    public void accept(ValueVisitor visitor) {
        visitor.visitMap(this);
    }

    @Override
    public Value toValue() {
        return ValueFactory.newMap(array);
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
        if (!v.isMap()) {
            return false;
        }

        Map<Value, Value> mv = v.asMapValue().toMap();
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



}
