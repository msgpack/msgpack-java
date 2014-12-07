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
package org.msgpack.core

import java.io.ByteArrayOutputStream

import org.msgpack.core.buffer.{OutputStreamBufferOutput, ArrayBufferInput}
import xerial.core.io.IOUtil

import scala.util.Random
import org.msgpack.value.ValueFactory

/**
 *
 */
class MessagePackerTest extends MessagePackSpec {

  val msgpack = MessagePack.DEFAULT

  def verifyIntSeq(answer:Array[Int], packed:Array[Byte]) {
    val unpacker = msgpack.newUnpacker(packed)
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
      val packer = msgpack.newPacker(b)
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
          IOUtil.withResource(msgpack.newPacker(out)) { packer =>
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
          IOUtil.withResource(msgpack.newPacker(out)) { packer =>
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

    "pack larger string array than byte buf" taggedAs ("larger-string-array-than-byte-buf") in {
      // Based on https://github.com/msgpack/msgpack-java/issues/154

      // TODO: Refactor this test code to fit other ones.
      def test(bufferSize: Int, stringSize: Int): Boolean = {
        val msgpack = new MessagePack(new MessagePack.ConfigBuilder().packerBufferSize(bufferSize).build)
        val str = "a" * stringSize
        val rawString = ValueFactory.newRawString(str.getBytes("UTF-8"))
        val array = ValueFactory.newArray(rawString)
        val out = new ByteArrayOutputStream()
        val packer = msgpack.newPacker(out)
        packer.packValue(array)
        packer.close()
        out.toByteArray
        true
      }

      val testCases = List(
        32 -> 30,
        33 -> 31,
        32 -> 31,
        34 -> 32
      )
      testCases.foreach{
        case (bufferSize, stringSize) => test(bufferSize, stringSize)
      }
    }
  }
}
