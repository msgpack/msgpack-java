package org.msgpack.unpacker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.UnpackerIterator;

import org.junit.Test;

public class TestBufferUnpacker {
    @Test
    public void testEachByte() throws Exception {
        List<Value> vs = new ArrayList<Value>();

        BufferPacker pk = new MessagePack().createBufferPacker();
        for (int i = 0; i < 50; i++) {
            Value v = createComplexType();
            vs.add(v);
            pk.write(v);
        }
        byte[] raw = pk.toByteArray();

        int n = 0;
        BufferUnpacker u = new MessagePack().createBufferUnpacker();
        UnpackerIterator it = u.iterator();

        for (int i = 0; i < raw.length; i++) {
            u.feed(raw, i, 1);
            while (it.hasNext()) {
                Value v = it.next();
                assertEquals(vs.get(n), v);
                n++;
            }
        }
        assertEquals(50, n);
    }

    @Test
    public void testElevenBytes() throws Exception {
        List<Value> vs = new ArrayList<Value>();

        BufferPacker pk = new MessagePack().createBufferPacker();
        for (int i = 0; i < 55; i++) {
            Value v = createComplexType();
            vs.add(v);
            pk.write(v);
        }
        byte[] raw = pk.toByteArray();

        int n = 0;
        BufferUnpacker u = new MessagePack().createBufferUnpacker();
        UnpackerIterator it = u.iterator();

        for (int i = 0; i < raw.length; i += 11) {
            u.feed(raw, i, 11);
            while (it.hasNext()) {
                Value v = it.next();
                assertEquals(vs.get(n), v);
                n++;
            }
        }
        assertEquals(55, n);
    }

    @Test
    public void testEachObject() throws Exception {
        BufferUnpacker u = new MessagePack().createBufferUnpacker();
        UnpackerIterator it = u.iterator();

        for (int i = 0; i < 50; i++) {
            Value v = createComplexType();
            BufferPacker pk = new MessagePack().createBufferPacker();
            pk.write(v);
            byte[] raw = pk.toByteArray();
            // pk.reset();

            u.feed(raw, 0, raw.length);

            assertTrue(it.hasNext());
            Value ov = it.next();
            assertEquals(v, ov);
            // assertFalse(it.hasNext());
        }
    }

    public Value createComplexType() throws Exception {
        Random rand = new Random(System.currentTimeMillis());
        byte[] b0 = new byte[0];
        byte[] b1 = new byte[10];
        rand.nextBytes(b1);
        byte[] b2 = new byte[1024];
        rand.nextBytes(b2);

        Value list = ValueFactory.createArrayValue(new Value[] {
                ValueFactory.createRawValue(b0),
                ValueFactory.createRawValue(b1),
                ValueFactory.createRawValue(b2), });

        Value map = ValueFactory.createMapValue(new Value[] {
                ValueFactory.createIntegerValue(0),
                ValueFactory.createIntegerValue(Integer.MIN_VALUE),
                ValueFactory.createIntegerValue(rand.nextInt()),
                ValueFactory.createIntegerValue(Integer.MAX_VALUE),
                ValueFactory.createFloatValue(rand.nextFloat()),
                ValueFactory.createBooleanValue(true),
                ValueFactory.createFloatValue(rand.nextDouble()),
                ValueFactory.createNilValue(), });

        List<Value> values = new ArrayList<Value>();

        for (int i = 0; i < 2; i++) {
            values.add(list);
            for (int j = 0; j < 100; j++) {
                values.add(map);
            }
        }

        Value complex = ValueFactory.createArrayValue(values
                .toArray(new Value[values.size()]));
        return complex;
    }
}
