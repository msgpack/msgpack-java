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

import java.math.BigInteger

import org.msgpack.core._

class ValueTest extends MessagePackSpec
{
  def checkSuccinctType(pack:MessagePacker => Unit, expectedAtMost:MessageFormat) {
    val b = createMessagePackData(pack)
    val v1 = MessagePack.newDefaultUnpacker(b).unpackValue()
    val mf = v1.asIntegerValue().mostSuccinctMessageFormat()
    mf.getValueType shouldBe ValueType.INTEGER
    mf.ordinal() shouldBe <= (expectedAtMost.ordinal())

    val v2 = new Variable
    MessagePack.newDefaultUnpacker(b).unpackValue(v2)
    val mf2 = v2.asIntegerValue().mostSuccinctMessageFormat()
    mf2.getValueType shouldBe ValueType.INTEGER
    mf2.ordinal() shouldBe <= (expectedAtMost.ordinal())
  }

  "Value" should {
    "tell most succinct integer type" in {
      forAll { (v: Byte) => checkSuccinctType(_.packByte(v), MessageFormat.INT8) }
      forAll { (v: Short) => checkSuccinctType(_.packShort(v), MessageFormat.INT16) }
      forAll { (v: Int) => checkSuccinctType(_.packInt(v), MessageFormat.INT32) }
      forAll { (v: Long) => checkSuccinctType(_.packLong(v), MessageFormat.INT64) }
      forAll { (v: Long) => checkSuccinctType(_.packBigInteger(BigInteger.valueOf(v)), MessageFormat.INT64) }
      forAll { (v: Long) =>
        whenever(v > 0) {
          // Create value between 2^63-1 < v <= 2^64-1
          checkSuccinctType(_.packBigInteger(BigInteger.valueOf(Long.MaxValue).add(BigInteger.valueOf(v))), MessageFormat.UINT64)
        }
      }
    }
  }
}
