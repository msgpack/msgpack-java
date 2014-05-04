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

public final class TestImmutableValue {

    @Test
    public void testNilValue() {
        Value v = ValueFactory.createNilValue();
        Assert.assertEquals(ValueType.NIL, v.getType());
        Assert.assertTrue(v.isNilValue());
        Assert.assertFalse(v.isBooleanValue());
        Assert.assertFalse(v.isIntegerValue());
        Assert.assertFalse(v.isFloatValue());
        Assert.assertFalse(v.isStringValue());
        Assert.assertFalse(v.isBinaryValue());
        Assert.assertFalse(v.isArrayValue());
        Assert.assertFalse(v.isMapValue());
        Assert.assertFalse(v.isExtendedValue());

        Assert.assertFalse(v.isRawValue());
        Assert.assertFalse(v.isNumberValue());
    }


    @Test
    public void testBooleanValue() {
        Value v = ValueFactory.createBooleanValue(false);
        Assert.assertEquals(ValueType.BOOLEAN, v.getType());
        Assert.assertFalse(v.isNilValue());
        Assert.assertTrue(v.isBooleanValue());
        Assert.assertFalse(v.isIntegerValue());
        Assert.assertFalse(v.isFloatValue());
        Assert.assertFalse(v.isStringValue());
        Assert.assertFalse(v.isBinaryValue());
        Assert.assertFalse(v.isArrayValue());
        Assert.assertFalse(v.isMapValue());
        Assert.assertFalse(v.isExtendedValue());

        Assert.assertFalse(v.isRawValue());
        Assert.assertFalse(v.isNumberValue());
    }

    @Test
    public void testIntegerValue() {
        Value v = ValueFactory.createIntegerValue(10);
        Assert.assertEquals(ValueType.INTEGER, v.getType());
        Assert.assertFalse(v.isNilValue());
        Assert.assertFalse(v.isBooleanValue());
        Assert.assertTrue(v.isIntegerValue());
        Assert.assertFalse(v.isFloatValue());
        Assert.assertFalse(v.isStringValue());
        Assert.assertFalse(v.isBinaryValue());
        Assert.assertFalse(v.isArrayValue());
        Assert.assertFalse(v.isMapValue());
        Assert.assertFalse(v.isExtendedValue());

        Assert.assertFalse(v.isRawValue());
        Assert.assertTrue(v.isNumberValue());
    }

    @Test
    public void testFloatValue() {
        Value v = ValueFactory.createFloatValue(0.1f);
        Assert.assertEquals(ValueType.FLOAT, v.getType());
        Assert.assertFalse(v.isNilValue());
        Assert.assertFalse(v.isBooleanValue());
        Assert.assertFalse(v.isIntegerValue());
        Assert.assertTrue(v.isFloatValue());
        Assert.assertFalse(v.isStringValue());
        Assert.assertFalse(v.isBinaryValue());
        Assert.assertFalse(v.isArrayValue());
        Assert.assertFalse(v.isMapValue());
        Assert.assertFalse(v.isExtendedValue());

        Assert.assertFalse(v.isRawValue());
        Assert.assertTrue(v.isNumberValue());
    }

    @Test
    public void testStringValue() {
        Value v = ValueFactory.createStringValue("msgpack");
        Assert.assertEquals(ValueType.STRING, v.getType());
        Assert.assertFalse(v.isNilValue());
        Assert.assertFalse(v.isBooleanValue());
        Assert.assertFalse(v.isIntegerValue());
        Assert.assertFalse(v.isFloatValue());
        Assert.assertTrue(v.isStringValue());
        Assert.assertFalse(v.isBinaryValue());
        Assert.assertFalse(v.isArrayValue());
        Assert.assertFalse(v.isMapValue());
        Assert.assertFalse(v.isExtendedValue());

        Assert.assertTrue(v.isRawValue());
        Assert.assertFalse(v.isNumberValue());
    }

    @Test
    public void testBinaryValue() {
        Value v = ValueFactory.createBinaryValue("msgpack".getBytes());
        Assert.assertEquals(ValueType.BINARY, v.getType());
        Assert.assertFalse(v.isNilValue());
        Assert.assertFalse(v.isBooleanValue());
        Assert.assertFalse(v.isIntegerValue());
        Assert.assertFalse(v.isFloatValue());
        Assert.assertFalse(v.isStringValue());
        Assert.assertTrue(v.isBinaryValue());
        Assert.assertFalse(v.isArrayValue());
        Assert.assertFalse(v.isMapValue());
        Assert.assertFalse(v.isExtendedValue());

        Assert.assertTrue(v.isRawValue());
        Assert.assertFalse(v.isNumberValue());
    }

    @Test
    public void testArrayValue() {
        Value v = ValueFactory.createArrayValue(Collections.<Value>emptyList());
        Assert.assertEquals(ValueType.ARRAY, v.getType());
        Assert.assertFalse(v.isNilValue());
        Assert.assertFalse(v.isBooleanValue());
        Assert.assertFalse(v.isIntegerValue());
        Assert.assertFalse(v.isFloatValue());
        Assert.assertFalse(v.isStringValue());
        Assert.assertFalse(v.isBinaryValue());
        Assert.assertTrue(v.isArrayValue());
        Assert.assertFalse(v.isMapValue());
        Assert.assertFalse(v.isExtendedValue());

        Assert.assertFalse(v.isRawValue());
        Assert.assertFalse(v.isNumberValue());

        Assert.assertTrue("getType should return Array", v.isArrayValue());
        ArrayValue arrayValue = (ArrayValue) v;
        Assert.assertTrue(arrayValue.isEmpty());
        Assert.assertEquals("size should be zero", 0, arrayValue.size());
    }

    @Test
    public void testMapValue() {
        Value v = ValueFactory.createMapValue(ImmutableArrayMapValueImpl.getEmptyMapInstance());
        Assert.assertEquals(ValueType.MAP, v.getType());
        Assert.assertFalse(v.isNilValue());
        Assert.assertFalse(v.isBooleanValue());
        Assert.assertFalse(v.isIntegerValue());
        Assert.assertFalse(v.isFloatValue());
        Assert.assertFalse(v.isStringValue());
        Assert.assertFalse(v.isBinaryValue());
        Assert.assertFalse(v.isArrayValue());
        Assert.assertTrue(v.isMapValue());
        Assert.assertFalse(v.isExtendedValue());

        Assert.assertFalse(v.isRawValue());
        Assert.assertFalse(v.isNumberValue());
    }

    // TODO: Add test after implementing ExtendedValue
    //@Test
    //public void testExtendedValue() {
    //    Value v = ValueFactory.createExtendedValue();
    //    assertValueConditionl(ValueType.EXTENDED, v.getType());
    //}


}
