package org.msgpack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Random;

import org.junit.Ignore;


@Ignore
public class TestSet {

    public void testBoolean() throws Exception {
	testBoolean(false);
	testBoolean(true);
    }

    public void testBoolean(boolean v) throws Exception {
    }

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

    public void testByte(byte v) throws Exception {
    }

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

    public void testShort(short v) throws Exception {
    }

    public void testInteger() throws Exception {
	testInteger(0);
	testInteger(-1);
	testInteger(1);
	testInteger(Integer.MIN_VALUE);
	testInteger(Integer.MAX_VALUE);
	Random rand = new Random();
	for (int i = 0; i < 1000; i++) {
	    testInteger(rand.nextInt());
	}
    }

    public void testInteger(int v) throws Exception {
    }

    public void testLong() throws Exception {
	testLong(0);
	testLong(-1);
	testLong(1);
	testLong(Long.MIN_VALUE);
	testLong(Long.MAX_VALUE);
	Random rand = new Random();
	for (int i = 0; i < 1000; i++) {
	    testLong(rand.nextLong());
	}
    }

    public void testLong(long v) throws Exception {
    }

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

    public void testFloat(float v) throws Exception {
    }

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

    public void testDouble(double v) throws Exception {
    }

    public void testNil() throws Exception {
    }

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

    public void testBigInteger(BigInteger v) throws Exception {
    }

    public void testBigDecimal() throws Exception {
	testBigDecimal(BigDecimal.valueOf(0));
	testBigDecimal(BigDecimal.valueOf(-1));
	testBigDecimal(BigDecimal.valueOf(1));
	testBigDecimal(BigDecimal.valueOf(Integer.MIN_VALUE));
	testBigDecimal(BigDecimal.valueOf(Integer.MAX_VALUE));
	testBigDecimal(BigDecimal.valueOf(Long.MIN_VALUE));
	testBigDecimal(BigDecimal.valueOf(Long.MAX_VALUE));
	
	testBigInteger(BigInteger.valueOf(0));
	testBigInteger(BigInteger.valueOf(-1));
	testBigInteger(BigInteger.valueOf(1));
	testBigInteger(BigInteger.valueOf(Integer.MIN_VALUE));
	testBigInteger(BigInteger.valueOf(Integer.MAX_VALUE));
	testBigInteger(BigInteger.valueOf(Long.MIN_VALUE));
	testBigInteger(BigInteger.valueOf(Long.MAX_VALUE));
	// TODO
    }

    public void testBigDecimal(BigDecimal v) throws Exception {
    }

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
		}
		testString(sb.toString());
	    }
	}
	// medium size string
	{
	    for (int i = 0; i < 100; i++) {
		sb = new StringBuilder();
		len = (int) Math.random() % 100 + (1 << 15);
		for (int j = 0; j < len; j++) {
		    sb.append('a' + ((int) Math.random()) & 26);
		}
		testString(sb.toString());
	    }
	}
	// large size string
	{
	    for (int i = 0; i < 10; i++) {
		sb = new StringBuilder();
		len = (int) Math.random() % 100 + (1 << 31);
		for (int j = 0; j < len; j++) {
		    sb.append('a' + ((int) Math.random()) & 26);
		}
		testString(sb.toString());
	    }
	}
    }

    public void testString(String v) throws Exception {
    }

    public void testByteArray() throws Exception {
	Random rand = new Random(System.currentTimeMillis());
	byte[] b0 = new byte[0];
	testByteArray(b0);
	byte[] b1 = new byte[10];
	rand.nextBytes(b1);
	testByteArray(b1);
	byte[] b2 = new byte[1024];
	rand.nextBytes(b2);
	testByteArray(b2);
    }

    public void testByteArray(byte[] v) throws Exception {
    }

    public void testByteBuffer() throws Exception {
	Random rand = new Random(System.currentTimeMillis());
	byte[] b0 = new byte[0];
	testByteBuffer(ByteBuffer.wrap(b0));
	byte[] b1 = new byte[10];
	rand.nextBytes(b1);
	testByteBuffer(ByteBuffer.wrap(b1));
	byte[] b2 = new byte[1024];
	rand.nextBytes(b2);
	testByteBuffer(ByteBuffer.wrap(b2));
    }

    public void testByteBuffer(ByteBuffer v) throws Exception {
    }

    public void testDate() throws Exception {
	Date d0 = new Date();
	testDate(d0);
    }

    public void testDate(Date v) throws Exception {
    }

}
