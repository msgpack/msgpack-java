package org.msgpack.core

import java.io.ByteArrayOutputStream

import org.msgpack.core.buffer.{OutputStreamBufferOutput, ArrayBufferInput}
import xerial.core.io.IOUtil

import scala.util.Random

/**
 *
 */
class MessagePackerTest extends MessagePackSpec {

  val mf = MessagePackFactory.DEFAULT

  def verifyIntSeq(answer:Array[Int], packed:Array[Byte]) {
    val unpacker = mf.newUnpacker(packed)
    val b = Array.newBuilder[Int]
    while(unpacker.hasNext) {
      b += unpacker.unpackInt()
    }
    val result = b.result
    result.size shouldBe answer.size
    result shouldBe answer
  }

  "MessagePacker" should {

    "reset the internal states" in {
      val intSeq = (0 until 100).map(i => Random.nextInt).toArray

      val b = new ByteArrayOutputStream
      val packer = mf.newPacker(b)
      intSeq foreach packer.packInt
      packer.close
      verifyIntSeq(intSeq, b.toByteArray)

      val intSeq2 = intSeq.reverse
      val b2 = new ByteArrayOutputStream
      packer.reset(new OutputStreamBufferOutput(b2))
      intSeq2 foreach packer.packInt
      packer.close
      verifyIntSeq(intSeq2, b2.toByteArray)

      val intSeq3 = intSeq2.sorted
      val b3 = new ByteArrayOutputStream
      packer.reset(new OutputStreamBufferOutput(b3))
      intSeq3 foreach packer.packInt
      packer.close
      verifyIntSeq(intSeq3, b3.toByteArray)
    }

    "improve the performance via reset method" taggedAs("reset") in {


      val N = 1000
      val t = time("packer", repeat = 10) {
        block("no-buffer-reset") {
          val out = new ByteArrayOutputStream
          IOUtil.withResource(MessagePackFactory.newDefaultPacker(out)) { packer =>
            for (i <- 0 until N) {
              val outputStream = new ByteArrayOutputStream()
              packer.reset(new OutputStreamBufferOutput(outputStream))
              packer.packInt(0)
              packer.flush()
            }
          }
        }

        block("buffer-reset") {
          val out = new ByteArrayOutputStream
          IOUtil.withResource(MessagePackFactory.newDefaultPacker(out)) { packer =>
            val bufferOut = new OutputStreamBufferOutput(new ByteArrayOutputStream())
            for (i <- 0 until N) {
              val outputStream = new ByteArrayOutputStream()
              bufferOut.reset(outputStream)
              packer.reset(bufferOut)
              packer.packInt(0)
              packer.flush()
            }
          }
        }
      }

      t("buffer-reset").averageWithoutMinMax should be <= t("no-buffer-reset").averageWithoutMinMax

    }

  }
}
