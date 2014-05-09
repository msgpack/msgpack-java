package org.msgpack.core

import org.scalatest.FunSuite
import org.msgpack.core.MessagePack.Code._
import org.scalatest.exceptions.TestFailedException
import org.msgpack.core.MessagePack.Code
import scala.util.Random
import xerial.core.log.LogLevel

/**
 * Created on 2014/05/07.
 */
class MessageTypeTest extends MessagePackSpec {

  "MessageFormat" should {
    "cover all byte codes" in {

      def checkV(b:Byte, tpe:MessageTypeFamily) {
        try
          MessageType.lookUp(b).getTypeFamily shouldBe tpe
        catch {
          case e:TestFailedException =>
            error(f"Failure when looking at byte ${b}%02x")
            throw e
        }
      }

      def checkF(b:Byte, f:MessageType) {
        MessageType.lookUp(b) shouldBe f
      }

      def check(b:Byte, tpe:MessageTypeFamily, f:MessageType) {
        checkV(b, tpe)
        checkF(b, f)
      }

      for(i <- 0 until 0x7f)
        check(i.toByte, MessageTypeFamily.INTEGER, MessageType.POSFIXINT)

      for(i <- 0x80 until 0x8f)
        check(i.toByte, MessageTypeFamily.MAP, MessageType.FIXMAP)

      for(i <- 0x90 until 0x9f)
        check(i.toByte, MessageTypeFamily.ARRAY, MessageType.FIXARRAY)

      check(Code.NIL, MessageTypeFamily.NIL, MessageType.NIL)

      check(Code.NEVER_USED, MessageTypeFamily.UNKNOWN, MessageType.UNKNOWN)

      for(i <- Seq(Code.TRUE, Code.FALSE))
        check(i, MessageTypeFamily.BOOLEAN, MessageType.BOOLEAN)

      check(Code.BIN8, MessageTypeFamily.BINARY, MessageType.BIN8)
      check(Code.BIN16, MessageTypeFamily.BINARY, MessageType.BIN16)
      check(Code.BIN32, MessageTypeFamily.BINARY, MessageType.BIN32)

      check(Code.FIXEXT1, MessageTypeFamily.EXTENDED, MessageType.FIXEXT1)
      check(Code.FIXEXT2, MessageTypeFamily.EXTENDED, MessageType.FIXEXT2)
      check(Code.FIXEXT4, MessageTypeFamily.EXTENDED, MessageType.FIXEXT4)
      check(Code.FIXEXT8, MessageTypeFamily.EXTENDED, MessageType.FIXEXT8)
      check(Code.FIXEXT16, MessageTypeFamily.EXTENDED, MessageType.FIXEXT16)
      check(Code.EXT8, MessageTypeFamily.EXTENDED, MessageType.EXT8)
      check(Code.EXT16, MessageTypeFamily.EXTENDED, MessageType.EXT16)
      check(Code.EXT32, MessageTypeFamily.EXTENDED, MessageType.EXT32)


      check(Code.INT8, MessageTypeFamily.INTEGER, MessageType.INT8)
      check(Code.INT16, MessageTypeFamily.INTEGER, MessageType.INT16)
      check(Code.INT32, MessageTypeFamily.INTEGER, MessageType.INT32)
      check(Code.INT64, MessageTypeFamily.INTEGER, MessageType.INT64)
      check(Code.UINT8, MessageTypeFamily.INTEGER, MessageType.UINT8)
      check(Code.UINT16, MessageTypeFamily.INTEGER, MessageType.UINT16)
      check(Code.UINT32, MessageTypeFamily.INTEGER, MessageType.UINT32)
      check(Code.UINT64, MessageTypeFamily.INTEGER, MessageType.UINT64)

      check(Code.STR8, MessageTypeFamily.STRING, MessageType.STR8)
      check(Code.STR16, MessageTypeFamily.STRING, MessageType.STR16)
      check(Code.STR32, MessageTypeFamily.STRING, MessageType.STR32)


      check(Code.FLOAT32, MessageTypeFamily.FLOAT, MessageType.FLOAT32)
      check(Code.FLOAT64, MessageTypeFamily.FLOAT, MessageType.FLOAT64)

      check(Code.ARRAY16, MessageTypeFamily.ARRAY, MessageType.ARRAY16)
      check(Code.ARRAY32, MessageTypeFamily.ARRAY, MessageType.ARRAY32)

      for(i <- 0xe0 until 0xff)
        check(i.toByte, MessageTypeFamily.INTEGER, MessageType.NEGFIXINT)

    }

    "improve the lookUp performance" in {

      val N = 1000000
      val idx = (0 until N).map(x => Random.nextInt(256).toByte).toArray[Byte]

      // Initialize
      MessageType.lookUp(0)

      time("lookup", repeat = 100, logLevel = LogLevel.INFO) {
        block("switch") {
          var i = 0
          while(i < N) {
            MessageType.toMessageFormat(idx(i))
            i += 1
          }
        }

        block("table") {
          var i = 0
          while(i < N) {
            MessageType.lookUp(idx(i))
            i += 1
          }
        }

      }

    }

  }



}
