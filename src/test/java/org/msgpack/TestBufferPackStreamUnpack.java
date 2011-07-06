package org.msgpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;

import org.junit.Test;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.StreamUnpacker;


public class TestBufferPackStreamUnpack extends TestSet {

    @Test @Override
    public void testBoolean() throws Exception {
	super.testBoolean();
    }

    @Override
    public void testBoolean(boolean v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeBoolean(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	boolean ret = unpacker.readBoolean();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testByte() throws Exception {
	super.testByte();
    }

    @Override
    public void testByte(byte v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeByte(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	byte ret = unpacker.readByte();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testShort() throws Exception {
	super.testShort();
    }

    @Override
    public void testShort(short v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeShort(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	short ret = unpacker.readShort();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testInteger() throws Exception {
	super.testInteger();
    }

    @Override
    public void testInteger(int v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeInt(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	int ret = unpacker.readInt();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testLong() throws Exception {
	super.testLong();
    }

    @Override
    public void testLong(long v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeLong(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	long ret = unpacker.readLong();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testFloat() throws Exception {
	super.testFloat();
    }

    @Override
    public void testFloat(float v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeFloat(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	float ret = unpacker.readFloat();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testDouble() throws Exception {
	super.testDouble();
    }

    @Override
    public void testDouble(double v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeDouble(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	double ret = unpacker.readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testNil() throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeNil();
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	unpacker.readNil();
    }

    @Test @Override
    public void testBigInteger() throws Exception {
	super.testBigInteger();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeBigInteger(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	BigInteger ret = unpacker.readBigInteger();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testString() throws Exception {
	super.testString();
    }

    @Override
    public void testString(String v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeString(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	String ret = unpacker.readString();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testByteArray() throws Exception {
	super.testByteArray();
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeByteArray(v);
	byte[] bytes = packer.toByteArray();
	StreamUnpacker unpacker = new StreamUnpacker(new ByteArrayInputStream(bytes));
	byte[] ret = unpacker.readByteArray();
	assertArrayEquals(v, ret);
    }
}
