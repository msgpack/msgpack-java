package org.msgpack;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.packer.BufferPacker;
import org.msgpack.type.RubySymbol;
import org.msgpack.unpacker.BufferUnpacker;


public class TestBufferPackBufferUnpack extends TestSet {

    @Test @Override
    public void testBoolean() throws Exception {
	super.testBoolean();
    }

    @Override
    public void testBoolean(boolean v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	boolean ret = unpacker.readBoolean();
	assertEquals(v, ret);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testByte() throws Exception {
	super.testByte();
    }

    @Override
    public void testByte(byte v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	byte ret = unpacker.readByte();
	assertEquals(v, ret);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testShort() throws Exception {
	super.testShort();
    }

    @Override
    public void testShort(short v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	short ret = unpacker.readShort();
	assertEquals(v, ret);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testInteger() throws Exception {
	super.testInteger();
    }

    @Override
    public void testInteger(int v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	int ret = unpacker.readInt();
	assertEquals(v, ret);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testLong() throws Exception {
	super.testLong();
    }

    @Override
    public void testLong(long v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	long ret = unpacker.readLong();
	assertEquals(v, ret);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testFloat() throws Exception {
	super.testFloat();
    }

    @Override
    public void testFloat(float v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	float ret = unpacker.readFloat();
	assertEquals(v, ret, 10e-10);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testDouble() throws Exception {
	super.testDouble();
    }

    @Override
    public void testDouble(double v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	double ret = unpacker.readDouble();
	assertEquals(v, ret, 10e-10);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testNil() throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeNil();
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	unpacker.readNil();
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testBigInteger() throws Exception {
	super.testBigInteger();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	BigInteger ret = unpacker.read(BigInteger.class);
	assertEquals(v, ret);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testString() throws Exception {
	super.testString();
    }

    @Override
    public void testString(String v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	String ret = unpacker.read(String.class);
	assertEquals(v, ret);
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }
    
    @Test @Override
    public void testRubySymbol() throws Exception {
	super.testRubySymbol();
    }
    
    @Override
    public void testRubySymbol(RubySymbol v) throws Exception {
    	MessagePack msgpack = new MessagePack();
    	BufferPacker packer = msgpack.createBufferPacker();
    	packer.write(v);
    	byte[] bytes = packer.toByteArray();
    	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
    	unpacker.resetReadByteCount();
    	RubySymbol ret = unpacker.read(RubySymbol.class);
    	assertEquals(v, ret);
    	assertEquals(bytes.length, unpacker.getReadByteCount());
    }
    
    @Test @Override
    public void testDate() throws Exception {
	super.testDate();
    }
    
    @Override
    public void testDate(Date v) throws Exception {
    	MessagePack msgpack = new MessagePack();
    	BufferPacker packer = msgpack.createBufferPacker();
    	packer.write(v);
    	byte[] bytes = packer.toByteArray();
    	BufferUnpacker  unpacker = msgpack.createBufferUnpacker(bytes);
    	unpacker.resetReadByteCount();
    	Date ret = unpacker.read(Date.class);
    	assertEquals(v, ret);
    	assertEquals(bytes.length, unpacker.getReadByteCount());
    }

    @Test @Override
    public void testByteArray() throws Exception {
	super.testByteArray();
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.write(v);
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
	byte[] ret = unpacker.read(byte[].class);
	assertArrayEquals(v, ret);
	assertEquals(bytes.length, unpacker.getReadByteCount());
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
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
	assertEquals(bytes.length, unpacker.getReadByteCount());
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.resetReadByteCount();
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
	assertEquals(bytes.length, unpacker.getReadByteCount());
    }
}
