package org.msgpack.util.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.msgpack.util.json.JSONBufferPacker;
import org.msgpack.util.json.JSONBufferUnpacker;


public class TestSimpleJSONPackUnpack {
    @Test
    public void testSimplePackUnpack() throws IOException {
        MessagePack msgpack = new MessagePack();

        Value v = ValueFactory.createMapValue(new Value[] {
                        ValueFactory.createRawValue("k1"),
                        ValueFactory.createIntegerValue(1),
                        ValueFactory.createRawValue("k2"),
                        ValueFactory.createArrayValue(new Value[] {
                            ValueFactory.createNilValue(),
                            ValueFactory.createBooleanValue(true),
                            ValueFactory.createBooleanValue(false)
                        }),
                        ValueFactory.createRawValue("k3"),
                        ValueFactory.createFloatValue(0.1)
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

