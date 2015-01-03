package org.msgpack.value

import org.scalatest.FunSuite
import org.msgpack.core.MessagePackSpec

/**
 * Created on 6/13/14.
 */
class ValueFactoryTest extends MessagePackSpec {

  def isValid(v:Value,
              expected:ValueType,
              isNil: Boolean = false,
              isBoolean: Boolean = false,
              isInteger: Boolean = false,
              isString : Boolean = false,
              isFloat: Boolean = false,
              isBinary: Boolean = false,
              isArray: Boolean = false,
              isMap: Boolean = false,
              isExtended : Boolean = false,
              isRaw : Boolean = false,
              isNumber : Boolean = false
               ) {
    v.isNil shouldBe isNil
    v.isBoolean shouldBe isBoolean
    v.isInteger shouldBe isInteger
    v.isFloat shouldBe isFloat
    v.isString shouldBe isString
    v.isBinary shouldBe isBinary
    v.isArray shouldBe isArray
    v.isMap shouldBe isMap
    v.isExtended shouldBe isExtended
    v.isRaw shouldBe isRaw
    v.isNumber shouldBe isNumber
  }

  "ValueFactory" should {

    "create valid type values" in {
      isValid(ValueFactory.nilValue(), expected=ValueType.NIL, isNil = true)
      forAll{(v:Boolean) => isValid(ValueFactory.newBoolean(v), expected=ValueType.BOOLEAN, isBoolean = true)}
      forAll{(v:Int) => isValid(ValueFactory.newInt(v), expected=ValueType.INTEGER, isInteger = true, isNumber = true)}
      forAll{(v:Float) => isValid(ValueFactory.newFloat(v), expected=ValueType.FLOAT, isFloat = true, isNumber = true)}
      forAll{(v:String) => isValid(ValueFactory.newString(v), expected=ValueType.STRING, isString = true, isRaw = true)}
      forAll{(v:Array[Byte]) => isValid(ValueFactory.newBinary(v), expected=ValueType.BINARY, isBinary = true, isRaw = true)}
      isValid(ValueFactory.emptyArray(), expected=ValueType.ARRAY, isArray = true)
      isValid(ValueFactory.emptyMap(), expected=ValueType.MAP, isMap = true)
      forAll{(v:Array[Byte]) => isValid(ValueFactory.newExtendedValue(0, v), expected=ValueType.EXTENDED, isExtended=true, isRaw=true)}
    }

  }

  "StringValue" should {
    "return the same hash code if they are equal" in {
      val str = "a"
      val a1 = ValueFactory.newRawString(str.getBytes("UTF-8"))
      val a2 = ValueFactory.newString(str)

      a1.shouldEqual(a2)
      a1.hashCode.shouldEqual(a2.hashCode)
      a2.shouldEqual(a1)
      a2.hashCode.shouldEqual(a1.hashCode)
    }
  }
}
