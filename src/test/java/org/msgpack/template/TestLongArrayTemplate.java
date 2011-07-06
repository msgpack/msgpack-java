package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.StreamUnpacker;


public class TestLongArrayTemplate {

    @Test
    public void testStreamPackStreamUnpack00() throws Exception {
	new TestStreamPackStreamUnpack(0).testLongArray();
    }

    @Test
    public void testStreamPackStreamUnpack01() throws Exception {
	new TestStreamPackStreamUnpack(1).testLongArray();
    }

    @Test
    public void testStreamPackStreamUnpack02() throws Exception {
	new TestStreamPackStreamUnpack(2).testLongArray();
    }

    @Test
    public void testStreamPackBufferUnpack00() throws Exception {
	new TestStreamPackBufferUnpack(0).testLongArray();
    }

    @Test
    public void testStreamPackBufferUnpack01() throws Exception {
	new TestStreamPackBufferUnpack(1).testLongArray();
    }

    @Test
    public void testStreamPackBufferUnpack02() throws Exception {
	new TestStreamPackBufferUnpack(2).testLongArray();
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
    public void testBufferPackStreamUnpack00() throws Exception {
	new TestBufferPackStreamUnpack(0).testLongArray();
    }

    @Test
    public void testBufferPackStreamUnpack01() throws Exception {
	new TestBufferPackStreamUnpack(1).testLongArray();
    }

    @Test
    public void testBufferPackStreamUnpack02() throws Exception {
	new TestBufferPackStreamUnpack(2).testLongArray();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	private int index;

	TestStreamPackStreamUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testLongArray() throws Exception {
	    super.testLongArray();
	}

	@Override
	public void testLongArray(long[] v) throws Exception {
	    Template tmpl = LongArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    long[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		ret0 = new long[v.length];
		break;
	    case 2:
		ret0 = new long[(int) v.length / 2];
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    long[] ret = (long[]) tmpl.read(unpacker, ret0);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i]);
	    }
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	private int index;

	TestStreamPackBufferUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testLongArray() throws Exception {
	    super.testLongArray();
	}

	@Override
	public void testLongArray(long[] v) throws Exception {
	    Template tmpl = LongArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    long[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		ret0 = new long[v.length];
		break;
	    case 2:
		ret0 = new long[(int) v.length / 2];
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    long[] ret = (long[]) tmpl.read(unpacker, ret0);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i]);
	    }
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
	    Template tmpl = LongArrayTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    long[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		ret0 = new long[v.length];
		break;
	    case 2:
		ret0 = new long[(int) v.length / 2];
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    long[] ret = (long[]) tmpl.read(unpacker, ret0);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i]);
	    }
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	private int index;

	TestBufferPackStreamUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testLongArray() throws Exception {
	    super.testLongArray();
	}

	@Override
	public void testLongArray(long[] v) throws Exception {
	    Template tmpl = LongArrayTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    long[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		ret0 = new long[v.length];
		break;
	    case 2:
		ret0 = new long[(int) v.length / 2];
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    long[] ret = (long[]) tmpl.read(unpacker, ret0);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i]);
	    }
	}
    }
}
