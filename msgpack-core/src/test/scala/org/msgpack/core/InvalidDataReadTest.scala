package org.msgpack.core

/**
 *
 */
class InvalidDataReadTest extends MessagePackSpec {

  "Reading long EXT32" in {
      // Prepare an EXT32 data with 2GB (Int.MaxValue size) payload for testing the behavior of MessageUnpacker.skipValue()
      // Actually preparing 2GB of data, however, is too much for CI, so we create only the header part.
      val msgpack = createMessagePackData(p => p.packExtensionTypeHeader(MessagePack.Code.EXT32, Int.MaxValue))
      val u = MessagePack.newDefaultUnpacker(msgpack)
      try {
        // This error will be thrown after reading the header as the input has no EXT32 body
        intercept[MessageInsufficientBufferException] {
          u.skipValue()
        }
      }
      finally  {
        u.close()
      }
  }
}
