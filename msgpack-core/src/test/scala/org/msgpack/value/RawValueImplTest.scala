package org.msgpack.value

import scala.util.Random
import org.msgpack.core.MessagePack.Code
import org.msgpack.core.{MessagePack, MessageFormat, MessageFormatException, MessagePackSpec}
import org.msgpack.core.MessagePack.Code._
import java.io.ByteArrayOutputStream


class RawValueImplTest extends MessagePackSpec {

  "RawValueImple" should {
    "toString shouldn't return empty value" in {
      val str = "aaa"
      def newRawStr() = ValueFactory.newRawString(str.getBytes("UTF-8"))

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
        rawStr.asString().toString shouldBe str
      }
    }
  }
}
