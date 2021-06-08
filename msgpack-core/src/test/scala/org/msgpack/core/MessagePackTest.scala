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

import org.msgpack.core.MessagePack.{Code, PackerConfig, UnpackerConfig}
import org.msgpack.core.MessagePackSpec.toHex
import org.msgpack.value.{Value, Variable}
import org.scalacheck.Prop.propBoolean
import org.scalacheck.{Arbitrary, Gen}
import wvlet.airspec.AirSpec
import wvlet.airspec.spi.PropertyCheck

import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.CharBuffer
import java.nio.charset.{CodingErrorAction, UnmappableCharacterException}
import java.time.Instant
import scala.util.Random

/**
  * Created on 2014/05/07.
  */
class MessagePackTest extends AirSpec with PropertyCheck with Benchmark {

  private def isValidUTF8(s: String) = {
    MessagePack.UTF8.newEncoder().canEncode(s)
  }

  private def containsUnmappableCharacter(s: String): Boolean = {
    try {
      MessagePack.UTF8
        .newEncoder()
        .onUnmappableCharacter(CodingErrorAction.REPORT)
        .encode(CharBuffer.wrap(s))
      false
    } catch {
      case e: UnmappableCharacterException =>
        true
      case _: Exception => false
    }
  }

  test("clone packer config") {
    val config = new PackerConfig()
      .withBufferSize(10)
      .withBufferFlushThreshold(32 * 1024)
      .withSmallStringOptimizationThreshold(142)
    val copy = config.clone()

    copy shouldBe config
  }

  test("clone unpacker config") {
    val config = new UnpackerConfig()
      .withBufferSize(1)
      .withActionOnMalformedString(CodingErrorAction.IGNORE)
      .withActionOnUnmappableString(CodingErrorAction.REPORT)
      .withAllowReadingBinaryAsString(false)
      .withStringDecoderBufferSize(34)
      .withStringSizeLimit(4324)

    val copy = config.clone()
    copy shouldBe config
  }

  test("detect fixint values") {

    for (i <- 0 until 0x79) {
      Code.isPosFixInt(i.toByte) shouldBe true
    }

    for (i <- 0x80 until 0xff) {
      Code.isPosFixInt(i.toByte) shouldBe false
    }
  }

  test("detect fixarray values") {
    val packer = MessagePack.newDefaultBufferPacker()
    packer.packArrayHeader(0)
    packer.close
    val bytes = packer.toByteArray
    MessagePack.newDefaultUnpacker(bytes).unpackArrayHeader() shouldBe 0
    try {
      MessagePack.newDefaultUnpacker(bytes).unpackMapHeader()
      fail("Shouldn't reach here")
    } catch {
      case e: MessageTypeException => // OK
    }
  }

  test("detect fixmap values") {
    val packer = MessagePack.newDefaultBufferPacker()
    packer.packMapHeader(0)
    packer.close
    val bytes = packer.toByteArray
    MessagePack.newDefaultUnpacker(bytes).unpackMapHeader() shouldBe 0
    try {
      MessagePack.newDefaultUnpacker(bytes).unpackArrayHeader()
      fail("Shouldn't reach here")
    } catch {
      case e: MessageTypeException => // OK
    }
  }

  test("detect fixint quickly") {

    val N   = 100000
    val idx = (0 until N).map(x => Random.nextInt(256).toByte).toArray[Byte]

    time("check fixint", repeat = 100) {

      block("mask") {
        var i     = 0
        var count = 0
        while (i < N) {
          if ((idx(i) & Code.POSFIXINT_MASK) == 0) {
            count += 1
          }
          i += 1
        }
      }

      block("mask in func") {
        var i     = 0
        var count = 0
        while (i < N) {
          if (Code.isPosFixInt(idx(i))) {
            count += 1
          }
          i += 1
        }
      }

      block("shift cmp") {
        var i     = 0
        var count = 0
        while (i < N) {
          if ((idx(i) >>> 7) == 0) {
            count += 1
          }
          i += 1
        }

      }

    }

  }

