package org.msgpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;

import org.junit.Test;


public class TestSimpleConvertUnconvert {
    @Test
    public void testSimpleConvert() throws IOException {
        MessagePack msgpack = new MessagePack();
        byte[] raw = msgpack.write(new int[] {1,2,3});

        Value v = msgpack.read(raw);

        int[] array = msgpack.convert(v, new int[3]);
        assertArrayEquals(new int[] {1,2,3}, array);

        Value v2 = msgpack.unconvert(array);
        assertEquals(v, v2);
    }
}

