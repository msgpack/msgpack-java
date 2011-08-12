package org.msgpack.type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Converter;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;


public class TestEquals extends TestSet {
    @Override
    public void testBoolean(boolean v) throws Exception {
        Value a = ValueFactory.booleanValue(v);
        Value b = ValueFactory.booleanValue(v);
        testEquals(a, b);
    }

    @Override
    public void testBooleanArray(boolean[] v) throws Exception {
        Value[] vs1 = new Value[v.length];
        Value[] vs2 = new Value[v.length];
        for(int i=0; i < v.length; i++) {
            vs1[i] = ValueFactory.booleanValue(v[i]);
            vs2[i] = ValueFactory.booleanValue(v[i]);
        }
        Value v1 = ValueFactory.arrayValue(vs1);
        Value v2 = ValueFactory.arrayValue(vs2);
        testEquals(v1, v2);
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
        Value v1 = ValueFactory.rawValue(v);
        Value v2 = ValueFactory.rawValue(v);
        testEquals(v1, v2);
    }

    @Override
    public void testString(String v) throws Exception {
        Value v1 = ValueFactory.rawValue(v);
        Value v2 = ValueFactory.rawValue(v);
        testEquals(v1, v2);
    }

    @Override
    public void testFloat(float v) throws Exception {
        Value v1 = ValueFactory.floatValue(v);
        Value v2 = ValueFactory.floatValue(v);
        testEquals(v1, v2);
    }

    @Override
    public void testDouble(double v) throws Exception {
        Value v1 = ValueFactory.floatValue(v);
        Value v2 = ValueFactory.floatValue(v);
        testEquals(v1, v2);
    }

    @Test
    public void testMapOrder() throws Exception {
        Value v1 = ValueFactory.mapValue(new Value[] {
            ValueFactory.rawValue("k0"), ValueFactory.nilValue(),
            ValueFactory.rawValue("k1"), ValueFactory.rawValue("v1"),
            ValueFactory.rawValue("k2"), ValueFactory.rawValue("v2"),
            ValueFactory.rawValue("k3"), ValueFactory.rawValue("v3"),
            ValueFactory.rawValue("k4"), ValueFactory.rawValue("v4"),
            ValueFactory.rawValue("k5"), ValueFactory.rawValue("v5"),
            ValueFactory.rawValue("k6"), ValueFactory.rawValue("v6"),
            ValueFactory.rawValue("k7"), ValueFactory.rawValue("v7"),
            ValueFactory.rawValue("k8"), ValueFactory.rawValue("v8"),
            ValueFactory.rawValue("k9"), ValueFactory.rawValue("v9"),
            ValueFactory.rawValue("k10"), ValueFactory.rawValue("v10"),
            ValueFactory.rawValue("k11"), ValueFactory.rawValue("v11"),
            ValueFactory.rawValue("k12"), ValueFactory.rawValue("v12"),
            ValueFactory.rawValue("k13"), ValueFactory.rawValue("v13"),
            ValueFactory.rawValue("k14"), ValueFactory.rawValue("v14"),
            ValueFactory.rawValue("k15"), ValueFactory.rawValue("v15"),
            ValueFactory.rawValue("k16"), ValueFactory.rawValue("v16"),
            ValueFactory.rawValue("k17"), ValueFactory.rawValue("v17"),
            ValueFactory.rawValue("k18"), ValueFactory.rawValue("v18"),
            ValueFactory.rawValue("k19"), ValueFactory.rawValue("v19"),
        });
        Value v2 = ValueFactory.mapValue(new Value[] {
            ValueFactory.rawValue("k3"), ValueFactory.rawValue("v3"),
            ValueFactory.rawValue("k11"), ValueFactory.rawValue("v11"),
            ValueFactory.rawValue("k4"), ValueFactory.rawValue("v4"),
            ValueFactory.rawValue("k10"), ValueFactory.rawValue("v10"),
            ValueFactory.rawValue("k5"), ValueFactory.rawValue("v5"),
            ValueFactory.rawValue("k6"), ValueFactory.rawValue("v6"),
            ValueFactory.rawValue("k15"), ValueFactory.rawValue("v15"),
            ValueFactory.rawValue("k7"), ValueFactory.rawValue("v7"),
            ValueFactory.rawValue("k14"), ValueFactory.rawValue("v14"),
            ValueFactory.rawValue("k8"), ValueFactory.rawValue("v8"),
            ValueFactory.rawValue("k13"), ValueFactory.rawValue("v13"),
            ValueFactory.rawValue("k9"), ValueFactory.rawValue("v9"),
            ValueFactory.rawValue("k12"), ValueFactory.rawValue("v12"),
            ValueFactory.rawValue("k0"), ValueFactory.nilValue(),
            ValueFactory.rawValue("k1"), ValueFactory.rawValue("v1"),
            ValueFactory.rawValue("k2"), ValueFactory.rawValue("v2"),
            ValueFactory.rawValue("k18"), ValueFactory.rawValue("v18"),
            ValueFactory.rawValue("k19"), ValueFactory.rawValue("v19"),
            ValueFactory.rawValue("k16"), ValueFactory.rawValue("v16"),
            ValueFactory.rawValue("k17"), ValueFactory.rawValue("v17"),
        });
        testEquals(v1, v2);
    }

