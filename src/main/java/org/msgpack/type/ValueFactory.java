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

public final class ValueFactory {
    public static NilValue nilValue() {
        return NilValue.getInstance();
    }

    public static BooleanValue booleanValue(boolean v) {
        if(v) {
            return TrueValueImpl.getInstance();
        } else {
            return FalseValueImpl.getInstance();
        }
    }

    public static IntegerValue integerValue(byte v) {
        return new IntValueImpl((int)v);
    }

    public static IntegerValue integerValue(short v) {
        return new IntValueImpl((int)v);
    }

    public static IntegerValue integerValue(int v) {
        return new IntValueImpl(v);
    }

    public static IntegerValue integerValue(long v) {
        return new LongValueImpl(v);
    }

    public static IntegerValue integerValue(BigInteger v) {
        return new BigIntegerValueImpl(v);
    }

    public static FloatValue floatValue(float v) {
        return new FloatValueImpl(v);
    }

    public static FloatValue floatValue(double v) {
        return new DoubleValueImpl(v);
    }

    public static RawValue rawValue() {
        return ByteArrayRawValueImpl.getEmptyInstance();
    }

    public static RawValue rawValue(byte[] b) {
        return rawValue(b, false);
    }

    public static RawValue rawValue(byte[] b, boolean gift) {
        return new ByteArrayRawValueImpl(b, gift);
    }

    public static RawValue rawValue(byte[] b, int off, int len) {
        return new ByteArrayRawValueImpl(b, off, len);
    }

    public static RawValue rawValue(String s) {
        return new StringRawValueImpl(s);
    }

    public static ArrayValue arrayValue() {
        return ArrayValueImpl.getEmptyInstance();
    }

    public static ArrayValue arrayValue(Value[] array) {
        if(array.length == 0) {
            // TODO EmptyArrayValueImpl?
            return ArrayValueImpl.getEmptyInstance();
        }
        return arrayValue(array, false);
    }

    public static ArrayValue arrayValue(Value[] array, boolean gift) {
        if(array.length == 0) {
            // TODO EmptyArrayValueImpl?
            return ArrayValueImpl.getEmptyInstance();
        }
        return new ArrayValueImpl(array, gift);
    }

    public static MapValue mapValue() {
        return SequentialMapValueImpl.getEmptyInstance();
    }

    public static MapValue mapValue(Value[] kvs) {
        if(kvs.length == 0) {
            // TODO EmptyMapValueImpl?
            return SequentialMapValueImpl.getEmptyInstance();
        }
        return mapValue(kvs, false);
    }

    public static MapValue mapValue(Value[] kvs, boolean gift) {
        if(kvs.length == 0) {
            // TODO EmptyMapValueImpl?
            return SequentialMapValueImpl.getEmptyInstance();
        }
        return new SequentialMapValueImpl(kvs, gift);
    }

    //TODO
    //public static Value get(Object obj) {
    //    return new Unconverter().pack(obj).getResult();
    //}

    private ValueFactory() { }
}

