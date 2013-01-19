package org.msgpack;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.msgpack.annotation.Message;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Unconverter;
import org.msgpack.type.Value;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Converter;

public class TestSimpleArrays {

    @Message
    public static class PrimitiveTest {
        public boolean[] b = new boolean[0];
        public short[] s = new short[0];
        public int[] i = new int[0];
        // public long[] l = new long[0]; // FIXME javassist?
        public float[] f = new float[0];

        // public double[] d = new double[0]; // FIXME javassist?

        public PrimitiveTest() {
        }
    }

    @Test
    public void testPrimitive() throws Exception {
        MessagePack msgpack = new MessagePack();

        PrimitiveTest t = new PrimitiveTest();
        t.b = new boolean[] { true, false };
        t.s = new short[] { 0, 1 };
        t.i = new int[] { 2, 3 };
        // t.l = new long[] {4, 5};
        t.f = new float[] { 2.0f, 4.0f };
        // t.d = new double[] {8.0, 16.0};

        BufferPacker packer = msgpack.createBufferPacker();
        packer.write(t);
        byte[] raw = packer.toByteArray();
        BufferUnpacker unpacker = msgpack.createBufferUnpacker(raw);
        PrimitiveTest u = unpacker.read(PrimitiveTest.class);
        assertEquals(t.b.length, u.b.length);
        for (int i = 0; i < t.b.length; i++) {
            assertEquals(t.b[i], u.b[i]);
        }
        assertEquals(t.s.length, u.s.length);
        for (int i = 0; i < t.s.length; i++) {
            assertEquals(t.s[i], u.s[i]);
        }
        assertEquals(t.i.length, u.i.length);
        for (int i = 0; i < t.i.length; i++) {
            assertEquals(t.i[i], u.i[i]);
        }
        // assertEquals(t.l.length, u.l.length);
        // for(int i=0; i < t.l.length; i++) { assertEquals(t.l[i], u.l[i]); }
        assertEquals(t.f.length, u.f.length);
        for (int i = 0; i < t.f.length; i++) {
            assertEquals(t.f[i], u.f[i], 10e-10);
        }
        // assertEquals(t.d.length, u.d.length);
        // for(int i=0; i < t.d.length; i++) { assertEquals(t.d[i], u.d[i]); }

        Unconverter unconverter = new Unconverter(msgpack);
        unconverter.write(t);
        Value value = unconverter.getResult();
        Converter converter = new Converter(msgpack, value);
        PrimitiveTest c = converter.read(PrimitiveTest.class);
        assertEquals(t.b.length, c.b.length);
        for (int i = 0; i < t.b.length; i++) {
            assertEquals(t.b[i], c.b[i]);
        }
        assertEquals(t.s.length, c.s.length);
        for (int i = 0; i < t.s.length; i++) {
            assertEquals(t.s[i], c.s[i]);
        }
        assertEquals(t.i.length, c.i.length);
        for (int i = 0; i < t.i.length; i++) {
            assertEquals(t.i[i], c.i[i]);
        }
        // assertEquals(t.l.length, c.l.length);
        // for(int i=0; i < t.l.length; i++) { assertEquals(t.l[i], c.l[i]); }
        assertEquals(t.f.length, c.f.length);
        for (int i = 0; i < t.f.length; i++) {
            assertEquals(t.f[i], c.f[i], 10e-10);
        }
        // assertEquals(t.d.length, c.d.length);
        // for(int i=0; i < t.d.length; i++) { assertEquals(t.d[i], c.d[i]); }
    }

    @Message
    public static class ReferenceTest {
        public ReferenceTest() {
        }

        public Boolean[] b;
        public Short[] s;
        public Integer[] i;
        public Long[] l;
        public Float[] f;
        public Double[] d;
        public String[] str;
    }

