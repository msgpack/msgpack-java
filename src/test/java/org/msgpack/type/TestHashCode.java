package org.msgpack.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class TestHashCode extends TestEquals {
    @Override
    protected void testEquals(Value v1, Value v2) {
        assertEquals(v1.hashCode(), v2.hashCode());
    }
}

