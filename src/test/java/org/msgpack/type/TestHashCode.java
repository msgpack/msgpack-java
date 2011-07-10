package org.msgpack.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class TestHashCode {
    @Test
    public void testBoolean() {
        Value f = ValueFactory.booleanValue(false);
        Value t = ValueFactory.booleanValue(false);
        assertNotSame(f.hashCode(), t.hashCode());
    }

    @Test
    public void testFloat() {
        Value f = ValueFactory.floatValue(0.0);
        Value d = ValueFactory.floatValue(0.0);
        assertEquals(f.hashCode(), d.hashCode());
    }

    @Test
    public void testRaw() throws UnsupportedEncodingException {
        testRaw("");
        testRaw("a");
        testRaw("あ");
        testRaw("𠀋");
    }

    private void testRaw(String s) throws UnsupportedEncodingException {
        Value v1 = ValueFactory.rawValue(s);
        Value v2 = ValueFactory.rawValue(s.getBytes("UTF-8"));
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    public void testInteger() {
        testInteger(0);
        testInteger(-1);
        testInteger(1);
        testInteger(Byte.MIN_VALUE);
        testInteger(Byte.MAX_VALUE);
        testInteger(Short.MIN_VALUE);
        testInteger(Short.MAX_VALUE);
        testInteger(Integer.MIN_VALUE);
        testInteger(Integer.MAX_VALUE);
        testInteger(Long.MIN_VALUE);
        testInteger(Long.MAX_VALUE);
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            testInteger(rand.nextLong());
        }
    }

    private void testInteger(long a) {
        if(compatibleWithByte(a)) {
            Value v1 = ValueFactory.integerValue(a);
            Value v2 = ValueFactory.integerValue((byte)a);
            assertEquals(v1, v2);
            assertEquals(v1.hashCode(), v2.hashCode());
        }
        if(compatibleWithShort(a)) {
            Value v1 = ValueFactory.integerValue(a);
            Value v2 = ValueFactory.integerValue((short)a);
            assertEquals(v1, v2);
            assertEquals(v1.hashCode(), v2.hashCode());
        }
        if(compatibleWithInt(a)) {
            Value v1 = ValueFactory.integerValue(a);
            Value v2 = ValueFactory.integerValue((int)a);
            assertEquals(v1, v2);
            assertEquals(v1.hashCode(), v2.hashCode());
        }
        {
            Value v1 = ValueFactory.integerValue(a);
            Value v2 = ValueFactory.integerValue(BigInteger.valueOf(a));
            assertEquals(v1, v2);
            assertEquals(v1.hashCode(), v2.hashCode());
        }
    }

    private boolean compatibleWithByte(long a) {
       return (long)Byte.MIN_VALUE <= a && a <= (long)Byte.MAX_VALUE;
    }

    private boolean compatibleWithShort(long a) {
       return (long)Short.MIN_VALUE <= a && a <= (long)Short.MAX_VALUE;
    }

    private boolean compatibleWithInt(long a) {
       return (long)Integer.MIN_VALUE <= a && a <= (long)Integer.MAX_VALUE;
    }
}

