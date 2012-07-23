package org.msgpack.type;

import static org.junit.Assert.assertEquals;

public class TestHashCode extends TestEquals {
    @Override
    protected void testEquals(Value v1, Value v2) {
        assertEquals(v1.hashCode(), v2.hashCode());
    }
}
