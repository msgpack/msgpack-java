package org.msgpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;


public class TestBufferPackUnpack extends TestSet {

    @Test @Override
    public void testBoolean() throws Exception {
	super.testBoolean();
    }

    @Override
    public void testBoolean(boolean v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeBoolean(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeByte(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeShort(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeInt(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeLong(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeFloat(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeDouble(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	double ret = unpacker.readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testNil() throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeNil();
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	unpacker.readNil();
    }

    @Test @Override
    public void testBigInteger() throws Exception {
	super.testBigInteger();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeBigInteger(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeString(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
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
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeByteArray(v);
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	byte[] ret = unpacker.readByteArray();
	assertArrayEquals(v, ret);
    }

    @Test @Override
    public void testList() throws Exception {
	super.testList();
    }

    @Override
    public <E> void testList(List<E> v, Class<E> elementClass) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
        if (v == null) {
            packer.writeNil();
        } else {
            packer.writeArrayBegin(v.size());
            for (Object o : v) {
        	packer.write(o);
            }
            packer.writeArrayEnd();
        }
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
	int size = unpacker.readArrayBegin();
	List<E> ret = new ArrayList<E>(size);
	for (int i = 0; i < size; ++i) {
	    ret.add(unpacker.read(elementClass));
	}
	unpacker.readArrayEnd();
	assertEquals(v.size(), ret.size());
	Iterator<E> v_iter = v.iterator();
	Iterator<E> ret_iter = ret.iterator();
	while (v_iter.hasNext()) {
	    assertEquals(v_iter.next(), ret_iter.next());
	}
    }

    @Test @Override
    public void testMap() throws Exception {
	super.testMap();
    }

    @Override
    public <K, V> void testMap(Map<K, V> v, Class<K> keyElementClass, Class<V> valueElementClass) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	if (v == null) {
	    packer.writeNil();
	} else {
	    packer.writeMapBegin(v.size());
	    for (Map.Entry<K, V> e : ((Map<K, V>) v).entrySet()) {
		packer.write(e.getKey());
		packer.write(e.getValue());
	    }
	    packer.writeMapEnd();
	}
	byte[] bytes = packer.toByteArray();
	Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
	int size = unpacker.readMapBegin();
	Map<K, V> ret = new HashMap<K, V>(size);
	for (int i = 0; i < size; ++i) {
	    K key = unpacker.read(keyElementClass);
	    V value = unpacker.read(valueElementClass);
	    ret.put(key, value);
	}
	unpacker.readMapEnd();
	assertEquals(v.size(), ret.size());
	for (Map.Entry<K, V> e : ((Map<K, V>) v).entrySet()) {
	    Object value = ret.get(e.getKey());
	    assertEquals(e.getValue(), value);
	}
    }
}
