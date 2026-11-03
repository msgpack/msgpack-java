package org.msgpack.core

import org.msgpack.value.{Value, Variable}
import wvlet.airspec.AirSpec
import wvlet.log.io.IOUtil.withResource

import java.nio.ByteBuffer

class MapSizeLimitTest extends AirSpec:

  private def mapHeader(size: Long): Array[Byte] =
    ByteBuffer.allocate(5).put(MessagePack.Code.MAP32).putInt(size.toInt).array()

  private def unpack(unpacker: MessageUnpacker, mutable: Boolean): Value =
    if mutable then unpacker.unpackValue(new Variable()) else unpacker.unpackValue()

  for mutable <- Seq(false, true) do
    // The first two sizes overflow the interleaved key/value array length.
    // The unsigned sizes retain unpackMapHeader's existing size exception.
    for size <- Seq(0x40000000L, 0x7fffffffL, 0x80000000L, 0xffffffffL) do
      test(s"reject oversized map $size (mutable=$mutable)") {
        withResource(MessagePack.newDefaultUnpacker(mapHeader(size))) { unpacker =>
          val error = intercept[MessageSizeException] {
            unpack(unpacker, mutable)
          }
          error.getSize shouldBe size
          unpacker.getTotalReadBytes shouldBe 5L
        }
      }

    // Exercise fixmap, map16 and map32 without allocating near the size limit.
    for size <- Seq(0, 1, 16, 65536) do
      test(s"unpack ordinary map $size (mutable=$mutable)") {
        val bytes = withResource(MessagePack.newDefaultBufferPacker()) { packer =>
          packer.packMapHeader(size)
          for i <- 0 until size do
            packer.packInt(i).packInt(-i)
          packer.toByteArray
        }
        withResource(MessagePack.newDefaultUnpacker(bytes)) { unpacker =>
          val map = unpack(unpacker, mutable).asMapValue()
          map.size() shouldBe size
          val entries = map.getKeyValueArray()
          for i <- 0 until size do
            entries(i * 2).asIntegerValue().asInt() shouldBe i
            entries(i * 2 + 1).asIntegerValue().asInt() shouldBe -i
          unpacker.hasNext shouldBe false
        }
      }

    test(s"unpack nested map (mutable=$mutable)") {
      val bytes = withResource(MessagePack.newDefaultBufferPacker()) { packer =>
        packer.packMapHeader(1).packInt(1).packMapHeader(1).packInt(2).packInt(3)
        packer.toByteArray
      }
      withResource(MessagePack.newDefaultUnpacker(bytes)) { unpacker =>
        val entries = unpack(unpacker, mutable).asMapValue().getKeyValueArray()
        entries(0).asIntegerValue().asInt() shouldBe 1
        val nested = entries(1).asMapValue().getKeyValueArray()
        nested(0).asIntegerValue().asInt() shouldBe 2
        nested(1).asIntegerValue().asInt() shouldBe 3
      }
    }

  // Streaming callers need no interleaved Value[] allocation and retain their range.
  for size <- Seq(0x3fffffffL, 0x40000000L, 0x7fffffffL) do
    test(s"stream map header $size") {
      withResource(MessagePack.newDefaultUnpacker(mapHeader(size))) { unpacker =>
        unpacker.unpackMapHeader() shouldBe size.toInt
        unpacker.hasNext shouldBe false
      }
    }

end MapSizeLimitTest
