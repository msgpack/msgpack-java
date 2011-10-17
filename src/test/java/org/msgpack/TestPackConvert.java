package org.msgpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import org.junit.Test;
import org.msgpack.packer.Packer;
import org.msgpack.type.ValueFactory;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Converter;
import org.msgpack.type.Value;


public class TestPackConvert extends TestSet {

    @Test @Override
    public void testBoolean() throws Exception {
	super.testBoolean();
    }

    @Override
    public void testBoolean(boolean v) throws Exception {
	MessagePack msgpack = new MessagePack();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isBooleanValue());
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
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isIntegerValue());
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
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isIntegerValue());
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
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isIntegerValue());
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
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isIntegerValue());
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
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isFloatValue());
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
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isFloatValue());
	double ret = new Converter(value).readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testNil() throws Exception {
	MessagePack msgpack = new MessagePack();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.writeNil();
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	assertTrue(value.isNilValue());
	new Converter(value).readNil();
    }

    @Test @Override
    public void testBigInteger() throws Exception {
	super.testBigInteger();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	MessagePack msgpack = new MessagePack();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	BigInteger ret = new Converter(value).read(BigInteger.class);
	assertEquals(v, ret);
    }

    @Test @Override
    public void testString() throws Exception {
	super.testString();
    }

    @Override
    public void testString(String v) throws Exception {
	MessagePack msgpack = new MessagePack();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	String ret = new Converter(value).read(String.class);
	assertEquals(v, ret);
    }

    @Test @Override
    public void testByteArray() throws Exception {
	super.testByteArray();
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	MessagePack msgpack = new MessagePack();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
	packer.write(v);
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	Value value = unpacker.readValue();
	byte[] ret = new Converter(value).read(byte[].class);
	assertArrayEquals(v, ret);
    }

    @Test @Override
    public void testList() throws Exception {
	super.testList();
    }

    @Override
    public <E> void testList(List<E> v, Class<E> elementClass) throws Exception {
    	MessagePack msgpack = new MessagePack();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
        if (v == null) {
            packer.writeNil();
        } else {
            packer.writeArrayBegin(v.size());
            for (Object o : v) {
        	packer.write(o);
            }
            packer.writeArrayEnd();
        }
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
	int size = unpacker.readArrayBegin();
	List<E> ret = new ArrayList<E>(size);
	for (int i = 0; i < size; ++i) {
	    Value value = unpacker.readValue();
	    ret.add(new Converter(value).read(elementClass));
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
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	Packer packer = msgpack.createPacker(out);
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
	byte[] bytes = out.toByteArray();
	BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
	int size = unpacker.readMapBegin();
	Map<K, V> ret = new HashMap<K, V>(size);
	for (int i = 0; i < size; ++i) {
	    Value keyValue = unpacker.readValue();
	    K key = new Converter(keyValue).read(keyElementClass);
	    Value valueValue = unpacker.readValue();
	    V value = new Converter(valueValue).read(valueElementClass);
	    ret.put(key, value);
	}
	unpacker.readMapEnd();
	assertEquals(v.size(), ret.size());
	for (Map.Entry<K, V> e : ((Map<K, V>) v).entrySet()) {
	    Object value = ret.get(e.getKey());
	    assertEquals(e.getValue(), value);
	}
    }
    @Test
    public void testPackValue() throws IOException {
    	MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
        String text = "This is Value";
        Value value = ValueFactory.createRawValue(text);
        packer.write(value);
        byte[] bytes = out.toByteArray();
        Assert.assertEquals(text.length() + 1,bytes.length);
        Assert.assertEquals(0xa0 + text.length(), 0xff & bytes[0]);
    }


    @Test
    public void testPackValuePassedAsObject() throws IOException{
    	MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Packer packer = msgpack.createPacker(out);
        String text = "This is Value";
        Object value = ValueFactory.createRawValue(text);
        packer.write(value); // passed as object
        byte[] bytes = out.toByteArray();
        Assert.assertEquals(text.length() + 1,bytes.length);
        Assert.assertEquals(0xa0 + text.length(), 0xff & bytes[0]);

    }

}