  test("detect neg fix int values") {

    for (i <- 0 until 0xe0) {
      Code.isNegFixInt(i.toByte) shouldBe false
    }

    for (i <- 0xe0 until 0xff) {
      Code.isNegFixInt(i.toByte) shouldBe true
    }

  }

  private def check[A](
      v: A,
      pack: MessagePacker => Unit,
      unpack: MessageUnpacker => A,
      packerConfig: PackerConfig = new PackerConfig(),
      unpackerConfig: UnpackerConfig = new UnpackerConfig()
  ): Boolean = {
    var b: Array[Byte] = null
    try {
      val bs     = new ByteArrayOutputStream()
      val packer = packerConfig.newPacker(bs)
      pack(packer)
      packer.close()

      b = bs.toByteArray

      val unpacker = unpackerConfig.newUnpacker(b)
      val ret      = unpack(unpacker)
      ret shouldBe v
      true
    } catch {
      case e: Exception =>
        warn(e.getMessage)
        if (b != null) {
          warn(s"packed data (size:${b.length}): ${toHex(b)}")
        }
        throw e
    }
  }

  private def checkException[A](
      v: A,
      pack: MessagePacker => Unit,
      unpack: MessageUnpacker => A,
      packerConfig: PackerConfig = new PackerConfig(),
      unpaackerConfig: UnpackerConfig = new UnpackerConfig()
  ): Unit = {
    var b: Array[Byte] = null
    val bs             = new ByteArrayOutputStream()
    val packer         = packerConfig.newPacker(bs)
    pack(packer)
    packer.close()

    b = bs.toByteArray

    val unpacker = unpaackerConfig.newUnpacker(b)
    val ret      = unpack(unpacker)

    fail("cannot not reach here")
  }

  private def checkOverflow[A](v: A, pack: MessagePacker => Unit, unpack: MessageUnpacker => A): Unit = {
    try {
      checkException[A](v, pack, unpack)
    } catch {
      case e: MessageIntegerOverflowException => // OK
    }
  }

  test("pack/unpack primitive values") {
    forAll { (v: Boolean) =>
      check(v, _.packBoolean(v), _.unpackBoolean)
    }
    forAll { (v: Byte) =>
      check(v, _.packByte(v), _.unpackByte)
    }
    forAll { (v: Short) =>
      check(v, _.packShort(v), _.unpackShort)
    }
    forAll { (v: Int) =>
      check(v, _.packInt(v), _.unpackInt)
    }
    forAll { (v: Float) =>
      check(v, _.packFloat(v), _.unpackFloat)
    }
    forAll { (v: Long) =>
      check(v, _.packLong(v), _.unpackLong)
    }
    forAll { (v: Double) =>
      check(v, _.packDouble(v), _.unpackDouble)
    }
    check(
      null,
      _.packNil,
      { unpacker =>
        unpacker.unpackNil(); null
      }
    )
  }

  test("skipping a nil value") {
    check(true, _.packNil, _.tryUnpackNil)
    check(
      false,
      { packer =>
        packer.packString("val")
      },
      { unpacker =>
        unpacker.tryUnpackNil()
      }
    )
    check(
      "val",
      { packer =>
        packer.packString("val")
      },
      { unpacker =>
        unpacker.tryUnpackNil(); unpacker.unpackString()
      }
    )
    check(
      "val",
      { packer =>
        packer.packNil(); packer.packString("val")
      },
      { unpacker =>
        unpacker.tryUnpackNil(); unpacker.unpackString()
      }
    )
    try {
      checkException(null, { _ => }, _.tryUnpackNil)
    } catch {
      case e: MessageInsufficientBufferException => // OK
    }
  }

  test("pack/unpack integer values") {
    val sampleData = Seq[Long](
      Int.MinValue.toLong -
        10,
      -65535,
      -8191,
      -1024,
      -255,
      -127,
      -63,
      -31,
      -15,
      -7,
      -3,
      -1,
      0,
      2,
      4,
      8,
      16,
      32,
      64,
      128,
      256,
      1024,
      8192,
      65536,
      Int.MaxValue.toLong + 10
    )
    for (v <- sampleData) {
      check(v, _.packLong(v), _.unpackLong)

      if (v.isValidInt) {
        val vi = v.toInt
        check(vi, _.packInt(vi), _.unpackInt)
      } else {
        checkOverflow(v, _.packLong(v), _.unpackInt)
      }

      if (v.isValidShort) {
        val vi = v.toShort
        check(vi, _.packShort(vi), _.unpackShort)
      } else {
        checkOverflow(v, _.packLong(v), _.unpackShort)
      }

      if (v.isValidByte) {
        val vi = v.toByte
        check(vi, _.packByte(vi), _.unpackByte)
      } else {
        checkOverflow(v, _.packLong(v), _.unpackByte)
      }

    }

  }

