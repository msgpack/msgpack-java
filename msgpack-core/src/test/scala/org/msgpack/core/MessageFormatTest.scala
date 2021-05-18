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
package org.msgpack.core

import org.msgpack.value.MessagePackCode
import org.msgpack.value.ValueType
import wvlet.airspec.AirSpec
import wvlet.airspec.spi.AirSpecException

import scala.util.Random

/**
  * Created on 2014/05/07.
  */
class MessageFormatTest extends AirSpec with Benchmark {
  test("MessageFormat") {
    test("cover all byte codes") {
      def checkV(b: Byte, tpe: ValueType) {
        try MessageFormat.valueOf(b).getValueType shouldBe tpe
        catch {
          case e: AirSpecException =>
            error(f"Failure when looking at byte ${b}%02x")
            throw e
        }
      }

      def checkF(b: Byte, f: MessageFormat) {
        MessageFormat.valueOf(b) shouldBe f
      }

      def check(b: Byte, tpe: ValueType, f: MessageFormat) {
        checkV(b, tpe)
        checkF(b, f)
      }

      for (i <- 0 until 0x7f) {
        check(i.toByte, ValueType.INTEGER, MessageFormat.POSFIXINT)
      }

      for (i <- 0x80 until 0x8f) {
        check(i.toByte, ValueType.MAP, MessageFormat.FIXMAP)
      }

      for (i <- 0x90 until 0x9f) {
        check(i.toByte, ValueType.ARRAY, MessageFormat.FIXARRAY)
      }

      check(MessagePackCode.NIL, ValueType.NIL, MessageFormat.NIL)

      MessageFormat.valueOf(MessagePackCode.NEVER_USED) shouldBe MessageFormat.NEVER_USED

      for (i <- Seq(MessagePackCode.TRUE, MessagePackCode.FALSE)) {
        check(i, ValueType.BOOLEAN, MessageFormat.BOOLEAN)
      }

      check(MessagePackCode.BIN8, ValueType.BINARY, MessageFormat.BIN8)
      check(MessagePackCode.BIN16, ValueType.BINARY, MessageFormat.BIN16)
      check(MessagePackCode.BIN32, ValueType.BINARY, MessageFormat.BIN32)

      check(MessagePackCode.FIXEXT1, ValueType.EXTENSION, MessageFormat.FIXEXT1)
      check(MessagePackCode.FIXEXT2, ValueType.EXTENSION, MessageFormat.FIXEXT2)
      check(MessagePackCode.FIXEXT4, ValueType.EXTENSION, MessageFormat.FIXEXT4)
      check(MessagePackCode.FIXEXT8, ValueType.EXTENSION, MessageFormat.FIXEXT8)
      check(MessagePackCode.FIXEXT16, ValueType.EXTENSION, MessageFormat.FIXEXT16)
      check(MessagePackCode.EXT8, ValueType.EXTENSION, MessageFormat.EXT8)
      check(MessagePackCode.EXT16, ValueType.EXTENSION, MessageFormat.EXT16)
      check(MessagePackCode.EXT32, ValueType.EXTENSION, MessageFormat.EXT32)

      check(MessagePackCode.INT8, ValueType.INTEGER, MessageFormat.INT8)
      check(MessagePackCode.INT16, ValueType.INTEGER, MessageFormat.INT16)
      check(MessagePackCode.INT32, ValueType.INTEGER, MessageFormat.INT32)
      check(MessagePackCode.INT64, ValueType.INTEGER, MessageFormat.INT64)
      check(MessagePackCode.UINT8, ValueType.INTEGER, MessageFormat.UINT8)
      check(MessagePackCode.UINT16, ValueType.INTEGER, MessageFormat.UINT16)
      check(MessagePackCode.UINT32, ValueType.INTEGER, MessageFormat.UINT32)
      check(MessagePackCode.UINT64, ValueType.INTEGER, MessageFormat.UINT64)

      check(MessagePackCode.STR8, ValueType.STRING, MessageFormat.STR8)
      check(MessagePackCode.STR16, ValueType.STRING, MessageFormat.STR16)
      check(MessagePackCode.STR32, ValueType.STRING, MessageFormat.STR32)

      check(MessagePackCode.FLOAT32, ValueType.FLOAT, MessageFormat.FLOAT32)
      check(MessagePackCode.FLOAT64, ValueType.FLOAT, MessageFormat.FLOAT64)

      check(MessagePackCode.ARRAY16, ValueType.ARRAY, MessageFormat.ARRAY16)
      check(MessagePackCode.ARRAY32, ValueType.ARRAY, MessageFormat.ARRAY32)

      for (i <- 0xe0 to 0xff) {
        check(i.toByte, ValueType.INTEGER, MessageFormat.NEGFIXINT)
      }
    }

    test("improve the valueOf performance") {
      val N   = 1000000
      val idx = (0 until N).map(x => Random.nextInt(256).toByte).toArray[Byte]

      // Initialize
      MessageFormat.valueOf(0.toByte)

      time("lookup", repeat = 10) {
        block("switch") {
          var i = 0
          while (i < N) {
            MessageFormat.toMessageFormat(idx(i))
            i += 1
          }
        }

        block("table") {
          var i = 0
          while (i < N) {
            MessageFormat.valueOf(idx(i))
            i += 1
          }
        }
      }
    }
  }
}
