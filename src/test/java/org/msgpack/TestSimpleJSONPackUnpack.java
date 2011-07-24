package org.msgpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.JSONBufferPacker;
import org.msgpack.unpacker.JSONBufferUnpacker;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

import org.junit.Test;

public class TestSimpleJSONPackUnpack {
    @Test
    public void testSimplePackUnpack() throws IOException {
        MessagePack msgpack = new MessagePack();

        Value v = ValueFactory.mapValue(new Value[] {
                        ValueFactory.rawValue("k1"),
                        ValueFactory.integerValue(1),
                        ValueFactory.rawValue("k2"),
                        ValueFactory.arrayValue(new Value[] {
                            ValueFactory.nilValue(),
                            ValueFactory.booleanValue(true),
                            ValueFactory.booleanValue(false)
                        }),
                        ValueFactory.rawValue("k3"),
                        ValueFactory.floatValue(0.1)
                    });

        JSONBufferPacker pk = new JSONBufferPacker(msgpack);
        pk.write(v);

        byte[] raw = pk.toByteArray();

        String str = new String(raw);
        assertEquals("{\"k1\":1,\"k2\":[null,true,false],\"k3\":0.1}", str);

        JSONBufferUnpacker u = new JSONBufferUnpacker(msgpack).wrap(raw);
        Value v2 = u.readValue();

        assertEquals(v, v2);
    }
}

