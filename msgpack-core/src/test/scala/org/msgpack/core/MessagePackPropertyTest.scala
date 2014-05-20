package org.msgpack.core

import org.scalatest.prop.PropertyChecks
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import scala.util.Random

/**
 * Created on 2014/05/20.
 */
class MessagePackPropertyTest extends MessagePackSpec with PropertyChecks
{

  def check[A](v:A, pack:MessagePacker => Unit, unpack:MessageUnpacker => A) {
    try {
      val bs = new ByteArrayOutputStream()
      val packer = new MessagePacker(bs)
      pack(packer)
      packer.close()

      val b = bs.toByteArray
      debug(s"pack: ${toHex(b)}")

      val unpacker = new MessageUnpacker(b)
      val ret = unpack(unpacker)
      ret shouldBe v
    }
    catch {
      case e:Exception =>
        warn(e.getMessage)
        throw e
    }
  }


  "MessagePack" should {
    "pack/unpack primitive values" in {
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

    "pack/unpack strings" taggedAs("string") in {

      def isValidUTF8(s:String) = {
        try {
          val b = MessagePack.UTF8.encode(s)
          MessagePack.UTF8.decode(b)
          true
        }
        catch {
          case e:Exception =>
            false
        }
      }

      forAll { (v:String) =>
        whenever(isValidUTF8(v)) {
          check(v, _.packString(v), _.unpackString)
        }
      }
    }

    "pack/unpack binary" taggedAs("binary") in {
      forAll { (v: Array[Byte]) =>
        check(v,
        { packer => packer.packBinaryHeader(v.length); packer.writePayload(v) },
        { unpacker =>
          val len = unpacker.unpackBinaryHeader()
          val out = new Array[Byte](len)
          unpacker.readPayload(out, 0, len)
          out
        }
        )
      }
    }

    "pack/unpack arrays" taggedAs("array") in {
      forAll { (v: Array[Int]) =>
        check(v,
        { packer =>
          packer.packArrayHeader(v.length)
          v.map(packer.packInt(_))
        },
        { unpacker =>
          val len = unpacker.unpackArrayHeader()
          val out = new Array[Int](len)
          for(i <- 0 until v.length)
            out(i) = unpacker.unpackInt
          out
        }
        )
      }
    }

    "pack/unpack maps" taggedAs("map") in {
      forAll { (v: Array[Int]) =>

        val m = v.map(i => (i, i.toString))

        check(m,
        { packer =>
          packer.packMapHeader(v.length)
          m.map { case (k:Int, v:String) =>
            packer.packInt(k)
            packer.packString(v)
          }
        },
        { unpacker =>
          val len = unpacker.unpackMapHeader()
          val b = Seq.newBuilder[(Int, String)]
          for(i <- 0 until len)
            b += ((unpacker.unpackInt, unpacker.unpackString))
          b.result
        }
        )
      }
    }





  }
}