    @Override
    public void testByte(byte v) throws Exception {
        testLong((long)v);
    }

    @Override
    public void testShort(short v) throws Exception {
        testLong((long)v);
    }

    @Override
    public void testInteger(int v) throws Exception {
        testLong((long)v);
    }

    @Override
    public void testLong(long v) throws Exception {
        testBigInteger(BigInteger.valueOf(v));
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
        if(compatibleWithByte(v)) {
            Value vt = ValueFactory.integerValue(v);
            Value vByte = ValueFactory.integerValue(v.byteValue());
            Value vShort = ValueFactory.integerValue(v.shortValue());
            Value vInt = ValueFactory.integerValue(v.intValue());
            Value vLong = ValueFactory.integerValue(v.longValue());
            Value vBigInteger = ValueFactory.integerValue(v);
            testEquals(vt, vByte);
            testEquals(vt, vShort);
            testEquals(vt, vInt);
            testEquals(vt, vLong);
            testEquals(vt, vBigInteger);
        }
        if(compatibleWithShort(v)) {
            Value vt = ValueFactory.integerValue(v);
            Value vShort = ValueFactory.integerValue(v.shortValue());
            Value vInt = ValueFactory.integerValue(v.intValue());
            Value vLong = ValueFactory.integerValue(v.longValue());
            Value vBigInteger = ValueFactory.integerValue(v);
            testEquals(vt, vShort);
            testEquals(vt, vInt);
            testEquals(vt, vLong);
            testEquals(vt, vBigInteger);
        }
        if(compatibleWithInt(v)) {
            Value vt = ValueFactory.integerValue(v);
            Value vInt = ValueFactory.integerValue(v.intValue());
            Value vLong = ValueFactory.integerValue(v.longValue());
            Value vBigInteger = ValueFactory.integerValue(v);
            testEquals(vt, vInt);
            testEquals(vt, vLong);
            testEquals(vt, vBigInteger);
        }
        if(compatibleWithLong(v)) {
            Value vt = ValueFactory.integerValue(v);
            Value vLong = ValueFactory.integerValue(v.longValue());
            Value vBigInteger = ValueFactory.integerValue(v);
            testEquals(vt, vLong);
            testEquals(vt, vBigInteger);
        }
        {
            Value vt = ValueFactory.integerValue(v);
            Value vInt = ValueFactory.integerValue(v.intValue());
            Value vBigInteger = ValueFactory.integerValue(v);
            testEquals(vt, vInt);
            testEquals(vt, vBigInteger);
        }
    }

    @Test
    public void testNull() {
        Value v1 = ValueFactory.nilValue();
        Value v2 = ValueFactory.nilValue();
        testEquals(v1, v2);
    }

    private boolean compatibleWithByte(long v) {
       return (long)Byte.MIN_VALUE <= v && v <= (long)Byte.MAX_VALUE;
    }

    private boolean compatibleWithShort(long v) {
       return (long)Short.MIN_VALUE <= v && v <= (long)Short.MAX_VALUE;
    }

    private boolean compatibleWithInt(long v) {
       return (long)Integer.MIN_VALUE <= v && v <= (long)Integer.MAX_VALUE;
    }

    private static BigInteger BYTE_MAX = BigInteger.valueOf((long)Byte.MAX_VALUE);
    private static BigInteger SHORT_MAX = BigInteger.valueOf((long)Short.MAX_VALUE);
    private static BigInteger INT_MAX = BigInteger.valueOf((long)Integer.MAX_VALUE);
    private static BigInteger LONG_MAX = BigInteger.valueOf((long)Long.MAX_VALUE);

    private static BigInteger BYTE_MIN = BigInteger.valueOf((long)Byte.MIN_VALUE);
    private static BigInteger SHORT_MIN = BigInteger.valueOf((long)Short.MIN_VALUE);
    private static BigInteger INT_MIN = BigInteger.valueOf((long)Integer.MIN_VALUE);
    private static BigInteger LONG_MIN = BigInteger.valueOf((long)Long.MIN_VALUE);

    protected boolean compatibleWithByte(BigInteger v) {
        if(v.compareTo(BYTE_MAX) > 0 || v.compareTo(BYTE_MIN) < 0) {
            return false;
        }
        return true;
    }

    protected boolean compatibleWithShort(BigInteger v) {
        if(v.compareTo(SHORT_MAX) > 0 || v.compareTo(SHORT_MIN) < 0) {
            return false;
        }
        return true;
    }

    protected boolean compatibleWithInt(BigInteger v) {
        if(v.compareTo(INT_MAX) > 0 || v.compareTo(INT_MIN) < 0) {
            return false;
        }
        return true;
    }

    protected boolean compatibleWithLong(BigInteger v) {
        if(v.compareTo(LONG_MAX) > 0 || v.compareTo(LONG_MIN) < 0) {
            return false;
        }
        return true;
    }

    protected void testEquals(final Value v1, final Value v2) {
        assertTrue(v2.equals(v1));
        assertTrue(v2.equals(new ProxyValue() {
            protected Value getValue() {
                return v1;
            }
        }));
        assertTrue(v1.equals(new ProxyValue() {
            protected Value getValue() {
                return v2;
            }
        }));
    }
}

