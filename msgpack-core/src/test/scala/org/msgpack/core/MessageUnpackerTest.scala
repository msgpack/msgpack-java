package org.msgpack.core

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

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
    info(s"packed: ${toHex(arr)}")

    return arr
  }


  "MessageUnpacker" should {

    "parse message packed data" in {
      val arr = testData

      val unpacker = MessagePack.newUnpacker(arr)

      var f : MessageFormat = null
      do {
        f = unpacker.getNextFormat()
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
            debug(s"unknown type: $f")
        }
      }
      while (f != MessageFormat.EOF)


    }

  }

}
