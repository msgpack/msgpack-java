package org.msgpack.core

import scala.util.Random
import MessagePack.Code
import org.scalatest.prop.PropertyChecks
import java.io.ByteArrayOutputStream
import java.math.BigInteger

/**
 * Created on 2014/05/07.
 */
class MessagePackTest extends MessagePackSpec with PropertyChecks {

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

      val N = 10000000
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

    def check[A](v: A, pack: MessagePacker => Unit, unpack: MessageUnpacker => A) {
      var b: Array[Byte] = null
      try {
        val bs = new ByteArrayOutputStream()
        val packer = new MessagePacker(bs)
        pack(packer)
        packer.close()

        b = bs.toByteArray

        val unpacker = new MessageUnpacker(b)
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

    "pack/unpack BigInteger" in {
      forAll { (a: Long) =>
        val v = BigInteger.valueOf(a)
        check(v, _.packBigInteger(v), _.unpackBigInteger)
      }
    }

    "pack/unpack strings" taggedAs ("string") in {

      def isValidUTF8(s: String) = {
        MessagePack.UTF8.newEncoder().canEncode(s)
      }


      forAll { (v: String) =>
        whenever(isValidUTF8(v)) {
          check(v, _.packString(v), _.unpackString)
        }
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
    }

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
    }

    "pack/unpack extended types" taggedAs("ext") in {
      forAll { (dataLen: Int, tpe: Int) =>
        val l = Math.abs(dataLen)
        val t = Math.abs(tpe) % 128
        whenever(l >= 0) {
          val ext = new ExtendedTypeHeader(l, t)
          check(ext, _.packExtendedTypeHeader(ext.getType, ext.getLength), _.unpackExtendedTypeHeader())
        }
      }
    }

  }
}