package org.msgpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Ignore;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;


public class TestBufferPackUnpack extends TestSetPredefinedTypes {

    @Override
    public void testBoolean(boolean v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeBoolean(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	boolean ret = unpacker.readBoolean();
	assertEquals(v, ret);
    }

    @Override
    public void testByte(byte v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeByte(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	byte ret = unpacker.readByte();
	assertEquals(v, ret);
    }

    @Override
    public void testShort(short v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeShort(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	short ret = unpacker.readShort();
	assertEquals(v, ret);
    }

    @Override
    public void testInt(int v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeInt(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	int ret = unpacker.readInt();
	assertEquals(v, ret);
    }

    @Override
    public void testLong(long v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeLong(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	int ret = unpacker.readInt();
	assertEquals(v, ret);
    }

    @Override
    public void testFloat(float v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeFloat(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	float ret = unpacker.readFloat();
	assertEquals(v, ret, 10e-10);
    }
    
    @Override
    public void testDouble(double v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeDouble(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	double ret = unpacker.readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Override
    public void testNil() throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeNil();
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	unpacker.readNil();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeBigInteger(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	BigInteger ret = unpacker.readBigInteger();
	assertEquals(v, ret);
    }

    @Override
    public void testString(String v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeString(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	String ret = unpacker.readString();
	assertEquals(v, ret);
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeByteArray(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	byte[] ret = unpacker.readByteArray();
	assertEquals(v.length, ret.length);
	for (int i = 0; i < v.length; ++i) {
	    assertEquals(v[i], ret[i]);
	}
    }
}
