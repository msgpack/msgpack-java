//
// MessagePack for Java
//
// Copyright (C) 2009-2010 FURUHASHI Sadayuki
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

import java.math.BigInteger;
import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;

class LongValueImpl extends IntegerValue {
    private long value;

    LongValueImpl(long value) {
        this.value = value;
    }

    private static long BYTE_MAX = (long)Byte.MAX_VALUE;
    private static long SHORT_MAX = (long)Short.MAX_VALUE;
    private static long INT_MAX = (long)Integer.MAX_VALUE;

    private static long BYTE_MIN = (long)Byte.MIN_VALUE;
    private static long SHORT_MIN = (long)Short.MIN_VALUE;
    private static long INT_MIN = (long)Integer.MIN_VALUE;

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
        if(value > INT_MAX || value < INT_MIN) {
            throw new MessageTypeException();  // TODO message
        }
        return (int)value;
    }

    public long getLong() {
        return value;
    }

    public BigInteger getBigInteger() {
        return BigInteger.valueOf(value);
    }

    public byte byteValue() {
        return (byte)value;
    }

    public short shortValue() {
        return (short)value;
    }

    public int intValue() {
        return (int)value;
    }

    public long longValue() {
        return value;
    }

    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf(value);
    }

    public float floatValue() {
        return (float)value;
    }

    public double doubleValue() {
        return (double)value;
    }

    public void writeTo(Packer pk) throws IOException {
        pk.writeLong(value);
    }

    // TODO compareTo

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof IntegerValue)) {
            return false;
        }

        try {
            // TODO
            return value == ((IntegerValue) o).getLong();
        } catch (MessageTypeException ex) {
            return false;
        }
    }

    public int hashCode() {
        if(INT_MIN <= value && value <= INT_MAX) {
            return (int)value;
        } else {
            return (int)(value^(value>>>32));
        }
    }

    public String toString() {
        return Long.toString(value);
    }

    public StringBuilder toString(StringBuilder sb) {
        return sb.append(Long.toString(value));
    }
}