    @Test
    public void testReference() throws Exception {
        MessagePack msgpack = new MessagePack();

        ReferenceTest t = new ReferenceTest();
        t.b = new Boolean[] { true, false };
        t.s = new Short[] { 0, 1 };
        t.i = new Integer[] { 2, 3 };
        t.l = new Long[] { 4l, 5l };
        t.f = new Float[] { 2.0f, 4.0f };
        t.d = new Double[] { 8.0, 16.0 };
        t.str = new String[] { "furuhashi", "java" };

        BufferPacker packer = msgpack.createBufferPacker();
        packer.write(t);
        byte[] raw = packer.toByteArray();
        BufferUnpacker unpacker = msgpack.createBufferUnpacker(raw);
        ReferenceTest u = unpacker.read(ReferenceTest.class);
        assertEquals(t.b.length, u.b.length);
        for (int i = 0; i < t.b.length; i++) {
            assertEquals(t.b[i], u.b[i]);
        }
        assertEquals(t.s.length, u.s.length);
        for (int i = 0; i < t.s.length; i++) {
            assertEquals(t.s[i], u.s[i]);
        }
        assertEquals(t.i.length, u.i.length);
        for (int i = 0; i < t.i.length; i++) {
            assertEquals(t.i[i], u.i[i]);
        }
        assertEquals(t.l.length, u.l.length);
        for (int i = 0; i < t.l.length; i++) {
            assertEquals(t.l[i], u.l[i]);
        }
        assertEquals(t.f.length, u.f.length);
        for (int i = 0; i < t.f.length; i++) {
            assertEquals(t.f[i], u.f[i]);
        }
        assertEquals(t.d.length, u.d.length);
        for (int i = 0; i < t.d.length; i++) {
            assertEquals(t.d[i], u.d[i]);
        }
        assertEquals(t.str.length, u.str.length);
        for (int i = 0; i < t.str.length; i++) {
            assertEquals(t.str[i], u.str[i]);
        }

        Unconverter unconverter = new Unconverter(msgpack);
        unconverter.write(t);
        Value value = unconverter.getResult();
        Converter converter = new Converter(msgpack, value);
        ReferenceTest c = converter.read(ReferenceTest.class);
        assertEquals(t.b.length, c.b.length);
        for (int i = 0; i < t.b.length; i++) {
            assertEquals(t.b[i], c.b[i]);
        }
        assertEquals(t.s.length, c.s.length);
        for (int i = 0; i < t.s.length; i++) {
            assertEquals(t.s[i], c.s[i]);
        }
        assertEquals(t.i.length, c.i.length);
        for (int i = 0; i < t.i.length; i++) {
            assertEquals(t.i[i], c.i[i]);
        }
        assertEquals(t.l.length, c.l.length);
        for (int i = 0; i < t.l.length; i++) {
            assertEquals(t.l[i], c.l[i]);
        }
        assertEquals(t.f.length, c.f.length);
        for (int i = 0; i < t.f.length; i++) {
            assertEquals(t.f[i], c.f[i]);
        }
        assertEquals(t.d.length, c.d.length);
        for (int i = 0; i < t.d.length; i++) {
            assertEquals(t.d[i], c.d[i]);
        }
        assertEquals(t.str.length, c.str.length);
        for (int i = 0; i < t.str.length; i++) {
            assertEquals(t.str[i], c.str[i]);
        }
    }

    @Message
    public static class GenericsTest {
        public List<String>[] slist;
        public Map<String, Integer>[] imap;

