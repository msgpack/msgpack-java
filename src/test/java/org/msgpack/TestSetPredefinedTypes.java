package org.msgpack;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;


@Ignore
public abstract class TestSetPredefinedTypes {

    @Test
    public void testBoolean() throws Exception {
	testBoolean(false);
	testBoolean(true);
    }

    public abstract void testBoolean(boolean v) throws Exception;

    @Test
    public void testByte() throws Exception {
	testShort((byte) 0);
	testShort((byte) -1);
	testShort((byte) 1);
	testByte(Byte.MIN_VALUE);
	testByte(Byte.MAX_VALUE);
	byte[] bytes = new byte[1000];
	Random rand = new Random();
	rand.nextBytes(bytes);
	for (int i = 0; i < bytes.length; ++i) {
	    testByte(bytes[i]);
	}
    }

    public abstract void testByte(byte v) throws Exception;

    @Test
    public void testShort() throws Exception {
	testShort((short) 0);
	testShort((short) -1);
	testShort((short) 1);
	testShort(Short.MIN_VALUE);
	testShort(Short.MAX_VALUE);
	Random rand = new Random();
	byte[] bytes = new byte[2000];
	rand.nextBytes(bytes);
	for (int i = 0; i < bytes.length; i = i + 2) {
	    testShort((short) ((bytes[i] << 8) | (bytes[i + 1] & 0xff)));
	}
    }

    public abstract void testShort(short v) throws Exception;

    @Test
    public void testInt() throws Exception {
	testInt(0);
	testInt(-1);
	testInt(1);
	testInt(Integer.MIN_VALUE);
	testInt(Integer.MAX_VALUE);
	Random rand = new Random();
	for (int i = 0; i < 1000; i++) {
	    testInt(rand.nextInt());
	}
    }

    public abstract void testInt(int v) throws Exception;

    @Test
    public void testLong() throws Exception {
	testLong(0);
	testLong(-1);
	testLong(1);
	testLong(Long.MIN_VALUE);
	testLong(Long.MAX_VALUE);
	testLong(Long.MIN_VALUE);
	testLong(Long.MAX_VALUE);
	Random rand = new Random();
	for (int i = 0; i < 1000; i++) {
	    testLong(rand.nextLong());
	}
    }

    public abstract void testLong(long v) throws Exception;

    @Test
    public void testFloat() throws Exception {
	testFloat((float) 0.0);
	testFloat((float) -0.0);
	testFloat((float) 1.0);
	testFloat((float) -1.0);
	testFloat((float) Float.MAX_VALUE);
	testFloat((float) Float.MIN_VALUE);
	testFloat((float) Float.NaN);
	testFloat((float) Float.NEGATIVE_INFINITY);
	testFloat((float) Float.POSITIVE_INFINITY);
	Random rand = new Random();
	for (int i = 0; i < 1000; i++) {
	    testFloat(rand.nextFloat());
	}
    }

    public abstract void testFloat(float v) throws Exception;

    @Test
    public void testDouble() throws Exception {
	testDouble((double) 0.0);
	testDouble((double) -0.0);
	testDouble((double) 1.0);
	testDouble((double) -1.0);
	testDouble((double) Double.MAX_VALUE);
	testDouble((double) Double.MIN_VALUE);
	testDouble((double) Double.NaN);
	testDouble((double) Double.NEGATIVE_INFINITY);
	testDouble((double) Double.POSITIVE_INFINITY);
	Random rand = new Random();
	for (int i = 0; i < 1000; i++) {
	    testDouble(rand.nextDouble());
	}
    }

    public abstract void testDouble(double v) throws Exception;

    @Test
    public abstract void testNil() throws Exception;

    @Test
    public void testBigInteger() throws Exception {
	testBigInteger(BigInteger.valueOf(0));
	testBigInteger(BigInteger.valueOf(-1));
	testBigInteger(BigInteger.valueOf(1));
	testBigInteger(BigInteger.valueOf(Integer.MIN_VALUE));
	testBigInteger(BigInteger.valueOf(Integer.MAX_VALUE));
	testBigInteger(BigInteger.valueOf(Long.MIN_VALUE));
	testBigInteger(BigInteger.valueOf(Long.MAX_VALUE));
	BigInteger max = BigInteger.valueOf(Long.MAX_VALUE).setBit(63);
	testBigInteger(max);
	Random rand = new Random();
	for (int i = 0; i < 1000; i++) {
	    testBigInteger(max.subtract(BigInteger.valueOf(Math.abs(rand.nextLong()))));
	}
    }

    public abstract void testBigInteger(BigInteger v) throws Exception;

    @Test
    public void testString() throws Exception {
	testString("");
	testString("a");
	testString("ab");
	testString("abc");
	StringBuilder sb;
	int len;
	// small size string
	{
	    for (int i = 0; i < 100; i++) {
		sb = new StringBuilder();
		len = (int) Math.random() % 31 + 1;
		for (int j = 0; j < len; j++) {
		    sb.append('a' + ((int) Math.random()) & 26);
		    testString(sb.toString());
		}
	    }
	}
	// medium size string
	{
	    for (int i = 0; i < 100; i++) {
		sb = new StringBuilder();
		len = (int) Math.random() % 100 + (1 << 15);
		for (int j = 0; j < len; j++) {
		    sb.append('a' + ((int) Math.random()) & 26);
		    testString(sb.toString());
		}
	    }
	}
	// large size string
	{
	    for (int i = 0; i < 10; i++) {
		sb = new StringBuilder();
		len = (int) Math.random() % 100 + (1 << 31);
		for (int j = 0; j < len; j++) {
		    sb.append('a' + ((int) Math.random()) & 26);
		    testString(sb.toString());
		}
	    }
	}
    }

    public abstract void testString(String v) throws Exception;

    @Test
    public void testByteArray() throws Exception {
	testByteArray(new byte[0]);
	Random rand = new Random();
	// small size byte array
	byte[] bytes;
	int len;
	{
	    len = (int) Math.random() % 31 + 1;
	    bytes = new byte[len];
	    for (int i = 0; i < 100; ++i) {
		rand.nextBytes(bytes);
		testByteArray(bytes);
	    }
	}
	// medium size byte array
	{
	    len = (int) Math.random() % 100 + (1 << 15);
	    bytes = new byte[len];
	    for (int i = 0; i < 100; ++i) {
		rand.nextBytes(bytes);
		testByteArray(bytes);
	    }
	}
	// large size byte array
	{
	    len = (int) Math.random() % 100 + (1 << 31);
	    bytes = new byte[len];
	    for (int i = 0; i < 100; ++i) {
		rand.nextBytes(bytes);
		testByteArray(bytes);
	    }
	}
    }

    public abstract void testByteArray(byte[] v) throws Exception;
}
