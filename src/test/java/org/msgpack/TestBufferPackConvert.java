package org.msgpack;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Converter;
import org.msgpack.type.Value;


public class TestBufferPackConvert extends TestSet {

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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isBoolean());
	boolean ret = new Converter(value).readBoolean();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isInteger());
	byte ret = new Converter(value).readByte();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isInteger());
	short ret = new Converter(value).readShort();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isInteger());
	int ret = new Converter(value).readInt();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isInteger());
	long ret = new Converter(value).readLong();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isFloat());
	float ret = new Converter(value).readFloat();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isFloat());
	double ret = new Converter(value).readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testNil() throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	packer.writeNil();
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isNil());
	new Converter(value).readNil();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isInteger());
	BigInteger ret = new Converter(value).readBigInteger();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isRaw());
	String ret = new Converter(value).readString();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isRaw());
	byte[] ret = new Converter(value).readByteArray();
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
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
	int size = unpacker.readArrayBegin();
	List ret = new ArrayList(size);
	for (int i = 0; i < size; ++i) {
	    Value value = unpacker.readValue();
	    ret.add(new Converter(value).read(elementClass));
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
    public <K, V> void testMap(Map<K, V> v, Class<K> keyElementClass, Class<V> valueElementClass) throws Exception {
	MessagePack msgpack = new MessagePack();
	BufferPacker packer = msgpack.createBufferPacker();
	if (v == null) {
	    packer.writeNil();
	} else {
	    packer.writeMapBegin(v.size());
	    for (Map.Entry<Object, Object> e : ((Map<Object, Object>) v).entrySet()) {
		packer.write(e.getKey());
		packer.write(e.getValue());
	    }
	    packer.writeMapEnd();
	}
	byte[] bytes = packer.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
	int size = unpacker.readMapBegin();
	Map ret = new HashMap(size);
	for (int i = 0; i < size; ++i) {
	    Value keyValue = unpacker.readValue();
	    Object key = new Converter(keyValue).read(keyElementClass);
	    Value valueValue = unpacker.readValue();
	    Object value = new Converter(valueValue).read(valueElementClass);
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
