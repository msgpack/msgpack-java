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

import org.msgpack.core.{MessagePack, MessagePacker, MessageTypeCastException}
import wvlet.airspec.AirSpec
import wvlet.airspec.spi.PropertyCheck

import java.time.Instant
import java.util
import scala.jdk.CollectionConverters._

/**
  */
class VariableTest extends AirSpec with PropertyCheck {
  private def check(pack: MessagePacker => Unit, checker: Variable => Unit): Unit = {
    val packer = MessagePack.newDefaultBufferPacker()
    pack(packer)
    val msgpack = packer.toByteArray
    packer.close()
    val v        = new Variable()
    val unpacker = MessagePack.newDefaultUnpacker(msgpack)
    unpacker.unpackValue(v)
    checker(v)
    unpacker.close()
  }

  /**
    * Test Value -> MsgPack -> Value
    */
  private def roundTrip(v: Value): Unit = {
    val packer = MessagePack.newDefaultBufferPacker()
    v.writeTo(packer)
    val msgpack  = packer.toByteArray
    val unpacker = MessagePack.newDefaultUnpacker(msgpack)
    val v1       = unpacker.unpackValue()
    unpacker.close()
    v shouldBe v1
    v.immutableValue() shouldBe v1
  }

  private def validateValue[V <: Value](
      v: V,
      asNil: Boolean = false,
      asBoolean: Boolean = false,
      asInteger: Boolean = false,
      asFloat: Boolean = false,
      asBinary: Boolean = false,
      asString: Boolean = false,
      asArray: Boolean = false,
      asMap: Boolean = false,
      asExtension: Boolean = false,
      asTimestamp: Boolean = false
  ): V = {
    v.isNilValue shouldBe asNil
    v.isBooleanValue shouldBe asBoolean
    v.isIntegerValue shouldBe asInteger
    v.isNumberValue shouldBe asInteger | asFloat
    v.isFloatValue shouldBe asFloat
    v.isRawValue shouldBe asBinary | asString
    v.isBinaryValue shouldBe asBinary
    v.isStringValue shouldBe asString
    v.isArrayValue shouldBe asArray
    v.isMapValue shouldBe asMap
    v.isExtensionValue shouldBe asExtension | asTimestamp
    v.isTimestampValue shouldBe asTimestamp

    if (asNil) {
      v.getValueType shouldBe ValueType.NIL
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asNilValue()
      }
    }

    if (asBoolean) {
      v.getValueType shouldBe ValueType.BOOLEAN
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asBooleanValue()
      }
    }

    if (asInteger) {
      v.getValueType shouldBe ValueType.INTEGER
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asIntegerValue()
      }
    }

    if (asFloat) {
      v.getValueType shouldBe ValueType.FLOAT
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asFloatValue()
      }
    }

    if (asBinary | asString) {
      v.asRawValue()
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asRawValue()
      }
    }

    if (asBinary) {
      v.getValueType shouldBe ValueType.BINARY
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asBinaryValue()
      }
    }

    if (asString) {
      v.getValueType shouldBe ValueType.STRING
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asStringValue()
      }
    }

    if (asArray) {
      v.getValueType shouldBe ValueType.ARRAY
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asArrayValue()
      }
    }

    if (asMap) {
      v.getValueType shouldBe ValueType.MAP
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asMapValue()
      }
    }

    if (asExtension) {
      v.getValueType shouldBe ValueType.EXTENSION
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asExtensionValue()
      }
    }

    if (asTimestamp) {
      v.getValueType shouldBe ValueType.EXTENSION
      roundTrip(v)
    } else {
      intercept[MessageTypeCastException] {
        v.asTimestampValue()
      }
    }

    v
  }

  test("Variable") {
    test("read nil") {
      check(
        _.packNil,
        checker = { v =>
          val iv = validateValue(v.asNilValue(), asNil = true)
          iv.toJson shouldBe "null"
        }
      )
    }

    test("read integers") {
      forAll { i: Int =>
        check(
          _.packInt(i),
          checker = { v =>
            val iv = validateValue(v.asIntegerValue(), asInteger = true)
            iv.asInt() shouldBe i
            iv.asLong() shouldBe i.toLong
          }
        )
      }
    }

    test("read double") {
      forAll { x: Double =>
        check(
          _.packDouble(x),
          checker = { v =>
            val iv = validateValue(v.asFloatValue(), asFloat = true)
          //iv.toDouble shouldBe v
          //iv.toFloat shouldBe x.toFloat
          }
        )
      }
    }

    test("read boolean") {
      forAll { x: Boolean =>
        check(
          _.packBoolean(x),
          checker = { v =>
            val iv = validateValue(v.asBooleanValue(), asBoolean = true)
            iv.getBoolean shouldBe x
          }
        )
      }
    }

    test("read binary") {
      forAll { x: Array[Byte] =>
        check(
          { packer =>
            packer.packBinaryHeader(x.length); packer.addPayload(x)
          },
          checker = { v =>
            val iv = validateValue(v.asBinaryValue(), asBinary = true)
            util.Arrays.equals(iv.asByteArray(), x)
          }
        )
      }
    }

    test("read string") {
      forAll { x: String =>
        check(
          _.packString(x),
          checker = { v =>
            val iv = validateValue(v.asStringValue(), asString = true)
            iv.asString() shouldBe x
          }
        )
      }
    }

    test("read array") {
      forAll { x: Seq[Int] =>
        check(
          { packer =>
            packer.packArrayHeader(x.size)
            x.foreach { packer.packInt(_) }
          },
          checker = { v =>
            val iv  = validateValue(v.asArrayValue(), asArray = true)
            val lst = iv.list().asScala.map(_.asIntegerValue().toInt)
            lst shouldBe x
          }
        )
      }
    }

    test("read map") {
      forAll { x: Seq[Int] =>
        // Generate map with unique keys
        val map = x.zipWithIndex.map { case (x, i) => (s"key-${i}", x) }
        check(
          { packer =>
            packer.packMapHeader(map.size)
            map.foreach { x =>
              packer.packString(x._1)
              packer.packInt(x._2)
            }
          },
          checker = { v =>
            val iv  = validateValue(v.asMapValue(), asMap = true)
            val lst = iv.map().asScala.map(p => (p._1.asStringValue().asString(), p._2.asIntegerValue().asInt())).toSeq
            lst.sortBy(_._1) shouldBe map.sortBy(_._1)
          }
        )
      }
    }

    test("read timestamps") {
      forAll { millis: Long =>
        val i = Instant.ofEpochMilli(millis)
        check(
          _.packTimestamp(i),
          checker = { v =>
            val ts = validateValue(v.asTimestampValue(), asTimestamp = true)
            ts.isTimestampValue shouldBe true
            ts.toInstant shouldBe i
          }
        )
      }
    }
  }
}
