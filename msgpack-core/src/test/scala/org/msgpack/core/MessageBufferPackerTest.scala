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

import org.msgpack.value.Value
import org.msgpack.value.ValueFactory._

class MessageBufferPackerTest extends MessagePackSpec {
  "MessageBufferPacker" should {
    "be equivalent to ByteArrayOutputStream" in {
      val packer1 = MessagePack.newDefaultBufferPacker
      packer1.packValue(newMap(Array[Value](
        newString("a"), newInteger(1),
        newString("b"), newString("s"))))

      val stream = new ByteArrayOutputStream
      val packer2 = MessagePack.newDefaultPacker(stream)
      packer2.packValue(newMap(Array[Value](
        newString("a"), newInteger(1),
        newString("b"), newString("s"))))
      packer2.flush

      packer1.toByteArray shouldBe stream.toByteArray
    }
  }
}
