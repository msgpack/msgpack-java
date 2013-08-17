package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Unpacker;


public class TestLongArrayTemplate {

    @Test
    public void testPackUnpack00() throws Exception {
	new TestPackUnpack(0).testLongArray();
    }

    @Test
    public void testPackUnpack01() throws Exception {
	new TestPackUnpack(1).testLongArray();
    }

    @Test
    public void testPackUnpack02() throws Exception {
	new TestPackUnpack(2).testLongArray();
    }

    @Test
    public void testPackBufferUnpack00() throws Exception {
	new TestPackBufferUnpack(0).testLongArray();
    }

    @Test
    public void testPackBufferUnpack01() throws Exception {
	new TestPackBufferUnpack(1).testLongArray();
    }

    @Test
    public void testPackBufferUnpack02() throws Exception {
	new TestPackBufferUnpack(2).testLongArray();
    }

    @Test
    public void testBufferPackBufferUnpack00() throws Exception {
	new TestBufferPackBufferUnpack(0).testLongArray();
    }

    @Test
    public void testBufferPackBufferUnpack01() throws Exception {
	new TestBufferPackBufferUnpack(1).testLongArray();
    }

    @Test
    public void testBufferPackBufferUnpack02() throws Exception {
	new TestBufferPackBufferUnpack(2).testLongArray();
    }

    @Test
    public void testBufferPackUnpack00() throws Exception {
	new TestBufferPackUnpack(0).testLongArray();
    }

    @Test
    public void testBufferPackUnpack01() throws Exception {
	new TestBufferPackUnpack(1).testLongArray();
    }

    @Test
    public void testBufferPackUnpack02() throws Exception {
	new TestBufferPackUnpack(2).testLongArray();
    }

    private static class TestPackUnpack extends TestSet {
	private int index;

	TestPackUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testLongArray() throws Exception {
	    super.testLongArray();
	}

	@Override
	public void testLongArray(long[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<long[]> tmpl = LongArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    unpacker.resetReadByteCount();
	    long[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		if (v == null) {
		    ret0 = new long[0];
		} else {
		    ret0 = new long[v.length];
		}
		break;
	    case 2:
		if (v == null) {
		    ret0 = new long[0];
		} else {
		    ret0 = new long[(int) v.length / 2];
		}
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    long[] ret = tmpl.read(unpacker, ret0);
	    assertLongArrayEquals(v, ret);
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestPackBufferUnpack extends TestSet {
	private int index;

	TestPackBufferUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testLongArray() throws Exception {
	    super.testLongArray();
	}

	@Override
	public void testLongArray(long[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<long[]> tmpl = LongArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    unpacker.resetReadByteCount();
	    long[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		if (v == null) {
		    ret0 = new long[0];
		} else {
		    ret0 = new long[v.length];
		}
		break;
	    case 2:
		if (v == null) {
		    ret0 = new long[0];
		} else {
		    ret0 = new long[(int) v.length / 2];
		}
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    long[] ret = tmpl.read(unpacker, ret0);
	    assertLongArrayEquals(v, ret);
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	private int index;

	TestBufferPackBufferUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testLongArray() throws Exception {
	    super.testLongArray();
	}

	@Override
	public void testLongArray(long[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<long[]> tmpl = LongArrayTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	    unpacker.resetReadByteCount();
	    long[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		if (v == null) {
		    ret0 = new long[0];
		} else {
		    ret0 = new long[v.length];
		}
		break;
	    case 2:
		if (v == null) {
		    ret0 = new long[0];
		} else {
		    ret0 = new long[(int) v.length / 2];
		}
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    long[] ret = tmpl.read(unpacker, ret0);
	    assertLongArrayEquals(v, ret);
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    private static class TestBufferPackUnpack extends TestSet {
	private int index;

	TestBufferPackUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testLongArray() throws Exception {
	    super.testLongArray();
	}

	@Override
	public void testLongArray(long[] v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Template<long[]> tmpl = LongArrayTemplate.instance;
	    BufferPacker packer = msgpack.createBufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	    unpacker.resetReadByteCount();
	    long[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		if (v == null) {
		    ret0 = new long[0];
		} else {
		    ret0 = new long[v.length];
		}
		break;
	    case 2:
		if (v == null) {
		    ret0 = new long[0];
		} else {
		    ret0 = new long[(int) v.length / 2];
		}
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    long[] ret = tmpl.read(unpacker, ret0);
	    assertLongArrayEquals(v, ret);
	    assertEquals(bytes.length, unpacker.getReadByteCount());
	}
    }

    public static void assertLongArrayEquals(long[] v, long[] ret) {
	if (v == null) {
	    assertEquals(null, ret);
	    return;
	}
	assertEquals(v.length, ret.length);
	for (int i = 0; i < v.length; ++i) {
	    assertEquals(v[i], ret[i]);
	}
    }
}
