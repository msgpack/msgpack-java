package org.msgpack.core.buffer

import akka.util.ByteString
import org.msgpack.core.{MessagePackSpec, MessageUnpacker}

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

    new
        MessageUnpacker(input).unpackString()
  }

  "Unpacking a ByteString's ByteBuffer" should {
    "fail with a regular MessageBuffer" in {

      // can't demonstrate with new ByteBufferInput(byteString.asByteBuffer)
      // as Travis tests run with JDK6 that picks up MessageBufferU
      a[RuntimeException] shouldBe thrownBy(unpackString(new
          MessageBuffer(byteString.asByteBuffer)))
    }

    "succeed with a MessageBufferU" in {
      unpackString(new
          MessageBufferU(byteString.asByteBuffer)) shouldBe unpackedString
    }
  }
}
