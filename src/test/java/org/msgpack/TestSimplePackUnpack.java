package org.msgpack;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.value.Value;

import junit.framework.TestCase;

public class TestSimplePackUnpack extends TestCase {
    @Test
    public void testSimplePackUnpack() throws IOException {
        MessagePack msgpack = new MessagePack();

        byte[] raw = msgpack.pack(new int[] {1,2,3});

        Value v = msgpack.unpack(raw);

        int[] a = msgpack.unpack(raw, new int[3]);

        Value vb = msgpack.unpack(ByteBuffer.wrap(raw));

        int[] ab = msgpack.unpack(ByteBuffer.wrap(raw), new int[3]);

        System.out.println("value: "+vb);
    }
}

