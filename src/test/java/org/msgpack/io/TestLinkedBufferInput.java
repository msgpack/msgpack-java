package org.msgpack.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;


public class TestLinkedBufferInput {
    @Test
    public void testReadByte() throws IOException {
        byte[] src = new byte[8];
        src[0] = (byte)1;
        src[2] = (byte)1;
        src[4] = (byte)1;
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);
        LinkedBufferInput b2 = new LinkedBufferInput(8);
        b2.feed(src);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        assertEndOfBuffer(b2);
    }

    @Test
    public void testFeedByteArrayCopy() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(small);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(large);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        assertEndOfBuffer(b2);
    }

    @Test
    public void testFeedByteArrayReference() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(small, true);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(large, true);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        assertEndOfBuffer(b2);
    }

    @Test
    public void testFeedByteArrayCopyReference() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(small);
            }
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(small, true);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(large);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(large, true);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        try {
            b2.readByte();
            fail();
        } catch(EndOfBufferException eof) {
        }
    }

    @Test
    public void testGetPrimitives() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream o = new DataOutputStream(bo);
        o.writeByte((byte)2);
        o.writeShort((short)2);
        o.writeInt(2);
        o.writeLong(2L);
        o.writeFloat(1.1f);
        o.writeDouble(1.1);
        byte[] src = bo.toByteArray();

        LinkedBufferInput b = new LinkedBufferInput(1024);

        for(int i=0; i < 2; i++) {
            b.feed(src);

            assertEquals((byte)2, b.getByte());
            assertEquals((byte)2, b.getByte());
            b.advance();
            assertEquals((short)2, b.getShort());
            assertEquals((short)2, b.getShort());
            b.advance();
            assertEquals(2, b.getInt());
            assertEquals(2, b.getInt());
            b.advance();
            assertEquals(2L, b.getLong());
            assertEquals(2L, b.getLong());
            b.advance();
            assertEquals(1.1f, b.getFloat(), 0.000001f);
            assertEquals(1.1f, b.getFloat(), 0.000001f);
            b.advance();
            assertEquals(1.1, b.getDouble(), 0.000001);
            assertEquals(1.1, b.getDouble(), 0.000001);
            b.advance();
        }
    }

    @Test
    public void testGetPrimitivesChunks() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream o = new DataOutputStream(bo);
        o.writeByte((byte)2);
        o.writeShort((short)2);
        o.writeInt(2);
        o.writeLong(2L);
        o.writeFloat(1.1f);
        o.writeDouble(1.1);
        byte[] src = bo.toByteArray();

        LinkedBufferInput b = new LinkedBufferInput(1024);

        for(int i=0; i < 2; i++) {
            int p = 0;

            b.feed(src, p++, 1, true);
            assertEquals((byte)2, b.getByte());
            assertEquals((byte)2, b.getByte());
            b.advance();

            for(int j=0; j < 2; j++) {
                try {
                    b.getShort();
                    fail();
                } catch(EndOfBufferException eof) {
                }
                b.feed(src, p++, 1, true);
            }
            assertEquals((short)2, b.getShort());
            assertEquals((short)2, b.getShort());
            b.advance();

            for(int j=0; j < 4; j++) {
                try {
                    b.getInt();
                    fail();
                } catch(EndOfBufferException eof) {
                }
                b.feed(src, p++, 1, true);
            }
            assertEquals(2, b.getInt());
            assertEquals(2, b.getInt());
            b.advance();

            for(int j=0; j < 8; j++) {
                try {
                    b.getLong();
                    fail();
                } catch(EndOfBufferException eof) {
                }
                b.feed(src, p++, 1, true);
            }
            assertEquals(2L, b.getLong());
            assertEquals(2L, b.getLong());
            b.advance();

            for(int j=0; j < 4; j++) {
                try {
                    b.getFloat();
                    fail();
                } catch(EndOfBufferException eof) {
                }
                b.feed(src, p++, 1, true);
            }
            assertEquals(1.1f, b.getFloat(), 0.000001f);
            assertEquals(1.1f, b.getFloat(), 0.000001f);
            b.advance();

            for(int j=0; j < 8; j++) {
                try {
                    b.getDouble();
                    fail();
                } catch(EndOfBufferException eof) {
                }
                b.feed(src, p++, 1, true);
            }
            assertEquals(1.1, b.getDouble(), 0.000001);
            assertEquals(1.1, b.getDouble(), 0.000001);
            b.advance();
        }
    }


    @Test
    public void testFeedByteBufferCopy() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(ByteBuffer.wrap(small));
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(ByteBuffer.wrap(large));
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        assertEndOfBuffer(b2);
    }

    @Test
    public void testFeedByteBufferReference() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(ByteBuffer.wrap(small), true);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(ByteBuffer.wrap(large), true);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        assertEndOfBuffer(b2);
    }

    @Test
    public void testFeedByteBufferCopyReference() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(ByteBuffer.wrap(small));
            }
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(ByteBuffer.wrap(small), true);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(ByteBuffer.wrap(large));
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(ByteBuffer.wrap(large), true);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        assertEndOfBuffer(b2);
    }

    @Test
    public void testClear() throws IOException {
        byte[] src = new byte[8];

        LinkedBufferInput b = new LinkedBufferInput(11);

        for(int i=0; i < 2; i++) {
            try {
                b.readByte();
                fail();
            } catch(EndOfBufferException eof) {
            }

            b.feed(src);

            b.clear();

            assertEndOfBuffer(b);

            b.feed(src);

            for(int j=0; j < src.length; j++) {
                b.readByte();
            }

            b.clear();
        }
    }

    @Test
    public void testClearRecycle() throws IOException {
        byte[] data = new byte[8];
        data[0] = (byte)1;
        data[2] = (byte)1;
        data[4] = (byte)1;

        LinkedBufferInput b = new LinkedBufferInput(16);

        b.feed(data);
        assertEquals(1, b.link.size());
        assertEquals(8, b.writable);
        b.clear();
        assertEquals(1, b.link.size());
        assertEquals(16, b.writable);

        b.feed(data);
        b.feed(data);
        assertEquals(1, b.link.size());
        assertEquals(0, b.writable);
        b.clear();
        assertEquals(1, b.link.size());
        assertEquals(16, b.writable);
    }

    @Test
    public void testCopyReferencedBuffer() throws IOException {
        byte[] data = new byte[16];
        data[0] = (byte)4;
        data[3] = (byte)5;
        data[6] = (byte)6;
        data[10] = (byte)7;

        LinkedBufferInput b = new LinkedBufferInput(32);
        int n;
        byte[] buf = new byte[16];

        b.feed(data, true);
        b.feed(data, true);
        b.feed(data, true);
        assertEquals(3, b.link.size());
        assertEquals(-1, b.writable);

        b.copyReferencedBuffer();
        assertEquals(1, b.link.size());
        assertEquals(0, b.writable);

        n = b.read(buf, 0, 16);
        assertEquals(n, 16);
        assertArrayEquals(data, buf);

        n = b.read(buf, 0, 16);
        assertEquals(n, 16);
        assertArrayEquals(data, buf);

        n = b.read(buf, 0, 16);
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
    }

    @Test
    public void testCopyReferencedBufferOptimized() throws IOException {
        byte[] data = new byte[16];
        data[0] = (byte)4;
        data[3] = (byte)5;
        data[6] = (byte)6;
        data[10] = (byte)7;

        LinkedBufferInput b = new LinkedBufferInput(32);
        int n;
        byte[] buf = new byte[16];

        b.feed(data, true);
        b.feed(data, true);
        b.feed(data);  // buffer allocated
        assertEquals(3, b.link.size());
        assertEquals(16, b.writable);

        b.copyReferencedBuffer();
        assertEquals(2, b.link.size());
        assertEquals(16, b.writable);

        n = b.read(buf, 0, 16);
        assertEquals(n, 16);
        assertArrayEquals(data, buf);

        n = b.read(buf, 0, 16);
        assertEquals(n, 16);
        assertArrayEquals(data, buf);

        n = b.read(buf, 0, 16);
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
    }

    @Test
    public void testBufferRecycleByteArray() throws IOException {
        byte[] data = new byte[16];
        data[0] = (byte)4;
        data[3] = (byte)5;
        data[6] = (byte)6;
        data[10] = (byte)7;

        LinkedBufferInput b = new LinkedBufferInput(32);
        int n;
        byte[] buf = new byte[16];

        b.feed(data);  // feed 1; buffer allocated; remains 32-16 = 16 bytes
        assertEquals(1, b.link.size());
        assertEquals(16, b.writable);
        ByteBuffer allocated = b.link.peekLast();

        b.feed(data);  // feed 2; remains 16-16 = 0 bytes
        assertEquals(1, b.link.size());
        assertEquals(0, b.writable);
        assertEquals(true, allocated == b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume 16 bytes 1
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(1, b.link.size());
        assertEquals(0, b.writable);  // no writable
        assertEquals(true, allocated == b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume 16 bytes 2; comsume all buffer; recycled
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEndOfBuffer(b);
        assertEquals(1, b.link.size());
        assertEquals(32, b.writable);  // recycled
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(data);  // feed 1; remains 32-16 = 16 bytes
        assertEquals(1, b.link.size());
        assertEquals(16, b.writable);
        assertEquals(true, allocated == b.link.peekLast());

        b.clear();  // clear; recycled
        assertEndOfBuffer(b);
        assertEquals(1, b.link.size());
        assertEquals(32, b.writable);  // recycled
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(data, true);  // feed reference 1;
        assertEquals(2, b.link.size());  // leaves last writable buffer
        assertEquals(32, b.writable);    // which remains 32 bytes
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(data, true);  // feed reference 2;
        assertEquals(3, b.link.size());  // leaves last writable buffer
        assertEquals(32, b.writable);    // which remains 32 bytes
        assertEquals(true, allocated == b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume first link 1
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(2, b.link.size());  // first link is removed
        assertEquals(32, b.writable);    // which remains 32 bytes
        assertEquals(true, allocated == b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume first link 2
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(1, b.link.size());  // first link is removed
        assertEquals(32, b.writable);    // which remains 32 bytes
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(data);  // feed 1; remains 32-16 = 16 bytes;
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(1, b.link.size());
        assertEquals(16, b.writable);
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(data, true);  // feed reference 2; writable buffer is hidden
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(2, b.link.size());
        assertEquals(-1, b.writable);  // now not writable
        assertEquals(true, allocated != b.link.peekLast());
        assertEquals(true, allocated == b.link.peekFirst());

        n = b.read(buf, 0, 16);  // consume data 1
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(1, b.link.size());  // recycled buffer is removed
        assertEquals(-1, b.writable);
        assertEquals(true, allocated != b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume data 2
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEndOfBuffer(b);
        assertEquals(0, b.link.size());
        assertEquals(-1, b.writable);
    }

    // copied from testBufferRecycleByteArray
    @Test
    public void testBufferRecycleByteBuffer() throws IOException {
        byte[] data = new byte[16];
        data[0] = (byte)4;
        data[3] = (byte)5;
        data[6] = (byte)6;
        data[10] = (byte)7;

        ByteBuffer bb = ByteBuffer.wrap(data);

        LinkedBufferInput b = new LinkedBufferInput(32);
        int n;
        byte[] buf = new byte[16];

        b.feed(bb.duplicate());  // feed 1; buffer allocated; remains 32-16 = 16 bytes
        assertEquals(1, b.link.size());
        assertEquals(16, b.writable);
        ByteBuffer allocated = b.link.peekLast();

        b.feed(bb.duplicate());  // feed 2; remains 16-16 = 0 bytes
        assertEquals(1, b.link.size());
        assertEquals(0, b.writable);
        assertEquals(true, allocated == b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume 16 bytes 1
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(1, b.link.size());
        assertEquals(0, b.writable);  // no writable
        assertEquals(true, allocated == b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume 16 bytes 2; comsume all buffer; recycled
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEndOfBuffer(b);
        assertEquals(1, b.link.size());
        assertEquals(32, b.writable);  // recycled
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(bb.duplicate());  // feed 1; remains 32-16 = 16 bytes
        assertEquals(1, b.link.size());
        assertEquals(16, b.writable);
        assertEquals(true, allocated == b.link.peekLast());

        b.clear();  // clear; recycled
        assertEndOfBuffer(b);
        assertEquals(1, b.link.size());
        assertEquals(32, b.writable);  // recycled
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(bb.duplicate(), true);  // feed reference 1;
        assertEquals(2, b.link.size());  // leaves last writable buffer
        assertEquals(32, b.writable);    // which remains 32 bytes
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(bb.duplicate(), true);  // feed reference 2;
        assertEquals(3, b.link.size());  // leaves last writable buffer
        assertEquals(32, b.writable);    // which remains 32 bytes
        assertEquals(true, allocated == b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume first link 1
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(2, b.link.size());  // first link is removed
        assertEquals(32, b.writable);    // which remains 32 bytes
        assertEquals(true, allocated == b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume first link 2
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(1, b.link.size());  // first link is removed
        assertEquals(32, b.writable);    // which remains 32 bytes
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(bb.duplicate());  // feed 1; remains 32-16 = 16 bytes;
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(1, b.link.size());
        assertEquals(16, b.writable);
        assertEquals(true, allocated == b.link.peekLast());

        b.feed(bb.duplicate(), true);  // feed reference 2; writable buffer is hidden
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(2, b.link.size());
        assertEquals(-1, b.writable);  // now not writable
        assertEquals(true, allocated != b.link.peekLast());
        assertEquals(true, allocated == b.link.peekFirst());

        n = b.read(buf, 0, 16);  // consume data 1
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEquals(1, b.link.size());  // recycled buffer is removed
        assertEquals(-1, b.writable);
        assertEquals(true, allocated != b.link.peekLast());

        n = b.read(buf, 0, 16);  // consume data 2
        assertEquals(n, 16);
        assertArrayEquals(data, buf);
        assertEndOfBuffer(b);
        assertEquals(0, b.link.size());
        assertEquals(-1, b.writable);
    }

    private void assertEndOfBuffer(LinkedBufferInput b) throws IOException {
        try {
            b.readByte();
            fail();
        } catch(EndOfBufferException eof) {
        }
    }
}

