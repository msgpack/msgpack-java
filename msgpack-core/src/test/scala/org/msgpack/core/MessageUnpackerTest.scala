package org.msgpack.core

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

/**
 * Created on 2014/05/07.
 */
class MessageUnpackerTest extends MessagePackSpec {

  def toHex(arr:Array[Byte]) = arr.map(x => f"$x%02x").mkString(" ")

  "MessageUnpacker" should {

    "parse message packed data" in {

      val out = new ByteArrayOutputStream()
      val packer = MessagePack.newPacker(out)

      packer
        .packMapHeader(2)
        .packInt(1)
        .packString("leo")
        .packMapHeader(2)
        .packInt(2)
        .packString("aina")

      packer.close()

      val arr = out.toByteArray
      info(s"packed: ${toHex(arr)}")

      val unpacker = MessagePack.newUnpacker(arr)


      val f = unpacker.getNextFormat
      f.getValueType match {
        case ValueType.MAP =>
          val mapLen = unpacker.unpackMapHeader()
          info(s"map size: $mapLen")


      }


    }

  }

}
