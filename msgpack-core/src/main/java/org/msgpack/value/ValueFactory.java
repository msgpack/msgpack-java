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
package org.msgpack.value;

import org.msgpack.value.impl.ImmutableNilValueImpl;
import org.msgpack.value.impl.ImmutableBooleanValueImpl;
import org.msgpack.value.impl.ImmutableLongValueImpl;
import org.msgpack.value.impl.ImmutableBigIntegerValueImpl;
import org.msgpack.value.impl.ImmutableBinaryValueImpl;
import org.msgpack.value.impl.ImmutableDoubleValueImpl;
import org.msgpack.value.impl.ImmutableStringValueImpl;
import org.msgpack.value.impl.ImmutableArrayValueImpl;
import org.msgpack.value.impl.ImmutableMapValueImpl;
import org.msgpack.value.impl.ImmutableExtensionValueImpl;

import java.util.*;
import java.math.BigInteger;

public final class ValueFactory {
    private ValueFactory() { }

    public static ImmutableNilValue nil() {
        return ImmutableNilValueImpl.get();
    }

    public static ImmutableBooleanValue newBoolean(boolean v) {
        return v ? ImmutableBooleanValueImpl.TRUE : ImmutableBooleanValueImpl.FALSE;
    }

    public static ImmutableIntegerValue newInteger(byte v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue newInteger(short v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue newInteger(int v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue newInteger(long v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue newInteger(BigInteger v) {
        return new ImmutableBigIntegerValueImpl(v);
    }

    public static ImmutableFloatValue newFloat(float v) {
        return new ImmutableDoubleValueImpl(v);
    }

    public static ImmutableFloatValue newFloat(double v) {
        return new ImmutableDoubleValueImpl(v);
    }

    public static ImmutableBinaryValue newBinary(byte[] b) {
        return new ImmutableBinaryValueImpl(b);
    }

    public static ImmutableBinaryValue newBinary(byte[] b, int off, int len) {
        return new ImmutableBinaryValueImpl(Arrays.copyOfRange(b, off, len));
    }

    public static ImmutableStringValue newString(String s) {
        return new ImmutableStringValueImpl(s);
    }

    public static ImmutableStringValue newString(byte[] b) {
        return new ImmutableStringValueImpl(b);
    }

    public static ImmutableStringValue newString(byte[] b, int off, int len) {
        return new ImmutableStringValueImpl(Arrays.copyOfRange(b, off, len));
    }

    public static ImmutableArrayValue newArray(List<? extends Value> list) {
        if (list.isEmpty()) {
            return ImmutableArrayValueImpl.empty();
        }
        Value[] array = list.toArray(new Value[list.size()]);
        return new ImmutableArrayValueImpl(array);
    }

    public static ImmutableArrayValue newArray(Value[] array) {
        if (array.length == 0) {
            return ImmutableArrayValueImpl.empty();
        }
        return new ImmutableArrayValueImpl(Arrays.copyOf(array, array.length));
    }

    public static ImmutableArrayValue newArrayOf(Value... elem) {
        return newArray(elem);
    }


    public static ImmutableArrayValue emptyArray() {
        return ImmutableArrayValueImpl.empty();
    }


    public static <K extends Value, V extends Value>
    ImmutableMapValue newMap(Map<K, V> map) {
        Value[] kvs = new Value[map.size() * 2];
        Iterator<Map.Entry<K, V>> ite = map.entrySet().iterator();
        int index = 0;
        while (ite.hasNext()) {
            Map.Entry<K, V> pair = ite.next();
            kvs[index] = pair.getKey();
            index++;
            kvs[index] = pair.getValue();
            index++;
        }
        return newMap(kvs);
    }

    public static ImmutableMapValue newMap(Value[] kvs) {
        if (kvs.length == 0) {
            return ImmutableMapValueImpl.empty();
        }
        return new ImmutableMapValueImpl(Arrays.copyOf(kvs, kvs.length));
    }

    public static ImmutableMapValue emptyMap() {
        return ImmutableMapValueImpl.empty();
    }


    public static class MapEntry {
        public final Value key;
        public final Value value;

        public MapEntry(Value key, Value value) {
            this.key = key;
            this.value = value;
        }
    }

    public static MapValue newMap(MapEntry... pairs) {
        MapBuilder b = new MapBuilder();
        for(MapEntry p : pairs) {
            b.put(p);
        }
        return b.build();
    }


    public static MapBuilder newMapBuilder() {
        return new MapBuilder();
    }

    public static MapEntry newMapEntry(Value key, Value value) {
        return new MapEntry(key, value);

    }

    public static class MapBuilder {
        private Map<Value, Value> map = new HashMap<Value, Value>();
        public MapBuilder() {}

        public MapValue build() {
            return newMap(map);
        }

        public void put(MapEntry pair) {
            put(pair.key, pair.value);
        }

        public void put(Value key, Value value) {
            map.put(key, value);
        }
    }

    public static ImmutableExtensionValue newExtension(byte type, byte[] data) {
        return new ImmutableExtensionValueImpl(type, data);
    }
}