  test("pack/unpack BigInteger") {
    forAll { (a: Long) =>
      val v = BigInteger.valueOf(a)
      check(v, _.packBigInteger(v), _.unpackBigInteger)
    }

    for (bi <- Seq(BigInteger.valueOf(Long.MaxValue).add(BigInteger.valueOf(1)))) {
      check(bi, _.packBigInteger(bi), _.unpackBigInteger())
    }

    for (bi <- Seq(BigInteger.valueOf(Long.MaxValue).shiftLeft(10))) {
      try {
        checkException(bi, _.packBigInteger(bi), _.unpackBigInteger())
        fail("cannot reach here")
      } catch {
        case e: IllegalArgumentException => // OK
      }
    }

  }

  test("pack/unpack strings") {
    val utf8Strings = Arbitrary.arbitrary[String].suchThat(isValidUTF8 _)
    utf8Strings.map { v =>
      check(v, _.packString(v), _.unpackString)
    }
  }

  test("pack/unpack large strings") {
    // Large string
    val strLen = Seq(1000, 2000, 10000, 50000, 100000, 500000)
    for (l <- strLen) {
      val v: String =
        Iterator.continually(Random.nextString(l * 10)).find(isValidUTF8).get
      check(v, _.packString(v), _.unpackString)
    }
  }

  test("report errors when packing/unpacking malformed strings") {
    pending("We need to produce malformed utf-8 strings in Java 8")
    // Create 100 malformed UTF8 Strings
    val r = new Random(0)
    val malformedStrings = Iterator
      .continually {
        val b = new Array[Byte](10)
        r.nextBytes(b)
        b
      }
      .filter(b => !isValidUTF8(new String(b)))
      .take(100)

    for (malformedBytes <- malformedStrings) {
      // Pack tests
      val malformed = new String(malformedBytes)
      try {
        checkException(malformed, _.packString(malformed), _.unpackString())
      } catch {
        case e: MessageStringCodingException => // OK
      }

      try {
        checkException(
          malformed,
          { packer =>
            packer.packRawStringHeader(malformedBytes.length)
            packer.writePayload(malformedBytes)
          },
          _.unpackString()
        )
      } catch {
        case e: MessageStringCodingException => // OK
      }
    }
  }

  test("report errors when packing/unpacking strings that contain unmappable characters") {

    val unmappable = Array[Byte](0xfc.toByte, 0x0a.toByte)
    //val unmappableChar = Array[Char](new Character(0xfc0a).toChar)

    // Report error on unmappable character
    val unpackerConfig = new UnpackerConfig()
      .withActionOnMalformedString(CodingErrorAction.REPORT)
      .withActionOnUnmappableString(CodingErrorAction.REPORT)

    for (bytes <- Seq(unmappable)) {
      try {
        checkException(
          bytes,
          { packer =>
            packer.packRawStringHeader(bytes.length)
            packer.writePayload(bytes)
          },
          _.unpackString(),
          new PackerConfig(),
          unpackerConfig
        )
      } catch {
        case e: MessageStringCodingException => // OK
      }
    }
  }

  test("pack/unpack binary") {
    forAll { (v: Array[Byte]) =>
      check(
        v,
        { packer =>
          packer.packBinaryHeader(v.length); packer.writePayload(v)
        },
        { unpacker =>
          val len = unpacker.unpackBinaryHeader()
          val out = new Array[Byte](len)
          unpacker.readPayload(out, 0, len)
          out
        }
      )
    }

    val len = Seq(1000, 2000, 10000, 50000, 100000, 500000)
    for (l <- len) {
      val v = new Array[Byte](l)
      Random.nextBytes(v)
      check(
        v,
        { packer =>
          packer.packBinaryHeader(v.length); packer.writePayload(v)
        },
        { unpacker =>
          val len = unpacker.unpackBinaryHeader()
          val out = new Array[Byte](len)
          unpacker.readPayload(out, 0, len)
          out
        }
      )
    }
  }

