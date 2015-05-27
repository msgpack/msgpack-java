package org.msgpack.jackson.dataformat;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class MessagePackExtendedTypeTest {
    private void assertExtendedType(MessagePackExtendedType x,
                                    int expectedExtType, ByteBuffer expectedByteBuffer) {
        assertEquals(expectedExtType, x.extType());
        assertEquals(expectedByteBuffer, x.byteBuffer());
        assertTrue(x.byteBuffer().isReadOnly());
    }

    @Test
    public void testMessagePackExtendedType() {
        byte[] bs = new byte[] {0x00, (byte) 0xCC, (byte) 0xFF};
        ByteBuffer expectedByteBuffer = ByteBuffer.wrap(bs);

        int extType = 1;
        MessagePackExtendedType extendedType =
                new MessagePackExtendedType(extType, ByteBuffer.wrap(bs));
        assertExtendedType(extendedType, extType, expectedByteBuffer);

        extType = 2;
        ByteBuffer bb = ByteBuffer.allocate(3);
        bb.put(bs);
        bb.position(0);
        extendedType = new MessagePackExtendedType(extType, bb);
        assertExtendedType(extendedType, extType, expectedByteBuffer);

        extType = 3;
        bb = ByteBuffer.allocateDirect(3);
        bb.put(bs);
        bb.position(0);
        extendedType = new MessagePackExtendedType(extType, bb);
        assertExtendedType(extendedType, extType, expectedByteBuffer);

        extType = -1;
        extendedType =
                new MessagePackExtendedType(extType, ByteBuffer.wrap(bs).asReadOnlyBuffer());
        assertExtendedType(extendedType, extType, expectedByteBuffer);

        extType = -2;
        bb = ByteBuffer.allocate(3);
        bb.put(bs);
        bb.position(0);
        extendedType = new MessagePackExtendedType(extType, bb.asReadOnlyBuffer());
        assertExtendedType(extendedType, extType, expectedByteBuffer);

        extType = -3;
        bb = ByteBuffer.allocateDirect(3);
        bb.put(bs);
        bb.position(0);
        extendedType = new MessagePackExtendedType(extType, bb.asReadOnlyBuffer());
        assertExtendedType(extendedType, extType, expectedByteBuffer);
    }
}