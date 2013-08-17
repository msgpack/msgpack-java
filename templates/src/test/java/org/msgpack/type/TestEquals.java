package org.msgpack.type;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;
import org.msgpack.TestSet;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

public class TestEquals extends TestSet {
    @Override
    public void testBoolean(boolean v) throws Exception {
        Value a = ValueFactory.createBooleanValue(v);
        Value b = ValueFactory.createBooleanValue(v);
        testEquals(a, b);
    }

    @Override
    public void testBooleanArray(boolean[] v) throws Exception {
        Value[] vs1 = new Value[v.length];
        Value[] vs2 = new Value[v.length];
        for (int i = 0; i < v.length; i++) {
            vs1[i] = ValueFactory.createBooleanValue(v[i]);
            vs2[i] = ValueFactory.createBooleanValue(v[i]);
        }
        Value v1 = ValueFactory.createArrayValue(vs1);
        Value v2 = ValueFactory.createArrayValue(vs2);
        testEquals(v1, v2);
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
        Value v1 = ValueFactory.createRawValue(v);
        Value v2 = ValueFactory.createRawValue(v);
        testEquals(v1, v2);
    }

    @Override
    public void testString(String v) throws Exception {
        Value v1 = ValueFactory.createRawValue(v);
        Value v2 = ValueFactory.createRawValue(v);
        testEquals(v1, v2);
    }

    @Override
    public void testFloat(float v) throws Exception {
        Value v1 = ValueFactory.createFloatValue(v);
        Value v2 = ValueFactory.createFloatValue(v);
        testEquals(v1, v2);
    }

    @Override
    public void testDouble(double v) throws Exception {
        Value v1 = ValueFactory.createFloatValue(v);
        Value v2 = ValueFactory.createFloatValue(v);
        testEquals(v1, v2);
    }

    @Test
    public void testMapOrder() throws Exception {
        Value v1 = ValueFactory.createMapValue(new Value[] {
                ValueFactory.createRawValue("k0"),
                ValueFactory.createNilValue(),
                ValueFactory.createRawValue("k1"),
                ValueFactory.createRawValue("v1"),
                ValueFactory.createRawValue("k2"),
                ValueFactory.createRawValue("v2"),
                ValueFactory.createRawValue("k3"),
                ValueFactory.createRawValue("v3"),
                ValueFactory.createRawValue("k4"),
                ValueFactory.createRawValue("v4"),
                ValueFactory.createRawValue("k5"),
                ValueFactory.createRawValue("v5"),
                ValueFactory.createRawValue("k6"),
                ValueFactory.createRawValue("v6"),
                ValueFactory.createRawValue("k7"),
                ValueFactory.createRawValue("v7"),
                ValueFactory.createRawValue("k8"),
                ValueFactory.createRawValue("v8"),
                ValueFactory.createRawValue("k9"),
                ValueFactory.createRawValue("v9"),
                ValueFactory.createRawValue("k10"),
                ValueFactory.createRawValue("v10"),
                ValueFactory.createRawValue("k11"),
                ValueFactory.createRawValue("v11"),
                ValueFactory.createRawValue("k12"),
                ValueFactory.createRawValue("v12"),
                ValueFactory.createRawValue("k13"),
                ValueFactory.createRawValue("v13"),
                ValueFactory.createRawValue("k14"),
                ValueFactory.createRawValue("v14"),
                ValueFactory.createRawValue("k15"),
                ValueFactory.createRawValue("v15"),
                ValueFactory.createRawValue("k16"),
                ValueFactory.createRawValue("v16"),
                ValueFactory.createRawValue("k17"),
                ValueFactory.createRawValue("v17"),
                ValueFactory.createRawValue("k18"),
                ValueFactory.createRawValue("v18"),
                ValueFactory.createRawValue("k19"),
                ValueFactory.createRawValue("v19"), });
        Value v2 = ValueFactory.createMapValue(new Value[] {
                ValueFactory.createRawValue("k3"),
                ValueFactory.createRawValue("v3"),
                ValueFactory.createRawValue("k11"),
                ValueFactory.createRawValue("v11"),
                ValueFactory.createRawValue("k4"),
                ValueFactory.createRawValue("v4"),
                ValueFactory.createRawValue("k10"),
                ValueFactory.createRawValue("v10"),
                ValueFactory.createRawValue("k5"),
                ValueFactory.createRawValue("v5"),
                ValueFactory.createRawValue("k6"),
                ValueFactory.createRawValue("v6"),
                ValueFactory.createRawValue("k15"),
                ValueFactory.createRawValue("v15"),
                ValueFactory.createRawValue("k7"),
                ValueFactory.createRawValue("v7"),
                ValueFactory.createRawValue("k14"),
                ValueFactory.createRawValue("v14"),
                ValueFactory.createRawValue("k8"),
                ValueFactory.createRawValue("v8"),
                ValueFactory.createRawValue("k13"),
                ValueFactory.createRawValue("v13"),
                ValueFactory.createRawValue("k9"),
                ValueFactory.createRawValue("v9"),
                ValueFactory.createRawValue("k12"),
                ValueFactory.createRawValue("v12"),
                ValueFactory.createRawValue("k0"),
                ValueFactory.createNilValue(),
                ValueFactory.createRawValue("k1"),
                ValueFactory.createRawValue("v1"),
                ValueFactory.createRawValue("k2"),
                ValueFactory.createRawValue("v2"),
                ValueFactory.createRawValue("k18"),
                ValueFactory.createRawValue("v18"),
                ValueFactory.createRawValue("k19"),
                ValueFactory.createRawValue("v19"),
                ValueFactory.createRawValue("k16"),
                ValueFactory.createRawValue("v16"),
                ValueFactory.createRawValue("k17"),
                ValueFactory.createRawValue("v17"), });
        testEquals(v1, v2);
    }

