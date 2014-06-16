package org.msgpack;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.msgpack.testclasses.EnumTypeFieldsClass;
import org.msgpack.testclasses.EnumTypeFieldsClassNotNullable;
import org.msgpack.testclasses.IndexedFieldsBeanClass;
import org.msgpack.testclasses.IndexedFieldsBeanClassNotNullable;
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
import org.msgpack.type.Value;


public class TestMessagePack02 {

    @Test
    public void testPrimitiveTypeFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestPrimitiveTypeFieldsClassBufferPackBufferUnpack().testPrimitiveTypeFieldsClass();
    }

    @Test
    public void testPrimitiveTypeFieldsClassBufferPackConvert() throws Exception {
	new TestPrimitiveTypeFieldsClassBufferPackConvert().testPrimitiveTypeFieldsClass();
    }

    @Test
    public void testPrimitiveTypeFieldsClassBufferPackUnpack() throws Exception {
	new TestPrimitiveTypeFieldsClassBufferPackUnpack().testPrimitiveTypeFieldsClass();
    }

    @Test
    public void testPrimitiveTypeFieldsClassPackBufferUnpack() throws Exception {
	new TestPrimitiveTypeFieldsClassPackBufferUnpack().testPrimitiveTypeFieldsClass();
    }

    @Test
    public void testPrimitiveTypeFieldsClassPackConvert() throws Exception {
	new TestPrimitiveTypeFieldsClassPackConvert().testPrimitiveTypeFieldsClass();
    }

    @Test
    public void testPrimitiveTypeFieldsClassPackUnpack() throws Exception {
	new TestPrimitiveTypeFieldsClassPackUnpack().testPrimitiveTypeFieldsClass();
    }

    @Test
    public void testPrimitiveTypeFieldsClassUnconvertConvert() throws Exception {
	new TestPrimitiveTypeFieldsClassUnconvertConvert().testPrimitiveTypeFieldsClass();
    }

    @Test
    public void testPrimitiveTypeFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestPrimitiveTypeFieldsClassNotNullableBufferPackBufferUnpack().testPrimitiveTypeFieldsClassNotNullable();
    }

    @Test
    public void testPrimitiveTypeFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestPrimitiveTypeFieldsClassNotNullableBufferPackConvert().testPrimitiveTypeFieldsClassNotNullable();
    }

    @Test
    public void testPrimitiveTypeFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestPrimitiveTypeFieldsClassNotNullableBufferPackUnpack().testPrimitiveTypeFieldsClassNotNullable();
    }

    @Test
    public void testPrimitiveTypeFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestPrimitiveTypeFieldsClassNotNullablePackBufferUnpack().testPrimitiveTypeFieldsClassNotNullable();
    }

    @Test
    public void testPrimitiveTypeFieldsClassNotNullablePackConvert() throws Exception {
	new TestPrimitiveTypeFieldsClassNotNullablePackConvert().testPrimitiveTypeFieldsClassNotNullable();
    }

    @Test
    public void testPrimitiveTypeFieldsClassNotNullablePackUnpack() throws Exception {
	new TestPrimitiveTypeFieldsClassNotNullablePackUnpack().testPrimitiveTypeFieldsClassNotNullable();
    }

    @Test
    public void testPrimitiveTypeFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestPrimitiveTypeFieldsClassNotNullableUnconvertConvert().testPrimitiveTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceTypeFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestReferenceTypeFieldsClassBufferPackBufferUnpack().testReferenceTypeFieldsClass();
    }

    @Test
    public void testReferenceTypeFieldsClassBufferPackConvert() throws Exception {
	new TestReferenceTypeFieldsClassBufferPackConvert().testReferenceTypeFieldsClass();
    }

    @Test
    public void testReferenceTypeFieldsClassBufferPackUnpack() throws Exception {
	new TestReferenceTypeFieldsClassBufferPackUnpack().testReferenceTypeFieldsClass();
    }

    @Test
    public void testReferenceTypeFieldsClassPackBufferUnpack() throws Exception {
	new TestReferenceTypeFieldsClassPackBufferUnpack().testReferenceTypeFieldsClass();
    }

    @Test
    public void testReferenceTypeFieldsClassPackConvert() throws Exception {
	new TestReferenceTypeFieldsClassPackConvert().testReferenceTypeFieldsClass();
    }

    @Test
    public void testReferenceTypeFieldsClassPackUnpack() throws Exception {
	new TestReferenceTypeFieldsClassPackUnpack().testReferenceTypeFieldsClass();
    }

    @Test
    public void testReferenceTypeFieldsClassUnconvertConvert() throws Exception {
	new TestReferenceTypeFieldsClassUnconvertConvert().testReferenceTypeFieldsClass();
    }

    @Test
    public void testReferenceTypeFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestReferenceTypeFieldsClassNotNullableBufferPackBufferUnpack().testReferenceTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceTypeFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestReferenceTypeFieldsClassNotNullableBufferPackConvert().testReferenceTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceTypeFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestReferenceTypeFieldsClassNotNullableBufferPackUnpack().testReferenceTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceTypeFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestReferenceTypeFieldsClassNotNullablePackBufferUnpack().testReferenceTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceTypeFieldsClassNotNullablePackConvert() throws Exception {
	new TestReferenceTypeFieldsClassNotNullablePackConvert().testReferenceTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceTypeFieldsClassNotNullablePackUnpack() throws Exception {
	new TestReferenceTypeFieldsClassNotNullablePackUnpack().testReferenceTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceTypeFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestReferenceTypeFieldsClassNotNullableUnconvertConvert().testReferenceTypeFieldsClassNotNullable();
    }

    @Test
    public void testListTypeFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestListTypeFieldsClassBufferPackBufferUnpack().testListTypeFieldsClass();
    }

    @Test
    public void testListTypeFieldsClassBufferPackConvert() throws Exception {
	new TestListTypeFieldsClassBufferPackConvert().testListTypeFieldsClass();
    }

    @Test
    public void testListTypeFieldsClassBufferPackUnpack() throws Exception {
	new TestListTypeFieldsClassBufferPackUnpack().testListTypeFieldsClass();
    }

    @Test
    public void testListTypeFieldsClassPackBufferUnpack() throws Exception {
	new TestListTypeFieldsClassPackBufferUnpack().testListTypeFieldsClass();
    }

    @Test
    public void testListTypeFieldsClassPackConvert() throws Exception {
	new TestListTypeFieldsClassPackConvert().testListTypeFieldsClass();
    }

    @Test
    public void testListTypeFieldsClassPackUnpack() throws Exception {
	new TestListTypeFieldsClassPackUnpack().testListTypeFieldsClass();
    }

    @Test
    public void testListTypeFieldsClassUnconvertConvert() throws Exception {
	new TestListTypeFieldsClassUnconvertConvert().testListTypeFieldsClass();
    }

    @Test
    public void testListTypeFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestListTypeFieldsClassNotNullableBufferPackBufferUnpack().testListTypeFieldsClassNotNullable();
    }

    @Test
    public void testListTypeFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestListTypeFieldsClassNotNullableBufferPackConvert().testListTypeFieldsClassNotNullable();
    }

    @Test
    public void testListTypeFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestListTypeFieldsClassNotNullableBufferPackUnpack().testListTypeFieldsClassNotNullable();
    }

    @Test
    public void testListTypeFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestListTypeFieldsClassNotNullablePackBufferUnpack().testListTypeFieldsClassNotNullable();
    }

    @Test
    public void testListTypeFieldsClassNotNullablePackConvert() throws Exception {
	new TestListTypeFieldsClassNotNullablePackConvert().testListTypeFieldsClassNotNullable();
    }

    @Test
    public void testListTypeFieldsClassNotNullablePackUnpack() throws Exception {
	new TestListTypeFieldsClassNotNullablePackUnpack().testListTypeFieldsClassNotNullable();
    }

    @Test
    public void testListTypeFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestListTypeFieldsClassNotNullableUnconvertConvert().testListTypeFieldsClassNotNullable();
    }

    @Test
    public void testMapTypeFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestMapTypeFieldsClassBufferPackBufferUnpack().testMapTypeFieldsClass();
    }

    @Test
    public void testMapTypeFieldsClassBufferPackConvert() throws Exception {
	new TestMapTypeFieldsClassBufferPackConvert().testMapTypeFieldsClass();
    }

    @Test
    public void testMapTypeFieldsClassBufferPackUnpack() throws Exception {
	new TestMapTypeFieldsClassBufferPackUnpack().testMapTypeFieldsClass();
    }

    @Test
    public void testMapTypeFieldsClassPackBufferUnpack() throws Exception {
	new TestMapTypeFieldsClassPackBufferUnpack().testMapTypeFieldsClass();
    }

    @Test
    public void testMapTypeFieldsClassPackConvert() throws Exception {
	new TestMapTypeFieldsClassPackConvert().testMapTypeFieldsClass();
    }

    @Test
    public void testMapTypeFieldsClassPackUnpack() throws Exception {
	new TestMapTypeFieldsClassPackUnpack().testMapTypeFieldsClass();
    }

    @Test
    public void testMapTypeFieldsClassUnconvertConvert() throws Exception {
	new TestMapTypeFieldsClassUnconvertConvert().testMapTypeFieldsClass();
    }

    @Test
    public void testMapTypeFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestMapTypeFieldsClassNotNullableBufferPackBufferUnpack().testMapTypeFieldsClassNotNullable();
    }

    @Test
    public void testMapTypeFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestMapTypeFieldsClassNotNullableBufferPackConvert().testMapTypeFieldsClassNotNullable();
    }

    @Test
    public void testMapTypeFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestMapTypeFieldsClassNotNullableBufferPackUnpack().testMapTypeFieldsClassNotNullable();
    }

    @Test
    public void testMapTypeFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestMapTypeFieldsClassNotNullablePackBufferUnpack().testMapTypeFieldsClassNotNullable();
    }

    @Test
    public void testMapTypeFieldsClassNotNullablePackConvert() throws Exception {
	new TestMapTypeFieldsClassNotNullablePackConvert().testMapTypeFieldsClassNotNullable();
    }

    @Test
    public void testMapTypeFieldsClassNotNullablePackUnpack() throws Exception {
	new TestMapTypeFieldsClassNotNullablePackUnpack().testMapTypeFieldsClassNotNullable();
    }

    @Test
    public void testMapTypeFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestMapTypeFieldsClassNotNullableUnconvertConvert().testMapTypeFieldsClassNotNullable();
    }

    @Test
    public void testEnumTypeFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestEnumTypeFieldsClassBufferPackBufferUnpack().testEnumTypeFieldsClass();
    }

    @Test
    public void testEnumTypeFieldsClassBufferPackConvert() throws Exception {
	new TestEnumTypeFieldsClassBufferPackConvert().testEnumTypeFieldsClass();
    }

    @Test
    public void testEnumTypeFieldsClassBufferPackUnpack() throws Exception {
	new TestEnumTypeFieldsClassBufferPackUnpack().testEnumTypeFieldsClass();
    }

    @Test
    public void testEnumTypeFieldsClassPackBufferUnpack() throws Exception {
	new TestEnumTypeFieldsClassPackBufferUnpack().testEnumTypeFieldsClass();
    }

    @Test
    public void testEnumTypeFieldsClassPackConvert() throws Exception {
	new TestEnumTypeFieldsClassPackConvert().testEnumTypeFieldsClass();
    }

    @Test
    public void testEnumTypeFieldsClassPackUnpack() throws Exception {
	new TestEnumTypeFieldsClassPackUnpack().testEnumTypeFieldsClass();
    }

    @Test
    public void testEnumTypeFieldsClassUnconvertConvert() throws Exception {
	new TestEnumTypeFieldsClassUnconvertConvert().testEnumTypeFieldsClass();
    }

    @Test
    public void testEnumTypeFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestEnumTypeFieldsClassNotNullableBufferPackBufferUnpack().testEnumTypeFieldsClassNotNullable();
    }

    @Test
    public void testEnumTypeFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestEnumTypeFieldsClassNotNullableBufferPackConvert().testEnumTypeFieldsClassNotNullable();
    }

    @Test
    public void testEnumTypeFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestEnumTypeFieldsClassNotNullableBufferPackUnpack().testEnumTypeFieldsClassNotNullable();
    }

    @Test
    public void testEnumTypeFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestEnumTypeFieldsClassNotNullablePackBufferUnpack().testEnumTypeFieldsClassNotNullable();
    }

    @Test
    public void testEnumTypeFieldsClassNotNullablePackConvert() throws Exception {
	new TestEnumTypeFieldsClassNotNullablePackConvert().testEnumTypeFieldsClassNotNullable();
    }

    @Test
    public void testEnumTypeFieldsClassNotNullablePackUnpack() throws Exception {
	new TestEnumTypeFieldsClassNotNullablePackUnpack().testEnumTypeFieldsClassNotNullable();
    }

    @Test
    public void testEnumTypeFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestEnumTypeFieldsClassNotNullableUnconvertConvert().testEnumTypeFieldsClassNotNullable();
    }

    @Test
    public void testModifiersFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestModifiersFieldsClassBufferPackBufferUnpack().testModifiersFieldsClass();
    }

    @Test
    public void testModifiersFieldsClassBufferPackConvert() throws Exception {
	new TestModifiersFieldsClassBufferPackConvert().testModifiersFieldsClass();
    }

    @Test
    public void testModifiersFieldsClassBufferPackUnpack() throws Exception {
	new TestModifiersFieldsClassBufferPackUnpack().testModifiersFieldsClass();
    }

    @Test
    public void testModifiersFieldsClassPackBufferUnpack() throws Exception {
	new TestModifiersFieldsClassPackBufferUnpack().testModifiersFieldsClass();
    }

    @Test
    public void testModifiersFieldsClassPackConvert() throws Exception {
	new TestModifiersFieldsClassPackConvert().testModifiersFieldsClass();
    }

    @Test
    public void testModifiersFieldsClassPackUnpack() throws Exception {
	new TestModifiersFieldsClassPackUnpack().testModifiersFieldsClass();
    }

    @Test
    public void testModifiersFieldsClassUnconvertConvert() throws Exception {
	new TestModifiersFieldsClassUnconvertConvert().testModifiersFieldsClass();
    }

    @Test
    public void testModifiersFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestModifiersFieldsClassNotNullableBufferPackBufferUnpack().testModifiersFieldsClassNotNullable();
    }

    @Test
    public void testModifiersFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestModifiersFieldsClassNotNullableBufferPackConvert().testModifiersFieldsClassNotNullable();
    }

    @Test
    public void testModifiersFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestModifiersFieldsClassNotNullableBufferPackUnpack().testModifiersFieldsClassNotNullable();
    }

    @Test
    public void testModifiersFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestModifiersFieldsClassNotNullablePackBufferUnpack().testModifiersFieldsClassNotNullable();
    }

    @Test
    public void testModifiersFieldsClassNotNullablePackConvert() throws Exception {
	new TestModifiersFieldsClassNotNullablePackConvert().testModifiersFieldsClassNotNullable();
    }

    @Test
    public void testModifiersFieldsClassNotNullablePackUnpack() throws Exception {
	new TestModifiersFieldsClassNotNullablePackUnpack().testModifiersFieldsClassNotNullable();
    }

    @Test
    public void testModifiersFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestModifiersFieldsClassNotNullableUnconvertConvert().testModifiersFieldsClassNotNullable();
    }

    @Test
    public void testUserDefinedTypeFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestUserDefinedTypeFieldsClassBufferPackBufferUnpack().testUserDefinedTypeFieldsClass();
    }

    @Test
    public void testUserDefinedTypeFieldsClassBufferPackConvert() throws Exception {
	new TestUserDefinedTypeFieldsClassBufferPackConvert().testUserDefinedTypeFieldsClass();
    }

    @Test
    public void testUserDefinedTypeFieldsClassBufferPackUnpack() throws Exception {
	new TestUserDefinedTypeFieldsClassBufferPackUnpack().testUserDefinedTypeFieldsClass();
    }

    @Test
    public void testUserDefinedTypeFieldsClassPackBufferUnpack() throws Exception {
	new TestUserDefinedTypeFieldsClassPackBufferUnpack().testUserDefinedTypeFieldsClass();
    }

    @Test
    public void testUserDefinedTypeFieldsClassPackConvert() throws Exception {
	new TestUserDefinedTypeFieldsClassPackConvert().testUserDefinedTypeFieldsClass();
    }

    @Test
    public void testUserDefinedTypeFieldsClassPackUnpack() throws Exception {
	new TestUserDefinedTypeFieldsClassPackUnpack().testUserDefinedTypeFieldsClass();
    }

    @Test
    public void testUserDefinedTypeFieldsClassUnconvertConvert() throws Exception {
	new TestUserDefinedTypeFieldsClassUnconvertConvert().testUserDefinedTypeFieldsClass();
    }

    @Test
    public void testUserDefinedTypeFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestUserDefinedTypeFieldsClassNotNullableBufferPackBufferUnpack().testUserDefinedTypeFieldsClassNotNullable();
    }

    @Test
    public void testUserDefinedTypeFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestUserDefinedTypeFieldsClassNotNullableBufferPackConvert().testUserDefinedTypeFieldsClassNotNullable();
    }

    @Test
    public void testUserDefinedTypeFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestUserDefinedTypeFieldsClassNotNullableBufferPackUnpack().testUserDefinedTypeFieldsClassNotNullable();
    }

    @Test
    public void testUserDefinedTypeFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestUserDefinedTypeFieldsClassNotNullablePackBufferUnpack().testUserDefinedTypeFieldsClassNotNullable();
    }

    @Test
    public void testUserDefinedTypeFieldsClassNotNullablePackConvert() throws Exception {
	new TestUserDefinedTypeFieldsClassNotNullablePackConvert().testUserDefinedTypeFieldsClassNotNullable();
    }

    @Test
    public void testUserDefinedTypeFieldsClassNotNullablePackUnpack() throws Exception {
	new TestUserDefinedTypeFieldsClassNotNullablePackUnpack().testUserDefinedTypeFieldsClassNotNullable();
    }

    @Test
    public void testUserDefinedTypeFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestUserDefinedTypeFieldsClassNotNullableUnconvertConvert().testUserDefinedTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestReferenceCycleTypeFieldsClassBufferPackBufferUnpack().testReferenceCycleTypeFieldsClass();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassBufferPackConvert() throws Exception {
	new TestReferenceCycleTypeFieldsClassBufferPackConvert().testReferenceCycleTypeFieldsClass();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassBufferPackUnpack() throws Exception {
	new TestReferenceCycleTypeFieldsClassBufferPackUnpack().testReferenceCycleTypeFieldsClass();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassPackBufferUnpack() throws Exception {
	new TestReferenceCycleTypeFieldsClassPackBufferUnpack().testReferenceCycleTypeFieldsClass();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassPackConvert() throws Exception {
	new TestReferenceCycleTypeFieldsClassPackConvert().testReferenceCycleTypeFieldsClass();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassPackUnpack() throws Exception {
	new TestReferenceCycleTypeFieldsClassPackUnpack().testReferenceCycleTypeFieldsClass();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassUnconvertConvert() throws Exception {
	new TestReferenceCycleTypeFieldsClassUnconvertConvert().testReferenceCycleTypeFieldsClass();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestReferenceCycleTypeFieldsClassNotNullableBufferPackBufferUnpack().testReferenceCycleTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestReferenceCycleTypeFieldsClassNotNullableBufferPackConvert().testReferenceCycleTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestReferenceCycleTypeFieldsClassNotNullableBufferPackUnpack().testReferenceCycleTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestReferenceCycleTypeFieldsClassNotNullablePackBufferUnpack().testReferenceCycleTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassNotNullablePackConvert() throws Exception {
	new TestReferenceCycleTypeFieldsClassNotNullablePackConvert().testReferenceCycleTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassNotNullablePackUnpack() throws Exception {
	new TestReferenceCycleTypeFieldsClassNotNullablePackUnpack().testReferenceCycleTypeFieldsClassNotNullable();
    }

    @Test
    public void testReferenceCycleTypeFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestReferenceCycleTypeFieldsClassNotNullableUnconvertConvert().testReferenceCycleTypeFieldsClassNotNullable();
    }

    @Test
    public void testInheritanceClassBufferPackBufferUnpack() throws Exception {
	new TestInheritanceClassBufferPackBufferUnpack().testInheritanceClass();
    }

    @Test
    public void testInheritanceClassBufferPackConvert() throws Exception {
	new TestInheritanceClassBufferPackConvert().testInheritanceClass();
    }

    @Test
    public void testInheritanceClassBufferPackUnpack() throws Exception {
	new TestInheritanceClassBufferPackUnpack().testInheritanceClass();
    }

    @Test
    public void testInheritanceClassPackBufferUnpack() throws Exception {
	new TestInheritanceClassPackBufferUnpack().testInheritanceClass();
    }

    @Test
    public void testInheritanceClassPackConvert() throws Exception {
	new TestInheritanceClassPackConvert().testInheritanceClass();
    }

    @Test
    public void testInheritanceClassPackUnpack() throws Exception {
	new TestInheritanceClassPackUnpack().testInheritanceClass();
    }

    @Test
    public void testInheritanceClassUnconvertConvert() throws Exception {
	new TestInheritanceClassUnconvertConvert().testInheritanceClass();
    }

    @Test
    public void testInheritanceClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestInheritanceClassNotNullableBufferPackBufferUnpack().testInheritanceClassNotNullable();
    }

    @Test
    public void testInheritanceClassNotNullableBufferPackConvert() throws Exception {
	new TestInheritanceClassNotNullableBufferPackConvert().testInheritanceClassNotNullable();
    }

    @Test
    public void testInheritanceClassNotNullableBufferPackUnpack() throws Exception {
	new TestInheritanceClassNotNullableBufferPackUnpack().testInheritanceClassNotNullable();
    }

    @Test
    public void testInheritanceClassNotNullablePackBufferUnpack() throws Exception {
	new TestInheritanceClassNotNullablePackBufferUnpack().testInheritanceClassNotNullable();
    }

    @Test
    public void testInheritanceClassNotNullablePackConvert() throws Exception {
	new TestInheritanceClassNotNullablePackConvert().testInheritanceClassNotNullable();
    }

    @Test
    public void testInheritanceClassNotNullablePackUnpack() throws Exception {
	new TestInheritanceClassNotNullablePackUnpack().testInheritanceClassNotNullable();
    }

    @Test
    public void testInheritanceClassNotNullableUnconvertConvert() throws Exception {
	new TestInheritanceClassNotNullableUnconvertConvert().testInheritanceClassNotNullable();
    }

    @Test
    public void testMessagePackableTypeFieldsClassBufferPackBufferUnpack() throws Exception {
	new TestMessagePackableTypeFieldsClassBufferPackBufferUnpack().testMessagePackableTypeFieldsClass();
    }

    @Test
    public void testMessagePackableTypeFieldsClassBufferPackConvert() throws Exception {
	new TestMessagePackableTypeFieldsClassBufferPackConvert().testMessagePackableTypeFieldsClass();
    }

    @Test
    public void testMessagePackableTypeFieldsClassBufferPackUnpack() throws Exception {
	new TestMessagePackableTypeFieldsClassBufferPackUnpack().testMessagePackableTypeFieldsClass();
    }

    @Test
    public void testMessagePackableTypeFieldsClassPackBufferUnpack() throws Exception {
	new TestMessagePackableTypeFieldsClassPackBufferUnpack().testMessagePackableTypeFieldsClass();
    }

    @Test
    public void testMessagePackableTypeFieldsClassPackConvert() throws Exception {
	new TestMessagePackableTypeFieldsClassPackConvert().testMessagePackableTypeFieldsClass();
    }

    @Test
    public void testMessagePackableTypeFieldsClassPackUnpack() throws Exception {
	new TestMessagePackableTypeFieldsClassPackUnpack().testMessagePackableTypeFieldsClass();
    }

    @Test
    public void testMessagePackableTypeFieldsClassUnconvertConvert() throws Exception {
	new TestMessagePackableTypeFieldsClassUnconvertConvert().testMessagePackableTypeFieldsClass();
    }

    @Test
    public void testMessagePackableTypeFieldsClassNotNullableBufferPackBufferUnpack() throws Exception {
	new TestMessagePackableTypeFieldsClassNotNullableBufferPackBufferUnpack().testMessagePackableTypeFieldsClassNotNullable();
    }

    @Test
    public void testMessagePackableTypeFieldsClassNotNullableBufferPackConvert() throws Exception {
	new TestMessagePackableTypeFieldsClassNotNullableBufferPackConvert().testMessagePackableTypeFieldsClassNotNullable();
    }

    @Test
    public void testMessagePackableTypeFieldsClassNotNullableBufferPackUnpack() throws Exception {
	new TestMessagePackableTypeFieldsClassNotNullableBufferPackUnpack().testMessagePackableTypeFieldsClassNotNullable();
    }

    @Test
    public void testMessagePackableTypeFieldsClassNotNullablePackBufferUnpack() throws Exception {
	new TestMessagePackableTypeFieldsClassNotNullablePackBufferUnpack().testMessagePackableTypeFieldsClassNotNullable();
    }

    @Test
    public void testMessagePackableTypeFieldsClassNotNullablePackConvert() throws Exception {
	new TestMessagePackableTypeFieldsClassNotNullablePackConvert().testMessagePackableTypeFieldsClassNotNullable();
    }

    @Test
    public void testMessagePackableTypeFieldsClassNotNullablePackUnpack() throws Exception {
	new TestMessagePackableTypeFieldsClassNotNullablePackUnpack().testMessagePackableTypeFieldsClassNotNullable();
    }

    @Test
    public void testMessagePackableTypeFieldsClassNotNullableUnconvertConvert() throws Exception {
	new TestMessagePackableTypeFieldsClassNotNullableUnconvertConvert().testMessagePackableTypeFieldsClassNotNullable();
    }

    public static class TestPrimitiveTypeFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClass() throws Exception {
	    super.testPrimitiveTypeFieldsClass();
	}

	@Override
	public void testPrimitiveTypeFieldsClass(PrimitiveTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    PrimitiveTypeFieldsClass ret = msgpack.read(bytes, PrimitiveTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClass() throws Exception {
	    super.testPrimitiveTypeFieldsClass();
	}

	@Override
	public void testPrimitiveTypeFieldsClass(PrimitiveTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    PrimitiveTypeFieldsClass ret = msgpack.convert(value, PrimitiveTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClass() throws Exception {
	    super.testPrimitiveTypeFieldsClass();
	}

	@Override
	public void testPrimitiveTypeFieldsClass(PrimitiveTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    PrimitiveTypeFieldsClass ret = msgpack.read(in, PrimitiveTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClass() throws Exception {
	    super.testPrimitiveTypeFieldsClass();
	}

	@Override
	public void testPrimitiveTypeFieldsClass(PrimitiveTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    PrimitiveTypeFieldsClass ret = msgpack.read(bytes, PrimitiveTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClass() throws Exception {
	    super.testPrimitiveTypeFieldsClass();
	}

	@Override
	public void testPrimitiveTypeFieldsClass(PrimitiveTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    PrimitiveTypeFieldsClass ret = msgpack.convert(value, PrimitiveTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClass() throws Exception {
	    super.testPrimitiveTypeFieldsClass();
	}

	@Override
	public void testPrimitiveTypeFieldsClass(PrimitiveTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    PrimitiveTypeFieldsClass ret = msgpack.read(in, PrimitiveTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClass() throws Exception {
	    super.testPrimitiveTypeFieldsClass();
	}

	@Override
	public void testPrimitiveTypeFieldsClass(PrimitiveTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    PrimitiveTypeFieldsClass ret = msgpack.convert(value, PrimitiveTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClassNotNullable() throws Exception {
	    super.testPrimitiveTypeFieldsClassNotNullable();
	}

	@Override
	public void testPrimitiveTypeFieldsClassNotNullable(PrimitiveTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    PrimitiveTypeFieldsClassNotNullable ret = msgpack.read(bytes, PrimitiveTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClassNotNullable() throws Exception {
	    super.testPrimitiveTypeFieldsClassNotNullable();
	}

	@Override
	public void testPrimitiveTypeFieldsClassNotNullable(PrimitiveTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    PrimitiveTypeFieldsClassNotNullable ret = msgpack.convert(value, PrimitiveTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClassNotNullable() throws Exception {
	    super.testPrimitiveTypeFieldsClassNotNullable();
	}

	@Override
	public void testPrimitiveTypeFieldsClassNotNullable(PrimitiveTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    PrimitiveTypeFieldsClassNotNullable ret = msgpack.read(in, PrimitiveTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClassNotNullable() throws Exception {
	    super.testPrimitiveTypeFieldsClassNotNullable();
	}

	@Override
	public void testPrimitiveTypeFieldsClassNotNullable(PrimitiveTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    PrimitiveTypeFieldsClassNotNullable ret = msgpack.read(bytes, PrimitiveTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClassNotNullable() throws Exception {
	    super.testPrimitiveTypeFieldsClassNotNullable();
	}

	@Override
	public void testPrimitiveTypeFieldsClassNotNullable(PrimitiveTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    PrimitiveTypeFieldsClassNotNullable ret = msgpack.convert(value, PrimitiveTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClassNotNullable() throws Exception {
	    super.testPrimitiveTypeFieldsClassNotNullable();
	}

	@Override
	public void testPrimitiveTypeFieldsClassNotNullable(PrimitiveTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    PrimitiveTypeFieldsClassNotNullable ret = msgpack.read(in, PrimitiveTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestPrimitiveTypeFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testPrimitiveTypeFieldsClassNotNullable() throws Exception {
	    super.testPrimitiveTypeFieldsClassNotNullable();
	}

	@Override
	public void testPrimitiveTypeFieldsClassNotNullable(PrimitiveTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    PrimitiveTypeFieldsClassNotNullable ret = msgpack.convert(value, PrimitiveTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClass() throws Exception {
	    super.testReferenceTypeFieldsClass();
	}

	@Override
	public void testReferenceTypeFieldsClass(ReferenceTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ReferenceTypeFieldsClass ret = msgpack.read(bytes, ReferenceTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClass() throws Exception {
	    super.testReferenceTypeFieldsClass();
	}

	@Override
	public void testReferenceTypeFieldsClass(ReferenceTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    ReferenceTypeFieldsClass ret = msgpack.convert(value, ReferenceTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClass() throws Exception {
	    super.testReferenceTypeFieldsClass();
	}

	@Override
	public void testReferenceTypeFieldsClass(ReferenceTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    ReferenceTypeFieldsClass ret = msgpack.read(in, ReferenceTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClass() throws Exception {
	    super.testReferenceTypeFieldsClass();
	}

	@Override
	public void testReferenceTypeFieldsClass(ReferenceTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    ReferenceTypeFieldsClass ret = msgpack.read(bytes, ReferenceTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClass() throws Exception {
	    super.testReferenceTypeFieldsClass();
	}

	@Override
	public void testReferenceTypeFieldsClass(ReferenceTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    ReferenceTypeFieldsClass ret = msgpack.convert(value, ReferenceTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClass() throws Exception {
	    super.testReferenceTypeFieldsClass();
	}

	@Override
	public void testReferenceTypeFieldsClass(ReferenceTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    ReferenceTypeFieldsClass ret = msgpack.read(in, ReferenceTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClass() throws Exception {
	    super.testReferenceTypeFieldsClass();
	}

	@Override
	public void testReferenceTypeFieldsClass(ReferenceTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    ReferenceTypeFieldsClass ret = msgpack.convert(value, ReferenceTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceTypeFieldsClassNotNullable(ReferenceTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ReferenceTypeFieldsClassNotNullable ret = msgpack.read(bytes, ReferenceTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceTypeFieldsClassNotNullable(ReferenceTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    ReferenceTypeFieldsClassNotNullable ret = msgpack.convert(value, ReferenceTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceTypeFieldsClassNotNullable(ReferenceTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    ReferenceTypeFieldsClassNotNullable ret = msgpack.read(in, ReferenceTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceTypeFieldsClassNotNullable(ReferenceTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    ReferenceTypeFieldsClassNotNullable ret = msgpack.read(bytes, ReferenceTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceTypeFieldsClassNotNullable(ReferenceTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    ReferenceTypeFieldsClassNotNullable ret = msgpack.convert(value, ReferenceTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceTypeFieldsClassNotNullable(ReferenceTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    ReferenceTypeFieldsClassNotNullable ret = msgpack.read(in, ReferenceTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceTypeFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceTypeFieldsClassNotNullable(ReferenceTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    ReferenceTypeFieldsClassNotNullable ret = msgpack.convert(value, ReferenceTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClass() throws Exception {
	    super.testListTypeFieldsClass();
	}

	@Override
	public void testListTypeFieldsClass(ListTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ListTypeFieldsClass ret = msgpack.read(bytes, ListTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClass() throws Exception {
	    super.testListTypeFieldsClass();
	}

	@Override
	public void testListTypeFieldsClass(ListTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    ListTypeFieldsClass ret = msgpack.convert(value, ListTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClass() throws Exception {
	    super.testListTypeFieldsClass();
	}

	@Override
	public void testListTypeFieldsClass(ListTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    ListTypeFieldsClass ret = msgpack.read(in, ListTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClass() throws Exception {
	    super.testListTypeFieldsClass();
	}

	@Override
	public void testListTypeFieldsClass(ListTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    ListTypeFieldsClass ret = msgpack.read(bytes, ListTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClass() throws Exception {
	    super.testListTypeFieldsClass();
	}

	@Override
	public void testListTypeFieldsClass(ListTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    ListTypeFieldsClass ret = msgpack.convert(value, ListTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClass() throws Exception {
	    super.testListTypeFieldsClass();
	}

	@Override
	public void testListTypeFieldsClass(ListTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    ListTypeFieldsClass ret = msgpack.read(in, ListTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClass() throws Exception {
	    super.testListTypeFieldsClass();
	}

	@Override
	public void testListTypeFieldsClass(ListTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    ListTypeFieldsClass ret = msgpack.convert(value, ListTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClassNotNullable() throws Exception {
	    super.testListTypeFieldsClassNotNullable();
	}

	@Override
	public void testListTypeFieldsClassNotNullable(ListTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ListTypeFieldsClassNotNullable ret = msgpack.read(bytes, ListTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClassNotNullable() throws Exception {
	    super.testListTypeFieldsClassNotNullable();
	}

	@Override
	public void testListTypeFieldsClassNotNullable(ListTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    ListTypeFieldsClassNotNullable ret = msgpack.convert(value, ListTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClassNotNullable() throws Exception {
	    super.testListTypeFieldsClassNotNullable();
	}

	@Override
	public void testListTypeFieldsClassNotNullable(ListTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    ListTypeFieldsClassNotNullable ret = msgpack.read(in, ListTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClassNotNullable() throws Exception {
	    super.testListTypeFieldsClassNotNullable();
	}

	@Override
	public void testListTypeFieldsClassNotNullable(ListTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    ListTypeFieldsClassNotNullable ret = msgpack.read(bytes, ListTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClassNotNullable() throws Exception {
	    super.testListTypeFieldsClassNotNullable();
	}

	@Override
	public void testListTypeFieldsClassNotNullable(ListTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    ListTypeFieldsClassNotNullable ret = msgpack.convert(value, ListTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClassNotNullable() throws Exception {
	    super.testListTypeFieldsClassNotNullable();
	}

	@Override
	public void testListTypeFieldsClassNotNullable(ListTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    ListTypeFieldsClassNotNullable ret = msgpack.read(in, ListTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestListTypeFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testListTypeFieldsClassNotNullable() throws Exception {
	    super.testListTypeFieldsClassNotNullable();
	}

	@Override
	public void testListTypeFieldsClassNotNullable(ListTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    ListTypeFieldsClassNotNullable ret = msgpack.convert(value, ListTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClass() throws Exception {
	    super.testMapTypeFieldsClass();
	}

	@Override
	public void testMapTypeFieldsClass(MapTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    MapTypeFieldsClass ret = msgpack.read(bytes, MapTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClass() throws Exception {
	    super.testMapTypeFieldsClass();
	}

	@Override
	public void testMapTypeFieldsClass(MapTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    MapTypeFieldsClass ret = msgpack.convert(value, MapTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClass() throws Exception {
	    super.testMapTypeFieldsClass();
	}

	@Override
	public void testMapTypeFieldsClass(MapTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    MapTypeFieldsClass ret = msgpack.read(in, MapTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClass() throws Exception {
	    super.testMapTypeFieldsClass();
	}

	@Override
	public void testMapTypeFieldsClass(MapTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    MapTypeFieldsClass ret = msgpack.read(bytes, MapTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClass() throws Exception {
	    super.testMapTypeFieldsClass();
	}

	@Override
	public void testMapTypeFieldsClass(MapTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    MapTypeFieldsClass ret = msgpack.convert(value, MapTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClass() throws Exception {
	    super.testMapTypeFieldsClass();
	}

	@Override
	public void testMapTypeFieldsClass(MapTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    MapTypeFieldsClass ret = msgpack.read(in, MapTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClass() throws Exception {
	    super.testMapTypeFieldsClass();
	}

	@Override
	public void testMapTypeFieldsClass(MapTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    MapTypeFieldsClass ret = msgpack.convert(value, MapTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClassNotNullable() throws Exception {
	    super.testMapTypeFieldsClassNotNullable();
	}

	@Override
	public void testMapTypeFieldsClassNotNullable(MapTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    MapTypeFieldsClassNotNullable ret = msgpack.read(bytes, MapTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClassNotNullable() throws Exception {
	    super.testMapTypeFieldsClassNotNullable();
	}

	@Override
	public void testMapTypeFieldsClassNotNullable(MapTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    MapTypeFieldsClassNotNullable ret = msgpack.convert(value, MapTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClassNotNullable() throws Exception {
	    super.testMapTypeFieldsClassNotNullable();
	}

	@Override
	public void testMapTypeFieldsClassNotNullable(MapTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    MapTypeFieldsClassNotNullable ret = msgpack.read(in, MapTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClassNotNullable() throws Exception {
	    super.testMapTypeFieldsClassNotNullable();
	}

	@Override
	public void testMapTypeFieldsClassNotNullable(MapTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    MapTypeFieldsClassNotNullable ret = msgpack.read(bytes, MapTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClassNotNullable() throws Exception {
	    super.testMapTypeFieldsClassNotNullable();
	}

	@Override
	public void testMapTypeFieldsClassNotNullable(MapTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    MapTypeFieldsClassNotNullable ret = msgpack.convert(value, MapTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClassNotNullable() throws Exception {
	    super.testMapTypeFieldsClassNotNullable();
	}

	@Override
	public void testMapTypeFieldsClassNotNullable(MapTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    MapTypeFieldsClassNotNullable ret = msgpack.read(in, MapTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMapTypeFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMapTypeFieldsClassNotNullable() throws Exception {
	    super.testMapTypeFieldsClassNotNullable();
	}

	@Override
	public void testMapTypeFieldsClassNotNullable(MapTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    MapTypeFieldsClassNotNullable ret = msgpack.convert(value, MapTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClass() throws Exception {
	    super.testEnumTypeFieldsClass();
	}

	@Override
	public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    EnumTypeFieldsClass ret = msgpack.read(bytes, EnumTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClass() throws Exception {
	    super.testEnumTypeFieldsClass();
	}

	@Override
	public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    EnumTypeFieldsClass ret = msgpack.convert(value, EnumTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClass() throws Exception {
	    super.testEnumTypeFieldsClass();
	}

	@Override
	public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    EnumTypeFieldsClass ret = msgpack.read(in, EnumTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClass() throws Exception {
	    super.testEnumTypeFieldsClass();
	}

	@Override
	public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    EnumTypeFieldsClass ret = msgpack.read(bytes, EnumTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClass() throws Exception {
	    super.testEnumTypeFieldsClass();
	}

	@Override
	public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    EnumTypeFieldsClass ret = msgpack.convert(value, EnumTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClass() throws Exception {
	    super.testEnumTypeFieldsClass();
	}

	@Override
	public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    EnumTypeFieldsClass ret = msgpack.read(in, EnumTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClass() throws Exception {
	    super.testEnumTypeFieldsClass();
	}

	@Override
	public void testEnumTypeFieldsClass(EnumTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    EnumTypeFieldsClass ret = msgpack.convert(value, EnumTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClassNotNullable() throws Exception {
	    super.testEnumTypeFieldsClassNotNullable();
	}

	@Override
	public void testEnumTypeFieldsClassNotNullable(EnumTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    EnumTypeFieldsClassNotNullable ret = msgpack.read(bytes, EnumTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClassNotNullable() throws Exception {
	    super.testEnumTypeFieldsClassNotNullable();
	}

	@Override
	public void testEnumTypeFieldsClassNotNullable(EnumTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    EnumTypeFieldsClassNotNullable ret = msgpack.convert(value, EnumTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClassNotNullable() throws Exception {
	    super.testEnumTypeFieldsClassNotNullable();
	}

	@Override
	public void testEnumTypeFieldsClassNotNullable(EnumTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    EnumTypeFieldsClassNotNullable ret = msgpack.read(in, EnumTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClassNotNullable() throws Exception {
	    super.testEnumTypeFieldsClassNotNullable();
	}

	@Override
	public void testEnumTypeFieldsClassNotNullable(EnumTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    EnumTypeFieldsClassNotNullable ret = msgpack.read(bytes, EnumTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClassNotNullable() throws Exception {
	    super.testEnumTypeFieldsClassNotNullable();
	}

	@Override
	public void testEnumTypeFieldsClassNotNullable(EnumTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    EnumTypeFieldsClassNotNullable ret = msgpack.convert(value, EnumTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClassNotNullable() throws Exception {
	    super.testEnumTypeFieldsClassNotNullable();
	}

	@Override
	public void testEnumTypeFieldsClassNotNullable(EnumTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    EnumTypeFieldsClassNotNullable ret = msgpack.read(in, EnumTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestEnumTypeFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testEnumTypeFieldsClassNotNullable() throws Exception {
	    super.testEnumTypeFieldsClassNotNullable();
	}

	@Override
	public void testEnumTypeFieldsClassNotNullable(EnumTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    EnumTypeFieldsClassNotNullable ret = msgpack.convert(value, EnumTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClass() throws Exception {
	    super.testModifiersFieldsClass();
	}

	@Override
	public void testModifiersFieldsClass(ModifiersFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ModifiersFieldsClass ret = msgpack.read(bytes, ModifiersFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClass() throws Exception {
	    super.testModifiersFieldsClass();
	}

	@Override
	public void testModifiersFieldsClass(ModifiersFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    ModifiersFieldsClass ret = msgpack.convert(value, ModifiersFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClass() throws Exception {
	    super.testModifiersFieldsClass();
	}

	@Override
	public void testModifiersFieldsClass(ModifiersFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    ModifiersFieldsClass ret = msgpack.read(in, ModifiersFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClass() throws Exception {
	    super.testModifiersFieldsClass();
	}

	@Override
	public void testModifiersFieldsClass(ModifiersFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    ModifiersFieldsClass ret = msgpack.read(bytes, ModifiersFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClass() throws Exception {
	    super.testModifiersFieldsClass();
	}

	@Override
	public void testModifiersFieldsClass(ModifiersFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    ModifiersFieldsClass ret = msgpack.convert(value, ModifiersFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClass() throws Exception {
	    super.testModifiersFieldsClass();
	}

	@Override
	public void testModifiersFieldsClass(ModifiersFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    ModifiersFieldsClass ret = msgpack.read(in, ModifiersFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClass() throws Exception {
	    super.testModifiersFieldsClass();
	}

	@Override
	public void testModifiersFieldsClass(ModifiersFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    ModifiersFieldsClass ret = msgpack.convert(value, ModifiersFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClassNotNullable() throws Exception {
	    super.testModifiersFieldsClassNotNullable();
	}

	@Override
	public void testModifiersFieldsClassNotNullable(ModifiersFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ModifiersFieldsClassNotNullable ret = msgpack.read(bytes, ModifiersFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClassNotNullable() throws Exception {
	    super.testModifiersFieldsClassNotNullable();
	}

	@Override
	public void testModifiersFieldsClassNotNullable(ModifiersFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    ModifiersFieldsClassNotNullable ret = msgpack.convert(value, ModifiersFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClassNotNullable() throws Exception {
	    super.testModifiersFieldsClassNotNullable();
	}

	@Override
	public void testModifiersFieldsClassNotNullable(ModifiersFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    ModifiersFieldsClassNotNullable ret = msgpack.read(in, ModifiersFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClassNotNullable() throws Exception {
	    super.testModifiersFieldsClassNotNullable();
	}

	@Override
	public void testModifiersFieldsClassNotNullable(ModifiersFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    ModifiersFieldsClassNotNullable ret = msgpack.read(bytes, ModifiersFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClassNotNullable() throws Exception {
	    super.testModifiersFieldsClassNotNullable();
	}

	@Override
	public void testModifiersFieldsClassNotNullable(ModifiersFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    ModifiersFieldsClassNotNullable ret = msgpack.convert(value, ModifiersFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClassNotNullable() throws Exception {
	    super.testModifiersFieldsClassNotNullable();
	}

	@Override
	public void testModifiersFieldsClassNotNullable(ModifiersFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    ModifiersFieldsClassNotNullable ret = msgpack.read(in, ModifiersFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestModifiersFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testModifiersFieldsClassNotNullable() throws Exception {
	    super.testModifiersFieldsClassNotNullable();
	}

	@Override
	public void testModifiersFieldsClassNotNullable(ModifiersFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    ModifiersFieldsClassNotNullable ret = msgpack.convert(value, ModifiersFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClass() throws Exception {
	    super.testUserDefinedTypeFieldsClass();
	}

	@Override
	public void testUserDefinedTypeFieldsClass(UserDefinedTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    UserDefinedTypeFieldsClass ret = msgpack.read(bytes, UserDefinedTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClass() throws Exception {
	    super.testUserDefinedTypeFieldsClass();
	}

	@Override
	public void testUserDefinedTypeFieldsClass(UserDefinedTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    UserDefinedTypeFieldsClass ret = msgpack.convert(value, UserDefinedTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClass() throws Exception {
	    super.testUserDefinedTypeFieldsClass();
	}

	@Override
	public void testUserDefinedTypeFieldsClass(UserDefinedTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    UserDefinedTypeFieldsClass ret = msgpack.read(in, UserDefinedTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClass() throws Exception {
	    super.testUserDefinedTypeFieldsClass();
	}

	@Override
	public void testUserDefinedTypeFieldsClass(UserDefinedTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    UserDefinedTypeFieldsClass ret = msgpack.read(bytes, UserDefinedTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClass() throws Exception {
	    super.testUserDefinedTypeFieldsClass();
	}

	@Override
	public void testUserDefinedTypeFieldsClass(UserDefinedTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    UserDefinedTypeFieldsClass ret = msgpack.convert(value, UserDefinedTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClass() throws Exception {
	    super.testUserDefinedTypeFieldsClass();
	}

	@Override
	public void testUserDefinedTypeFieldsClass(UserDefinedTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    UserDefinedTypeFieldsClass ret = msgpack.read(in, UserDefinedTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClass() throws Exception {
	    super.testUserDefinedTypeFieldsClass();
	}

	@Override
	public void testUserDefinedTypeFieldsClass(UserDefinedTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    UserDefinedTypeFieldsClass ret = msgpack.convert(value, UserDefinedTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClassNotNullable() throws Exception {
	    super.testUserDefinedTypeFieldsClassNotNullable();
	}

	@Override
	public void testUserDefinedTypeFieldsClassNotNullable(UserDefinedTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    UserDefinedTypeFieldsClassNotNullable ret = msgpack.read(bytes, UserDefinedTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClassNotNullable() throws Exception {
	    super.testUserDefinedTypeFieldsClassNotNullable();
	}

	@Override
	public void testUserDefinedTypeFieldsClassNotNullable(UserDefinedTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    UserDefinedTypeFieldsClassNotNullable ret = msgpack.convert(value, UserDefinedTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClassNotNullable() throws Exception {
	    super.testUserDefinedTypeFieldsClassNotNullable();
	}

	@Override
	public void testUserDefinedTypeFieldsClassNotNullable(UserDefinedTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    UserDefinedTypeFieldsClassNotNullable ret = msgpack.read(in, UserDefinedTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClassNotNullable() throws Exception {
	    super.testUserDefinedTypeFieldsClassNotNullable();
	}

	@Override
	public void testUserDefinedTypeFieldsClassNotNullable(UserDefinedTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    UserDefinedTypeFieldsClassNotNullable ret = msgpack.read(bytes, UserDefinedTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClassNotNullable() throws Exception {
	    super.testUserDefinedTypeFieldsClassNotNullable();
	}

	@Override
	public void testUserDefinedTypeFieldsClassNotNullable(UserDefinedTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    UserDefinedTypeFieldsClassNotNullable ret = msgpack.convert(value, UserDefinedTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClassNotNullable() throws Exception {
	    super.testUserDefinedTypeFieldsClassNotNullable();
	}

	@Override
	public void testUserDefinedTypeFieldsClassNotNullable(UserDefinedTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    UserDefinedTypeFieldsClassNotNullable ret = msgpack.read(in, UserDefinedTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestUserDefinedTypeFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testUserDefinedTypeFieldsClassNotNullable() throws Exception {
	    super.testUserDefinedTypeFieldsClassNotNullable();
	}

	@Override
	public void testUserDefinedTypeFieldsClassNotNullable(UserDefinedTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    UserDefinedTypeFieldsClassNotNullable ret = msgpack.convert(value, UserDefinedTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClass() throws Exception {
	    super.testReferenceCycleTypeFieldsClass();
	}

	@Override
	public void testReferenceCycleTypeFieldsClass(ReferenceCycleTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ReferenceCycleTypeFieldsClass ret = msgpack.read(bytes, ReferenceCycleTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClass() throws Exception {
	    super.testReferenceCycleTypeFieldsClass();
	}

	@Override
	public void testReferenceCycleTypeFieldsClass(ReferenceCycleTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    ReferenceCycleTypeFieldsClass ret = msgpack.convert(value, ReferenceCycleTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClass() throws Exception {
	    super.testReferenceCycleTypeFieldsClass();
	}

	@Override
	public void testReferenceCycleTypeFieldsClass(ReferenceCycleTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    ReferenceCycleTypeFieldsClass ret = msgpack.read(in, ReferenceCycleTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClass() throws Exception {
	    super.testReferenceCycleTypeFieldsClass();
	}

	@Override
	public void testReferenceCycleTypeFieldsClass(ReferenceCycleTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    ReferenceCycleTypeFieldsClass ret = msgpack.read(bytes, ReferenceCycleTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClass() throws Exception {
	    super.testReferenceCycleTypeFieldsClass();
	}

	@Override
	public void testReferenceCycleTypeFieldsClass(ReferenceCycleTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    ReferenceCycleTypeFieldsClass ret = msgpack.convert(value, ReferenceCycleTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClass() throws Exception {
	    super.testReferenceCycleTypeFieldsClass();
	}

	@Override
	public void testReferenceCycleTypeFieldsClass(ReferenceCycleTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    ReferenceCycleTypeFieldsClass ret = msgpack.read(in, ReferenceCycleTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClass() throws Exception {
	    super.testReferenceCycleTypeFieldsClass();
	}

	@Override
	public void testReferenceCycleTypeFieldsClass(ReferenceCycleTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    ReferenceCycleTypeFieldsClass ret = msgpack.convert(value, ReferenceCycleTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceCycleTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceCycleTypeFieldsClassNotNullable(ReferenceCycleTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ReferenceCycleTypeFieldsClassNotNullable ret = msgpack.read(bytes, ReferenceCycleTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceCycleTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceCycleTypeFieldsClassNotNullable(ReferenceCycleTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    ReferenceCycleTypeFieldsClassNotNullable ret = msgpack.convert(value, ReferenceCycleTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceCycleTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceCycleTypeFieldsClassNotNullable(ReferenceCycleTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    ReferenceCycleTypeFieldsClassNotNullable ret = msgpack.read(in, ReferenceCycleTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceCycleTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceCycleTypeFieldsClassNotNullable(ReferenceCycleTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    ReferenceCycleTypeFieldsClassNotNullable ret = msgpack.read(bytes, ReferenceCycleTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceCycleTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceCycleTypeFieldsClassNotNullable(ReferenceCycleTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    ReferenceCycleTypeFieldsClassNotNullable ret = msgpack.convert(value, ReferenceCycleTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceCycleTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceCycleTypeFieldsClassNotNullable(ReferenceCycleTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    ReferenceCycleTypeFieldsClassNotNullable ret = msgpack.read(in, ReferenceCycleTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestReferenceCycleTypeFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testReferenceCycleTypeFieldsClassNotNullable() throws Exception {
	    super.testReferenceCycleTypeFieldsClassNotNullable();
	}

	@Override
	public void testReferenceCycleTypeFieldsClassNotNullable(ReferenceCycleTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    ReferenceCycleTypeFieldsClassNotNullable ret = msgpack.convert(value, ReferenceCycleTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClass() throws Exception {
	    super.testIndexedFieldsBeanClass();
	}

	@Override
	public void testIndexedFieldsBeanClass(IndexedFieldsBeanClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    IndexedFieldsBeanClass ret = msgpack.read(bytes, IndexedFieldsBeanClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClass() throws Exception {
	    super.testIndexedFieldsBeanClass();
	}

	@Override
	public void testIndexedFieldsBeanClass(IndexedFieldsBeanClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    IndexedFieldsBeanClass ret = msgpack.convert(value, IndexedFieldsBeanClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClass() throws Exception {
	    super.testIndexedFieldsBeanClass();
	}

	@Override
	public void testIndexedFieldsBeanClass(IndexedFieldsBeanClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    IndexedFieldsBeanClass ret = msgpack.read(in, IndexedFieldsBeanClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClass() throws Exception {
	    super.testIndexedFieldsBeanClass();
	}

	@Override
	public void testIndexedFieldsBeanClass(IndexedFieldsBeanClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    IndexedFieldsBeanClass ret = msgpack.read(bytes, IndexedFieldsBeanClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClass() throws Exception {
	    super.testIndexedFieldsBeanClass();
	}

	@Override
	public void testIndexedFieldsBeanClass(IndexedFieldsBeanClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    IndexedFieldsBeanClass ret = msgpack.convert(value, IndexedFieldsBeanClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClass() throws Exception {
	    super.testIndexedFieldsBeanClass();
	}

	@Override
	public void testIndexedFieldsBeanClass(IndexedFieldsBeanClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    IndexedFieldsBeanClass ret = msgpack.read(in, IndexedFieldsBeanClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClass() throws Exception {
	    super.testIndexedFieldsBeanClass();
	}

	@Override
	public void testIndexedFieldsBeanClass(IndexedFieldsBeanClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    IndexedFieldsBeanClass ret = msgpack.convert(value, IndexedFieldsBeanClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClassNotNullable() throws Exception {
	    super.testIndexedFieldsBeanClassNotNullable();
	}

	@Override
	public void testIndexedFieldsBeanClassNotNullable(IndexedFieldsBeanClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    IndexedFieldsBeanClassNotNullable ret = msgpack.read(bytes, IndexedFieldsBeanClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClassNotNullable() throws Exception {
	    super.testIndexedFieldsBeanClassNotNullable();
	}

	@Override
	public void testIndexedFieldsBeanClassNotNullable(IndexedFieldsBeanClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    IndexedFieldsBeanClassNotNullable ret = msgpack.convert(value, IndexedFieldsBeanClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClassNotNullable() throws Exception {
	    super.testIndexedFieldsBeanClassNotNullable();
	}

	@Override
	public void testIndexedFieldsBeanClassNotNullable(IndexedFieldsBeanClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    IndexedFieldsBeanClassNotNullable ret = msgpack.read(in, IndexedFieldsBeanClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClassNotNullable() throws Exception {
	    super.testIndexedFieldsBeanClassNotNullable();
	}

	@Override
	public void testIndexedFieldsBeanClassNotNullable(IndexedFieldsBeanClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    IndexedFieldsBeanClassNotNullable ret = msgpack.read(bytes, IndexedFieldsBeanClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClassNotNullable() throws Exception {
	    super.testIndexedFieldsBeanClassNotNullable();
	}

	@Override
	public void testIndexedFieldsBeanClassNotNullable(IndexedFieldsBeanClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    IndexedFieldsBeanClassNotNullable ret = msgpack.convert(value, IndexedFieldsBeanClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClassNotNullable() throws Exception {
	    super.testIndexedFieldsBeanClassNotNullable();
	}

	@Override
	public void testIndexedFieldsBeanClassNotNullable(IndexedFieldsBeanClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    IndexedFieldsBeanClassNotNullable ret = msgpack.read(in, IndexedFieldsBeanClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestIndexedFieldsBeanClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testIndexedFieldsBeanClassNotNullable() throws Exception {
	    super.testIndexedFieldsBeanClassNotNullable();
	}

	@Override
	public void testIndexedFieldsBeanClassNotNullable(IndexedFieldsBeanClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    IndexedFieldsBeanClassNotNullable ret = msgpack.convert(value, IndexedFieldsBeanClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClass() throws Exception {
	    super.testInheritanceClass();
	}

	@Override
	public void testInheritanceClass(InheritanceClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    InheritanceClass ret = msgpack.read(bytes, InheritanceClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClass() throws Exception {
	    super.testInheritanceClass();
	}

	@Override
	public void testInheritanceClass(InheritanceClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    InheritanceClass ret = msgpack.convert(value, InheritanceClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClass() throws Exception {
	    super.testInheritanceClass();
	}

	@Override
	public void testInheritanceClass(InheritanceClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    InheritanceClass ret = msgpack.read(in, InheritanceClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClass() throws Exception {
	    super.testInheritanceClass();
	}

	@Override
	public void testInheritanceClass(InheritanceClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    InheritanceClass ret = msgpack.read(bytes, InheritanceClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClass() throws Exception {
	    super.testInheritanceClass();
	}

	@Override
	public void testInheritanceClass(InheritanceClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    InheritanceClass ret = msgpack.convert(value, InheritanceClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClass() throws Exception {
	    super.testInheritanceClass();
	}

	@Override
	public void testInheritanceClass(InheritanceClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    InheritanceClass ret = msgpack.read(in, InheritanceClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClass() throws Exception {
	    super.testInheritanceClass();
	}

	@Override
	public void testInheritanceClass(InheritanceClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    InheritanceClass ret = msgpack.convert(value, InheritanceClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClassNotNullable() throws Exception {
	    super.testInheritanceClassNotNullable();
	}

	@Override
	public void testInheritanceClassNotNullable(InheritanceClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    InheritanceClassNotNullable ret = msgpack.read(bytes, InheritanceClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClassNotNullable() throws Exception {
	    super.testInheritanceClassNotNullable();
	}

	@Override
	public void testInheritanceClassNotNullable(InheritanceClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    InheritanceClassNotNullable ret = msgpack.convert(value, InheritanceClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClassNotNullable() throws Exception {
	    super.testInheritanceClassNotNullable();
	}

	@Override
	public void testInheritanceClassNotNullable(InheritanceClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    InheritanceClassNotNullable ret = msgpack.read(in, InheritanceClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClassNotNullable() throws Exception {
	    super.testInheritanceClassNotNullable();
	}

	@Override
	public void testInheritanceClassNotNullable(InheritanceClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    InheritanceClassNotNullable ret = msgpack.read(bytes, InheritanceClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClassNotNullable() throws Exception {
	    super.testInheritanceClassNotNullable();
	}

	@Override
	public void testInheritanceClassNotNullable(InheritanceClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    InheritanceClassNotNullable ret = msgpack.convert(value, InheritanceClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClassNotNullable() throws Exception {
	    super.testInheritanceClassNotNullable();
	}

	@Override
	public void testInheritanceClassNotNullable(InheritanceClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    InheritanceClassNotNullable ret = msgpack.read(in, InheritanceClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestInheritanceClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testInheritanceClassNotNullable() throws Exception {
	    super.testInheritanceClassNotNullable();
	}

	@Override
	public void testInheritanceClassNotNullable(InheritanceClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    InheritanceClassNotNullable ret = msgpack.convert(value, InheritanceClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClass() throws Exception {
	    super.testMessagePackableTypeFieldsClass();
	}

	@Override
	public void testMessagePackableTypeFieldsClass(MessagePackableTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    MessagePackableTypeFieldsClass ret = msgpack.read(bytes, MessagePackableTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClass() throws Exception {
	    super.testMessagePackableTypeFieldsClass();
	}

	@Override
	public void testMessagePackableTypeFieldsClass(MessagePackableTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    MessagePackableTypeFieldsClass ret = msgpack.convert(value, MessagePackableTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClass() throws Exception {
	    super.testMessagePackableTypeFieldsClass();
	}

	@Override
	public void testMessagePackableTypeFieldsClass(MessagePackableTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    MessagePackableTypeFieldsClass ret = msgpack.read(in, MessagePackableTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClass() throws Exception {
	    super.testMessagePackableTypeFieldsClass();
	}

	@Override
	public void testMessagePackableTypeFieldsClass(MessagePackableTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    MessagePackableTypeFieldsClass ret = msgpack.read(bytes, MessagePackableTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClass() throws Exception {
	    super.testMessagePackableTypeFieldsClass();
	}

	@Override
	public void testMessagePackableTypeFieldsClass(MessagePackableTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    MessagePackableTypeFieldsClass ret = msgpack.convert(value, MessagePackableTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClass() throws Exception {
	    super.testMessagePackableTypeFieldsClass();
	}

	@Override
	public void testMessagePackableTypeFieldsClass(MessagePackableTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    MessagePackableTypeFieldsClass ret = msgpack.read(in, MessagePackableTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClass() throws Exception {
	    super.testMessagePackableTypeFieldsClass();
	}

	@Override
	public void testMessagePackableTypeFieldsClass(MessagePackableTypeFieldsClass v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    MessagePackableTypeFieldsClass ret = msgpack.convert(value, MessagePackableTypeFieldsClass.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassNotNullableBufferPackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClassNotNullable() throws Exception {
	    super.testMessagePackableTypeFieldsClassNotNullable();
	}

	@Override
	public void testMessagePackableTypeFieldsClassNotNullable(MessagePackableTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    MessagePackableTypeFieldsClassNotNullable ret = msgpack.read(bytes, MessagePackableTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassNotNullableBufferPackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClassNotNullable() throws Exception {
	    super.testMessagePackableTypeFieldsClassNotNullable();
	}

	@Override
	public void testMessagePackableTypeFieldsClassNotNullable(MessagePackableTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    Value value = msgpack.read(bytes);
	    MessagePackableTypeFieldsClassNotNullable ret = msgpack.convert(value, MessagePackableTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassNotNullableBufferPackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClassNotNullable() throws Exception {
	    super.testMessagePackableTypeFieldsClassNotNullable();
	}

	@Override
	public void testMessagePackableTypeFieldsClassNotNullable(MessagePackableTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    byte[] bytes = msgpack.write(v);
	    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	    MessagePackableTypeFieldsClassNotNullable ret = msgpack.read(in, MessagePackableTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassNotNullablePackBufferUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClassNotNullable() throws Exception {
	    super.testMessagePackableTypeFieldsClassNotNullable();
	}

	@Override
	public void testMessagePackableTypeFieldsClassNotNullable(MessagePackableTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    MessagePackableTypeFieldsClassNotNullable ret = msgpack.read(bytes, MessagePackableTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassNotNullablePackConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClassNotNullable() throws Exception {
	    super.testMessagePackableTypeFieldsClassNotNullable();
	}

	@Override
	public void testMessagePackableTypeFieldsClassNotNullable(MessagePackableTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    byte[] bytes = out.toByteArray();
	    Value value = msgpack.read(bytes);
	    MessagePackableTypeFieldsClassNotNullable ret = msgpack.convert(value, MessagePackableTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassNotNullablePackUnpack extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClassNotNullable() throws Exception {
	    super.testMessagePackableTypeFieldsClassNotNullable();
	}

	@Override
	public void testMessagePackableTypeFieldsClassNotNullable(MessagePackableTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    msgpack.write(out, v);
	    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    MessagePackableTypeFieldsClassNotNullable ret = msgpack.read(in, MessagePackableTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

    public static class TestMessagePackableTypeFieldsClassNotNullableUnconvertConvert extends org.msgpack.template.builder.TestSet {
	@Test @Override
	public void testMessagePackableTypeFieldsClassNotNullable() throws Exception {
	    super.testMessagePackableTypeFieldsClassNotNullable();
	}

	@Override
	public void testMessagePackableTypeFieldsClassNotNullable(MessagePackableTypeFieldsClassNotNullable v) throws Exception {
	    MessagePack msgpack = new MessagePack();
	    Value value = msgpack.unconvert(v);
	    MessagePackableTypeFieldsClassNotNullable ret = msgpack.convert(value, MessagePackableTypeFieldsClassNotNullable.class);
	    assertEquals(v, ret);
	}
    }

}
