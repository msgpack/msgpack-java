package org.msgpack.core

import org.msgpack.core.MessagePack.UnpackerConfig
import org.msgpack.value.Variable
import wvlet.airspec.AirSpec

class NestingDepthLimitTest extends AirSpec:

  private def nestedFixArrayPayload(arrayNesting: Int): Array[Byte] =
    val payload = new Array[Byte](arrayNesting + 1)
    var i       = 0
    while i < arrayNesting do
      payload(i) = 0x91.toByte
      i += 1
    payload(arrayNesting) = 0xc0.toByte
    payload

  test("throws when unpackValue nesting exceeds the configured limit") {
    val limit = 10
    val msgpack = nestedFixArrayPayload(limit + 1)

    test("unpackValue") {
      val unpacker = new UnpackerConfig().withMaxNestingDepth(limit).newUnpacker(msgpack)
      intercept[MessageSizeException] {
        unpacker.unpackValue()
      }
    }

    test("unpackValue(var)") {
      val unpacker = new UnpackerConfig().withMaxNestingDepth(limit).newUnpacker(msgpack)
      intercept[MessageSizeException] {
        unpacker.unpackValue(new Variable())
      }
    }
  }

  test("unpackValue succeeds at the configured nesting limit") {
    val limit   = 10
    val msgpack = nestedFixArrayPayload(limit)
    val unpacker = new UnpackerConfig().withMaxNestingDepth(limit).newUnpacker(msgpack)
    unpacker.unpackValue()
  }

end NestingDepthLimitTest
