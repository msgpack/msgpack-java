//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
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

import java.math.BigInteger;
import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;

class IntValueImpl extends IntegerValue {
    private int value;

    IntValueImpl(int value) {
        this.value = value;
    }

    private static int BYTE_MAX = (int)Byte.MAX_VALUE;
    private static int SHORT_MAX = (int)Short.MAX_VALUE;

    private static int BYTE_MIN = (int)Byte.MIN_VALUE;
    private static int SHORT_MIN = (int)Short.MIN_VALUE;

    public byte getByte() {
        if(value > BYTE_MAX || value < BYTE_MIN) {
            throw new MessageTypeException();  // TODO message
        }
        return (byte)value;
    }

    public short getShort() {
        if(value > SHORT_MAX || value < SHORT_MIN) {
            throw new MessageTypeException();  // TODO message
        }
        return (short)value;
    }

    public int getInt() {
        return value;
    }

    public long getLong() {
        return value;
    }

    public BigInteger getBigInteger() {
        return BigInteger.valueOf((long)value);
    }

    public byte byteValue() {
        return (byte)value;
    }

    public short shortValue() {
        return (short)value;
    }

    public int intValue() {
        return value;
    }

    public long longValue() {
        return (long)value;
    }

    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf((long)value);
    }

    public float floatValue() {
        return (float)value;
    }

    public double doubleValue() {
        return (double)value;
    }

    public void writeTo(Packer pk) throws IOException {
        pk.write(value);
    }

    // TODO compareTo

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if(!v.isInteger()) {
            return false;
        }

        try {
            // TODO
            return value == v.asIntegerValue().getInt();
        } catch (MessageTypeException ex) {
            return false;
        }
    }

    public int hashCode() {
        return value;
    }

    public String toString() {
        return Integer.toString(value);
    }

    public StringBuilder toString(StringBuilder sb) {
        return sb.append(Integer.toString(value));
    }
}

