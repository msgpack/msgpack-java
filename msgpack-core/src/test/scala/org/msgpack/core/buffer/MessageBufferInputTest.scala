package org.msgpack.core.buffer

import org.msgpack.core.MessagePackSpec
import java.io._
import xerial.core.io.IOUtil
import scala.util.Random
import java.util.zip.{GZIPOutputStream, GZIPInputStream}
import java.nio.ByteBuffer

/**
 * Created on 5/30/14.
 */
class MessageBufferInputTest extends MessagePackSpec {

  val targetInputSize = Seq(0, 10, 500, 1000, 2000, 4000, 8000, 10000, 30000, 50000, 100000)

  def testData(size:Int) = {
    //debug(s"test data size: ${size}")
    val b = new Array[Byte](size)
    Random.nextBytes(b)
    b
  }

  def testDataSet = {
    targetInputSize.map(testData)
  }
  
  def runTest(factory:Array[Byte] => MessageBufferInput) {
    for(b <- testDataSet) {
      checkInputData(b, factory(b))
    }
  }

  implicit class InputData(b:Array[Byte]) {
    def compress = {
      val compressed = new ByteArrayOutputStream()
      val out = new GZIPOutputStream(compressed)
      out.write(b)
      out.close()
      compressed.toByteArray
    }

    def toByteBuffer = {
      ByteBuffer.wrap(b)
    }

    def saveToTmpFile : File = {
      val tmp = File.createTempFile("testbuf", ".dat", new File("target"))
      tmp.getParentFile.mkdirs()
      tmp.deleteOnExit()
      IOUtil.withResource(new FileOutputStream(tmp)) { out =>
        out.write(b)
      }
      tmp
    }
  }



  def checkInputData(inputData:Array[Byte], in:MessageBufferInput) {
    When(s"input data size = ${inputData.length}")
    var cursor = 0
    for(m <- Iterator.continually(in.next).takeWhile(_ != null)) {
      m.toByteArray() shouldBe inputData.slice(cursor, cursor + m.size())
      cursor += m.size()
    }
    cursor shouldBe inputData.length

  }


  "MessageBufferInput" should {
    "support byte arrays" in {
      runTest(new ArrayBufferInput(_))
    }

    "support ByteBuffers" in {
      runTest(b => new ByteBufferInput(b.toByteBuffer))
    }

    "support InputStreams" taggedAs("is") in {
      runTest(b => 
        new InputStreamBufferInput(
          new GZIPInputStream(new ByteArrayInputStream(b.compress)))
      )
    }

    "support file input channel" taggedAs("fc") in {
      runTest { b =>
        val tmp = b.saveToTmpFile
        try {
          InputStreamBufferInput.newBufferInput(new FileInputStream(tmp))
        }
        finally {
          tmp.delete()
        }
      }
    }

  }
}
