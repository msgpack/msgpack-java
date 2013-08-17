package org.msgpack;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.packer.Unconverter;
import org.msgpack.unpacker.Converter;
import org.msgpack.type.Value;


public class TestUnconvertReconvert extends TestSet {

    @Test @Override
    public void testBoolean() throws Exception {
	super.testBoolean();
    }

    @Override
    public void testBoolean(boolean v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }

    @Test @Override
    public void testByte() throws Exception {
	super.testByte();
    }

    @Override
    public void testByte(byte v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }

    @Test @Override
    public void testShort() throws Exception {
	super.testShort();
    }

    @Override
    public void testShort(short v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }

    @Test @Override
    public void testInteger() throws Exception {
	super.testInteger();
    }

    @Override
    public void testInteger(int v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }

    @Test @Override
    public void testLong() throws Exception {
	super.testLong();
    }

    @Override
    public void testLong(long v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }

    @Test @Override
    public void testFloat() throws Exception {
	super.testFloat();
    }

    @Override
    public void testFloat(float v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
	assertEquals(r, ret);
    }

    @Test @Override
    public void testDouble() throws Exception {
	super.testDouble();
    }

    @Override
    public void testDouble(double v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
	assertEquals(r, ret);
    }

    @Test @Override
    public void testNil() throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.writeNil();
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        unpacker.readNil();
    }

    @Test @Override
    public void testBigInteger() throws Exception {
	super.testBigInteger();
    }

    @Override
    public void testBigInteger(BigInteger v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }

    @Test @Override
    public void testString() throws Exception {
	super.testString();
    }

    @Override
    public void testString(String v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }

    @Test @Override
    public void testByteArray() throws Exception {
	super.testByteArray();
    }

    @Override
    public void testByteArray(byte[] v) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
        packer.write(v);
        Value r = packer.getResult();
        Converter unpacker = new Converter(msgpack, r);
        Value ret = unpacker.readValue();
	assertEquals(r, ret);
    }

    @Test @Override
    public void testList() throws Exception {
	super.testList();
    }

    @Override
    public <E> void testList(List<E> v, Class<E> elementClass) throws Exception {
	MessagePack msgpack = new MessagePack();
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
        Value r = packer.getResult();
	Converter unpacker = new Converter(msgpack, r);
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }

    @Test @Override
    public void testMap() throws Exception {
	super.testMap();
    }

    @Override
    public <K, V> void testMap(Map<K, V> v, Class<K> keyElementClass, Class<V> valueElementClass) throws Exception {
	MessagePack msgpack = new MessagePack();
        Unconverter packer = new Unconverter(msgpack);
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
        Value r = packer.getResult();
	Converter unpacker = new Converter(msgpack, r);
	if (unpacker.trySkipNil()) {
	    assertEquals(null, v);
	    return;
	}
        Value ret = unpacker.readValue();
        assertEquals(r, ret);
    }
}

