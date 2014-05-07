package org.msgpack.core

import org.scalatest.FunSuite
import org.msgpack.core.MessagePack.Code._
import org.scalatest.exceptions.TestFailedException
import org.msgpack.core.MessagePack.Code

/**
 * Created on 2014/05/07.
 */
class MessageFormatTest extends MessagePackSpec {

  "MessageFormat" should {
    "cover all byte codes" in {

      def check(b:Byte, tpe:ValueType) {
        try
          MessageFormat.lookUp(b).getValueType shouldBe tpe
        catch {
          case e:TestFailedException =>
            error(f"Failure when looking at byte ${b}%02x")
            throw e
        }
      }

      for(i <- 0 until 0x7f)
        check(i.toByte, ValueType.INTEGER)

      for(i <- 0x80 until 0x8f)
        check(i.toByte, ValueType.MAP)

      for(i <- 0x90 until 0x9f)
        check(i.toByte, ValueType.ARRAY)

      check(NIL, ValueType.NIL)
      check(NEVER_USED, ValueType.UNKNOWN)
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

    "report detailed format type" in {


    }


  }



}
