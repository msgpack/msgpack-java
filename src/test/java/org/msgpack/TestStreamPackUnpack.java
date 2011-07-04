package org.msgpack;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.msgpack.packer.Packer;
import org.msgpack.packer.StreamPacker;
import org.msgpack.unpacker.StreamUnpacker;
import org.msgpack.unpacker.Unpacker;


public class TestStreamPackUnpack extends TestSetPredefinedTypes {

    @Override
    public void testBoolean(boolean v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeBoolean(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	boolean ret = unpacker.readBoolean();
	assertEquals(v, ret);
    }

    @Override
    public void testByte(byte v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeByte(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	byte ret = unpacker.readByte();
	assertEquals(v, ret);
    }

    @Override
    public void testShort(short v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeShort(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	short ret = unpacker.readShort();
	assertEquals(v, ret);
    }

    @Override
    public void testInt(int v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeInt(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	int ret = unpacker.readInt();
	assertEquals(v, ret);
    }

    @Override
    public void testLong(long v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeLong(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	long ret = unpacker.readLong();
	assertEquals(v, ret);
    }

    @Override
    public void testFloat(float v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeFloat(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	float ret = unpacker.readFloat();
	assertEquals(v, ret, 10e-10);
    }
    
    @Override
    public void testDouble(double v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeDouble(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	double ret = unpacker.readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Override
    public void testNil() throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeNil();
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	unpacker.readNil();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeBigInteger(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	BigInteger ret = unpacker.readBigInteger();
	assertEquals(v, ret);
    }

    @Override
    public void testString(String v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeString(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	String ret = unpacker.readString();
	assertEquals(v, ret);
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	StreamPacker packer = new StreamPacker(out);
	packer.writeByteArray(v);
	packer.flush();
	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	Unpacker unpacker = new StreamUnpacker(in);
	byte[] ret = unpacker.readByteArray();
	assertEquals(v.length, ret.length);
	for (int i = 0; i < v.length; ++i) {
	    assertEquals(v[i], ret[i]);
	}
    }
}
