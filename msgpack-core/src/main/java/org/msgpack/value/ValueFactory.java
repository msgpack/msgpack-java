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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.math.BigInteger;

public final class ValueFactory {
    private ValueFactory() { }

    public static ImmutableNilValue newNilValue() {
        return ImmutableNilValueImpl.get();
    }

    public static ImmutableBooleanValue newBooleanValue(boolean v) {
        if (v) {
            return ImmutableBooleanValueImpl.trueInstance();
        } else {
            return ImmutableBooleanValueImpl.falseInstance();
        }
    }

    public static ImmutableIntegerValue newIntegerValue(byte v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue newIntegerValue(short v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue newIntegerValue(int v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue newIntegerValue(long v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue newIntegerValue(BigInteger v) {
        return new ImmutableBigIntegerValueImpl(v);
    }

    public static ImmutableFloatValue newFloatValue(float v) {
        return new ImmutableDoubleValueImpl(v);
    }

    public static ImmutableFloatValue newFloatValue(double v) {
        return new ImmutableDoubleValueImpl(v);
    }

    public static ImmutableBinaryValue newBinaryValue(byte[] b) {
        return new ImmutableBinaryValueImpl(b);
    }

    public static ImmutableBinaryValue newBinaryValue(byte[] b, int off, int len) {
        return new ImmutableBinaryValueImpl(Arrays.copyOfRange(b, off, len));
    }

    public static ImmutableStringValue newStringValue(String s) {
        return new ImmutableStringValueImpl(s);
    }

    public static ImmutableStringValue newStringValue(byte[] b) {
        return new ImmutableStringValueImpl(b);
    }

    public static ImmutableStringValue newStringValue(byte[] b, int off, int len) {
        return new ImmutableStringValueImpl(Arrays.copyOfRange(b, off, len));
    }

    public static ImmutableArrayValue newArrayValue(List<? extends Value> list) {
        if (list.isEmpty()) {
            return ImmutableArrayValueImpl.empty();
        }
        Value[] array = list.toArray(new Value[list.size()]);
        return new ImmutableArrayValueImpl(array);
    }

    public static ImmutableArrayValue newArrayValue(Value[] array) {
        if (array.length == 0) {
            return ImmutableArrayValueImpl.empty();
        }
        return new ImmutableArrayValueImpl(Arrays.copyOf(array, array.length));
    }

    public static <K extends Value, V extends Value>
    ImmutableMapValue newMapValue(Map<K, V> map) {
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
        return newMapValue(kvs);
    }

    public static ImmutableMapValue newMapValue(Value[] kvs) {
        if (kvs.length == 0) {
            return ImmutableMapValueImpl.empty();
        }
        return new ImmutableMapValueImpl(Arrays.copyOf(kvs, kvs.length));
    }

    public static ImmutableExtensionValue newExtensionValue(byte type, byte[] data) {
        return new ImmutableExtensionValueImpl(type, data);
    }
}
