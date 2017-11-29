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

import java.io._

import org.msgpack.core.MessagePackSpec

class MessageBufferOutputTest extends MessagePackSpec {

  def createTempFile = {
    val f = File.createTempFile("msgpackTest", "msgpack")
    f.deleteOnExit
    f
  }

  def createTempFileWithOutputStream = {
    val f   = createTempFile
    val out = new FileOutputStream(f)
    (f, out)
  }

  def createTempFileWithChannel = {
    val (f, out) = createTempFileWithOutputStream
    val ch       = out.getChannel
    (f, ch)
  }

  def writeIntToBuf(buf: MessageBufferOutput) = {
    val mb0 = buf.next(8)
    mb0.putInt(0, 42)
    buf.writeBuffer(4)
    buf.close
  }

  "OutputStreamBufferOutput" should {
    "reset buffer" in {
      val (f0, out0) = createTempFileWithOutputStream
      val buf        = new OutputStreamBufferOutput(out0)
      writeIntToBuf(buf)
      f0.length.toInt should be > 0

      val (f1, out1) = createTempFileWithOutputStream
      buf.reset(out1)
      writeIntToBuf(buf)
      f1.length.toInt should be > 0
    }
  }

  "ChannelBufferOutput" should {
    "reset buffer" in {
      val (f0, ch0) = createTempFileWithChannel
      val buf       = new ChannelBufferOutput(ch0)
      writeIntToBuf(buf)
      f0.length.toInt should be > 0

      val (f1, ch1) = createTempFileWithChannel
      buf.reset(ch1)
      writeIntToBuf(buf)
      f1.length.toInt should be > 0
    }
  }
}
