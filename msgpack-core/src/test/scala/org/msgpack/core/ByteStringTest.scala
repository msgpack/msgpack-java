package org.msgpack.core

import akka.util.ByteString
import org.msgpack.core.buffer.{MessageBufferU, MessageBuffer, MessageBufferInput, ByteBufferInput}

class ByteStringTest extends MessagePackSpec  {

  val unpackedString = "foo"
  val byteString = ByteString(createMessagePackData(_.packString(unpackedString)))

  "Regular ByteBufferInput" should {
    "fail to read a ByteString's ByteBuffer" in {

      val input = new ByteBufferInput(byteString.asByteBuffer)

      a[RuntimeException] shouldBe thrownBy(new MessageUnpacker(input).unpackString())
    }
  }

  "A MessageBufferU based ByteBufferInput" should {
    "be able to read a ByteString's ByteBuffer" in {

      val input = new MessageBufferInput {

        private var isRead = false

        override def next(): MessageBuffer =
          if (isRead) {
            null
          } else {
            isRead = true
            new MessageBufferU(byteString.asByteBuffer)
          }

        override def close(): Unit = {}
      }

      new MessageUnpacker(input).unpackString() shouldBe unpackedString
    }
  }
}
