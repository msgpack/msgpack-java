package org.msgpack.core.buffer

import org.msgpack.core.MessagePackSpec
import java.io._

class MessageBufferOutputTest extends MessagePackSpec {

  def createTempFile = {
    File.createTempFile("msgpackTest", "msgpack")
  }

  def createTempFileWithOutputStream = {
    val f = createTempFile
    val out = new FileOutputStream(f)
    (f, out)
  }

  def createTempFileWithChannel = {
    val (f, out) = createTempFileWithOutputStream
    val ch = out.getChannel
    (f, ch)
  }

  def writeIntToBuf(buf:MessageBufferOutput) = {
    val mb0 = buf.next(8)
    mb0.putInt(0, 42)
    buf.flush(mb0)
    buf.close
  }

  "OutputStreamBufferOutput" should {
    "reset buffer" in {
      val (f0, out0) = createTempFileWithOutputStream
      val buf = new OutputStreamBufferOutput(out0)
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
      val buf = new ChannelBufferOutput(ch0)
      writeIntToBuf(buf)
      f0.length.toInt should be > 0

      val (f1, ch1) = createTempFileWithChannel
      buf.reset(ch1)
      writeIntToBuf(buf)
      f1.length.toInt should be > 0
    }
  }

}
