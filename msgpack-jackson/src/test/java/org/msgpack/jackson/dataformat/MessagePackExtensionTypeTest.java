package org.msgpack.jackson.dataformat;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class MessagePackExtensionTypeTest {
    private void assertExtensionType(MessagePackExtensionType x,
                                    int expectedExtType, ByteBuffer expectedByteBuffer) {
        assertEquals(expectedExtType, x.extType());
        assertEquals(expectedByteBuffer, x.byteBuffer());
        assertTrue(x.byteBuffer().isReadOnly());
    }

    @Test
    public void testMessagePackextensionType() {
        byte[] bs = new byte[] {0x00, (byte) 0xCC, (byte) 0xFF};
        ByteBuffer expectedByteBuffer = ByteBuffer.wrap(bs);

        int extType = 1;
        MessagePackExtensionType extensionType =
                new MessagePackExtensionType(extType, ByteBuffer.wrap(bs));
        assertExtensionType(extensionType, extType, expectedByteBuffer);

        extType = 2;
        ByteBuffer bb = ByteBuffer.allocate(3);
        bb.put(bs);
        bb.position(0);
        extensionType = new MessagePackExtensionType(extType, bb);
        assertExtensionType(extensionType, extType, expectedByteBuffer);

        extType = 3;
        bb = ByteBuffer.allocateDirect(3);
        bb.put(bs);
        bb.position(0);
        extensionType = new MessagePackExtensionType(extType, bb);
        assertExtensionType(extensionType, extType, expectedByteBuffer);

        extType = -1;
        extensionType =
                new MessagePackExtensionType(extType, ByteBuffer.wrap(bs).asReadOnlyBuffer());
        assertExtensionType(extensionType, extType, expectedByteBuffer);

        extType = -2;
        bb = ByteBuffer.allocate(3);
        bb.put(bs);
        bb.position(0);
        extensionType = new MessagePackExtensionType(extType, bb.asReadOnlyBuffer());
        assertExtensionType(extensionType, extType, expectedByteBuffer);

        extType = -3;
        bb = ByteBuffer.allocateDirect(3);
        bb.put(bs);
        bb.position(0);
        extensionType = new MessagePackExtensionType(extType, bb.asReadOnlyBuffer());
        assertExtensionType(extensionType, extType, expectedByteBuffer);
    }
}