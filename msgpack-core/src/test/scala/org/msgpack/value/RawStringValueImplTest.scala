package org.msgpack.value

import org.msgpack.core.MessagePackSpec

class RawStringValueImplTest
  extends MessagePackSpec {

  "StringValue" should {
    "return the same hash code if they are equal" in {
      val str = "a"
      val a1 = ValueFactory.newString(str.getBytes("UTF-8"))
      val a2 = ValueFactory.newString(str)

      a1.shouldEqual(a2)
      a1.hashCode.shouldEqual(a2.hashCode)
      a2.shouldEqual(a1)
      a2.hashCode.shouldEqual(a1.hashCode)
    }
  }
}
