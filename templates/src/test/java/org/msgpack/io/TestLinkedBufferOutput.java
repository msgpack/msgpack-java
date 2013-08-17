package org.msgpack.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;


public class TestLinkedBufferOutput {
    @Test
    public void testGetSize() throws IOException {
        LinkedBufferOutput o = new LinkedBufferOutput(10);
        for(int i=0; i < 21; i++) {
            o.writeByte((byte)1);
            assertEquals(i+1, o.getSize());
        }
    }

    @Test
    public void testWritePrimitives() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream o1 = new DataOutputStream(bo);
        LinkedBufferOutput o2 = new LinkedBufferOutput(10);
        o1.writeByte((byte)2);
        o2.writeByte((byte)2);
        o1.writeShort((short)2);
        o2.writeShort((short)2);
        o1.writeInt(2);
        o2.writeInt(2);
        o1.writeLong(2L);
        o2.writeLong(2L);
        o1.writeFloat(1.1f);
        o2.writeFloat(1.1f);
        o1.writeDouble(1.1);
        o2.writeDouble(1.1);
        byte[] b1 = bo.toByteArray();
        byte[] b2 = o2.toByteArray();
        assertEquals(b1.length, b2.length);
        assertArrayEquals(b1, b2);
    }

    @Test
    public void testWriteByteAndPrimitives() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream o1 = new DataOutputStream(bo);
        LinkedBufferOutput o2 = new LinkedBufferOutput(10);
        o1.writeByte((byte)9);
        o1.writeByte((byte)2);
        o2.writeByteAndByte((byte)9, (byte)2);
        o1.writeByte((byte)9);
        o1.writeShort((short)2);
        o2.writeByteAndShort((byte)9, (short)2);
        o1.writeByte((byte)9);
        o1.writeInt(2);
        o2.writeByteAndInt((byte)9, 2);
        o1.writeByte((byte)9);
        o1.writeLong(2L);
        o2.writeByteAndLong((byte)9, 2L);
        o1.writeByte((byte)9);
        o1.writeFloat(1.1f);
        o2.writeByteAndFloat((byte)9, 1.1f);
        o1.writeByte((byte)9);
        o1.writeDouble(1.1);
        o2.writeByteAndDouble((byte)9, 1.1);
        byte[] b1 = bo.toByteArray();
        byte[] b2 = o2.toByteArray();
        assertEquals(b1.length, b2.length);
        assertArrayEquals(b1, b2);
    }

    @Test
    public void testWrite() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream o1 = new DataOutputStream(bo);
        LinkedBufferOutput o2 = new LinkedBufferOutput(10);
        byte[] raw = new byte[9];
        raw[0] = (byte)1;
        raw[7] = (byte)1;
        for(int i=0; i < 11; i++) {
            o1.write(raw, 0, raw.length);
            o2.write(raw, 0, raw.length);
        }
        byte[] b1 = bo.toByteArray();
        byte[] b2 = o2.toByteArray();
        assertEquals(b1.length, b2.length);
        assertArrayEquals(b1, b2);
    }
}

