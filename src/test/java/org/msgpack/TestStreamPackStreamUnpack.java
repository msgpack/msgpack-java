package org.msgpack;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.junit.Test;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.StreamUnpacker;


public class TestStreamPackStreamUnpack extends TestSet {

    @Test @Override
    public void testBoolean() throws Exception {
	super.testBoolean();
    }

    @Override
    public void testBoolean(boolean v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeBoolean(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	boolean ret = unpacker.readBoolean();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testByte() throws Exception {
	super.testByte();
    }

    @Override
    public void testByte(byte v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeByte(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	byte ret = unpacker.readByte();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testShort() throws Exception {
	super.testShort();
    }

    @Override
    public void testShort(short v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeShort(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	short ret = unpacker.readShort();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testInteger() throws Exception {
	super.testInteger();
    }

    @Override
    public void testInteger(int v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeInt(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	int ret = unpacker.readInt();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testLong() throws Exception {
	super.testLong();
    }

    @Override
    public void testLong(long v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeLong(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	long ret = unpacker.readLong();
	assertEquals(v, ret);
    }

    @Override
    public void testFloat(float v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeFloat(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	float ret = unpacker.readFloat();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testDouble() throws Exception {
	super.testDouble();
    }

    @Override
    public void testDouble(double v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeDouble(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	double ret = unpacker.readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testNil() throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeNil();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	unpacker.readNil();
    }

    @Test @Override
    public void testBigInteger() throws Exception {
	super.testBigInteger();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeBigInteger(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	BigInteger ret = unpacker.readBigInteger();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testString() throws Exception {
	super.testString();
    }

    @Override
    public void testString(String v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeString(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	String ret = unpacker.readString();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testByteArray() throws Exception {
	super.testByteArray();
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeByteArray(v);
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	StreamUnpacker unpacker = new StreamUnpacker(in);
	byte[] ret = unpacker.readByteArray();
	assertArrayEquals(v, ret);
    }
}