        public GenericsTest() {
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Ignore
    @Test
    public void testGenerics() throws Exception {
        MessagePack msgpack = new MessagePack();

        GenericsTest t = new GenericsTest();
        t.slist = new List[2];
        t.slist[0] = new ArrayList();
        t.slist[0].add("aa");
        t.slist[0].add("bb");
        t.slist[1] = new ArrayList();
        t.slist[1].add("cc");
        t.imap = new Map[2];
        t.imap[0] = new HashMap();
        t.imap[0].put("aa", 1);
        t.imap[0].put("bb", 2);
        t.imap[1] = new HashMap();
        t.imap[1].put("cc", 3);

        BufferPacker packer = msgpack.createBufferPacker();
        packer.write(t);
        byte[] raw = packer.toByteArray();
        BufferUnpacker unpacker = msgpack.createBufferUnpacker(raw);
        GenericsTest u = unpacker.read(GenericsTest.class);
        assertEquals(t.slist.length, u.slist.length);
        for (int i = 0; i < t.slist.length; i++) {
            assertEquals(t.slist[i].size(), u.slist[i].size());
            for (int j = 0; j < t.slist[i].size(); j++) {
                assertEquals(t.slist[i].get(j), u.slist[i].get(j));
            }
        }
        for (int i = 0; i < t.imap.length; i++) {
            assertEquals(t.imap[i].size(), u.imap[i].size());
            for (String j : t.imap[i].keySet()) {
                assertEquals(t.imap[i].get(j), u.imap[i].get(j));
            }
        }

        Unconverter unconverter = new Unconverter(msgpack);
        unconverter.write(t);
        Value value = unconverter.getResult();
        Converter converter = new Converter(msgpack, value);
        GenericsTest c = converter.read(GenericsTest.class);
        assertEquals(t.slist.length, c.slist.length);
        for (int i = 0; i < t.slist.length; i++) {
            assertEquals(t.slist[i].size(), c.slist[i].size());
            for (int j = 0; j < t.slist[i].size(); j++) {
                assertEquals(t.slist[i].get(j), c.slist[i].get(j));
            }
        }
        for (int i = 0; i < t.imap.length; i++) {
            assertEquals(t.imap[i].size(), c.imap[i].size());
            for (String j : t.imap[i].keySet()) {
                assertEquals(t.imap[i].get(j), c.imap[i].get(j));
            }
        }
    }

    @Message
    public static class Dim2Test {
        public int[][] i;
        public byte[][] b;
        public String[][] str;
        //public List<String>[][] slist;

        public Dim2Test() {
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testDim2() throws Exception {
        MessagePack msgpack = new MessagePack();
        Dim2Test t = new Dim2Test();
        t.i = new int[2][];
        t.i[0] = new int[] { 0, 1 };
        t.i[1] = new int[] { 2, 3, 4 };
        t.b = new byte[2][];
        t.b[0] = new byte[] { 5, 6 };
        t.b[1] = new byte[] { 7, 8, 9 };
        t.str = new String[2][];
        t.str[0] = new String[] { "aa", "bb" };
        t.str[1] = new String[] { "cc", "dd", "ee" };
        /**
        t.slist = new List[2][];
        t.slist[0] = new List[1];
        t.slist[0][0] = new ArrayList();
        t.slist[0][0].add("ff");
        t.slist[0][0].add("gg");
        t.slist[1] = new List[2];
        t.slist[1][0] = new ArrayList();
        t.slist[1][0].add("hh");
        t.slist[1][0].add("ii");
        t.slist[1][1] = new ArrayList();
        t.slist[1][1].add("jj");
        t.slist[1][1].add("kk");
        */

        BufferPacker packer = msgpack.createBufferPacker();
        packer.write(t);
        byte[] raw = packer.toByteArray();
        BufferUnpacker unpacker = msgpack.createBufferUnpacker(raw);
        Dim2Test u = unpacker.read(Dim2Test.class);
        assertEquals(t.i.length, u.i.length);
        for (int i = 0; i < t.i.length; i++) {
            assertEquals(t.i[i].length, u.i[i].length);
            for (int j = 0; j < t.i[i].length; j++) {
                assertEquals(t.i[i][j], u.i[i][j]);
            }
        }
        assertEquals(t.b.length, u.b.length);
        for (int i = 0; i < t.b.length; i++) {
            assertEquals(t.b[i].length, u.b[i].length);
            for (int j = 0; j < t.i[i].length; j++) {
                assertEquals(t.b[i][j], u.b[i][j]);
            }
        }
        assertEquals(t.str.length, u.str.length);
        for (int i = 0; i < t.str.length; i++) {
            assertEquals(t.str[i].length, u.str[i].length);
            for (int j = 0; j < t.str[i].length; j++) {
                assertEquals(t.str[i][j], u.str[i][j]);
            }
        }
        /**
        assertEquals(t.slist.length, u.slist.length);
        for (int i = 0; i < t.slist.length; i++) {
            assertEquals(t.slist[i].length, u.slist[i].length);
            for (int j = 0; j < t.slist[i].length; j++) {
                assertEquals(t.slist[i][j].size(), u.slist[i][j].size());
                for (int k = 0; k < t.slist[i][j].size(); k++) {
                    assertEquals(t.slist[i][j].get(k), u.slist[i][j].get(k));
                }
            }
        }
        */
    }

    @Message
    public static class Dim3Test {
        public int[][][] i;
        public String[][][] str;
        public List<String>[][][] slist;

        public Dim3Test() {
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Ignore
    @Test
    public void testDim3() throws Exception {
        MessagePack msgpack = new MessagePack();

        Dim3Test t = new Dim3Test();
        t.i = new int[2][][];
        t.i[0] = new int[2][];
        t.i[0][0] = new int[] { 0, 1 };
        t.i[0][1] = new int[] { 2, 3, 4 };
        t.i[1] = new int[1][];
        t.i[1][0] = new int[] { 5 };
        t.str = new String[2][][];
        t.str[0] = new String[1][];
        t.str[0][0] = new String[] { "aa", "bb" };
        t.str[1] = new String[2][];
        t.str[1][0] = new String[] { "cc", "dd", "ee" };
        t.str[1][1] = new String[] { "ff" };
        t.slist = new List[2][][];
        t.slist[0] = new List[2][];
        t.slist[0][0] = new List[1];
        t.slist[0][0][0] = new ArrayList();
        t.slist[0][0][0].add("ff");
        t.slist[0][0][0].add("gg");
        t.slist[0][1] = new List[2];
        t.slist[0][1][0] = new ArrayList();
        t.slist[0][1][0].add("hh");
        t.slist[0][1][0].add("ii");
        t.slist[0][1][1] = new ArrayList();
        t.slist[0][1][1].add("jj");
        t.slist[0][1][1].add("kk");
        t.slist[1] = new List[1][];
        t.slist[1][0] = new List[0];

        BufferPacker packer = msgpack.createBufferPacker();
        packer.write(t);
        byte[] raw = packer.toByteArray();
        BufferUnpacker unpacker = msgpack.createBufferUnpacker(raw);
        Dim3Test u = unpacker.read(Dim3Test.class);
        assertEquals(t.i.length, t.i.length);
        for (int i = 0; i < t.i.length; i++) {
            assertEquals(t.i[i].length, u.i[i].length);
            for (int j = 0; j < t.i[i].length; j++) {
                for (int k = 0; k < t.i[i].length; k++) {
                    assertEquals(t.i[i][j][k], u.i[i][j][k]);
                }
            }
        }
        assertEquals(t.str.length, t.str.length);
        for (int i = 0; i < t.str.length; i++) {
            assertEquals(t.str[i].length, u.str[i].length);
            for (int j = 0; j < t.str[i].length; j++) {
                assertEquals(t.str[i][j].length, u.str[i][j].length);
                for (int k = 0; k < t.str[i][j].length; k++) {
                    assertEquals(t.str[i][j][k], u.str[i][j][k]);
                }
            }
        }
        assertEquals(t.slist.length, t.slist.length);
        for (int i = 0; i < t.slist.length; i++) {
            assertEquals(t.slist[i].length, u.slist[i].length);
            for (int j = 0; j < t.slist[i].length; j++) {
                assertEquals(t.slist[i][j].length, u.slist[i][j].length);
                for (int k = 0; k < t.slist[i][j].length; k++) {
                    assertEquals(t.slist[i][j][k].size(),
                            u.slist[i][j][k].size());
                    for (int l = 0; l < t.slist[i][j][k].size(); l++) {
                        assertEquals(t.slist[i][j][k].get(l),
                                u.slist[i][j][k].get(l));
                    }
                }
            }
        }
    }

    @Test
    public void testLocal() throws IOException {
        MessagePack msgpack = new MessagePack();

        int[][][] src = new int[10][20][30];
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 20; ++j) {
                for (int k = 0; k < 30; ++k) {
                    src[i][j][k] = (int) (Math.random() * 100);
                }
            }
        }

        BufferPacker packer = msgpack.createBufferPacker();
        packer.write(src);
        byte[] raw = packer.toByteArray();
        BufferUnpacker unpacker = msgpack.createBufferUnpacker(raw);
        int[][][] u = unpacker.read(int[][][].class);
        assertEquals(src.length, u.length);
        for (int i = 0; i < src.length; ++i) {
            assertEquals(src[i].length, u[i].length);
            for (int j = 0; j < src[i].length; ++j) {
                assertEquals(src[i][j].length, u[i][j].length);
                for (int k = 0; k < src[i][j].length; ++k) {
                    assertEquals(src[i][j][k], u[i][j][k]);
                }
            }
        }

        Unconverter unconverter = new Unconverter(msgpack);
        unconverter.write(src);
        Value value = unconverter.getResult();
        Converter converter = new Converter(msgpack, value);
        int[][][] c = converter.read(int[][][].class);
        assertEquals(src.length, c.length);
        for (int i = 0; i < src.length; ++i) {
            assertEquals(src[i].length, c[i].length);
            for (int j = 0; j < src[i].length; ++j) {
                assertEquals(src[i][j].length, c[i][j].length);
                for (int k = 0; k < src[i][j].length; ++k) {
                    assertEquals(src[i][j][k], c[i][j][k]);
                }
            }
        }
    }
}
