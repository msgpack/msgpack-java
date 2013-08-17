package org.msgpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.unpacker.BufferUnpacker;

import org.junit.Test;


public class TestSimplePackable {
    // all files are REQUIRED
    public static class Sample01 implements MessagePackable {
        public String f0;
        public int[] f1;
        public List<String> f2;

        public Sample01() { }

        public void writeTo(Packer pk) throws IOException {
            pk.writeArrayBegin(3);
                pk.write(f0);
                pk.writeArrayBegin(f1.length);
                    for(int e : f1) {
                        pk.write(e);
                    }
                pk.writeArrayEnd();
                pk.writeArrayBegin(f2.size());
                    for(String e : f2) {
                        pk.write(e);
                    }
                pk.writeArrayEnd();
            pk.writeArrayEnd();
        }

        public void readFrom(Unpacker u) throws IOException {
            u.readArrayBegin();
                f0 = u.readString();
                int nf1 = u.readArrayBegin();
                    f1 = new int[nf1];
                    for(int i=0; i < nf1; i++) {
                        f1[i] = u.readInt();
                    }
                u.readArrayEnd();
                int nf2 = u.readArrayBegin();
                    f2 = new ArrayList<String>(nf2);
                    for(int i=0; i < nf2; i++) {
                        f2.add(u.readString());
                    }
                u.readArrayEnd();
            u.readArrayEnd();
        }
    }

    @Test
    public void testSample01() throws IOException {
        MessagePack msgpack = new MessagePack();

        Sample01 a = new Sample01();
        a.f0 = "aaa";
        a.f1 = new int[3];
        a.f1[0] = 1010;
        a.f1[1] = 2020;
        a.f1[2] = 3030;
        a.f2 = new ArrayList<String>();
        a.f2.add("xx");
        a.f2.add("yy");

        BufferPacker pk = msgpack.createBufferPacker();
        a.writeTo(pk);

        byte[] raw = pk.toByteArray();

        BufferUnpacker u = msgpack.createBufferUnpacker().wrap(raw);
        Sample01 b = new Sample01();
        b.readFrom(u);

        assertEquals(a.f0, b.f0);
        assertArrayEquals(a.f1, b.f1);
        assertEquals(a.f2, b.f2);
    }

    // some files are OPTIONAL or NULLABLE
    public static class Sample02 implements MessagePackable {
        public String f0;         // nullable
        public long f1;           // primitive
        public Integer f2;        // required
        public String f3;         // optional

        public Sample02() { }

        public void writeTo(Packer pk) throws IOException {
            pk.writeArrayBegin(4);
                pk.write(f0);
                pk.write(f1);
                if(f2 == null) {
                    throw new MessageTypeException("f2 is required but null");
                }
                pk.write(f2);
                pk.write(f3);
            pk.writeArrayEnd();
        }

        public void readFrom(Unpacker u) throws IOException {
            u.readArrayBegin();
                f0 = u.read(String.class);
                f1 = u.readLong();
                if(u.trySkipNil()) {
                    f2 = null;
                } else {
                    f2 = u.read(Integer.class);
                }
                if(u.trySkipNil()) {
                    f3 = null;
                } else {
                    f3 = u.read(String.class);
                }
            u.readArrayEnd();
        }
    }

    @Test
    public void testSample02() throws IOException {
        MessagePack msgpack = new MessagePack();

        Sample02 a = new Sample02();
        a.f0 = "aaa";
        a.f1 = 1;
        a.f2 = 22;
        a.f3 = null;

        BufferPacker pk = msgpack.createBufferPacker();
        a.writeTo(pk);

        byte[] raw = pk.toByteArray();

        BufferUnpacker u = msgpack.createBufferUnpacker().wrap(raw);
        Sample02 b = new Sample02();
        b.readFrom(u);

        assertEquals(a.f0, b.f0);
        assertEquals(a.f1, b.f1);
        assertEquals(a.f2, b.f2);
        assertEquals(a.f3, b.f3);
    }
}

