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

import java.nio.ByteBuffer

import org.msgpack.core.MessagePackSpec

import scala.util.Random

/**
 * Created on 2014/05/01.
 */
class MessageBufferTest
  extends MessagePackSpec {

  "MessageBuffer" should {

    val universal = MessageBuffer.allocate(0).isInstanceOf[MessageBufferU]
    "check buffer type" in {
      val b = MessageBuffer.allocate(0)
      info(s"MessageBuffer type: ${b.getClass.getName}")
    }

    "wrap byte array considering position and remaining values" taggedAs ("wrap-ba") in {
      val d = Array[Byte](10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
      val mb = MessageBuffer.wrap(d, 2, 2)
      mb.getByte(0) shouldBe 12
      mb.size() shouldBe 2
    }

    "wrap ByteBuffer considering position and remaining values" taggedAs ("wrap-bb") in {
      val d = Array[Byte](10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
      val subset = ByteBuffer.wrap(d, 2, 2)
      val mb = MessageBuffer.wrap(subset)
      mb.getByte(0) shouldBe 12
      mb.size() shouldBe 2
    }

    "have better performance than ByteBuffer" in {

      val N = 1000000
      val M = 64 * 1024 * 1024

      val ub = MessageBuffer.allocate(M)
      val ud = if (universal) MessageBuffer.wrap(ByteBuffer.allocate(M)) else MessageBuffer.wrap(ByteBuffer.allocateDirect(M))
      val hb = ByteBuffer.allocate(M)
      val db = ByteBuffer.allocateDirect(M)

      def bench(f: Int => Unit) {
        var i = 0
        while (i < N) {
          f((i * 4) % M)
          i += 1
        }
      }

      val r = new
          Random(0)
      val rs = new
          Array[Int](N)
      (0 until N).map(i => rs(i) = r.nextInt(N))
      def randomBench(f: Int => Unit) {
        var i = 0
        while (i < N) {
          f((rs(i) * 4) % M)
          i += 1
        }
      }

      val rep = 3
      info(f"Reading buffers (of size:${M}%,d) ${N}%,d x $rep times")
      time("sequential getInt", repeat = rep) {
        block("unsafe array") {
          var i = 0
          while (i < N) {
            ub.getInt((i * 4) % M)
            i += 1
          }
        }

        block("unsafe direct") {
          var i = 0
          while (i < N) {
            ud.getInt((i * 4) % M)
            i += 1
          }
        }

        block("allocate") {
          var i = 0
          while (i < N) {
            hb.getInt((i * 4) % M)
            i += 1
          }
        }

        block("allocateDirect") {
          var i = 0
          while (i < N) {
            db.getInt((i * 4) % M)
            i += 1
          }
        }
      }

      time("random getInt", repeat = rep) {
        block("unsafe array") {
          var i = 0
          while (i < N) {
            ub.getInt((rs(i) * 4) % M)
            i += 1
          }
        }

        block("unsafe direct") {
          var i = 0
          while (i < N) {
            ud.getInt((rs(i) * 4) % M)
            i += 1
          }
        }

        block("allocate") {
          var i = 0
          while (i < N) {
            hb.getInt((rs(i) * 4) % M)
            i += 1
          }
        }

        block("allocateDirect") {
          var i = 0
          while (i < N) {
            db.getInt((rs(i) * 4) % M)
            i += 1
          }
        }
      }
    }
    val builder = Seq.newBuilder[MessageBuffer]
    builder += MessageBuffer.allocate(10)
    builder += MessageBuffer.wrap(ByteBuffer.allocate(10))
    if (!universal) builder += MessageBuffer.wrap(ByteBuffer.allocateDirect(10))
    val buffers = builder.result()

    "convert to ByteBuffer" in {
      for (t <- buffers) {
        val bb = t.sliceAsByteBuffer
        bb.position() shouldBe 0
        bb.limit() shouldBe 10
        bb.capacity shouldBe 10
      }
    }

    "put ByteBuffer on itself" in {
      for (t <- buffers) {
        val b = Array[Byte](0x02, 0x03)
        val srcArray = ByteBuffer.wrap(b)
        val srcHeap = ByteBuffer.allocate(b.length)
        srcHeap.put(b).flip
        val srcOffHeap = ByteBuffer.allocateDirect(b.length)
        srcOffHeap.put(b).flip

        for (src <- Seq(srcArray, srcHeap, srcOffHeap)) {
          // Write header bytes
          val header = Array[Byte](0x00, 0x01)
          t.putBytes(0, header, 0, header.length)
          // Write src after the header
          t.putByteBuffer(header.length, src, header.length)

          t.getByte(0) shouldBe 0x00
          t.getByte(1) shouldBe 0x01
          t.getByte(2) shouldBe 0x02
          t.getByte(3) shouldBe 0x03
        }
      }
    }

    "put MessageBuffer on itself" in {
      for (t <- buffers) {
        val b = Array[Byte](0x02, 0x03)
        val srcArray = ByteBuffer.wrap(b)
        val srcHeap = ByteBuffer.allocate(b.length)
        srcHeap.put(b).flip
        val srcOffHeap = ByteBuffer.allocateDirect(b.length)
        srcOffHeap.put(b).flip
        val builder = Seq.newBuilder[ByteBuffer]
        builder ++= Seq(srcArray, srcHeap)
        if (!universal) builder += srcOffHeap

        for (src <- builder.result().map(d => MessageBuffer.wrap(d))) {
          // Write header bytes
          val header = Array[Byte](0x00, 0x01)
          t.putBytes(0, header, 0, header.length)
          // Write src after the header
          t.putMessageBuffer(header.length, src, 0, header.length)

          t.getByte(0) shouldBe 0x00
          t.getByte(1) shouldBe 0x01
          t.getByte(2) shouldBe 0x02
          t.getByte(3) shouldBe 0x03
        }
      }
    }

    "copy sliced buffer" in {
      def prepareBytes : Array[Byte] = {
        Array[Byte](0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07)
      }

      def prepareDirectBuffer : ByteBuffer = {
        val directBuffer = ByteBuffer.allocateDirect(prepareBytes.length)
        directBuffer.put(prepareBytes)
        directBuffer.flip
        directBuffer
      }

      def checkSliceAndCopyTo(srcBuffer: MessageBuffer, dstBuffer: MessageBuffer) = {
        val sliced = srcBuffer.slice(2, 5)

        sliced.size() shouldBe 5
        sliced.getByte(0) shouldBe 0x02
        sliced.getByte(1) shouldBe 0x03
        sliced.getByte(2) shouldBe 0x04
        sliced.getByte(3) shouldBe 0x05
        sliced.getByte(4) shouldBe 0x06

        sliced.copyTo(3, dstBuffer, 1, 2) // copy 0x05 and 0x06 to dstBuffer[1] and [2]

        dstBuffer.getByte(0) shouldBe 0x00
        dstBuffer.getByte(1) shouldBe 0x05 // copied by sliced.getByte(3)
        dstBuffer.getByte(2) shouldBe 0x06 // copied by sliced.getByte(4)
        dstBuffer.getByte(3) shouldBe 0x03
        dstBuffer.getByte(4) shouldBe 0x04
        dstBuffer.getByte(5) shouldBe 0x05
        dstBuffer.getByte(6) shouldBe 0x06
        dstBuffer.getByte(7) shouldBe 0x07
      }

      checkSliceAndCopyTo(MessageBuffer.wrap(prepareBytes), MessageBuffer.wrap(prepareBytes))
      checkSliceAndCopyTo(MessageBuffer.wrap(ByteBuffer.wrap(prepareBytes)), MessageBuffer.wrap(ByteBuffer.wrap(prepareBytes)))
      if (!universal) {
        checkSliceAndCopyTo(MessageBuffer.wrap(prepareDirectBuffer), MessageBuffer.wrap(prepareDirectBuffer))
      }
    }
  }
}


