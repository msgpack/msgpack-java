package org.msgpack.core

import wvlet.airspec.AirSpec

import java.nio.ByteBuffer

class PayloadSizeLimitTest extends AirSpec:

  test("detects malicious EXT32 file with huge declared size but tiny actual data") {
    // Craft a malicious EXT32 header:
    // 0xC9 = EXT32 format
    // 4 bytes = declared length (100MB, which exceeds 64MB threshold)
    // 1 byte = extension type
    // followed by only 1 byte of actual data (not 100MB)
    val declaredLength = 100 * 1024 * 1024      // 100 MB
    val buffer         = ByteBuffer.allocate(7) // header(1) + length(4) + type(1) + data(1)
    buffer.put(0xc9.toByte) // EXT32 format code
    buffer.putInt(declaredLength) // declared length (big-endian)
    buffer.put(0x01.toByte) // extension type
    buffer.put(0x41.toByte) // only 1 byte of actual data
    val maliciousData  = buffer.array()

    val unpacker = MessagePack.newDefaultUnpacker(maliciousData)

    // Should throw MessageSizeException because the declared size exceeds actual data
    intercept[MessageSizeException] {
      unpacker.unpackValue()
    }
  }

  test("detects malicious BIN32 file with huge declared size but tiny actual data") {
    // Craft a malicious BIN32 header:
    // 0xC6 = BIN32 format
    // 4 bytes = declared length (100MB, which exceeds 64MB threshold)
    // followed by only 1 byte of actual data (not 100MB)
    val declaredLength = 100 * 1024 * 1024      // 100 MB
    val buffer         = ByteBuffer.allocate(6) // header(1) + length(4) + data(1)
    buffer.put(0xc6.toByte) // BIN32 format code
    buffer.putInt(declaredLength) // declared length (big-endian)
    buffer.put(0x41.toByte) // only 1 byte of actual data
    val maliciousData  = buffer.array()

    val unpacker = MessagePack.newDefaultUnpacker(maliciousData)

    // Should throw MessageSizeException because the declared size exceeds actual data
    intercept[MessageSizeException] {
      unpacker.unpackValue()
    }
  }

  test("legitimate extension data works correctly") {
    val packer   = MessagePack.newDefaultBufferPacker()
    val testData = Array.fill[Byte](1000)(0x42)
    packer.packExtensionTypeHeader(0x01.toByte, testData.length)
    packer.writePayload(testData)
    val msgpack = packer.toByteArray

    val unpacker = MessagePack.newDefaultUnpacker(msgpack)
    val value    = unpacker.unpackValue()

    assert(value.isExtensionValue)
    assert(value.asExtensionValue().getData.length == 1000)
  }

  test("legitimate binary data works correctly") {
    val packer   = MessagePack.newDefaultBufferPacker()
    val testData = Array.fill[Byte](1000)(0x42)
    packer.packBinaryHeader(testData.length)
    packer.writePayload(testData)
    val msgpack = packer.toByteArray

    val unpacker = MessagePack.newDefaultUnpacker(msgpack)
    val value    = unpacker.unpackValue()

    assert(value.isBinaryValue)
    assert(value.asBinaryValue().asByteArray().length == 1000)
  }

  test("readPayload directly with malicious size throws exception") {
    // Test readPayload(int) directly with a malicious input
    val declaredLength = 100 * 1024 * 1024      // 100 MB
    val buffer         = ByteBuffer.allocate(6) // header(1) + length(4) + data(1)
    buffer.put(0xc6.toByte) // BIN32 format code
    buffer.putInt(declaredLength) // declared length (big-endian)
    buffer.put(0x41.toByte) // only 1 byte of actual data
    val maliciousData  = buffer.array()

    val unpacker = MessagePack.newDefaultUnpacker(maliciousData)

    // First, unpack the binary header to get the declared length
    val len = unpacker.unpackBinaryHeader()
    assert(len == declaredLength)

    // Then try to read the payload - should throw exception
    intercept[MessageSizeException] {
      unpacker.readPayload(len)
    }
  }

  test("small payloads under threshold work with upfront allocation") {
    // Payloads under 64MB should use the efficient upfront allocation path
    val packer   = MessagePack.newDefaultBufferPacker()
    val testData = Array.fill[Byte](10000)(0x42) // 10KB, well under threshold
    packer.packBinaryHeader(testData.length)
    packer.writePayload(testData)
    val msgpack = packer.toByteArray

    val unpacker = MessagePack.newDefaultUnpacker(msgpack)
    val len      = unpacker.unpackBinaryHeader()
    val payload  = unpacker.readPayload(len)

    assert(payload.length == 10000)
    assert(payload.forall(_ == 0x42.toByte))
  }

end PayloadSizeLimitTest
