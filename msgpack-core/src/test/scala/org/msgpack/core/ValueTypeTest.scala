package org.msgpack.core

import MessagePack.Code._
import scala.util.Random
import xerial.core.log.LogLevel


/**
 * Created on 2014/05/06.
 */
class ValueTypeTest extends MessagePackSpec {

  "ValueType" should {

    "lookup ValueType from a byte value" in {

      ValueType.lookUp(INT8) shouldBe ValueType.INTEGER
      ValueType.lookUp(INT16) shouldBe ValueType.INTEGER
      ValueType.lookUp(INT32) shouldBe ValueType.INTEGER
      ValueType.lookUp(INT64) shouldBe ValueType.INTEGER
      ValueType.lookUp(UINT8) shouldBe ValueType.INTEGER
      ValueType.lookUp(UINT16) shouldBe ValueType.INTEGER

      ValueType.lookUp(STR8) shouldBe ValueType.STRING
      ValueType.lookUp(STR16) shouldBe ValueType.STRING
      ValueType.lookUp(STR32) shouldBe ValueType.STRING

    }

    "lookup table" in {

      val N = 10000000
      val idx = {
        val b = Array.newBuilder[Byte]
        for(i <- 0 until N)
          b += (Random.nextInt(256) - 127).toByte
        b.result()
      }

      time("lookup", repeat=100, logLevel = LogLevel.INFO) {
        block("switch") {
          var i = 0
          while(i < N) {
            ValueType.toValueType(idx(i))
            i += 1
          }
        }

        block("table") {
          var i = 0
          while(i < N) {
            ValueType.lookUp(idx(i))
            i += 1
          }
        }

      }

    }



  }
}
