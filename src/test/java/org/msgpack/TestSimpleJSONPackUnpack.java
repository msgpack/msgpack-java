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
import org.msgpack.packer.JSONStreamPacker;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

import org.junit.Test;

public class TestSimpleJSONPackUnpack {
    @Test
    public void testSimplePackUnpack() throws IOException {
        MessagePack msgpack = new MessagePack();
        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        JSONStreamPacker pk = new JSONStreamPacker(msgpack, bo);

        pk.write(
                ValueFactory.mapValue(new Value[] {
                        ValueFactory.rawValue("k1"),
                        ValueFactory.integerValue(1),
                        ValueFactory.rawValue("k2"),
                        ValueFactory.arrayValue(new Value[] {
                            ValueFactory.rawValue("a"),
                            ValueFactory.rawValue("b"),
                            ValueFactory.rawValue("c")
                        }),
                        ValueFactory.rawValue("k3"),
                        ValueFactory.floatValue(0.1)
                    }));

        byte[] raw = bo.toByteArray();

        String str = new String(raw);
        assertEquals("{\"k1\":1,\"k2\":[\"a\",\"b\",\"c\"],\"k3\":0.1}", str);
    }
}