    @Override
    public void testByte(byte v) throws Exception {
        testLong((long) v);
    }

    @Override
    public void testShort(short v) throws Exception {
        testLong((long) v);
    }

    @Override
    public void testInteger(int v) throws Exception {
        testLong((long) v);
    }

    @Override
    public void testLong(long v) throws Exception {
        testBigInteger(BigInteger.valueOf(v));
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
        if (compatibleWithByte(v)) {
            Value vt = ValueFactory.createIntegerValue(v);
            Value vByte = ValueFactory.createIntegerValue(v.byteValue());
            Value vShort = ValueFactory.createIntegerValue(v.shortValue());
            Value vInt = ValueFactory.createIntegerValue(v.intValue());
            Value vLong = ValueFactory.createIntegerValue(v.longValue());
            Value vBigInteger = ValueFactory.createIntegerValue(v);
            testEquals(vt, vByte);
            testEquals(vt, vShort);
            testEquals(vt, vInt);
            testEquals(vt, vLong);
            testEquals(vt, vBigInteger);
        }
        if (compatibleWithShort(v)) {
            Value vt = ValueFactory.createIntegerValue(v);
            Value vShort = ValueFactory.createIntegerValue(v.shortValue());
            Value vInt = ValueFactory.createIntegerValue(v.intValue());
            Value vLong = ValueFactory.createIntegerValue(v.longValue());
            Value vBigInteger = ValueFactory.createIntegerValue(v);
            testEquals(vt, vShort);
            testEquals(vt, vInt);
            testEquals(vt, vLong);
            testEquals(vt, vBigInteger);
        }
        if (compatibleWithInt(v)) {
            Value vt = ValueFactory.createIntegerValue(v);
            Value vInt = ValueFactory.createIntegerValue(v.intValue());
            Value vLong = ValueFactory.createIntegerValue(v.longValue());
            Value vBigInteger = ValueFactory.createIntegerValue(v);
            testEquals(vt, vInt);
            testEquals(vt, vLong);
            testEquals(vt, vBigInteger);
        }
        if (compatibleWithLong(v)) {
            Value vt = ValueFactory.createIntegerValue(v);
            Value vLong = ValueFactory.createIntegerValue(v.longValue());
            Value vBigInteger = ValueFactory.createIntegerValue(v);
            testEquals(vt, vLong);
            testEquals(vt, vBigInteger);
        }
        {
            Value vt = ValueFactory.createIntegerValue(v);
            Value vInt = ValueFactory.createIntegerValue(v.intValue());
            Value vBigInteger = ValueFactory.createIntegerValue(v);
            testEquals(vt, vInt);
            testEquals(vt, vBigInteger);
        }
    }

    @Test
    public void testNull() {
        Value v1 = ValueFactory.createNilValue();
        Value v2 = ValueFactory.createNilValue();
        testEquals(v1, v2);
    }

    @SuppressWarnings("unused")
    private boolean compatibleWithByte(long v) {
        return (long) Byte.MIN_VALUE <= v && v <= (long) Byte.MAX_VALUE;
    }

    @SuppressWarnings("unused")
    private boolean compatibleWithShort(long v) {
        return (long) Short.MIN_VALUE <= v && v <= (long) Short.MAX_VALUE;
    }

    @SuppressWarnings("unused")
    private boolean compatibleWithInt(long v) {
        return (long) Integer.MIN_VALUE <= v && v <= (long) Integer.MAX_VALUE;
    }

    private static BigInteger BYTE_MAX = BigInteger
            .valueOf((long) Byte.MAX_VALUE);
    private static BigInteger SHORT_MAX = BigInteger
            .valueOf((long) Short.MAX_VALUE);
    private static BigInteger INT_MAX = BigInteger
            .valueOf((long) Integer.MAX_VALUE);
    private static BigInteger LONG_MAX = BigInteger
            .valueOf((long) Long.MAX_VALUE);

    private static BigInteger BYTE_MIN = BigInteger
            .valueOf((long) Byte.MIN_VALUE);
    private static BigInteger SHORT_MIN = BigInteger
            .valueOf((long) Short.MIN_VALUE);
    private static BigInteger INT_MIN = BigInteger
            .valueOf((long) Integer.MIN_VALUE);
    private static BigInteger LONG_MIN = BigInteger
            .valueOf((long) Long.MIN_VALUE);

    protected boolean compatibleWithByte(BigInteger v) {
        if (v.compareTo(BYTE_MAX) > 0 || v.compareTo(BYTE_MIN) < 0) {
            return false;
        }
        return true;
    }

    protected boolean compatibleWithShort(BigInteger v) {
        if (v.compareTo(SHORT_MAX) > 0 || v.compareTo(SHORT_MIN) < 0) {
            return false;
        }
        return true;
    }

    protected boolean compatibleWithInt(BigInteger v) {
        if (v.compareTo(INT_MAX) > 0 || v.compareTo(INT_MIN) < 0) {
            return false;
        }
        return true;
    }

    protected boolean compatibleWithLong(BigInteger v) {
        if (v.compareTo(LONG_MAX) > 0 || v.compareTo(LONG_MIN) < 0) {
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
