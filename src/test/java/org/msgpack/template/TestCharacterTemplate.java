package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.TestSet;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Unpacker;

public class TestCharacterTemplate {

    @Test
    public void testPackUnpack() throws Exception {
        new TestPackUnpack().testCharacter();
    }

    @Test
    public void testPackBufferUnpack() throws Exception {
        new TestPackBufferUnpack().testCharacter();
    }

    @Test
    public void testBufferPackBufferUnpack() throws Exception {
        new TestBufferPackBufferUnpack().testCharacter();
    }

    @Test
    public void testBufferPackUnpack() throws Exception {
        new TestBufferPackUnpack().testCharacter();
    }

    private static class TestPackUnpack extends TestSet {
        @Test @Override
        public void testCharacter() throws Exception {
            super.testCharacter();
        }

        @Override
        public void testCharacter(Character v) throws Exception {
            MessagePack msgpack = new MessagePack();
            Template<Character> tmpl = CharacterTemplate.instance;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            tmpl.write(packer, v);
            byte[] bytes = out.toByteArray();
            Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
            Character ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        }
    }

    private static class TestPackBufferUnpack extends TestSet {
        @Test @Override
        public void testCharacter() throws Exception {
            super.testCharacter();
        }

        @Override
        public void testCharacter(Character v) throws Exception {
            MessagePack msgpack = new MessagePack();
            Template<Character> tmpl = CharacterTemplate.instance;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Packer packer = msgpack.createPacker(out);
            tmpl.write(packer, v);
            byte[] bytes = out.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
            Character ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        }
    }

    private static class TestBufferPackBufferUnpack extends TestSet {
        @Test @Override
        public void testCharacter() throws Exception {
            super.testCharacter();
        }

        @Override
        public void testCharacter(Character v) throws Exception {
            MessagePack msgpack = new MessagePack();
            Template<Character> tmpl = CharacterTemplate.instance;
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker(bytes);
            Character ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        }
    }

    private static class TestBufferPackUnpack extends TestSet {
        @Test @Override
        public void testCharacter() throws Exception {
            super.testCharacter();
        }

        @Override
        public void testCharacter(Character v) throws Exception {
            MessagePack msgpack = new MessagePack();
            Template<Character> tmpl = CharacterTemplate.instance;
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            Unpacker unpacker = msgpack.createUnpacker(new ByteArrayInputStream(bytes));
            Character ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        }
    }
}
