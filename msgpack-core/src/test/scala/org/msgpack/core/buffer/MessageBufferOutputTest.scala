//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.core.buffer

import wvlet.airspec.AirSpec

import java.io._

class MessageBufferOutputTest extends AirSpec {

  private def createTempFile = {
    val f = File.createTempFile("msgpackTest", "msgpack")
    f.deleteOnExit
    f
  }

  private def createTempFileWithOutputStream = {
    val f   = createTempFile
    val out = new FileOutputStream(f)
    (f, out)
  }

  private def createTempFileWithChannel = {
    val (f, out) = createTempFileWithOutputStream
    val ch       = out.getChannel
    (f, ch)
  }

  private def writeIntToBuf(buf: MessageBufferOutput) = {
    val mb0 = buf.next(8)
    mb0.putInt(0, 42)
    buf.writeBuffer(4)
    buf.close
  }

  test("OutputStreamBufferOutput") {
    test("reset buffer") {
      val (f0, out0) = createTempFileWithOutputStream
      val buf        = new OutputStreamBufferOutput(out0)
      writeIntToBuf(buf)
      f0.length.toInt > 0 shouldBe true

      val (f1, out1) = createTempFileWithOutputStream
      buf.reset(out1)
      writeIntToBuf(buf)
      f1.length.toInt > 0 shouldBe true
    }
  }

  test("ChannelBufferOutput") {
    test("reset buffer") {
      val (f0, ch0) = createTempFileWithChannel
      val buf       = new ChannelBufferOutput(ch0)
      writeIntToBuf(buf)
      f0.length.toInt >= 0 shouldBe true

      val (f1, ch1) = createTempFileWithChannel
      buf.reset(ch1)
      writeIntToBuf(buf)
      f1.length.toInt > 0 shouldBe true
    }
  }
}
