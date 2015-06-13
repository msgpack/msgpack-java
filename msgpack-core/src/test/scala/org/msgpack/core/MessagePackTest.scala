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

import org.msgpack.value.{Variable, Value}

import scala.util.Random
import MessagePack.Code
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.CharBuffer
import java.nio.charset.{UnmappableCharacterException, CodingErrorAction}

/**
 * Created on 2014/05/07.
 */
class MessagePackTest extends MessagePackSpec  {

  def isValidUTF8(s: String) = {
    MessagePack.UTF8.newEncoder().canEncode(s)
  }

  def containsUnmappableCharacter(s: String) : Boolean = {
    try {
      MessagePack.UTF8.newEncoder().onUnmappableCharacter(CodingErrorAction.REPORT).encode(CharBuffer.wrap(s))
      false
    }
    catch {
      case e: UnmappableCharacterException =>
        true
      case _: Exception => false
    }
  }


  "MessagePack" should {
    "detect fixint values" in {

      for (i <- 0 until 0x79) {
        Code.isPosFixInt(i.toByte) shouldBe true
      }

      for (i <- 0x80 until 0xFF) {
        Code.isPosFixInt(i.toByte) shouldBe false
      }
    }

    "detect fixint quickly" in {

      val N = 100000
      val idx = (0 until N).map(x => Random.nextInt(256).toByte).toArray[Byte]

      time("check fixint", repeat = 100) {

        block("mask") {
          var i = 0
          var count = 0
          while (i < N) {
            if ((idx(i) & Code.POSFIXINT_MASK) == 0) {
              count += 1
            }
            i += 1
          }
        }

        block("mask in func") {
          var i = 0
          var count = 0
          while (i < N) {
            if (Code.isPosFixInt(idx(i))) {
              count += 1
            }
            i += 1
          }
        }

        block("shift cmp") {
          var i = 0
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

    "detect neg fix int values" in {

      for (i <- 0 until 0xe0) {
        Code.isNegFixInt(i.toByte) shouldBe false
      }

      for (i <- 0xe0 until 0xFF) {
        Code.isNegFixInt(i.toByte) shouldBe true
      }

    }


    def check[A](v: A, pack: MessagePacker => Unit, unpack: MessageUnpacker => A, msgpack:MessagePack = MessagePack.DEFAULT): Unit = {
      var b: Array[Byte] = null
      try {
        val bs = new ByteArrayOutputStream()
        val packer = msgpack.newPacker(bs)
        pack(packer)
        packer.close()

        b = bs.toByteArray

        val unpacker = msgpack.newUnpacker(b)
        val ret = unpack(unpacker)
        ret shouldBe v
      }
      catch {
        case e: Exception =>
          warn(e.getMessage)
          if (b != null)
            warn(s"packed data (size:${b.length}): ${toHex(b)}")
          throw e
      }
    }

    def checkException[A](v: A, pack: MessagePacker => Unit, unpack: MessageUnpacker => A, msgpack:MessagePack=MessagePack.DEFAULT) : Unit = {
      var b: Array[Byte] = null
      val bs = new ByteArrayOutputStream()
      val packer = msgpack.newPacker(bs)
      pack(packer)
      packer.close()

      b = bs.toByteArray

      val unpacker = msgpack.newUnpacker(b)
      val ret = unpack(unpacker)

      fail("cannot not reach here")
    }

    def checkOverflow[A](v: A, pack: MessagePacker => Unit, unpack: MessageUnpacker => A) {
      try {
        checkException[A](v, pack, unpack)
      }
      catch {
        case e:MessageIntegerOverflowException => // OK
      }
    }




    "pack/unpack primitive values" taggedAs("prim") in {
      forAll { (v: Boolean) => check(v, _.packBoolean(v), _.unpackBoolean)}
      forAll { (v: Byte) => check(v, _.packByte(v), _.unpackByte)}
      forAll { (v: Short) => check(v, _.packShort(v), _.unpackShort)}
      forAll { (v: Int) => check(v, _.packInt(v), _.unpackInt)}
      forAll { (v: Float) => check(v, _.packFloat(v), _.unpackFloat)}
      forAll { (v: Long) => check(v, _.packLong(v), _.unpackLong)}
      forAll { (v: Double) => check(v, _.packDouble(v), _.unpackDouble)}
      check(null, _.packNil, _.unpackNil())
    }

    "pack/unpack integer values" taggedAs("int") in {
      val sampleData = Seq[Long](Int.MinValue.toLong - 10, -65535, -8191, -1024, -255, -127, -63, -31, -15, -7, -3, -1, 0, 2, 4, 8, 16, 32, 64, 128, 256, 1024, 8192, 65536, Int.MaxValue.toLong + 10)
      for(v <- sampleData) {
        check(v, _.packLong(v), _.unpackLong)

        if(v.isValidInt) {
          val vi = v.toInt
          check(vi, _.packInt(vi), _.unpackInt)
        }
        else {
          checkOverflow(v, _.packLong(v), _.unpackInt)
        }

        if(v.isValidShort) {
          val vi = v.toShort
          check(vi, _.packShort(vi), _.unpackShort)
        }
        else {
          checkOverflow(v, _.packLong(v), _.unpackShort)
        }

        if(v.isValidByte) {
          val vi = v.toByte
          check(vi, _.packByte(vi), _.unpackByte)
        }
        else {
          checkOverflow(v, _.packLong(v), _.unpackByte)
        }

      }

    }

    "pack/unpack BigInteger" taggedAs("bi") in {
      forAll { (a: Long) =>
        val v = BigInteger.valueOf(a)
        check(v, _.packBigInteger(v), _.unpackBigInteger)
      }

      for(bi <- Seq(BigInteger.valueOf(Long.MaxValue).add(BigInteger.valueOf(1)))) {
        check(bi, _.packBigInteger(bi), _.unpackBigInteger())
      }

      for(bi <- Seq(BigInteger.valueOf(Long.MaxValue).shiftLeft(10))) {
        try {
          checkException(bi, _.packBigInteger(bi), _.unpackBigInteger())
          fail("cannot reach here")
        }
        catch {
          case e:IllegalArgumentException => // OK
        }
      }

    }

    "pack/unpack strings" taggedAs ("string") in {

      forAll { (v: String) =>
        whenever(isValidUTF8(v)) {
          check(v, _.packString(v), _.unpackString)
        }
      }
    }

    "pack/unpack large strings" taggedAs ("large-string") in {
      // Large string
      val strLen = Seq(1000, 2000, 10000, 50000, 100000, 500000)
      for(l <- strLen) {
        val v : String = Iterator.continually(Random.nextString(l * 10)).find(isValidUTF8).get
        check(v, _.packString(v), _.unpackString)
      }
    }


    "report errors when packing/unpacking malformed strings" taggedAs("malformed") in {
      // TODO produce malformed utf-8 strings in Java8"
      pending
      // Create 100 malformed UTF8 Strings
      val r = new Random(0)
      val malformedStrings = Iterator.continually {
        val b = new Array[Byte](10)
        r.nextBytes(b)
        b
      }
        .filter(b => !isValidUTF8(new String(b))).take(100)

      for (malformedBytes <- malformedStrings) {
        // Pack tests
        val malformed = new String(malformedBytes)
        try {
          checkException(malformed, _.packString(malformed), _.unpackString())
        }
        catch {
          case e: MessageStringCodingException => // OK
        }

        try {
          checkException(malformed, { packer =>
            packer.packRawStringHeader(malformedBytes.length)
            packer.writePayload(malformedBytes)
          },
          _.unpackString())
        }
        catch {
          case e: MessageStringCodingException => // OK
        }
      }
    }

    "report errors when packing/unpacking strings that contain unmappable characters" taggedAs("unmap") in {

      val unmappable = Array[Byte](0xfc.toByte, 0x0a.toByte)
      //val unmappableChar = Array[Char](new Character(0xfc0a).toChar)

      // Report error on unmappable character
      val config = new MessagePack.ConfigBuilder().onMalFormedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).build()
      val msgpack = new MessagePack(config)

      for(bytes <- Seq(unmappable)) {
        When("unpacking")
        try {
          checkException(bytes,
          { packer =>
            packer.packRawStringHeader(bytes.length)
            packer.writePayload(bytes)
          },
          _.unpackString(),
          msgpack)
        }
        catch {
          case e:MessageStringCodingException => // OK
        }

//        When("packing")
//        try {
//          val s = new String(unmappableChar)
//          checkException(s, _.packString(s), _.unpackString())
//        }
//        catch {
//          case e:MessageStringCodingException => // OK
//        }
     }
    }


    "pack/unpack binary" taggedAs ("binary") in {
      forAll { (v: Array[Byte]) =>
        check(v, { packer => packer.packBinaryHeader(v.length); packer.writePayload(v)}, { unpacker =>
          val len = unpacker.unpackBinaryHeader()
          val out = new Array[Byte](len)
          unpacker.readPayload(out, 0, len)
          out
        }
        )
      }

      val len = Seq(1000, 2000, 10000, 50000, 100000, 500000)
      for(l <- len) {
        val v = new Array[Byte](l)
        Random.nextBytes(v)
        check(v, { packer => packer.packBinaryHeader(v.length); packer.writePayload(v)}, { unpacker =>
          val len = unpacker.unpackBinaryHeader()
          val out = new Array[Byte](len)
          unpacker.readPayload(out, 0, len)
          out
        }
        )
      }
    }

    val testHeaderLength = Seq(1, 2, 4, 8, 16, 17, 32, 64, 255, 256, 1000, 2000, 10000, 50000, 100000, 500000)


    "pack/unpack arrays" taggedAs ("array") in {
      forAll { (v: Array[Int]) =>
        check(v, { packer =>
          packer.packArrayHeader(v.length)
          v.map(packer.packInt(_))
        }, { unpacker =>
          val len = unpacker.unpackArrayHeader()
          val out = new Array[Int](len)
          for (i <- 0 until v.length)
            out(i) = unpacker.unpackInt
          out
        }
        )
      }

      for(l <- testHeaderLength) {
        check(l, _.packArrayHeader(l), _.unpackArrayHeader())
      }

      try {
        checkException(0, _.packArrayHeader(-1), _.unpackArrayHeader)
      }
      catch {
        case e: IllegalArgumentException => // OK
      }

    }

    "pack/unpack maps" taggedAs ("map") in {
      forAll { (v: Array[Int]) =>

        val m = v.map(i => (i, i.toString))

        check(m, { packer =>
          packer.packMapHeader(v.length)
          m.map { case (k: Int, v: String) =>
            packer.packInt(k)
            packer.packString(v)
          }
        }, { unpacker =>
          val len = unpacker.unpackMapHeader()
          val b = Seq.newBuilder[(Int, String)]
          for (i <- 0 until len)
            b += ((unpacker.unpackInt, unpacker.unpackString))
          b.result
        }
        )
      }

      for(l <- testHeaderLength) {
        check(l, _.packMapHeader(l), _.unpackMapHeader())
      }

      try {
        checkException(0, _.packMapHeader(-1), _.unpackMapHeader)
      }
      catch {
        case e: IllegalArgumentException => // OK
      }


    }

    "pack/unpack extension types" taggedAs("ext") in {
      forAll { (dataLen: Int, tpe: Int) =>
        val l = Math.abs(dataLen)
        val t = Math.abs(tpe) % 128
        whenever(l >= 0) {
          val ext = new ExtensionTypeHeader(l, t)
          check(ext, _.packExtensionTypeHeader(ext.getType, ext.getLength), _.unpackExtensionTypeHeader())
        }
      }

      for(l <- testHeaderLength) {
        val ext = new ExtensionTypeHeader(l, Random.nextInt(128))
        check(ext, _.packExtensionTypeHeader(ext.getType, ext.getLength), _.unpackExtensionTypeHeader())
      }

    }

    "pack/unpack maps in lists" in {
      val aMap = List(Map("f" -> "x"))

      check(aMap, { packer =>
        packer.packArrayHeader(aMap.size)
        for (m <- aMap) {
          packer.packMapHeader(m.size)
          for ((k, v) <- m) {
            packer.packString(k)
            packer.packString(v)
          }
        }
      }, { unpacker =>
        val v = new Variable()
        unpacker.unpackValue(v)
        import scala.collection.JavaConversions._
        v.asArrayValue().map { m =>
          val mv = m.asMapValue()
          val kvs = mv.getKeyValueArray

          kvs.grouped(2).map({ kvp: Array[Value] =>
            val k = kvp(0)
            val v = kvp(1)

            (k.asStringValue().toString, v.asStringValue().toString)
          }).toMap
        }.toList
      })
    }

  }
}