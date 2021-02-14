package org.msgpack.core

/**
 *
 */
class InvalidDataReadTest extends MessagePackSpec {

  "Reading long EXT32" in {
      val msgpack = createMessagePackData(p => p.packExtensionTypeHeader(MessagePack.Code.EXT32, Int.MaxValue))
      val u = MessagePack.newDefaultUnpacker(msgpack)
      try {
        intercept[MessageInsufficientBufferException] {
          u.skipValue()
        }
      }
      finally  {
        u.close()
      }
  }
}
