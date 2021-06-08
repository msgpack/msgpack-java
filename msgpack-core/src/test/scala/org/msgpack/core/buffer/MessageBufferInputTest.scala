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

import org.msgpack.core.MessagePack
import wvlet.airspec.AirSpec
import wvlet.log.io.IOUtil.withResource

import java.io._
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{ServerSocketChannel, SocketChannel}
import java.util.concurrent.{Callable, Executors, TimeUnit}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}
import scala.util.Random

class MessageBufferInputTest extends AirSpec {

  private val targetInputSize =
    Seq(0, 10, 500, 1000, 2000, 4000, 8000, 10000, 30000, 50000, 100000)

  private def testData(size: Int): Array[Byte] = {
    //debug(s"test data size: ${size}")
    val b = new Array[Byte](size)
    Random.nextBytes(b)
    b
  }

  private def testDataSet: Seq[Array[Byte]] = {
    targetInputSize.map(testData)
  }

  private def runTest(factory: Array[Byte] => MessageBufferInput): Unit = {
    for (b <- testDataSet) {
      checkInputData(b, factory(b))
    }
  }

  implicit class InputData(b: Array[Byte]) {
    def compress = {
      val compressed = new ByteArrayOutputStream()
      val out        = new GZIPOutputStream(compressed)
      out.write(b)
      out.close()
      compressed.toByteArray
    }

    def toByteBuffer = {
      ByteBuffer.wrap(b)
    }

    def saveToTmpFile: File = {
      val tmp = File
        .createTempFile("testbuf", ".dat", new File("target"))
      tmp.getParentFile.mkdirs()
      tmp.deleteOnExit()
      withResource(new FileOutputStream(tmp)) { out =>
        out.write(b)
      }
      tmp
    }
  }

  private def checkInputData(inputData: Array[Byte], in: MessageBufferInput): Unit = {
    test(s"When input data size = ${inputData.length}") {
      var cursor = 0
      for (m <- Iterator.continually(in.next).takeWhile(_ != null)) {
        m.toByteArray() shouldBe inputData.slice(cursor, cursor + m.size())
        cursor += m.size()
      }
      cursor shouldBe inputData.length
    }
  }

  test("MessageBufferInput") {
    test("support byte arrays") {
      runTest(new ArrayBufferInput(_))
    }

    test("support ByteBuffers") {
      runTest(b => new ByteBufferInput(b.toByteBuffer))
    }

    test("support InputStreams") {
      runTest(b => new InputStreamBufferInput(new GZIPInputStream(new ByteArrayInputStream(b.compress))))
    }

    test("support file input channel") {
      runTest { b =>
        val tmp = b.saveToTmpFile
        try {
          InputStreamBufferInput
            .newBufferInput(new FileInputStream(tmp))
        } finally {
          tmp.delete()
        }
      }
    }
  }

  private def createTempFile = {
    val f = File.createTempFile("msgpackTest", "msgpack")
    f.deleteOnExit
    f
  }

  private def createTempFileWithInputStream = {
    val f   = createTempFile
    val out = new FileOutputStream(f)
    MessagePack.newDefaultPacker(out).packInt(42).close
    val in = new FileInputStream(f)
    (f, in)
  }

  private def createTempFileWithChannel = {
    val (f, in) = createTempFileWithInputStream
    val ch      = in.getChannel
    (f, ch)
  }

  private def readInt(buf: MessageBufferInput): Int = {
    val unpacker = MessagePack.newDefaultUnpacker(buf)
    unpacker.unpackInt
  }

  test("InputStreamBufferInput") {
    test("reset buffer") {
      val (f0, in0) = createTempFileWithInputStream
      val buf       = new InputStreamBufferInput(in0)
      readInt(buf) shouldBe 42

      val (f1, in1) = createTempFileWithInputStream
      buf.reset(in1)
      readInt(buf) shouldBe 42
    }

    test("be non-blocking") {

      withResource(new PipedOutputStream()) { pipedOutputStream =>
        withResource(new PipedInputStream()) { pipedInputStream =>
          pipedInputStream.connect(pipedOutputStream)

          val packer = MessagePack
            .newDefaultPacker(pipedOutputStream)
            .packArrayHeader(2)
            .packLong(42)
            .packString("hello world")

          packer.flush

          val unpacker = MessagePack.newDefaultUnpacker(pipedInputStream)
          unpacker.hasNext() shouldBe true
          unpacker.unpackArrayHeader() shouldBe 2
          unpacker.unpackLong() shouldBe 42L
          unpacker.unpackString() shouldBe "hello world"

          packer.close
          unpacker.close
        }
      }
    }
  }

  test("ChannelBufferInput") {
    test("reset buffer") {
      val (f0, in0) = createTempFileWithChannel
      val buf       = new ChannelBufferInput(in0)
      readInt(buf) shouldBe 42

      val (f1, in1) = createTempFileWithChannel
      buf.reset(in1)
      readInt(buf) shouldBe 42
    }

    test("unpack without blocking") {
      val server =
        ServerSocketChannel.open.bind(new InetSocketAddress("localhost", 0))
      val executorService = Executors.newCachedThreadPool

      try {
        executorService.execute(new Runnable {
          override def run: Unit = {
            val server_ch = server.accept
            val packer    = MessagePack.newDefaultPacker(server_ch)
            packer.packString("0123456789")
            packer.flush
            // Keep the connection open
            while (!executorService.isShutdown) {
              TimeUnit.SECONDS.sleep(1)
            }
            packer.close
          }
        })

        val future = executorService.submit(new Callable[String] {
          override def call: String = {
            val conn_ch  = SocketChannel.open(new InetSocketAddress("localhost", server.socket.getLocalPort))
            val unpacker = MessagePack.newDefaultUnpacker(conn_ch)
            val s        = unpacker.unpackString
            unpacker.close
            s
          }
        })

        future.get(5, TimeUnit.SECONDS) shouldBe "0123456789"
      } finally {
        executorService.shutdown
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
          executorService.shutdownNow
        }
      }
    }
  }
}