  val testHeaderLength = Seq(1, 2, 4, 8, 16, 17, 32, 64, 255, 256, 1000, 2000, 10000, 50000, 100000, 500000)

  test("pack/unpack arrays") {
    forAll { (v: Array[Int]) =>
      check(
        v,
        { packer =>
          packer.packArrayHeader(v.length)
          v.map(packer.packInt(_))
        },
        { unpacker =>
          val len = unpacker.unpackArrayHeader()
          val out = new Array[Int](len)
          for (i <- 0 until v.length) {
            out(i) = unpacker.unpackInt
          }
          out
        }
      )
    }

    for (l <- testHeaderLength) {
      check(l, _.packArrayHeader(l), _.unpackArrayHeader())
    }

    try {
      checkException(0, _.packArrayHeader(-1), _.unpackArrayHeader)
    } catch {
      case e: IllegalArgumentException => // OK
    }

  }

  test("pack/unpack maps") {
    forAll { (v: Array[Int]) =>
      val m = v.map(i => (i, i.toString)).toSeq

      check(
        m,
        { packer =>
          packer.packMapHeader(v.length)
          m.map {
            case (k: Int, v: String) =>
              packer.packInt(k)
              packer.packString(v)
          }
        },
        { unpacker =>
          val len = unpacker.unpackMapHeader()
          val b   = Seq.newBuilder[(Int, String)]
          for (i <- 0 until len) {
            b += ((unpacker.unpackInt, unpacker.unpackString))
          }
          b.result()
        }
      )
    }

    for (l <- testHeaderLength) {
      check(l, _.packMapHeader(l), _.unpackMapHeader())
    }

    try {
      checkException(0, _.packMapHeader(-1), _.unpackMapHeader)
    } catch {
      case e: IllegalArgumentException => // OK
    }

  }

  test("pack/unpack extension types") {
    forAll { (dataLen: Int, tpe: Byte) =>
      val l = Math.abs(dataLen)
      l >= 0 ==> {
        val ext =
          new ExtensionTypeHeader(ExtensionTypeHeader.checkedCastToByte(tpe), l)
        check(ext, _.packExtensionTypeHeader(ext.getType, ext.getLength), _.unpackExtensionTypeHeader())
      }
    }

    for (l <- testHeaderLength) {
      val ext = new ExtensionTypeHeader(ExtensionTypeHeader.checkedCastToByte(Random.nextInt(128)), l)
      check(ext, _.packExtensionTypeHeader(ext.getType, ext.getLength), _.unpackExtensionTypeHeader())
    }

  }

  test("pack/unpack maps in lists") {
    val aMap = List(Map("f" -> "x"))

    check(
      aMap,
      { packer =>
        packer.packArrayHeader(aMap.size)
        for (m <- aMap) {
          packer.packMapHeader(m.size)
          for ((k, v) <- m) {
            packer.packString(k)
            packer.packString(v)
          }
        }
      },
      { unpacker =>
        val v = new Variable()
        unpacker.unpackValue(v)
        import scala.jdk.CollectionConverters._
        v.asArrayValue().asScala
          .map { m =>
            val mv  = m.asMapValue()
            val kvs = mv.getKeyValueArray

            kvs
              .grouped(2)
              .map({ kvp: Array[Value] =>
                val k = kvp(0)
                val v = kvp(1)

                (k.asStringValue().asString, v.asStringValue().asString)
              })
              .toMap
          }
          .toList
      }
    )
  }

