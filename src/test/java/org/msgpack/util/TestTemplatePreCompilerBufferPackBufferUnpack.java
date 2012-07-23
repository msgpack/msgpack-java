package org.msgpack.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.Template;
import org.msgpack.template.builder.TestSet;
import org.msgpack.testclasses.FinalClass;
import org.msgpack.testclasses.InheritanceClass;
import org.msgpack.testclasses.InheritanceClassNotNullable;
import org.msgpack.testclasses.ListTypeFieldsClass;
import org.msgpack.testclasses.ListTypeFieldsClassNotNullable;
import org.msgpack.testclasses.MapTypeFieldsClass;
import org.msgpack.testclasses.MapTypeFieldsClassNotNullable;
import org.msgpack.testclasses.MessagePackableTypeFieldsClass;
import org.msgpack.testclasses.MessagePackableTypeFieldsClassNotNullable;
import org.msgpack.testclasses.ModifiersFieldsClass;
import org.msgpack.testclasses.ModifiersFieldsClassNotNullable;
import org.msgpack.testclasses.PrimitiveTypeFieldsClass;
import org.msgpack.testclasses.PrimitiveTypeFieldsClassNotNullable;
import org.msgpack.testclasses.ReferenceCycleTypeFieldsClass;
import org.msgpack.testclasses.ReferenceCycleTypeFieldsClassNotNullable;
import org.msgpack.testclasses.ReferenceTypeFieldsClass;
import org.msgpack.testclasses.ReferenceTypeFieldsClassNotNullable;
import org.msgpack.testclasses.UserDefinedTypeFieldsClass;
import org.msgpack.testclasses.UserDefinedTypeFieldsClassNotNullable;
import org.msgpack.unpacker.BufferUnpacker;

public class TestTemplatePreCompilerBufferPackBufferUnpack extends TestSet {

    @Test
    @Override
    public void testPrimitiveTypeFieldsClass() throws Exception {
        super.testPrimitiveTypeFieldsClass();
    }

