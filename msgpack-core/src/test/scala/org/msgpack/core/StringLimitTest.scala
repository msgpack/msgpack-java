package org.msgpack.core

import org.msgpack.core.MessagePack.UnpackerConfig
import org.msgpack.value.Variable
import wvlet.airspec.AirSpec

class StringLimitTest extends AirSpec {

  test("throws an exception when the string size exceeds a limit") {
    val customLimit = 100
    val packer      = MessagePack.newDefaultBufferPacker()
    packer.packString("a" * (customLimit + 1))
    val msgpack = packer.toByteArray

    test("unpackString") {
      val unpacker = new UnpackerConfig().withStringSizeLimit(customLimit).newUnpacker(msgpack)
      intercept[MessageSizeException] {
        unpacker.unpackString()
      }
    }

    test("unpackValue") {
      val unpacker = new UnpackerConfig().withStringSizeLimit(customLimit).newUnpacker(msgpack)
      intercept[MessageSizeException] {
        unpacker.unpackValue()
      }
    }

    test("unpackValue(var)") {
      val unpacker = new UnpackerConfig().withStringSizeLimit(customLimit).newUnpacker(msgpack)
      intercept[MessageSizeException] {
        val v = new Variable()
        unpacker.unpackValue(v)
      }
    }
  }
}
