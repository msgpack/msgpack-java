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

import java.io.ByteArrayOutputStream
import java.util.Arrays
import org.msgpack.value.ValueFactory._
import wvlet.airspec.AirSpec

class MessageBufferPackerTest extends AirSpec {
  test("MessageBufferPacker") {
    test("be equivalent to ByteArrayOutputStream") {
      val packer1 = MessagePack.newDefaultBufferPacker
      packer1.packValue(newMap(newString("a"), newInteger(1), newString("b"), newString("s")))

      val stream  = new ByteArrayOutputStream
      val packer2 = MessagePack.newDefaultPacker(stream)
      packer2.packValue(newMap(newString("a"), newInteger(1), newString("b"), newString("s")))
      packer2.flush

      packer1.toByteArray shouldBe stream.toByteArray
    }

    test("clear unflushed") {
      val packer = MessagePack.newDefaultBufferPacker
      packer.packInt(1)
      packer.clear()
      packer.packInt(2)

      packer.toByteArray shouldBe Array[Byte](2)
      val buffer = packer.toBufferList().get(0)
      buffer.toByteArray() shouldBe Array[Byte](2)
      val array = Arrays.copyOf(buffer.sliceAsByteBuffer().array(), buffer.size())
      array shouldBe Array[Byte](2)
    }

  }
}
