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

import org.msgpack.core.MessagePack.Code.*
import org.msgpack.core.{MessageFormat, MessageFormatException}
import wvlet.airspec.AirSpec

/**
  * Created on 2014/05/06.
  */
class ValueTypeTest extends AirSpec:

  test("lookup ValueType from a byte value") {
    def check(b: Byte, tpe: ValueType): Unit = MessageFormat.valueOf(b).getValueType shouldBe tpe

    for i <- 0 until 0x7f do
      check(i.toByte, ValueType.INTEGER)

    for i <- 0x80 until 0x8f do
      check(i.toByte, ValueType.MAP)

    for i <- 0x90 until 0x9f do
      check(i.toByte, ValueType.ARRAY)

    check(NIL, ValueType.NIL)

    try
      MessageFormat.valueOf(NEVER_USED).getValueType
      fail("NEVER_USED type should not have ValueType")
    catch
      case e: MessageFormatException =>
      // OK

    check(TRUE, ValueType.BOOLEAN)
    check(FALSE, ValueType.BOOLEAN)

    for t <- Seq(BIN8, BIN16, BIN32) do
      check(t, ValueType.BINARY)

    for t <- Seq(FIXEXT1, FIXEXT2, FIXEXT4, FIXEXT8, FIXEXT16, EXT8, EXT16, EXT32) do
      check(t, ValueType.EXTENSION)

    for t <- Seq(INT8, INT16, INT32, INT64, UINT8, UINT16, UINT32, UINT64) do
      check(t, ValueType.INTEGER)

    for t <- Seq(STR8, STR16, STR32) do
      check(t, ValueType.STRING)

    for t <- Seq(FLOAT32, FLOAT64) do
      check(t, ValueType.FLOAT)

    for t <- Seq(ARRAY16, ARRAY32) do
      check(t, ValueType.ARRAY)

    for i <- 0xe0 until 0xff do
      check(i.toByte, ValueType.INTEGER)
  }

end ValueTypeTest
