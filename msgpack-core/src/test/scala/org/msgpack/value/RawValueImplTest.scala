package org.msgpack.value

import org.msgpack.core.{MessagePack, MessagePackSpec}
import java.io.ByteArrayOutputStream


class RawValueImplTest extends MessagePackSpec {

  "RawValueImple" should {
    "toString shouldn't return empty value" in {
      val str = "aaa"
      def newRawStr() = ValueFactory.newString(str.getBytes("UTF-8"))

      def pack(v: Value): Array[Byte] = {
        val out = new ByteArrayOutputStream()
        val packer = MessagePack.newDefaultPacker(out)
        packer.packValue(v)
        packer.close()
        out.toByteArray
      }

      {
        val rawStr = newRawStr()
        pack(rawStr)
        rawStr.toString() shouldBe str
      }

      {
        val rawStr = newRawStr()
        pack(rawStr)
        rawStr.asStringValue().toString shouldBe str
      }
    }
  }
}
