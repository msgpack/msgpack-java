package org.msgpack.core.buffer

import java.nio.ByteBuffer
import xerial.core.log.LogLevel
import scala.util.Random
import org.msgpack.core.MessagePackSpec


/**
 * Created on 2014/05/01.
 */
class MessageBufferTest extends MessagePackSpec {

  "MessageBuffer" should {

    "get and put values" in {

      val b = MessageBuffer.newBuffer(8192)


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

      //Thread.sleep(1000)

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
