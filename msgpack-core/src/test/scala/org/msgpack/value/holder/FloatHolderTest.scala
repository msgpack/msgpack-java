package org.msgpack.value.holder

import org.msgpack.core.MessagePackSpec

/**
 *
 */
class FloatHolderTest extends MessagePackSpec {

  "FloatHolder" should {

    "display value in an appropriate format" in {

      val h = new FloatHolder
      val f = 0.1341f
      h.setFloat(f)
      h.toString shouldBe java.lang.Float.toString(f)

      val d = 0.1341341344
      h.setDouble(d)
      h.toString shouldBe java.lang.Double.toString(d)
    }

  }

}
