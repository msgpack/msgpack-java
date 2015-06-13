package org.msgpack.value.holder

import org.msgpack.core.MessagePackSpec
import org.msgpack.value.Variable

/**
 *
 */
class FloatHolderTest extends MessagePackSpec {

  "FloatHolder" should {

    "display value in an appropriate format" in {

      val h = new Variable
      val f = 0.1341f
      h.setFloatValue(f)
      h.toString shouldBe java.lang.Float.toString(f)

      val d = 0.1341341344
      h.setFloatValue(d)
      h.toString shouldBe java.lang.Double.toString(d)
    }

  }

}
