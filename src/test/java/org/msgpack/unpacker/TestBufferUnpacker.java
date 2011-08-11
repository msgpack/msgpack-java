package org.msgpack.unpacker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
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
        for(int i=0; i < 50; i++) {
            Value v = createComplexType();
            vs.add(v);
            pk.write(v);
        }
        byte[] raw = pk.toByteArray();

        int n = 0;
        BufferUnpacker u = new MessagePack().createBufferUnpacker();
        UnpackerIterator it = u.iterator();

        for(int i=0; i < raw.length; i++) {
            u.feed(raw, i, 1);
            while(it.hasNext()) {
                Value v = it.next();
                assertEquals(vs.get(n), v);
                n++;
            }
        }
    }

    public Value createComplexType() throws Exception {
        Random rand = new Random(System.currentTimeMillis());
        byte[] b0 = new byte[0];
        byte[] b1 = new byte[10];
        rand.nextBytes(b1);
        byte[] b2 = new byte[1024];
        rand.nextBytes(b2);

        Value list = ValueFactory.arrayValue(new Value[] {
                ValueFactory.rawValue(b0),
                ValueFactory.rawValue(b1),
                ValueFactory.rawValue(b2),
            });

        Value map = ValueFactory.mapValue(new Value[] {
                ValueFactory.integerValue(0), ValueFactory.integerValue(Integer.MIN_VALUE),
                ValueFactory.integerValue(rand.nextInt()), ValueFactory.integerValue(Integer.MAX_VALUE),
                ValueFactory.floatValue(rand.nextFloat()), ValueFactory.booleanValue(true),
                ValueFactory.floatValue(rand.nextDouble()), ValueFactory.nilValue(),
            });

        List<Value> values = new ArrayList<Value>();

        for(int i=0; i < 2; i++) {
            values.add(list);
            for(int j=0; j < 100; j++) {
                values.add(map);
            }
        }

        Value complex = ValueFactory.arrayValue(values.toArray(new Value[values.size()]));
        return complex;
    }
}

