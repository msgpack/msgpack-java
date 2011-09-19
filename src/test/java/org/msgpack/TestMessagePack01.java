package org.msgpack;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.msgpack.type.Value;


public class TestMessagePack01 {

    @Test
    public void testBigIntegerBufferPackBufferUnpack() throws Exception {
	new TestBigIntegerBufferPackBufferUnpack().testBigInteger();
    }

    @Test
    public void testBigIntegerBufferPackConvert() throws Exception {
	new TestBigIntegerBufferPackConvert().testBigInteger();
    }

    @Test
    public void testBigIntegerBufferPackUnpack() throws Exception {
	new TestBigIntegerBufferPackUnpack().testBigInteger();
    }

    @Test
    public void testBigIntegerPackConvert() throws Exception {
	new TestBigIntegerPackConvert().testBigInteger();
    }

    @Test
    public void testBigIntegerPackUnpack() throws Exception {
	new TestBigIntegerPackUnpack().testBigInteger();
    }

    @Test
    public void testBigIntegerUnconvertConvert() throws Exception {
	new TestBigIntegerUnconvertConvert().testBigInteger();
    }
    
    @Test
    public void testStringBufferPackBufferUnpack() throws Exception {
	new TestStringBufferPackBufferUnpack().testString();
    }

    @Test
    public void testStringBufferPackConvert() throws Exception {
	new TestStringBufferPackConvert().testString();
    }

    @Test
    public void testStringBufferPackUnpack() throws Exception {
	new TestStringBufferPackUnpack().testString();
    }

    @Test
    public void testStringPackConvert() throws Exception {
	new TestStringPackConvert().testString();
    }

    @Test
    public void testStringPackUnpack() throws Exception {
	new TestStringPackUnpack().testString();
    }

    @Test
    public void testStringUnconvertConvert() throws Exception {
	new TestStringUnconvertConvert().testString();
    }

    @Test
    public void testByteArrayBufferPackBufferUnpack() throws Exception {
	new TestByteArrayBufferPackBufferUnpack().testByteArray();
    }

    @Test
    public void testByteArrayBufferPackConvert() throws Exception {
	new TestByteArrayBufferPackConvert().testByteArray();
    }

    @Test
    public void testByteArrayBufferPackUnpack() throws Exception {
	new TestByteArrayBufferPackUnpack().testByteArray();
    }

    @Test
    public void testByteArrayPackConvert() throws Exception {
	new TestByteArrayPackConvert().testByteArray();
    }

    @Test
    public void testByteArrayPackUnpack() throws Exception {
	new TestByteArrayPackUnpack().testByteArray();
    }

    @Test
    public void testByteArrayUnconvertConvert() throws Exception {
	new TestByteArrayUnconvertConvert().testByteArray();
    }

    @Ignore @Test // FIXME #MN *MUST* fix it in this version
    public void testListPackUnpack() throws Exception {
	new TestListPackUnpack().testList();
    }

    @Ignore @Test // FIXME #MN *MUST* fix it in this version
    public void testMapPackUnpack() throws Exception {
	new TestMapPackUnpack().testMap();
    }

    public static class TestBigIntegerBufferPackBufferUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testBigInteger() throws Exception {
	    super.testBigInteger();
	}

