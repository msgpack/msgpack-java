//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.value;


import org.junit.Assert;
import org.junit.Test;
import org.msgpack.core.ValueType;
import org.msgpack.value.impl.ImmutableArrayMapValueImpl;

import java.util.Collections;
import java.util.EnumSet;

public final class TestImmutableValue {
    EnumSet<ValueType> valueTypes = EnumSet.allOf(ValueType.class);

    @Test
    public void testNilValue() {
        Value v = ValueFactory.createNilValue();
        assertValueType(ValueType.NIL, v.getType());
        Assert.assertTrue(v.isNilValue());
    }

    @Test
    public void testBooleanValue() {
        Value v = ValueFactory.createBooleanValue(false);
        assertValueType(ValueType.BOOLEAN, v.getType());
        Assert.assertTrue(v.isBooleanValue());
    }

    @Test
    public void testIntegerValue() {
        Value v = ValueFactory.createIntegerValue(10);
        assertValueType(ValueType.INTEGER, v.getType());
        Assert.assertTrue(v.isIntegerValue());
    }

    @Test
    public void testFloatValue() {
        Value v = ValueFactory.createFloatValue(0.1f);
        assertValueType(ValueType.FLOAT, v.getType());
        Assert.assertTrue(v.isFloatValue());
    }

    @Test
    public void testStringValue() {
        Value v = ValueFactory.createStringValue("msgpack");
        assertValueType(ValueType.STRING, v.getType());
        Assert.assertTrue(v.isStringValue());
    }

    @Test
    public void testBinaryValue() {
        Value v = ValueFactory.createBinaryValue("msgpack".getBytes());
        assertValueType(ValueType.BINARY, v.getType());
        Assert.assertTrue(v.isBinaryValue());
    }

    @Test
    public void testArrayValue() {
        Value v = ValueFactory.createArrayValue(Collections.<Value>emptyList());
        assertValueType(ValueType.ARRAY, v.getType());
        Assert.assertTrue("getType should return Array", v.isArrayValue());
        ArrayValue arrayValue = (ArrayValue) v;
        Assert.assertTrue(arrayValue.isEmpty());
        Assert.assertEquals("size should be zero", 0, arrayValue.size());
        Assert.assertTrue(v.isArrayValue());
    }

    @Test
    public void testMapValue() {
        Value v = ValueFactory.createMapValue(ImmutableArrayMapValueImpl.getEmptyMapInstance());
        assertValueType(ValueType.MAP, v.getType());
        Assert.assertTrue(v.isMapValue());
    }

    //@Test
    //public void testExtendedValue() {
    //    Value v = ValueFactory.createExtendedValue();
    //    assertValueType(ValueType.MAP, v.getType());
    //}

    public void assertValueType(ValueType expected, ValueType actual) {
        for (ValueType type: valueTypes) {
            if (expected.equals(type)) {
                Assert.assertEquals("getType should return " + expected,
                        expected, actual);
            } else {
                Assert.assertNotSame("getType should not return " + expected,
                        type, actual);
            }
        }
    }

}
