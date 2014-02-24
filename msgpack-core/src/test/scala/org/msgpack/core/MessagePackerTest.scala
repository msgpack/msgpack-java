package org.msgpack.core
import org.scalatest.prop.PropertyChecks._
import org.msgpack.annotation.Message


object MessagePackTest {



}

class MessagePackerTest extends MessagePackSpec {

  import MessagePackTest._

  "MessagePacker" should {

    "serialize primitives" in {

      forAll{ (i:Int) =>
        val msg = MessagePack.pack(i)
        val v = MessagePack.unpack[Int](msg)
        v shouldBe i
      }


    }

    "be used with 0.6.x" in {
      val p = new Person(1, "leo")
      val v6 = new org.msgpack.MessagePack()
      v6.setClassLoader(classOf[Message].getClassLoader())

      v6.register(classOf[Person])
      val packed = v6.write(p)



    }

  }
}