	@Override
	public void testBigInteger(BigInteger v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    BigInteger ret = msgpack.read(bytes, BigInteger.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestBigIntegerBufferPackConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testBigInteger() throws Exception {
	    super.testBigInteger();
	}

	@Override
	public void testBigInteger(BigInteger v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    BigInteger ret = msgpack.convert(value, BigInteger.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestBigIntegerBufferPackUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testBigInteger() throws Exception {
	    super.testBigInteger();
	}

	@Override
	public void testBigInteger(BigInteger v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    BigInteger ret = msgpack.read(in, BigInteger.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestBigIntegerPackBufferUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testBigInteger() throws Exception {
	    super.testBigInteger();
	}

	@Override
	public void testBigInteger(BigInteger v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    BigInteger ret = msgpack.read(bytes, BigInteger.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestBigIntegerPackConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testBigInteger() throws Exception {
	    super.testBigInteger();
	}

	@Override
	public void testBigInteger(BigInteger v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    BigInteger ret = msgpack.convert(value, BigInteger.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestBigIntegerPackUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testBigInteger() throws Exception {
	    super.testBigInteger();
	}

	@Override
	public void testBigInteger(BigInteger v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    BigInteger ret = msgpack.read(in, BigInteger.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestBigIntegerUnconvertConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testBigInteger() throws Exception {
	    super.testBigInteger();
	}

	@Override
	public void testBigInteger(BigInteger v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    BigInteger ret = msgpack.convert(value, BigInteger.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestStringBufferPackBufferUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    String ret = msgpack.read(bytes, String.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestStringBufferPackConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    String ret = msgpack.convert(value, String.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestStringBufferPackUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    String ret = msgpack.read(in, String.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestStringPackBufferUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    String ret = msgpack.read(bytes, String.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestStringPackConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    String ret = msgpack.convert(value, String.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestStringPackUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    String ret = msgpack.read(in, String.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestStringUnconvertConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testString() throws Exception {
	    super.testString();
	}

	@Override
	public void testString(String v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    String ret = msgpack.convert(value, String.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestByteArrayBufferPackBufferUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    byte[] ret = msgpack.read(bytes, byte[].class);
	    assertArrayEquals(v, ret);
	}
    }

    public static class TestByteArrayBufferPackConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    byte[] ret = msgpack.convert(value, byte[].class);
	    assertArrayEquals(v, ret);
	}
    }

    public static class TestByteArrayBufferPackUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    byte[] ret = msgpack.read(in, byte[].class);
	    assertArrayEquals(v, ret);
	}
    }

    public static class TestByteArrayPackBufferUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    byte[] ret = msgpack.read(bytes, byte[].class);
	    assertArrayEquals(v, ret);
	}
    }

    public static class TestByteArrayPackConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    byte[] ret = msgpack.convert(value, byte[].class);
	    assertArrayEquals(v, ret);
	}
    }

    public static class TestByteArrayPackUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    byte[] ret = msgpack.read(in, byte[].class);
	    assertArrayEquals(v, ret);
	}
    }

    public static class TestByteArrayUnconvertConvert extends org.msgpack.TestSet {
	@Test @Override
	public void testByteArray() throws Exception {
	    super.testByteArray();
	}

	@Override
	public void testByteArray(byte[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    byte[] ret = msgpack.convert(value, byte[].class);
	    assertArrayEquals(v, ret);
	}
    }

    public static class TestListPackUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testList() throws Exception {
	    super.testList();
	}

	@Override
	public <E> void testList(List<E> v, Class<E> elementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    List<E> ret = msgpack.read(in, new ArrayList<E>());
	    if (v == null) {
		assertEquals(null, ret);
		return;
	    }
	    assertEquals(v.size(), ret.size());
	    Iterator<E> v_iter = v.iterator();
	    Iterator<E> ret_iter = ret.iterator();
	    while (v_iter.hasNext()) {
		assertEquals(v_iter.next(), ret_iter.next());
	    }
	}
    }

    public static class TestMapPackUnpack extends org.msgpack.TestSet {
	@Test @Override
	public void testMap() throws Exception {
	    super.testMap();
	}

	@Override
	public <K, V> void testMap(Map<K, V> v, Class<K> keyElementClass, Class<V> valueElementClass) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    Map<K, V> ret = msgpack.read(in, new HashMap<K, V>());
	    if (v == null) {
		assertEquals(null, ret);
		return;
	    }
	    assertEquals(v.size(), ret.size());
	    for (Map.Entry<K, V> e : ((Map<K, V>) v).entrySet()) {
		Object value = ret.get(e.getKey());
		assertEquals(e.getValue(), value);
	    }
	}
    }
}
