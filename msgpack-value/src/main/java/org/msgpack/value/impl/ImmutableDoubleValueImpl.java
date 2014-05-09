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

import java.io.IOException;
import java.math.BigInteger;
import java.math.BigDecimal;

import org.msgpack.value.Value;
import org.msgpack.value.ImmutableFloatValue;
import org.msgpack.core.ValueType;
import org.msgpack.core.MessagePacker;

public class ImmutableDoubleValueImpl
        extends AbstractImmutableValue implements ImmutableFloatValue {
    private final double value;

    public ImmutableDoubleValueImpl(double value) {
        this.value = value;
    }

    @Override
    public ValueType getType() {
        return ValueType.FLOAT;
    }

    @Override
    public byte byteValue() {
        return (byte) value;
    }

    @Override
    public short shortValue() {
        return (short) value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public BigInteger bigIntegerValue() {
        return new BigDecimal(value).toBigInteger();
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
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
        if (!v.isFloatValue()) {
            return false;
        }
        return value == v.asFloatValue().doubleValue();
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packDouble(value);
    }

    @Override
    public int hashCode() {
        long v = Double.doubleToLongBits(value);
        return (int) (v ^ (v >>> 32));
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}

