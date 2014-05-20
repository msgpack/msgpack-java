package org.msgpack.core

import xerial.core.log.LogLevel
import scala.util.Random
import MessagePack.Code
import org.scalatest.prop.PropertyChecks
import java.io.ByteArrayOutputStream

/**
 * Created on 2014/05/07.
 */
class MessagePackTest extends MessagePackSpec with PropertyChecks {

  "MessagePack" should {
    "detect fixint values" in {

      for (i <- 0 until 0x79) {
        Code.isPosFixInt(i.toByte) shouldBe true
      }

      for (i <- 0x80 until 0xFF) {
        Code.isPosFixInt(i.toByte) shouldBe false
      }
    }

    "detect fixint fast" in {

      val N = 1000000
      val idx = (0 until N).map(x => Random.nextInt(256).toByte).toArray[Byte]

      time("check fixint", repeat = 1000, logLevel = LogLevel.INFO) {

        block("mask") {
          var i = 0
          var count = 0
          while (i < N) {
            if ((idx(i) & Code.POSFIXINT_MASK) == 0) {
              count += 1
            }
            i += 1
          }
        }

        block("mask-f") {
          var i = 0
          var count = 0
          while (i < N) {
            if (Code.isPosFixInt(idx(i))) {
              count += 1
            }
            i += 1
          }
        }

        block("shift") {
          var i = 0
          var count = 0
          while (i < N) {
            if ((idx(i) >>> 7) == 0) {
              count += 1
            }
            i += 1
          }

        }

      }

    }

    "detect neg fix int values" in {

      for (i <- 0 until 0xe0) {
        Code.isNegFixInt(i.toByte) shouldBe false
      }

      for (i <- 0xe0 until 0xFF) {
        Code.isNegFixInt(i.toByte) shouldBe true
      }

    }




  }
}
