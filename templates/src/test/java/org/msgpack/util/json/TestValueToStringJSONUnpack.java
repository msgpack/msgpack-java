package org.msgpack.util.json;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.packer.Unconverter;
import org.msgpack.util.json.JSON;


public class TestValueToStringJSONUnpack extends TestSet {

    @Test @Override
    public void testBoolean() throws Exception {
	super.testBoolean();
    }

    @Override
    public void testBoolean(boolean v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	boolean ret = unpacker.readBoolean();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testByte() throws Exception {
	super.testByte();
    }

    @Override
    public void testByte(byte v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	byte ret = unpacker.readByte();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testShort() throws Exception {
	super.testShort();
    }

    @Override
    public void testShort(short v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	short ret = unpacker.readShort();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testInteger() throws Exception {
	super.testInteger();
    }

    @Override
    public void testInteger(int v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	int ret = unpacker.readInt();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testLong() throws Exception {
	super.testLong();
    }

    @Override
    public void testLong(long v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	long ret = unpacker.readLong();
	assertEquals(v, ret);
    }

    @Test @Override
    public void testFloat() throws Exception {
	super.testFloat();
    }

    @Override
    public void testFloat(float v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
        if(((Float)v).isInfinite()) {
            packer.write(v);
            String str = packer.getResult().toString();
            if(v < 0) {
                assertEquals("-Infinity", str);
            } else {
                assertEquals("Infinity", str);
            }
            return;
        } else if(((Float)v).isNaN()) {
            packer.write(v);
            String str = packer.getResult().toString();
            assertEquals("NaN", str);
            return;
        }
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	float ret = unpacker.readFloat();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testDouble() throws Exception {
	super.testDouble();
    }

    @Override
    public void testDouble(double v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
        if(((Double)v).isInfinite()) {
            packer.write(v);
            String str = packer.getResult().toString();
            if(v < 0) {
                assertEquals("-Infinity", str);
            } else {
                assertEquals("Infinity", str);
            }
            return;
        } else if(((Double)v).isNaN()) {
            packer.write(v);
            String str = packer.getResult().toString();
            assertEquals("NaN", str);
            return;
        }
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	double ret = unpacker.readDouble();
	assertEquals(v, ret, 10e-10);
    }

    @Test @Override
    public void testNil() throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	packer.writeNil();
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	unpacker.readNil();
    }

    @Ignore @Test @Override  // FIXME #SF JSON Unpacker doesn't support BigInteger
    public void testBigInteger() throws Exception {
	super.testBigInteger();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	BigInteger ret = unpacker.read(BigInteger.class);
	assertEquals(v, ret);
    }

    @Test @Override
    public void testString() throws Exception {
	super.testString();
    }

    @Override
    public void testString(String v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	packer.write(v);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
        String ret = unpacker.read(String.class);
	assertEquals(v, ret);
    }

    @Ignore @Test @Override // FIXME JSONPacker doesn't support bytes
    public void testByteArray() throws Exception {
	super.testByteArray();
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	//packer.write(v);
        String str = new String(v);
	packer.write(str);
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	String ret = unpacker.read(String.class);
	assertEquals(str, ret);
    }

    @Test @Override
    public void testList() throws Exception {
	super.testList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <E> void testList(List<E> v, Class<E> elementClass) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
	if (v == null) {
	    packer.writeNil();
	} else {
	    packer.writeArrayBegin(v.size());
	    for (Object o : v) {
		packer.write(o);
	    }
	    packer.writeArrayEnd();
	}
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
        if (unpacker.trySkipNil()) {
            assertEquals(null, v);
            return;
        }
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <K, V> void testMap(Map<K, V> v, Class<K> keyElementClass, Class<V> valueElementClass) throws Exception {
	MessagePack msgpack = new JSON();
        Unconverter packer = new Unconverter(msgpack);
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
        byte[] bytes = packer.getResult().toString().getBytes("UTF-8");
        if(!keyElementClass.equals(String.class)) {
            // TODO JSONUnpacker rejects maps whose keys are not string
            return;
        }
        Unpacker unpacker = msgpack.createBufferUnpacker(bytes);
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
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
