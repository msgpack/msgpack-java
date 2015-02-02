package org.msgpack.core.buffer

import java.nio.ByteBuffer
import scala.util.Random
import org.msgpack.core.MessagePackSpec

/**
 * Created on 2014/05/01.
 */
class MessageBufferTest extends MessagePackSpec {

  "MessageBuffer" should {

    "wrap ByteBuffer considering position and remaining values" taggedAs("wrap-bb") in {
      val d = Array[Byte](10,11,12,13,14,15,16,17,18,19)
      val subset = ByteBuffer.wrap(d, 2, 2)
      val mb = MessageBuffer.wrap(subset)
      mb.getByte(0) shouldBe 12
      mb.size() shouldBe 2
    }

    "have better performance than ByteBuffer" in {

      val N = 1000000
      val M = 64 * 1024 * 1024

      val ub = MessageBuffer.newBuffer(M)
      val ud = MessageBuffer.newDirectBuffer(M)
      val hb = ByteBuffer.allocate(M)
      val db = ByteBuffer.allocateDirect(M)

      def bench(f: Int => Unit) {
        var i = 0
        while(i < N) {
          f((i * 4) % M)
          i += 1
        }
      }

      val r = new Random(0)
      val rs = new Array[Int](N)
      (0 until N).map(i => rs(i) = r.nextInt(N))
      def randomBench(f: Int => Unit) {
        var i = 0
        while(i < N) {
          f((rs(i) * 4) % M)
          i += 1
        }
      }

      val rep = 3
      info(f"Reading buffers (of size:${M}%,d) ${N}%,d x $rep times")
      time("sequential getInt", repeat = rep) {
        block("unsafe array") {
          var i = 0
          while(i < N) {
            ub.getInt((i * 4) % M)
            i += 1
          }
        }

        block("unsafe direct") {
          var i = 0
          while(i < N) {
            ud.getInt((i * 4) % M)
            i += 1
          }
        }

        block("allocate") {
          var i = 0
          while(i < N) {
            hb.getInt((i * 4) % M)
            i += 1
          }
        }

        block("allocateDirect") {
          var i = 0
          while(i < N) {
            db.getInt((i * 4) % M)
            i += 1
          }
        }
      }

      time("random getInt", repeat = rep) {
        block("unsafe array") {
          var i = 0
          while(i < N) {
            ub.getInt((rs(i) * 4) % M)
            i += 1
          }
        }

        block("unsafe direct") {
          var i = 0
          while(i < N) {
            ud.getInt((rs(i) * 4) % M)
            i += 1
          }
        }

        block("allocate") {
          var i = 0
          while(i < N) {
            hb.getInt((rs(i) * 4) % M)
            i += 1
          }
        }

        block("allocateDirect") {
          var i = 0
           while(i < N) {
            db.getInt((rs(i) * 4) % M)
            i += 1
          }
        }
      }

    }

  }

}


