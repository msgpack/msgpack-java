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

public class ValueFactory {
/* TODO
    public NilValue newNilValue() {
        return NilValueImpl.getInstance();
    }

    public BooleanValue newBooleanValue(boolean v) {
        if (v) {
            return TrueValueImpl.getInstance();
        } else {
            return FalseValueImpl.getInstance();
        }
    }

    public IntegerValue newIntegerValue(byte v) {
        return new IntValueImpl((int) v);
    }

    public IntegerValue newIntegerValue(short v) {
        return new IntValueImpl((int) v);
    }

    public IntegerValue newIntegerValue(int v) {
        return new IntValueImpl(v);
    }

    public IntegerValue newIntegerValue(long v) {
        return new LongValueImpl(v);
    }

    public IntegerValue newIntegerValue(BigInteger v) {
        return new BigIntegerValueImpl(v);
    }

    public FloatValue newFloatValue(float v) {
        return new FloatValueImpl(v);
    }

    public FloatValue newFloatValue(double v) {
        return new DoubleValueImpl(v);
    }

    public BinaryValue newBinaryValue(byte[] b) {
        return new BinaryValueImpl(ByteBuffer.wrap(b));
    }

    public BinaryValue newBinaryValue(byte[] b, int off, int len) {
        return new BinaryValueImpl(ByteBuffer.wrap(b, off, len));
    }

    public BinaryValue newBinaryValue(ByteBuffer bb) {
        return new BinaryValueImpl(bb.duplicate());
    }

    public StringValue newStringValue(String s) {
        return new StringValueImpl(s);
    }

    public StringValue newRawStringValue(byte[] b) {
        return new RawStringValueImpl(ByteBuffer.wrap(b));
    }

    public StringValue newRawStringValue(byte[] b, int off, int len) {
        return new RawStringValueImpl(ByteBuffer.wrap(b, off, len));
    }

    public StringValue newRawStringValue(ByteBuffer bb) {
        return new RawStringValueImpl(bb.duplicate());
    }

    public ArrayValue newArrayValue(List<? extends Value> list) {
        if (list.isEmpty()) {
            return ArrayValueImpl.getEmptyArrayInstance();
        }
        Value[] array = list.toArray(new Value[list.size()]);
        return new ArrayValueImpl(array);
    }

    public ArrayValue newArrayValue(Value[] array) {
        if (array.length == 0) {
            return ArrayValueImpl.getEmptyArrayInstance();
        }
        return new ArrayValueImpl(array);
    }

    public <K extends Value, V extends Value>
            MapValue newMapValue(Map<K, V> map) {
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

    public MapValue newMapValue(Value[] kvs) {
        if (kvs.length == 0) {
            return ArrayMapValueImpl.getEmptyMapInstance();
        }
        return new ArrayMapValueImpl(kvs);
    }
*/
}
