package org.msgpack.core.buffer

import org.msgpack.core.MessagePackSpec
import java.nio.ByteBuffer
import xerial.core.log.LogLevel
import scala.util.Random

object Test {
  val b = ByteBuffer.allocate(10)
  b.getInt(0)

  val m = Buffer.newBuffer(10)
  m.getInt(0)
}

/**
 * Created on 2014/05/01.
 */
class BufferTest extends MessagePackSpec {

  "Buffer" should {
    "getInt" in {

      val N = 100000000
      val M = 64 * 1024 * 1024

      val ubb = Buffer.newBuffer(M)
      val udb = Buffer.newDirectBuffer(M)
      val bb = ByteBuffer.allocate(M)
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
      time("sequential getInt", repeat = rep, logLevel = LogLevel.INFO) {
        block("unsafe array") {
          var i = 0
          while(i < N) {
            ubb.getInt((i * 4) % M)
            i += 1
          }

        }

        block("unsafe direct") {
          bench(udb getInt _)
        }

        block("allocate") {
          bench(bb getInt _)
        }

        block("allocateDirect") {
          bench(db getInt _)
        }
      }

      time("random getInt", repeat = rep, logLevel = LogLevel.INFO) {
        block("unsafe array") {
          randomBench(ubb getInt _)
        }

        block("unsafe direct") {
          randomBench(udb getInt _)
        }

        block("allocate") {
          randomBench(bb getInt _)
        }

        block("allocateDirect") {
          randomBench(db getInt _)
        }
      }

    }


  }

}
