package org.msgpack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void testBooleanArray() throws Exception {
        testBooleanArray(null);
        testBooleanArray(new boolean[0]);
        testBooleanArray(new boolean[] { true });
        testBooleanArray(new boolean[] { false });
        testBooleanArray(new boolean[] { true, false });
        Random rand = new Random();
        boolean[] v = new boolean[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextBoolean();
        }
        testBooleanArray(v);
    }

    public void testBooleanArray(boolean[] v) throws Exception {
    }

    public void testByte() throws Exception {
        testByte((byte) 0);
        testByte((byte) -1);
        testByte((byte) 1);
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

    public void testByteArray() throws Exception {
        testByteArray(null);
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

    public void testShortArray() throws Exception {
        testShortArray(null);
        testShortArray(new short[0]);
        testShortArray(new short[] { 0 });
        testShortArray(new short[] { -1 });
        testShortArray(new short[] { 1 });
        testShortArray(new short[] { 0, -1, 1 });
        testShortArray(new short[] { Short.MIN_VALUE });
        testShortArray(new short[] { Short.MAX_VALUE });
        testShortArray(new short[] { Short.MIN_VALUE, Short.MAX_VALUE });
        Random rand = new Random();
        byte[] bytes = new byte[2];
        short[] v = new short[100];
        for (int i = 0; i < v.length; ++i) {
            rand.nextBytes(bytes);
            v[i] = (short) ((bytes[0] << 8) | (bytes[1] & 0xff));
        }
        testShortArray(v);
    }

    public void testShortArray(short[] v) throws Exception {
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

    public void testIntegerArray() throws Exception {
        testIntegerArray(null);
        testIntegerArray(new int[0]);
        testIntegerArray(new int[] { 0 });
        testIntegerArray(new int[] { -1 });
        testIntegerArray(new int[] { 1 });
        testIntegerArray(new int[] { 0, -1, 1 });
        testIntegerArray(new int[] { Integer.MIN_VALUE });
        testIntegerArray(new int[] { Integer.MAX_VALUE });
        testIntegerArray(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE });
        Random rand = new Random();
        int[] v = new int[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextInt();
        }
        testIntegerArray(v);
    }

    public void testIntegerArray(int[] v) throws Exception {
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

    public void testLongArray() throws Exception {
        testLongArray(null);
        testLongArray(new long[0]);
        testLongArray(new long[] { 0 });
        testLongArray(new long[] { -1 });
        testLongArray(new long[] { 1 });
        testLongArray(new long[] { 0, -1, 1 });
        testLongArray(new long[] { Long.MIN_VALUE });
        testLongArray(new long[] { Long.MAX_VALUE });
        testLongArray(new long[] { Long.MIN_VALUE, Long.MAX_VALUE });
        Random rand = new Random();
        long[] v = new long[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextLong();
        }
        testLongArray(v);
    }

    public void testLongArray(long[] v) throws Exception {
    }

    public void testFloat() throws Exception {
        testFloat((float) 0.0);
        testFloat((float) -0.0);
        testFloat((float) 1.0);
        testFloat((float) -1.0);
        testFloat(Float.MAX_VALUE);
        testFloat(Float.MIN_VALUE);
        testFloat(Float.NaN);
        testFloat(Float.NEGATIVE_INFINITY);
        testFloat(Float.POSITIVE_INFINITY);
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            testFloat(rand.nextFloat());
        }
    }

    public void testFloat(float v) throws Exception {
    }

    public void testFloatArray() throws Exception {
        testFloatArray(null);
        testFloatArray(new float[0]);
        testFloatArray(new float[] { (float) 0.0 });
        testFloatArray(new float[] { (float) -0.0 });
        testFloatArray(new float[] { (float) -1.0 });
        testFloatArray(new float[] { (float) 1.0 });
        testFloatArray(new float[] { (float) 0.0, (float) -0.0, (float) -1.0, (float) 1.0 });
        testFloatArray(new float[] { Float.MAX_VALUE });
        testFloatArray(new float[] { Float.MIN_VALUE });
        testFloatArray(new float[] { Float.NaN });
        testFloatArray(new float[] { Float.NEGATIVE_INFINITY });
        testFloatArray(new float[] { Float.POSITIVE_INFINITY });
        testFloatArray(new float[] { Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY });
        Random rand = new Random();
        float[] v = new float[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextFloat();
        }
        testFloatArray(v);
    }

    public void testFloatArray(float[] v) throws Exception {
    }

    public void testDouble() throws Exception {
        testDouble((double) 0.0);
        testDouble((double) -0.0);
        testDouble((double) 1.0);
        testDouble((double) -1.0);
        testDouble(Double.MAX_VALUE);
        testDouble(Double.MIN_VALUE);
        testDouble(Double.NaN);
        testDouble(Double.NEGATIVE_INFINITY);
        testDouble(Double.POSITIVE_INFINITY);
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            testDouble(rand.nextDouble());
        }
    }

    public void testDouble(double v) throws Exception {
    }

    public void testDoubleArray() throws Exception {
        testDoubleArray(null);
        testDoubleArray(new double[0]);
        testDoubleArray(new double[] { (double) 0.0 });
        testDoubleArray(new double[] { (double) -0.0 });
        testDoubleArray(new double[] { (double) -1.0 });
        testDoubleArray(new double[] { (double) 1.0 });
        testDoubleArray(new double[] { (double) 0.0, (double) -0.0, (double) -1.0, (double) 1.0 });
        testDoubleArray(new double[] { Double.MAX_VALUE });
        testDoubleArray(new double[] { Double.MIN_VALUE });
        testDoubleArray(new double[] { Double.NaN });
        testDoubleArray(new double[] { Double.NEGATIVE_INFINITY });
        testDoubleArray(new double[] { Double.POSITIVE_INFINITY });
        testDoubleArray(new double[] { Double.MAX_VALUE, Double.MIN_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY });
        Random rand = new Random();
        double[] v = new double[100];
        for (int i = 0; i < v.length; ++i) {
            v[i] = rand.nextDouble();
        }
        testDoubleArray(v);
    }

    public void testDoubleArray(double[] v) throws Exception {
    }

    public void testNil() throws Exception {
    }

    public void testString() throws Exception {
        testString(null);
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

    public void testByteBuffer() throws Exception {
        testByteBuffer(null);
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

    public void testList() throws Exception {
        testList(null, Integer.class);
        List<Integer> list0 = new ArrayList<Integer>();
        testList(list0, Integer.class);
        List<Integer> list1 = new ArrayList<Integer>();
        Random rand1 = new Random();
        for (int i = 0; i < 10; ++i) {
            list1.add(rand1.nextInt());
        }
        testList(list1, Integer.class);
        List<String> list2 = new ArrayList<String>();
        Random rand2 = new Random();
        for (int i = 0; i < 100; ++i) {
            list2.add("xx" + rand2.nextInt());
        }
        testList(list2, String.class);
        List<String> list3 = new ArrayList<String>();
        Random rand3 = new Random();
        for (int i = 0; i < 1000; ++i) {
            list3.add("xx" + rand3.nextInt());
        }
        testList(list3, String.class);
    }

    public <E> void testList(List<E> v, Class<E> elementClass) throws Exception {
    }

    public void testMap() throws Exception {
        testMap(null, Integer.class, Integer.class);
        Map<Integer, Integer> map0 = new HashMap<Integer, Integer>();
        testMap(map0, Integer.class, Integer.class);
        Map<Integer, Integer> map1 = new HashMap<Integer, Integer>();
        Random rand1 = new Random();
        for (int i = 0; i < 10; ++i) {
            map1.put(rand1.nextInt(), rand1.nextInt());
        }
        testMap(map1, Integer.class, Integer.class);
        Map<String, Integer> map2 = new HashMap<String, Integer>();
        Random rand2 = new Random();
        for (int i = 0; i < 100; ++i) {
            map2.put("xx" + rand2.nextInt(), rand2.nextInt());
        }
        testMap(map2, String.class, Integer.class);
        Map<String, Integer> map3 = new HashMap<String, Integer>();
        Random rand3= new Random();
        for (int i = 0; i < 1000; ++i) {
            map3.put("xx" + rand3.nextInt(), rand3.nextInt());
        }
        testMap(map3, String.class, Integer.class);
    }

    public <K, V> void testMap(Map<K, V> v, Class<K> keyElementClass, Class<V> valueElementClass) throws Exception {
    }

    public void testBigInteger() throws Exception {
        testBigInteger(null);
        testBigInteger(BigInteger.valueOf(0));
        testBigInteger(BigInteger.valueOf(-1));
        testBigInteger(BigInteger.valueOf(1));
        testBigInteger(BigInteger.valueOf(128l));
        testBigInteger(BigInteger.valueOf(512l));
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
        testBigDecimal(null);
        testBigDecimal(BigDecimal.valueOf(0));
        testBigDecimal(BigDecimal.valueOf(-1));
        testBigDecimal(BigDecimal.valueOf(1));
        testBigDecimal(BigDecimal.valueOf(Integer.MIN_VALUE));
        testBigDecimal(BigDecimal.valueOf(Integer.MAX_VALUE));
        testBigDecimal(BigDecimal.valueOf(Long.MIN_VALUE));
        testBigDecimal(BigDecimal.valueOf(Long.MAX_VALUE));
    }

    public void testBigDecimal(BigDecimal v) throws Exception {
    }

    public void testDate() throws Exception {
        testDate(null);
        Date d0 = new Date();
        testDate(d0);
    }

    public void testDate(Date v) throws Exception {
    }

    public void testCharacter() throws Exception {
        testCharacter(null);
        testCharacter('a');
        testCharacter('あ');
        testCharacter((char) 1);
        testCharacter(Character.MIN_VALUE);
        testCharacter(Character.MAX_VALUE);
    }

    public void testCharacter(Character v) throws Exception {
    }

}
