package org.msgpack;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.value.Value;
import org.msgpack.unpacker.Converter;
import org.msgpack.packer.Unconverter;

import junit.framework.TestCase;

public class TestSimpleConvertUnconvert extends TestCase {
    @Test
    public void testSimpleConvert() throws IOException {
        MessagePack msgpack = new MessagePack();
        byte[] raw = msgpack.pack(new int[] {1,2,3});

        Value v = msgpack.unpack(raw);

        int[] array = msgpack.convert(v, new int[3]);
        assertTrue(Arrays.equals(new int[] {1,2,3}, array));

        Value v2 = msgpack.unconvert(array);
        assertEquals(v, v2);
    }
}

