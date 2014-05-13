package org.msgpack.core

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import scala.util.Random
import org.msgpack.core.buffer.{OutputStreamBufferOutput, ArrayBufferInput}

/**
 * Created on 2014/05/07.
 */
class MessageUnpackerTest extends MessagePackSpec {

  def toHex(arr:Array[Byte]) = arr.map(x => f"$x%02x").mkString(" ")


  def testData : Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val packer = new MessagePacker(out)

    packer
      .packArrayHeader(2)
      .packInt(1)
      .packString("leo")
      .packArrayHeader(2)
      .packInt(2)
      .packString("aina")

    packer.close()

    val arr = out.toByteArray
    debug(s"packed: ${toHex(arr)}")

    arr
  }

  val intSeq = (for(i <- 0 until 100) yield Random.nextInt()).toArray[Int]

  def testData2 : Array[Byte] = {

    val out = new ByteArrayOutputStream()
    val packer = new MessagePacker(out);

    packer
      .packBoolean(true)
      .packBoolean(false)

    intSeq.foreach(packer.packInt)
    packer.close()

    val arr = out.toByteArray
    debug(s"packed: ${toHex(arr)}")
    arr
  }

  "MessageUnpacker" should {

    "parse message packed data" taggedAs("unpack") in {
      val arr = testData

      val unpacker = new MessageUnpacker(arr);

      while(unpacker.hasNext) {
        val f = unpacker.getNextFormat()
        f.getValueType match {
          case ValueType.ARRAY =>
            val arrLen = unpacker.unpackArrayHeader()
            debug(s"arr size: $arrLen")
          case ValueType.MAP =>
            val mapLen = unpacker.unpackMapHeader()
            debug(s"map size: $mapLen")
          case ValueType.INTEGER =>
            val i = unpacker.unpackInt()
            debug(s"int value: $i")
          case ValueType.STRING =>
            val s = unpacker.unpackString()
            debug(s"str value: $s")
          case other =>
            unpacker.skipValue()
            debug(s"unknown type: $f")
        }
      }
    }

    "skip reading values" in {

      val unpacker = new MessageUnpacker(testData)
      var skipCount = 0
      while(unpacker.hasNext) {
        unpacker.skipValue()
        skipCount += 1
      }

      skipCount shouldBe 2
    }

    "parse int data" in {

      debug(intSeq.mkString(", "))

      val ib = Seq.newBuilder[Int]

      val unpacker = new MessageUnpacker(testData2)
      while(unpacker.hasNext) {
        val f = unpacker.getNextFormat
        f.getValueType match {
          case ValueType.INTEGER =>
            val i = unpacker.unpackInt()
            trace(f"read int: $i%,d")
            ib += i
          case ValueType.BOOLEAN =>
            val b = unpacker.unpackBoolean()
            trace(s"read boolean: $b")
          case other =>
            unpacker.skipValue()
        }
      }

      ib.result shouldBe intSeq

    }



  }

}
