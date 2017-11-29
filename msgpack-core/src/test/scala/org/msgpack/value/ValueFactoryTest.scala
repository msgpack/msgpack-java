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
package org.msgpack.value

import org.msgpack.core.MessagePackSpec

/**
  *
  */
class ValueFactoryTest extends MessagePackSpec {

  def isValid(v: Value,
              expected: ValueType,
              isNil: Boolean = false,
              isBoolean: Boolean = false,
              isInteger: Boolean = false,
              isString: Boolean = false,
              isFloat: Boolean = false,
              isBinary: Boolean = false,
              isArray: Boolean = false,
              isMap: Boolean = false,
              isExtension: Boolean = false,
              isRaw: Boolean = false,
              isNumber: Boolean = false) {
    v.isNilValue shouldBe isNil
    v.isBooleanValue shouldBe isBoolean
    v.isIntegerValue shouldBe isInteger
    v.isFloatValue shouldBe isFloat
    v.isStringValue shouldBe isString
    v.isBinaryValue shouldBe isBinary
    v.isArrayValue shouldBe isArray
    v.isMapValue shouldBe isMap
    v.isExtensionValue shouldBe isExtension
    v.isRawValue shouldBe isRaw
    v.isNumberValue shouldBe isNumber
  }

  "ValueFactory" should {

    "create valid type values" in {
      isValid(ValueFactory.newNil(), expected = ValueType.NIL, isNil = true)
      forAll { (v: Boolean) =>
        isValid(ValueFactory.newBoolean(v), expected = ValueType.BOOLEAN, isBoolean = true)
      }
      forAll { (v: Int) =>
        isValid(ValueFactory.newInteger(v), expected = ValueType.INTEGER, isInteger = true, isNumber = true)
      }
      forAll { (v: Float) =>
        isValid(ValueFactory.newFloat(v), expected = ValueType.FLOAT, isFloat = true, isNumber = true)
      }
      forAll { (v: String) =>
        isValid(ValueFactory.newString(v), expected = ValueType.STRING, isString = true, isRaw = true)
      }
      forAll { (v: Array[Byte]) =>
        isValid(ValueFactory.newBinary(v), expected = ValueType.BINARY, isBinary = true, isRaw = true)
      }
      isValid(ValueFactory.emptyArray(), expected = ValueType.ARRAY, isArray = true)
      isValid(ValueFactory.emptyMap(), expected = ValueType.MAP, isMap = true)
      forAll { (v: Array[Byte]) =>
        isValid(ValueFactory.newExtension(0, v), expected = ValueType.EXTENSION, isExtension = true, isRaw = false)
      }
    }
  }
}
