package org.msgpack.core

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import scala.util.Random

/**
 * Created on 2014/05/07.
 */
class MessageUnpackerTest extends MessagePackSpec {

  def toHex(arr:Array[Byte]) = arr.map(x => f"$x%02x").mkString(" ")


  def testData : Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val packer = MessagePack.newPacker(out)

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
    val packer = MessagePack.newPacker(out)

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

    "parse message packed data" in {
      val arr = testData

      val unpacker = MessagePack.newUnpacker(arr)

      var f : MessageType = null
      do {
        f = unpacker.getNextFormat()
        f.getTypeFamily match {
          case MessageTypeFamily.ARRAY =>
            val arrLen = unpacker.unpackArrayHeader()
            debug(s"arr size: $arrLen")
          case MessageTypeFamily.MAP =>
            val mapLen = unpacker.unpackMapHeader()
            debug(s"map size: $mapLen")
          case MessageTypeFamily.INTEGER =>
            val i = unpacker.unpackInt()
            debug(s"int value: $i")
          case MessageTypeFamily.STRING =>
            val s = unpacker.unpackString()
            debug(s"str value: $s")
          case MessageTypeFamily.EOF =>
            debug(s"reached EOF")
          case other =>
            unpacker.skipValue();
            debug(s"unknown type: $f")
        }
      }
      while (f != MessageType.EOF)
    }

    "skip reading values" in {

      val unpacker = MessagePack.newUnpacker(testData)
      var skipCount = 0
      while(unpacker.skipValue()) {
        skipCount += 1
      }

      skipCount shouldBe 2
    }

    "parse int data" in {

      debug(intSeq.mkString(", "))

      val ib = Seq.newBuilder[Int]

      val unpacker = MessagePack.newUnpacker(testData2)
      var f : MessageType = null
      do {
        f = unpacker.getNextFormat
        f.getTypeFamily match {
          case MessageTypeFamily.INTEGER =>
            val i = unpacker.unpackInt()
            trace(f"read int: $i%,d")
            ib += i
          case MessageTypeFamily.BOOLEAN =>
            val b = unpacker.unpackBoolean()
            trace(s"read boolean: $b")
          case other =>
            unpacker.skipValue()
        }
      } while(f != MessageType.EOF)

      ib.result shouldBe intSeq

    }



  }

}
