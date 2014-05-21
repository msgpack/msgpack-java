package org.msgpack.core

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import scala.util.Random
import org.msgpack.core.buffer.{MessageBuffer, MessageBufferInput, OutputStreamBufferOutput, ArrayBufferInput}
import xerial.core.log.LogLevel
import scala.annotation.tailrec

/**
 * Created on 2014/05/07.
 */
class MessageUnpackerTest extends MessagePackSpec {



  def testData : Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val packer = new MessagePacker(out)

    packer
      .packArrayHeader(2)
      .packInt(1)
      .packString("leo")
      .packArrayHeader(2)
      .packInt(5)
      .packString("aina")

    packer.close()

    val arr = out.toByteArray
    debug(s"packed: ${toHex(arr)}, size:${arr.length}")

    arr
  }

  val intSeq = (for(i <- 0 until 100) yield Random.nextInt()).toArray[Int]

  def testData2 : Array[Byte] = {

    val out = new ByteArrayOutputStream()
    val packer = new MessagePacker(out);

    packer
      .packBoolean(true)
      .packBoolean(false)

    intSeq.foreach(packer.packInt)
    packer.close()

    val arr = out.toByteArray
    debug(s"packed: ${toHex(arr)}")
    arr
  }

  def write(packer:MessagePacker, r:Random) {
    val tpeIndex = Iterator.continually(r.nextInt(MessageFormat.values().length)).find(_ != MessageFormat.NEVER_USED.ordinal()).get
    val tpe = MessageFormat.values()(tpeIndex)
    tpe.getValueType match {

      case ValueType.INTEGER =>
        val v = r.nextInt(Int.MaxValue)
        trace(s"int: $v")
        packer.packInt(v)
      case ValueType.FLOAT =>
        val v = r.nextFloat()
        trace(s"float $v")
        packer.packFloat(v)
      case ValueType.BOOLEAN =>
        val v = r.nextBoolean()
        trace(s"boolean $v")
        packer.packBoolean(v)
      case ValueType.STRING =>
        val v = r.alphanumeric.take(r.nextInt(100)).mkString
        trace(s"string $v")
        packer.packString(v)
      case ValueType.BINARY =>
        val len = r.nextInt(100)
        val b = new Array[Byte](len)
        r.nextBytes(b)
        trace(s"binary: ${toHex(b)}")
        packer.packBinaryHeader(b.length)
        packer.writePayload(b)
      case ValueType.ARRAY =>
        val len = r.nextInt(5)
        trace(s"array len: $len")
        packer.packArrayHeader(len)
        var i = 0
        while(i < len) {
          write(packer, r)
          i += 1
        }
      case ValueType.MAP =>
        val len = r.nextInt(5) + 1
        packer.packMapHeader(len)
        trace(s"map len: ${len}")
        var i = 0
        while(i < len * 2) {
          write(packer, r)
          i += 1
        }
      case _ =>
        val v = r.nextInt(Int.MaxValue)
        trace(s"int: $v")
        packer.packInt(v)
    }
  }

  def testData3(N:Int) : Array[Byte] = {

    val out = new ByteArrayOutputStream()
    val packer = new MessagePacker(out)

    val r = new Random(0)

    (0 until N).foreach { i => write(packer, r) }

    packer.close()
    val arr = out.toByteArray
    trace(s"packed: ${toHex(arr)}")
    debug(s"size:${arr.length}")
    arr
  }


  def readValue(unpacker:MessageUnpacker) {
    val f = unpacker.getNextFormat()
    f.getValueType match {
      case ValueType.ARRAY =>
        val arrLen = unpacker.unpackArrayHeader()
        debug(s"arr size: $arrLen")
      case ValueType.MAP =>
        val mapLen = unpacker.unpackMapHeader()
        debug(s"map size: $mapLen")
      case ValueType.INTEGER =>
        val i = unpacker.unpackInt()
        debug(s"int value: $i")
      case ValueType.STRING =>
        val s = unpacker.unpackString()
        debug(s"str value: $s")
      case other =>
        unpacker.skipValue()
        debug(s"unknown type: $f")
    }
  }


  "MessageUnpacker" should {

    "parse message packed data" taggedAs("unpack") in {
      val arr = testData

      val unpacker = new MessageUnpacker(arr)

      var count = 0
      while(unpacker.hasNext) {
        count += 1
        readValue(unpacker)
      }
      count shouldBe 6
    }

    "skip reading values" in {

      val unpacker = new MessageUnpacker(testData)
      var skipCount = 0
      while(unpacker.hasNext) {
        unpacker.skipValue()
        skipCount += 1
      }

      skipCount shouldBe 2
    }

    "compare skip performance" taggedAs("skip") in {
      val N = 10000
      val data = testData3(N)

      time("skip performance", repeat = 1000, logLevel = LogLevel.INFO) {
        block("switch") {
          val unpacker = new MessageUnpacker(data)
          var skipCount = 0
          while(unpacker.hasNext) {
            unpacker.skipValue()
            skipCount += 1
          }
          skipCount shouldBe N
        }
      }

    }

    "parse int data" in {

      debug(intSeq.mkString(", "))

      val ib = Seq.newBuilder[Int]

      val unpacker = new MessageUnpacker(testData2)
      while(unpacker.hasNext) {
        val f = unpacker.getNextFormat
        f.getValueType match {
          case ValueType.INTEGER =>
            val i = unpacker.unpackInt()
            trace(f"read int: $i%,d")
            ib += i
          case ValueType.BOOLEAN =>
            val b = unpacker.unpackBoolean()
            trace(s"read boolean: $b")
          case other =>
            unpacker.skipValue()
        }
      }

      ib.result shouldBe intSeq

    }

    class SplitMessageBufferInput(array:Array[Array[Byte]]) extends MessageBufferInput {
      var cursor = 0
      override def next(): MessageBuffer = {
        if (cursor < array.length) {
          val a = array(cursor)
          cursor += 1
          MessageBuffer.wrap(a)
        }
        else
          null
      }

      override def close(): Unit = {}
    }

    "read data at the buffer boundary" taggedAs("boundary") in {



      trait SplitTest {
        val data : Array[Byte]
        def run {
          val unpacker = new MessageUnpacker(data)
          val numElems = {
            var c = 0
            while (unpacker.hasNext) {
              readValue(unpacker)
              c += 1
            }
            c
          }

          for (splitPoint <- 1 until data.length - 1) {
            debug(s"split at $splitPoint")
            val (h, t) = data.splitAt(splitPoint)
            val bin = new SplitMessageBufferInput(Array(h, t))
            val unpacker = new MessageUnpacker(bin)
            var count = 0
            while (unpacker.hasNext) {
              count += 1
              val f = unpacker.getNextFormat
              readValue(unpacker)
            }
            count shouldBe numElems
          }
        }
      }

      new SplitTest { val data = testData }.run
      new SplitTest { val data = testData3(30) }.run

    }



  }

}
