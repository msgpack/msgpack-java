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


public class TestFloatArrayTemplate {

    @Test
    public void testStreamPackStreamUnpack00() throws Exception {
	new TestStreamPackStreamUnpack(0).testFloatArray();
    }

    @Test
    public void testStreamPackStreamUnpack01() throws Exception {
	new TestStreamPackStreamUnpack(1).testFloatArray();
    }

    @Test
    public void testStreamPackStreamUnpack02() throws Exception {
	new TestStreamPackStreamUnpack(2).testFloatArray();
    }

    @Test
    public void testStreamPackBufferUnpack00() throws Exception {
	new TestStreamPackBufferUnpack(0).testFloatArray();
    }

    @Test
    public void testStreamPackBufferUnpack01() throws Exception {
	new TestStreamPackBufferUnpack(1).testFloatArray();
    }

    @Test
    public void testStreamPackBufferUnpack02() throws Exception {
	new TestStreamPackBufferUnpack(2).testFloatArray();
    }

    @Test
    public void testBufferPackBufferUnpack00() throws Exception {
	new TestBufferPackBufferUnpack(0).testFloatArray();
    }

    @Test
    public void testBufferPackBufferUnpack01() throws Exception {
	new TestBufferPackBufferUnpack(1).testFloatArray();
    }

    @Test
    public void testBufferPackBufferUnpack02() throws Exception {
	new TestBufferPackBufferUnpack(2).testFloatArray();
    }

    @Test
    public void testBufferPackStreamUnpack00() throws Exception {
	new TestBufferPackStreamUnpack(0).testFloatArray();
    }

    @Test
    public void testBufferPackStreamUnpack01() throws Exception {
	new TestBufferPackStreamUnpack(1).testFloatArray();
    }

    @Test
    public void testBufferPackStreamUnpack02() throws Exception {
	new TestBufferPackStreamUnpack(2).testFloatArray();
    }

    private static class TestStreamPackStreamUnpack extends TestSet {
	private int index;

	TestStreamPackStreamUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testFloatArray() throws Exception {
	    super.testFloatArray();
	}

	@Override
	public void testFloatArray(float[] v) throws Exception {
	    Template tmpl = FloatArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    float[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		ret0 = new float[v.length];
		break;
	    case 2:
		ret0 = new float[(int) v.length / 2];
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    float[] ret = (float[]) tmpl.read(unpacker, ret0);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i], 10e-10);
	    }
	}
    }

    private static class TestStreamPackBufferUnpack extends TestSet {
	private int index;

	TestStreamPackBufferUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testFloatArray() throws Exception {
	    super.testFloatArray();
	}

	@Override
	public void testFloatArray(float[] v) throws Exception {
	    Template tmpl = FloatArrayTemplate.instance;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    StreamPacker packer = new StreamPacker(out);
	    tmpl.write(packer, v);
	    byte[] bytes = out.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    float[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		ret0 = new float[v.length];
		break;
	    case 2:
		ret0 = new float[(int) v.length / 2];
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    float[] ret = (float[]) tmpl.read(unpacker, ret0);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i], 10e-10);
	    }
	}
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
	private int index;

	TestBufferPackBufferUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testFloatArray() throws Exception {
	    super.testFloatArray();
	}

	@Override
	public void testFloatArray(float[] v) throws Exception {
	    Template tmpl = FloatArrayTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    BufferUnpacker unpacker = new BufferUnpacker();
	    unpacker.wrap(bytes);
	    float[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		ret0 = new float[v.length];
		break;
	    case 2:
		ret0 = new float[(int) v.length / 2];
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    float[] ret = (float[]) tmpl.read(unpacker, ret0);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i], 10e-10);
	    }
	}
    }

    private static class TestBufferPackStreamUnpack extends TestSet {
	private int index;

	TestBufferPackStreamUnpack(int i) {
	    index = i;
	}

	@Test @Override
	public void testFloatArray() throws Exception {
	    super.testFloatArray();
	}

	@Override
	public void testFloatArray(float[] v) throws Exception {
	    Template tmpl = FloatArrayTemplate.instance;
	    BufferPacker packer = new BufferPacker();
	    tmpl.write(packer, v);
	    byte[] bytes = packer.toByteArray();
	    StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	    float[] ret0;
	    switch (index) {
	    case 0:
		ret0 = null;
		break;
	    case 1:
		ret0 = new float[v.length];
		break;
	    case 2:
		ret0 = new float[(int) v.length / 2];
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    float[] ret = (float[]) tmpl.read(unpacker, ret0);
	    assertEquals(v.length, ret.length);
	    for (int i = 0; i < v.length; ++i) {
		assertEquals(v[i], ret[i], 10e-10);
	    }
	}
    }
}
