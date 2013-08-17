package org.msgpack.unpacker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.ListTemplate;
import org.msgpack.template.MapTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;
import org.msgpack.unpacker.SizeLimitException;
import org.msgpack.unpacker.Unpacker;

public class TestSizeLimit {

    @Test
    public void testRawSizeLimit() throws Exception {
        MessagePack msgpack = new MessagePack();
        Template<byte[]> tmpl = Templates.TByteArray;
        { // set limit == 10, size < 10
            int len = 9;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(10);
            byte[] dst = unpacker.read(tmpl);
            assertEquals(src.length, dst.length);
        }
        { // set limit == 10, size == 10
            int len = 10;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(10);
            byte[] dst = unpacker.read(tmpl);
            assertEquals(src.length, dst.length);
        }
        { // set limit == 10, 10 < size < 32
            int len = 11;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(10);
            byte[] dst = unpacker.read(tmpl);
            assertEquals(src.length, dst.length);
        }
        { // set limit == 10, size == 32
            int len = 32;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(10);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 10, size > 32
            int len = 33;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(10);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 33, size < 33
            int len = 32;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(33);
            byte[] dst = unpacker.read(tmpl);
            assertEquals(src.length, dst.length);
        }
        { // set limit == 33, size == 33
            int len = 33;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(33);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 33, size > 33
            int len = 34;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(33);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 65536, size < 65536
            int len = 65535;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(65536);
            byte[] dst = unpacker.read(tmpl);
            assertEquals(src.length, dst.length);
        }
        { // set limit == 65536, size == 65536
            int len = 65536;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(65536);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 65536, size > 65536
            int len = 65537;
            byte[] src = new byte[len];
            for (int i = 0; i < len; i++) {
                src[i] = 0x0a;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setRawSizeLimit(65536);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
    }

    @Test
    public void testArraySizeLimit() throws Exception {
        MessagePack msgpack = new MessagePack();
        Template<List<Integer>> tmpl = new ListTemplate<Integer>(
                Templates.TInteger);
        { // set limit == 10, size < 10
            int len = 9;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(10);
            List<Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 10, size == 10
            int len = 10;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(10);
            List<Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 10, 10 < size < 16
            int len = 11;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(10);
            List<Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 10, size == 16
            int len = 16;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(10);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 10, size > 16
            int len = 17;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(10);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 20, size < 20
            int len = 19;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(20);
            List<Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 20, size == 20
            int len = 20;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(20);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 20, size > 20
            int len = 21;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(20);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 65536, size < 65536
            int len = 65535;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(65536);
            List<Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 65536, size == 65536
            int len = 65536;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(65536);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 65536, size > 65536
            int len = 65537;
            List<Integer> src = new ArrayList<Integer>(len);
            for (int i = 0; i < len; i++) {
                src.add(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setArraySizeLimit(65536);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
    }

    @Test
    public void testMapSizeLimit() throws Exception {
        MessagePack msgpack = new MessagePack();
        Template<Map<Integer, Integer>> tmpl = new MapTemplate<Integer, Integer>(
                Templates.TInteger, Templates.TInteger);
        { // set limit == 10, size < 10
            int len = 9;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(10);
            Map<Integer, Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 10, size == 10
            int len = 10;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(10);
            Map<Integer, Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 10, 10 < size < 16
            int len = 11;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(10);
            Map<Integer, Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 10, size == 16
            int len = 16;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(10);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 10, size > 16
            int len = 17;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(10);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 20, size < 20
            int len = 19;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(20);
            Map<Integer, Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 20, size == 20
            int len = 20;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(20);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 20, size > 20
            int len = 21;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(20);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 65536, size < 65536
            int len = 65535;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(65536);
            Map<Integer, Integer> dst = unpacker.read(tmpl);
            assertEquals(src.size(), dst.size());
        }
        { // set limit == 65536, size == 65536
            int len = 65536;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(65536);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
        { // set limit == 65536, size > 65536
            int len = 65537;
            Map<Integer, Integer> src = new HashMap<Integer, Integer>(len);
            for (int i = 0; i < len; i++) {
                src.put(i, i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            packer.write(src);
            byte[] bytes = out.toByteArray();

            Unpacker unpacker = msgpack
                    .createUnpacker(new ByteArrayInputStream(bytes));
            unpacker.setMapSizeLimit(65536);
            try {
                unpacker.read(tmpl);
                fail();
            } catch (Throwable t) {
                assertTrue(t instanceof SizeLimitException);
            }
        }
    }

}
