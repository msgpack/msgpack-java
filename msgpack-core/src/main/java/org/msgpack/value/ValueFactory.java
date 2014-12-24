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

import org.msgpack.value.impl.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Factory for creting Value instances
 */
public class ValueFactory {
    public static NilValue nilValue() {
        return NilValueImpl.getInstance();
    }

    public static BooleanValue newBoolean(boolean v) {
        return v ? BooleanValueImpl.TRUE : BooleanValueImpl.FALSE;
    }

    public static IntegerValue newByte(byte v) {
        return new IntegerValueImpl((int) v);
    }

    public static IntegerValue newShort(short v) {
        return new IntegerValueImpl((int) v);
    }

    public static IntegerValue newInt(int v) {
        return new IntegerValueImpl(v);
    }

    public static IntegerValue newLong(long v) {
        return new LongValueImpl(v);
    }

    public static IntegerValue newBigInteger(BigInteger v) {
        return new BigIntegerValueImpl(v);
    }

    public static FloatValue newFloat(float v) {
        return new FloatValueImpl(v);
    }

    public static FloatValue newDouble(double v) {
        return new DoubleValueImpl(v);
    }

    public static BinaryValue newBinary(byte[] b) {
        return new BinaryValueImpl(ByteBuffer.wrap(b));
    }

    public static BinaryValue newBinary(byte[] b, int off, int len) {
        return new BinaryValueImpl(ByteBuffer.wrap(b, off, len));
    }

    public static BinaryValue newBinary(ByteBuffer bb) {
        return new BinaryValueImpl(bb.duplicate());
    }

    public static StringValue newString(String s) {
        return new StringValueImpl(s);
    }

    public static StringValue newRawString(byte[] b) {
        return new RawStringValueImpl(ByteBuffer.wrap(b));
    }

    public static StringValue newRawString(byte[] b, int off, int len) {
        return new RawStringValueImpl(ByteBuffer.wrap(b, off, len));
    }

    public static StringValue newRawString(ByteBuffer bb) {
        return new RawStringValueImpl(bb.duplicate());
    }

    public static ArrayValue newArrayFrom(List<? extends Value> list) {
        if (list.isEmpty()) {
            return ArrayValueImpl.empty();
        }
        Value[] array = list.toArray(new Value[list.size()]);
        return new ArrayValueImpl(array);
    }

    public static ArrayValue newArray(Value... array) {
        if (array.length == 0) {
            return ArrayValueImpl.empty();
        }
        return new ArrayValueImpl(array);
    }

    public static ArrayValue emptyArray() {
        return ArrayValueImpl.empty();
    }

    public static <K extends Value, V extends Value> MapValue newMap(Map<K, V> map) {
        Value[] keyValueSequence = new Value[map.size() * 2];
        Iterator<Map.Entry<K, V>> ite = map.entrySet().iterator();
        int index = 0;
        while (ite.hasNext()) {
            Map.Entry<K, V> pair = ite.next();
            keyValueSequence[index++] = pair.getKey();
            keyValueSequence[index++] = pair.getValue();
        }
        return newMap(keyValueSequence);
    }

    public static MapValue newMap(Value[] keyValueSequence) {
        if (keyValueSequence.length == 0) {
            return MapValueImpl.empty();
        }
        return new MapValueImpl(keyValueSequence);
    }

    public static MapValue emptyMap() {
        return MapValueImpl.empty();
    }

    public static ExtendedValue newExtendedValue(int extType, byte[] extData) {
        return newExtendedValue(extType, ByteBuffer.wrap(extData));
    }

    public static ExtendedValue newExtendedValue(int extType, ByteBuffer extData) {
        return new ExtendedValueImpl(extType, extData);
    }

    /**
     * Hide the default constructor to forbid instantiation of this class
     */
    protected ValueFactory() {
    }
}
