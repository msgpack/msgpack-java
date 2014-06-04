package org.msgpack.core

import MessagePack.Code._
import scala.util.Random
import org.msgpack.core.MessagePack.Code


/**
 * Created on 2014/05/06.
 */
class ValueTypeTest extends MessagePackSpec {

  "ValueType" should {

    "lookup ValueType from a byte value" taggedAs("code") in {

      def check(b:Byte, tpe:ValueType) {
        ValueType.valueOf(b) shouldBe tpe
      }

      for(i <- 0 until 0x7f)
        check(i.toByte, ValueType.INTEGER)

      for(i <- 0x80 until 0x8f)
        check(i.toByte, ValueType.MAP)

      for(i <- 0x90 until 0x9f)
        check(i.toByte, ValueType.ARRAY)

      check(NIL, ValueType.NIL)

      try {
        ValueType.valueOf(NEVER_USED)
        fail("NEVER_USED type should not have ValueType")
      }
      catch {
        case e:MessageFormatException =>
          // OK
      }

      check(TRUE, ValueType.BOOLEAN)
      check(FALSE, ValueType.BOOLEAN)

      for(t <- Seq(BIN8, BIN16, BIN32))
        check(t, ValueType.BINARY)

      for(t <- Seq(FIXEXT1, FIXEXT2, FIXEXT4, FIXEXT8, FIXEXT16, EXT8, EXT16, EXT32))
        check(t, ValueType.EXTENDED)

      for(t <- Seq(INT8, INT16, INT32, INT64, UINT8, UINT16, UINT32, UINT64))
        check(t, ValueType.INTEGER)

      for(t <- Seq(STR8, STR16, STR32))
        check(t, ValueType.STRING)

      for(t <- Seq(FLOAT32, FLOAT64))
        check(t, ValueType.FLOAT)

      for(t <- Seq(ARRAY16, ARRAY32))
        check(t, ValueType.ARRAY)

      for(i <- 0xe0 until 0xff)
        check(i.toByte, ValueType.INTEGER)

    }

    "lookup table" in {

      val N = 100000
      val idx = {
        val b = Array.newBuilder[Byte]
        for(i <- 0 until N) {
          val r = Iterator.continually(Random.nextInt(256)).find(_.toByte != Code.NEVER_USED).get
          b += r.toByte
        }
        b.result()
      }

      time("lookup", repeat=100) {
        block("switch") {
          var i = 0
          while(i < N) {
            MessageFormat.toMessageFormat(idx(i)).getValueType()
            i += 1
          }
        }

        block("table") {
          var i = 0
          while(i < N) {
            ValueType.valueOf(idx(i))
            i += 1
          }
        }

      }

    }



  }
}