  test("pack/unpack timestamp values") {
    val posLong = Gen.chooseNum[Long](-31557014167219200L, 31556889864403199L)
    val posInt  = Gen.chooseNum(0, 1000000000 - 1) // NANOS_PER_SECOND
    forAll(posLong, posInt) { (second: Long, nano: Int) =>
      val v = Instant.ofEpochSecond(second, nano)
      check(v, { _.packTimestamp(v) }, { _.unpackTimestamp() })
    }
    // Using different insterfaces
    forAll(posLong, posInt) { (second: Long, nano: Int) =>
      val v = Instant.ofEpochSecond(second, nano)
      check(v, { _.packTimestamp(second, nano) }, { _.unpackTimestamp() })
    }
    val secLessThan34bits = Gen.chooseNum[Long](0, 1L << 34)
    forAll(secLessThan34bits, posInt) { (second: Long, nano: Int) =>
      val v = Instant.ofEpochSecond(second, nano)
      check(v, _.packTimestamp(v), _.unpackTimestamp())
    }
    forAll(secLessThan34bits, posInt) { (second: Long, nano: Int) =>
      val v = Instant.ofEpochSecond(second, nano)
      check(v, _.packTimestamp(second, nano), _.unpackTimestamp())
    }

    // Corner-cases around uint32 boundaries
    for (
      v <- Seq(
        Instant.ofEpochSecond(Instant.now().getEpochSecond, 123456789L), // uint32 nanoseq (out of int32 range)
        Instant.ofEpochSecond(-1302749144L, 0),                          // 1928-09-19T21:14:16Z
        Instant.ofEpochSecond(-747359729L, 0),                           // 1946-04-27T00:04:31Z
        Instant.ofEpochSecond(4257387427L, 0)                            // 2104-11-29T07:37:07Z
      )
    ) {
      check(v, _.packTimestamp(v), _.unpackTimestamp())
    }
  }

  test("pack/unpack timestamp in millis") {
    val posLong = Gen.chooseNum[Long](-31557014167219200L, 31556889864403199L)
    forAll(posLong) { (millis: Long) =>
      val v = Instant.ofEpochMilli(millis)
      check(v, { _.packTimestamp(millis) }, { _.unpackTimestamp() })
    }
  }

  test("pack/unpack timestamp through ExtValue") {
    val posLong = Gen.chooseNum[Long](-31557014167219200L, 31556889864403199L)
    forAll(posLong) { (millis: Long) =>
      val v = Instant.ofEpochMilli(millis)
      check(v, { _.packTimestamp(millis) },
        { u =>
          val extHeader = u.unpackExtensionTypeHeader()
          if(extHeader.isTimestampType) {
            u.unpackTimestamp(extHeader)
          }
          else {
            fail("Cannot reach here")
          }
        }
      )
    }
  }

  test("MessagePack.PackerConfig") {
    test("should be immutable") {
      val a = new MessagePack.PackerConfig()
      val b = a.withBufferSize(64 * 1024)
      a.equals(b) shouldBe false
    }

    test("should implement equals") {
      val a = new MessagePack.PackerConfig()
      val b = new MessagePack.PackerConfig()
      a.equals(b) shouldBe true
      a.withBufferSize(64 * 1024).equals(b) shouldBe false
      a.withSmallStringOptimizationThreshold(64).equals(b) shouldBe false
      a.withBufferFlushThreshold(64 * 1024).equals(b) shouldBe false
    }
  }

  test("MessagePack.UnpackerConfig") {
    test("should be immutable") {
      val a = new MessagePack.UnpackerConfig()
      val b = a.withBufferSize(64 * 1024)
      a.equals(b) shouldBe false
    }

    test("implement equals") {
      val a = new MessagePack.UnpackerConfig()
      val b = new MessagePack.UnpackerConfig()
      a.equals(b) shouldBe true
      a.withBufferSize(64 * 1024).equals(b) shouldBe false
      a.withAllowReadingStringAsBinary(false).equals(b) shouldBe false
      a.withAllowReadingBinaryAsString(false).equals(b) shouldBe false
      a.withActionOnMalformedString(CodingErrorAction.REPORT)
        .equals(b) shouldBe false
      a.withActionOnUnmappableString(CodingErrorAction.REPORT)
        .equals(b) shouldBe false
      a.withStringSizeLimit(32).equals(b) shouldBe false
      a.withStringDecoderBufferSize(32).equals(b) shouldBe false
    }
  }
}