    @Override
    public void testPrimitiveTypeFieldsClass(PrimitiveTypeFieldsClass v)
            throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    PrimitiveTypeFieldsClass.class);
            Template<PrimitiveTypeFieldsClass> tmpl = msgpack
                    .lookup(PrimitiveTypeFieldsClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            PrimitiveTypeFieldsClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(PrimitiveTypeFieldsClass.class);
            msgpack.unregister(PrimitiveTypeFieldsClass.class);
        }
    }

    @Test
    @Override
    public void testPrimitiveTypeFieldsClassNotNullable() throws Exception {
        super.testPrimitiveTypeFieldsClassNotNullable();
    }

    @Override
    public void testPrimitiveTypeFieldsClassNotNullable(
            PrimitiveTypeFieldsClassNotNullable v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    PrimitiveTypeFieldsClassNotNullable.class);
            Template<PrimitiveTypeFieldsClassNotNullable> tmpl = msgpack
                    .lookup(PrimitiveTypeFieldsClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            PrimitiveTypeFieldsClassNotNullable ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(PrimitiveTypeFieldsClassNotNullable.class);
            msgpack.unregister(PrimitiveTypeFieldsClassNotNullable.class);
        }
    }

    @Test
    @Override
    public void testReferenceTypeFieldsClass() throws Exception {
        super.testReferenceTypeFieldsClass();
    }

    @Override
    public void testReferenceTypeFieldsClass(ReferenceTypeFieldsClass v)
            throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    ReferenceTypeFieldsClass.class);
            Template<ReferenceTypeFieldsClass> tmpl = msgpack
                    .lookup(ReferenceTypeFieldsClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            ReferenceTypeFieldsClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(ReferenceTypeFieldsClass.class);
            msgpack.unregister(ReferenceTypeFieldsClass.class);
        }
    }

    @Test
    @Override
    public void testReferenceTypeFieldsClassNotNullable() throws Exception {
        super.testReferenceTypeFieldsClassNotNullable();
    }

    @Override
    public void testReferenceTypeFieldsClassNotNullable(
            ReferenceTypeFieldsClassNotNullable v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    ReferenceTypeFieldsClassNotNullable.class);
            Template<ReferenceTypeFieldsClassNotNullable> tmpl = msgpack
                    .lookup(ReferenceTypeFieldsClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            ReferenceTypeFieldsClassNotNullable ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(ReferenceTypeFieldsClassNotNullable.class);
            msgpack.unregister(ReferenceTypeFieldsClassNotNullable.class);
        }
    }

    @Test
    @Override
    public void testListTypeFieldsClass() throws Exception {
        super.testListTypeFieldsClass();
    }

    @Override
    public void testListTypeFieldsClass(ListTypeFieldsClass v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    ListTypeFieldsClass.class);
            Template<ListTypeFieldsClass> tmpl = msgpack
                    .lookup(ListTypeFieldsClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            ListTypeFieldsClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler.deleteTemplateClass(ListTypeFieldsClass.class);
            msgpack.unregister(ListTypeFieldsClass.class);
        }
    }

    @Test
    @Override
    public void testListTypeFieldsClassNotNullable() throws Exception {
        super.testListTypeFieldsClassNotNullable();
    }

    @Override
    public void testListTypeFieldsClassNotNullable(
            ListTypeFieldsClassNotNullable v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    ListTypeFieldsClassNotNullable.class);
            Template<ListTypeFieldsClassNotNullable> tmpl = msgpack
                    .lookup(ListTypeFieldsClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            ListTypeFieldsClassNotNullable ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(ListTypeFieldsClassNotNullable.class);
            msgpack.unregister(ListTypeFieldsClassNotNullable.class);
        }
    }

    @Test
    @Override
    public void testMapTypeFieldsClass() throws Exception {
        super.testMapTypeFieldsClass();
    }

    @Override
    public void testMapTypeFieldsClass(MapTypeFieldsClass v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    MapTypeFieldsClass.class);
            Template<MapTypeFieldsClass> tmpl = msgpack
                    .lookup(MapTypeFieldsClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            MapTypeFieldsClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler.deleteTemplateClass(MapTypeFieldsClass.class);
            msgpack.unregister(MapTypeFieldsClass.class);
        }
    }

    @Test
    @Override
    public void testMapTypeFieldsClassNotNullable() throws Exception {
        super.testMapTypeFieldsClassNotNullable();
    }

    @Override
    public void testMapTypeFieldsClassNotNullable(
            MapTypeFieldsClassNotNullable v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    MapTypeFieldsClassNotNullable.class);
            Template<MapTypeFieldsClassNotNullable> tmpl = msgpack
                    .lookup(MapTypeFieldsClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            MapTypeFieldsClassNotNullable ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(MapTypeFieldsClassNotNullable.class);
            msgpack.unregister(MapTypeFieldsClassNotNullable.class);
        }
    }

    @Test
    @Override
    public void testFinalClass() throws Exception {
        super.testFinalClass();
    }

    @Override
    public void testFinalClass(FinalClass v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    FinalClass.class);
            Template<FinalClass> tmpl = msgpack.lookup(FinalClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            FinalClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler.deleteTemplateClass(FinalClass.class);
            msgpack.unregister(FinalClass.class);
        }
    }

    @Test
    @Override
    public void testModifiersFieldsClass() throws Exception {
        super.testModifiersFieldsClass();
    }

    @Override
    public void testModifiersFieldsClass(ModifiersFieldsClass v)
            throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    ModifiersFieldsClass.class);
            Template<ModifiersFieldsClass> tmpl = msgpack
                    .lookup(ModifiersFieldsClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            ModifiersFieldsClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler.deleteTemplateClass(ModifiersFieldsClass.class);
            msgpack.unregister(ModifiersFieldsClass.class);
        }
    }

    @Test
    @Override
    public void testModifiersFieldsClassNotNullable() throws Exception {
        super.testModifiersFieldsClassNotNullable();
    }

    @Override
    public void testModifiersFieldsClassNotNullable(
            ModifiersFieldsClassNotNullable v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    ModifiersFieldsClassNotNullable.class);
            Template<ModifiersFieldsClassNotNullable> tmpl = msgpack
                    .lookup(ModifiersFieldsClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            ModifiersFieldsClassNotNullable ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(ModifiersFieldsClassNotNullable.class);
            msgpack.unregister(ModifiersFieldsClassNotNullable.class);
        }
    }

    @Test
    @Override
    public void testUserDefinedTypeFieldsClass() throws Exception {
        super.testUserDefinedTypeFieldsClass();
    }

    @Override
    public void testUserDefinedTypeFieldsClass(UserDefinedTypeFieldsClass v)
            throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    UserDefinedTypeFieldsClass.class);
            Template<UserDefinedTypeFieldsClass> tmpl = msgpack
                    .lookup(UserDefinedTypeFieldsClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            UserDefinedTypeFieldsClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(UserDefinedTypeFieldsClass.class);
            msgpack.unregister(UserDefinedTypeFieldsClass.class);
        }
    }

    @Test
    @Override
    public void testUserDefinedTypeFieldsClassNotNullable() throws Exception {
        super.testUserDefinedTypeFieldsClassNotNullable();
    }

    @Override
    public void testUserDefinedTypeFieldsClassNotNullable(
            UserDefinedTypeFieldsClassNotNullable v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    UserDefinedTypeFieldsClassNotNullable.class);
            Template<UserDefinedTypeFieldsClassNotNullable> tmpl = msgpack
                    .lookup(UserDefinedTypeFieldsClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            UserDefinedTypeFieldsClassNotNullable ret = tmpl.read(unpacker,
                    null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(UserDefinedTypeFieldsClassNotNullable.class);
            msgpack.unregister(UserDefinedTypeFieldsClassNotNullable.class);
        }
    }

    @org.junit.Ignore
    @Test
    @Override
    // FIXME #MN next version
    public void testReferenceCycleTypeFieldsClass() throws Exception {
        super.testReferenceCycleTypeFieldsClass();
    }

    @org.junit.Ignore
    @Override
    // FIXME #MN next version
    public void testReferenceCycleTypeFieldsClass(
            ReferenceCycleTypeFieldsClass v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    ReferenceCycleTypeFieldsClass.class);
            Template<ReferenceCycleTypeFieldsClass> tmpl = msgpack
                    .lookup(ReferenceCycleTypeFieldsClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            ReferenceCycleTypeFieldsClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(ReferenceCycleTypeFieldsClass.class);
            msgpack.unregister(ReferenceCycleTypeFieldsClass.class);
        }
    }

    @org.junit.Ignore
    @Test
    @Override
    // FIXME #MN next version
    public void testReferenceCycleTypeFieldsClassNotNullable() throws Exception {
        super.testReferenceCycleTypeFieldsClassNotNullable();
    }

    @org.junit.Ignore
    @Override
    // FIXME #MN next version
    public void testReferenceCycleTypeFieldsClassNotNullable(
            ReferenceCycleTypeFieldsClassNotNullable v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    ReferenceCycleTypeFieldsClassNotNullable.class);
            Template<ReferenceCycleTypeFieldsClassNotNullable> tmpl = msgpack
                    .lookup(ReferenceCycleTypeFieldsClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            ReferenceCycleTypeFieldsClassNotNullable ret = tmpl.read(unpacker,
                    null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(ReferenceCycleTypeFieldsClassNotNullable.class);
            msgpack.unregister(ReferenceCycleTypeFieldsClassNotNullable.class);
        }
    }

    @Test
    @Override
    public void testInheritanceClass() throws Exception {
        super.testInheritanceClass();
    }

    @Override
    public void testInheritanceClass(InheritanceClass v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    InheritanceClass.class);
            Template<InheritanceClass> tmpl = msgpack
                    .lookup(InheritanceClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            InheritanceClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler.deleteTemplateClass(InheritanceClass.class);
            msgpack.unregister(InheritanceClass.class);
        }
    }

    @Test
    @Override
    public void testInheritanceClassNotNullable() throws Exception {
        super.testInheritanceClassNotNullable();
    }

    @Override
    public void testInheritanceClassNotNullable(InheritanceClassNotNullable v)
            throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    InheritanceClassNotNullable.class);
            Template<InheritanceClassNotNullable> tmpl = msgpack
                    .lookup(InheritanceClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            InheritanceClassNotNullable ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(InheritanceClassNotNullable.class);
            msgpack.unregister(InheritanceClassNotNullable.class);
        }
    }

    @Test
    @Override
    public void testMessagePackableTypeFieldsClass() throws Exception {
        super.testMessagePackableTypeFieldsClass();
    }

    @Override
    public void testMessagePackableTypeFieldsClass(
            MessagePackableTypeFieldsClass v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    MessagePackableTypeFieldsClass.class);
            Template<MessagePackableTypeFieldsClass> tmpl = msgpack
                    .lookup(MessagePackableTypeFieldsClass.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            MessagePackableTypeFieldsClass ret = tmpl.read(unpacker, null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(MessagePackableTypeFieldsClass.class);
            msgpack.unregister(MessagePackableTypeFieldsClass.class);
        }
    }

    @Test
    @Override
    public void testMessagePackableTypeFieldsClassNotNullable()
            throws Exception {
        super.testMessagePackableTypeFieldsClassNotNullable();
    }

    @Override
    public void testMessagePackableTypeFieldsClassNotNullable(
            MessagePackableTypeFieldsClassNotNullable v) throws Exception {
        System.getProperties().setProperty(TemplatePrecompiler.DEST,
                "./target/test-classes");
        MessagePack msgpack = new MessagePack();
        try {
            TemplatePrecompiler.saveTemplateClass(new TemplateRegistry(null),
                    MessagePackableTypeFieldsClassNotNullable.class);
            Template<MessagePackableTypeFieldsClassNotNullable> tmpl = msgpack
                    .lookup(MessagePackableTypeFieldsClassNotNullable.class);
            BufferPacker packer = msgpack.createBufferPacker();
            tmpl.write(packer, v);
            byte[] bytes = packer.toByteArray();
            BufferUnpacker unpacker = msgpack.createBufferUnpacker();
            unpacker.wrap(bytes);
            MessagePackableTypeFieldsClassNotNullable ret = tmpl.read(unpacker,
                    null);
            assertEquals(v, ret);
        } finally {
            TemplatePrecompiler
                    .deleteTemplateClass(MessagePackableTypeFieldsClassNotNullable.class);
            msgpack.unregister(MessagePackableTypeFieldsClassNotNullable.class);
        }
    }

}
