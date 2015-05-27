package org.msgpack.core

import org.scalatest.exceptions.TestFailedException
import org.msgpack.core.MessagePack.Code
import scala.util.Random
import org.msgpack.value.ValueType

/**
 * Created on 2014/05/07.
 */
class MessageFormatTest extends MessagePackSpec {

  "MessageFormat" should {
    "cover all byte codes" in {

      def checkV(b:Byte, tpe:ValueType) {
        try
          MessageFormat.valueOf(b).getValueType shouldBe tpe
        catch {
          case e:TestFailedException =>
            error(f"Failure when looking at byte ${b}%02x")
            throw e
        }
      }

      def checkF(b:Byte, f:MessageFormat) {
        MessageFormat.valueOf(b) shouldBe f
      }

      def check(b:Byte, tpe:ValueType, f:MessageFormat) {
        checkV(b, tpe)
        checkF(b, f)
      }

      for(i <- 0 until 0x7f)
        check(i.toByte, ValueType.INTEGER, MessageFormat.POSFIXINT)

      for(i <- 0x80 until 0x8f)
        check(i.toByte, ValueType.MAP, MessageFormat.FIXMAP)

      for(i <- 0x90 until 0x9f)
        check(i.toByte, ValueType.ARRAY, MessageFormat.FIXARRAY)

      check(Code.NIL, ValueType.NIL, MessageFormat.NIL)

      MessageFormat.valueOf(Code.NEVER_USED) shouldBe MessageFormat.NEVER_USED

      for(i <- Seq(Code.TRUE, Code.FALSE))
        check(i, ValueType.BOOLEAN, MessageFormat.BOOLEAN)

      check(Code.BIN8, ValueType.BINARY, MessageFormat.BIN8)
      check(Code.BIN16, ValueType.BINARY, MessageFormat.BIN16)
      check(Code.BIN32, ValueType.BINARY, MessageFormat.BIN32)

      check(Code.FIXEXT1, ValueType.EXTENDED, MessageFormat.FIXEXT1)
      check(Code.FIXEXT2, ValueType.EXTENDED, MessageFormat.FIXEXT2)
      check(Code.FIXEXT4, ValueType.EXTENDED, MessageFormat.FIXEXT4)
      check(Code.FIXEXT8, ValueType.EXTENDED, MessageFormat.FIXEXT8)
      check(Code.FIXEXT16, ValueType.EXTENDED, MessageFormat.FIXEXT16)
      check(Code.EXT8, ValueType.EXTENDED, MessageFormat.EXT8)
      check(Code.EXT16, ValueType.EXTENDED, MessageFormat.EXT16)
      check(Code.EXT32, ValueType.EXTENDED, MessageFormat.EXT32)


      check(Code.INT8, ValueType.INTEGER, MessageFormat.INT8)
      check(Code.INT16, ValueType.INTEGER, MessageFormat.INT16)
      check(Code.INT32, ValueType.INTEGER, MessageFormat.INT32)
      check(Code.INT64, ValueType.INTEGER, MessageFormat.INT64)
      check(Code.UINT8, ValueType.INTEGER, MessageFormat.UINT8)
      check(Code.UINT16, ValueType.INTEGER, MessageFormat.UINT16)
      check(Code.UINT32, ValueType.INTEGER, MessageFormat.UINT32)
      check(Code.UINT64, ValueType.INTEGER, MessageFormat.UINT64)

      check(Code.STR8, ValueType.STRING, MessageFormat.STR8)
      check(Code.STR16, ValueType.STRING, MessageFormat.STR16)
      check(Code.STR32, ValueType.STRING, MessageFormat.STR32)


      check(Code.FLOAT32, ValueType.FLOAT, MessageFormat.FLOAT32)
      check(Code.FLOAT64, ValueType.FLOAT, MessageFormat.FLOAT64)

      check(Code.ARRAY16, ValueType.ARRAY, MessageFormat.ARRAY16)
      check(Code.ARRAY32, ValueType.ARRAY, MessageFormat.ARRAY32)

      for(i <- 0xe0 to 0xff)
        check(i.toByte, ValueType.INTEGER, MessageFormat.NEGFIXINT)

    }

    "improve the valueOf performance" in {

      val N = 1000000
      val idx = (0 until N).map(x => Random.nextInt(256).toByte).toArray[Byte]

      // Initialize
      MessageFormat.valueOf(0.toByte)

      time("lookup", repeat = 10) {
        block("switch") {
          var i = 0
          while(i < N) {
            MessageFormat.toMessageFormat(idx(i))
            i += 1
          }
        }

        block("table") {
          var i = 0
          while(i < N) {
            MessageFormat.valueOf(idx(i))
            i += 1
          }
        }

      }

    }

  }



}
