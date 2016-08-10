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
package org.msgpack.core.buffer

import akka.util.ByteString
import org.msgpack.core.{MessagePack, MessagePackSpec, MessageUnpacker}

class ByteStringTest
  extends MessagePackSpec {

  val unpackedString = "foo"
  val byteString = ByteString(createMessagePackData(_.packString(unpackedString)))

  def unpackString(messageBuffer: MessageBuffer) = {
    val input = new
        MessageBufferInput {

      private var isRead = false

      override def next(): MessageBuffer =
        if (isRead) {
          null
        }
        else {
          isRead = true
          messageBuffer
        }
      override def close(): Unit = {}
    }

    MessagePack.newDefaultUnpacker(input).unpackString()
  }

  "Unpacking a ByteString's ByteBuffer" should {
    "fail with a regular MessageBuffer" in {

      // can't demonstrate with new ByteBufferInput(byteString.asByteBuffer)
      // as Travis tests run with JDK6 that picks up MessageBufferU
      a[RuntimeException] shouldBe thrownBy(unpackString(new
          MessageBuffer(byteString.asByteBuffer)))
    }
  }
}
