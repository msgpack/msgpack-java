package org.msgpack;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;


public class TestBufferPackBufferUnpack extends TestSet {

    @Test @Override
    public void testBoolean() throws Exception {
	super.testBoolean();
    }

    @Override
    public void testBoolean(boolean v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeBoolean(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	boolean ret = unpacker.readBoolean();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testByte() throws Exception {
	super.testByte();
    }

    @Override
    public void testByte(byte v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeByte(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	byte ret = unpacker.readByte();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testShort() throws Exception {
	super.testShort();
    }

    @Override
    public void testShort(short v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeShort(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	short ret = unpacker.readShort();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testInteger() throws Exception {
	super.testInteger();
    }

    @Override
    public void testInteger(int v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeInt(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	int ret = unpacker.readInt();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testLong() throws Exception {
	super.testLong();
    }

    @Override
    public void testLong(long v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeLong(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	long ret = unpacker.readLong();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testFloat() throws Exception {
	super.testFloat();
    }

    @Override
    public void testFloat(float v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeFloat(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	float ret = unpacker.readFloat();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testDouble() throws Exception {
	super.testDouble();
    }

    @Override
    public void testDouble(double v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeDouble(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	double ret = unpacker.readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testNil() throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeNil();
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	unpacker.readNil();
    }

    @Test @Override
    public void testBigInteger() throws Exception {
	super.testBigInteger();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeBigInteger(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	BigInteger ret = unpacker.readBigInteger();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testString() throws Exception {
	super.testString();
    }

    @Override
    public void testString(String v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeString(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	String ret = unpacker.readString();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testByteArray() throws Exception {
	super.testByteArray();
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = new BufferPacker(msgpack);
	packer.writeByteArray(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker(msgpack);
	unpacker.wrap(bytes);
	byte[] ret = unpacker.readByteArray();
	assertArrayEquals(v, ret);
    }

    @Test @Override
    public void testList() throws Exception {
	super.testList();
    }

    @Override
    public void testList(List v, Class<?> elementClass) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeArrayBegin(v.size());
	for (Object o : v) {
	    packer.write(o);
	}
	packer.writeArrayEnd();
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	int size = unpacker.readArrayBegin();
	List ret = new ArrayList(size);
	for (int i = 0; i < size; ++i) {
	    ret.add(unpacker.read(elementClass));
	}
	unpacker.readArrayEnd();
	assertEquals(v.size(), ret.size());
	Iterator v_iter = v.iterator();
	Iterator ret_iter = ret.iterator();
	while (v_iter.hasNext()) {
	    assertEquals(v_iter.next(), ret_iter.next());
	}
    }

    @Test @Override
    public void testMap() throws Exception {
	super.testMap();
    }

    @Override
    public void testMap(Map v, Class<?> keyElementClass, Class<?> valueElementClass) throws Exception {
	BufferPacker packer = new BufferPacker();
	packer.writeMapBegin(v.size());
	for (Map.Entry<Object, Object> e : ((Map<Object, Object>) v).entrySet()) {
	    packer.write(e.getKey());
	    packer.write(e.getValue());
	}
	packer.writeMapEnd();
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = new BufferUnpacker();
	unpacker.wrap(bytes);
	int size = unpacker.readMapBegin();
	Map ret = new HashMap(size);
	for (int i = 0; i < size; ++i) {
	    Object key = unpacker.read(keyElementClass);
	    Object value = unpacker.read(valueElementClass);
	    ret.put(key, value);
	}
	unpacker.readMapEnd();
	assertEquals(v.size(), ret.size());
	for (Map.Entry<Object, Object> e : ((Map<Object, Object>) v).entrySet()) {
	    Object value = ret.get(e.getKey());
	    assertEquals(e.getValue(), value);
	}
    }
}
