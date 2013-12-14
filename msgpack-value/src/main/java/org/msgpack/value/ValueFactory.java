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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.msgpack.value.impl.ImmutableNilValueImpl;
import org.msgpack.value.impl.ImmutableTrueValueImpl;
import org.msgpack.value.impl.ImmutableFalseValueImpl;
import org.msgpack.value.impl.ImmutableIntValueImpl;
import org.msgpack.value.impl.ImmutableLongValueImpl;
import org.msgpack.value.impl.ImmutableBigIntegerValueImpl;
import org.msgpack.value.impl.ImmutableBinaryValueImpl;
import org.msgpack.value.impl.ImmutableFloatValueImpl;
import org.msgpack.value.impl.ImmutableDoubleValueImpl;
import org.msgpack.value.impl.ImmutableRawStringValueImpl;
import org.msgpack.value.impl.ImmutableStringValueImpl;
import org.msgpack.value.impl.ImmutableArrayValueImpl;
import org.msgpack.value.impl.ImmutableArrayMapValueImpl;

public final class ValueFactory {
    public static ImmutableNilValue createNilValue() {
        return ImmutableNilValueImpl.getInstance();
    }

    public static ImmutableBooleanValue createBooleanValue(boolean v) {
        if (v) {
            return ImmutableTrueValueImpl.getInstance();
        } else {
            return ImmutableFalseValueImpl.getInstance();
        }
    }

    public static ImmutableIntegerValue createIntegerValue(byte v) {
        return new ImmutableIntValueImpl((int) v);
    }

    public static ImmutableIntegerValue createIntegerValue(short v) {
        return new ImmutableIntValueImpl((int) v);
    }

    public static ImmutableIntegerValue createIntegerValue(int v) {
        return new ImmutableIntValueImpl(v);
    }

    public static ImmutableIntegerValue createIntegerValue(long v) {
        return new ImmutableLongValueImpl(v);
    }

    public static ImmutableIntegerValue createIntegerValue(BigInteger v) {
        return new ImmutableBigIntegerValueImpl(v);
    }

    public static ImmutableFloatValue createFloatValue(float v) {
        return new ImmutableFloatValueImpl(v);
    }

    public static ImmutableFloatValue createFloatValue(double v) {
        return new ImmutableDoubleValueImpl(v);
    }

    public static ImmutableBinaryValue createBinaryValue(byte[] b) {
        return new ImmutableBinaryValueImpl(ByteBuffer.wrap(b));
    }

    public static ImmutableBinaryValue createBinaryValue(byte[] b, int off, int len) {
        return new ImmutableBinaryValueImpl(ByteBuffer.wrap(b, off, len));
    }

    public static ImmutableBinaryValue createBinaryValue(ByteBuffer bb) {
        return new ImmutableBinaryValueImpl(bb.duplicate());
    }

    public static ImmutableStringValue createStringValue(String s) {
        return new ImmutableStringValueImpl(s);
    }

    public static ImmutableStringValue createRawStringValue(byte[] b) {
        return new ImmutableRawStringValueImpl(ByteBuffer.wrap(b));
    }

    public static ImmutableStringValue createRawStringValue(byte[] b, int off, int len) {
        return new ImmutableRawStringValueImpl(ByteBuffer.wrap(b, off, len));
    }

    public static ImmutableStringValue createRawStringValue(ByteBuffer bb) {
        return new ImmutableRawStringValueImpl(bb.duplicate());
    }

    public static ImmutableArrayValue createArrayValue(List<? extends Value> list) {
        if (list.isEmpty()) {
            return ImmutableArrayValueImpl.getEmptyArrayInstance();
        }
        Value[] array = list.toArray(new Value[list.size()]);
        return new ImmutableArrayValueImpl(array);
    }

    public static ImmutableArrayValue createArrayValue(Value[] array) {
        if (array.length == 0) {
            return ImmutableArrayValueImpl.getEmptyArrayInstance();
        }
        return new ImmutableArrayValueImpl(array);
    }

    public static <K extends Value, V extends Value>
            ImmutableMapValue createMapValue(Map<K, V> map) {
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
        return createMapValue(kvs);
    }

    public static ImmutableMapValue createMapValue(Value[] kvs) {
        if (kvs.length == 0) {
            return ImmutableArrayMapValueImpl.getEmptyMapInstance();
        }
        return new ImmutableArrayMapValueImpl(kvs);
    }

    private ValueFactory() {
    }
}
