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

import org.msgpack.core.MessagePackSpec.{createMessagePackData, toHex}
import org.msgpack.core.buffer._
import org.msgpack.value.ValueType
import wvlet.airspec.AirSpec
import wvlet.log.LogSupport
import wvlet.log.io.IOUtil.withResource

import java.io._
import java.nio.ByteBuffer
import java.util.Collections
import scala.jdk.CollectionConverters._
import scala.util.Random

object MessageUnpackerTest {
  class SplitMessageBufferInput(array: Array[Array[Byte]]) extends MessageBufferInput {
    var cursor = 0
    override def next(): MessageBuffer = {
      if (cursor < array.length) {
        val a = array(cursor)
        cursor += 1
        MessageBuffer.wrap(a)
      } else {
        null
      }
    }

    override def close(): Unit = {}
  }
}

import org.msgpack.core.MessageUnpackerTest._

class MessageUnpackerTest extends AirSpec with Benchmark {

  private val universal = MessageBuffer.allocate(0).isInstanceOf[MessageBufferU]
  private def testData: Array[Byte] = {
    val out    = new ByteArrayOutputStream()
    val packer = MessagePack.newDefaultPacker(out)

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

  private val intSeq = (for (i <- 0 until 100) yield Random.nextInt()).toArray[Int]

  private def testData2: Array[Byte] = {
    val out    = new ByteArrayOutputStream()
    val packer = MessagePack.newDefaultPacker(out);

    packer
      .packBoolean(true)
      .packBoolean(false)

    intSeq.foreach(packer.packInt)
    packer.close()

    val arr = out.toByteArray
    debug(s"packed: ${toHex(arr)}")
    arr
  }

  private def write(packer: MessagePacker, r: Random): Unit = {
    val tpeIndex = Iterator
      .continually(r.nextInt(MessageFormat.values().length))
      .find(_ != MessageFormat.NEVER_USED.ordinal())
      .get
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
        val b   = new Array[Byte](len)
        r.nextBytes(b)
        trace(s"binary: ${toHex(b)}")
        packer.packBinaryHeader(b.length)
        packer.writePayload(b)
      case ValueType.ARRAY =>
        val len = r.nextInt(5)
        trace(s"array len: $len")
        packer.packArrayHeader(len)
        var i = 0
        while (i < len) {
          write(packer, r)
          i += 1
        }
      case ValueType.MAP =>
        val len = r.nextInt(5) + 1
        packer.packMapHeader(len)
        trace(s"map len: ${len}")
        var i = 0
        while (i < len * 2) {
          write(packer, r)
          i += 1
        }
      case _ =>
        val v = r.nextInt(Int.MaxValue)
        trace(s"int: $v")
        packer.packInt(v)
    }
  }

  private def testData3(N: Int): Array[Byte] = {

    val out    = new ByteArrayOutputStream()
    val packer = MessagePack.newDefaultPacker(out)

    val r = new Random(0)

    (0 until N).foreach { i =>
      write(packer, r)
    }

    packer.close()
    val arr = out.toByteArray
    trace(s"packed: ${toHex(arr)}")
    debug(s"size:${arr.length}")
    arr
  }

  private def readValue(unpacker: MessageUnpacker): Unit = {
    val f = unpacker.getNextFormat()
    f.getValueType match {
      case ValueType.ARRAY =>
        val arrLen = unpacker.unpackArrayHeader()
        debug(s"arr size: $arrLen")
      case ValueType.MAP =>
        val mapLen = unpacker.unpackMapHeader()
        debug(s"map size: $mapLen")
      case ValueType.INTEGER =>
        val i = unpacker.unpackLong()
        debug(s"int value: $i")
      case ValueType.STRING =>
        val s = unpacker.unpackString()
        debug(s"str value: $s")
      case other =>
        unpacker.skipValue()
        debug(s"unknown type: $f")
    }
  }

  private def createTempFile: File = {
    val f = File.createTempFile("msgpackTest", "msgpack")
    f.deleteOnExit
    val p = MessagePack.newDefaultPacker(new FileOutputStream(f))
    p.packInt(99)
    p.close
    f
  }

  private def checkFile(u: MessageUnpacker): Boolean = {
    u.unpackInt shouldBe 99
    u.hasNext shouldBe false
  }

  private def unpackers(data: Array[Byte]): Seq[MessageUnpacker] = {
    val bb = ByteBuffer.allocate(data.length)
    val db = ByteBuffer.allocateDirect(data.length)
    bb.put(data).flip()
    db.put(data).flip()
    val builder = Seq.newBuilder[MessageUnpacker]
    builder += MessagePack.newDefaultUnpacker(data)
    builder += MessagePack.newDefaultUnpacker(bb)
    if (!universal) {
      builder += MessagePack.newDefaultUnpacker(db)
    }

    builder.result()
  }

  private def unpackerCollectionWithVariousBuffers(data: Array[Byte], chunkSize: Int): Seq[MessageUnpacker] = {
    val seqBytes         = Seq.newBuilder[MessageBufferInput]
    val seqByteBuffers   = Seq.newBuilder[MessageBufferInput]
    val seqDirectBuffers = Seq.newBuilder[MessageBufferInput]
    var left             = data.length
    var position         = 0
    while (left > 0) {
      val length = Math.min(chunkSize, left)
      seqBytes += new ArrayBufferInput(data, position, length)
      val bb = ByteBuffer.allocate(length)
      val db = ByteBuffer.allocateDirect(length)
      bb.put(data, position, length).flip()
      db.put(data, position, length).flip()
      seqByteBuffers += new ByteBufferInput(bb)
      seqDirectBuffers += new ByteBufferInput(db)
      left -= length
      position += length
    }
    val builder = Seq.newBuilder[MessageUnpacker]
    builder += MessagePack.newDefaultUnpacker(new SequenceMessageBufferInput(Collections.enumeration(seqBytes.result().asJava)))
    builder += MessagePack.newDefaultUnpacker(new SequenceMessageBufferInput(Collections.enumeration(seqByteBuffers.result().asJava)))
    if (!universal) {
      builder += MessagePack.newDefaultUnpacker(new SequenceMessageBufferInput(Collections.enumeration(seqDirectBuffers.result().asJava)))
    }

    builder.result()
  }

  test("MessageUnpacker") {

    test("parse message packed data") {
      val arr = testData

      for (unpacker <- unpackers(arr)) {

        var count = 0
        while (unpacker.hasNext) {
          count += 1
          readValue(unpacker)
        }
        count shouldBe 6
        unpacker.getTotalReadBytes shouldBe arr.length

        unpacker.close()
        unpacker.getTotalReadBytes shouldBe arr.length
      }
    }

    test("skip reading values") {

      for (unpacker <- unpackers(testData)) {
        var skipCount = 0
        while (unpacker.hasNext) {
          unpacker.skipValue()
          skipCount += 1
        }

        skipCount shouldBe 2
        unpacker.getTotalReadBytes shouldBe testData.length

        unpacker.close()
        unpacker.getTotalReadBytes shouldBe testData.length
      }
    }

    test("compare skip performance") {
      val N    = 10000
      val data = testData3(N)

      time("skip performance", repeat = 100) {
        block("switch") {
          for (unpacker <- unpackers(data)) {
            var skipCount = 0
            while (unpacker.hasNext) {
              unpacker.skipValue()
              skipCount += 1
            }
            skipCount shouldBe N
          }
        }
      }

      time("bulk skip performance", repeat = 100) {
        block("switch") {
          for (unpacker <- unpackers(data)) {
            unpacker.skipValue(N)
            unpacker.hasNext shouldBe false
          }
        }
      }

    }

    test("parse int data") {
      debug(intSeq.mkString(", "))

      for (unpacker <- unpackers(testData2)) {
        val ib = Seq.newBuilder[Int]

        while (unpacker.hasNext) {
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

        ib.result() shouldBe intSeq.toSeq
        unpacker.getTotalReadBytes shouldBe testData2.length

        unpacker.close()
        unpacker.getTotalReadBytes shouldBe testData2.length
      }
    }

    test("read data at the buffer boundary") {
      trait SplitTest extends LogSupport {
        val data: Array[Byte]
        def run: Unit = {
          for (unpacker <- unpackers(data)) {
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
              val (h, t)   = data.splitAt(splitPoint)
              val bin      = new SplitMessageBufferInput(Array(h, t))
              val unpacker = MessagePack.newDefaultUnpacker(bin)
              var count    = 0
              while (unpacker.hasNext) {
                count += 1
                val f = unpacker.getNextFormat
                readValue(unpacker)
              }
              count shouldBe numElems
              unpacker.getTotalReadBytes shouldBe data.length

              unpacker.close()
              unpacker.getTotalReadBytes shouldBe data.length
            }
          }
        }
      }

      new SplitTest { val data = testData }.run
      new SplitTest { val data = testData3(30) }.run
    }

    test("read integer at MessageBuffer boundaries") {
      val packer = MessagePack.newDefaultBufferPacker()
      (0 until 1170).foreach { i =>
        packer.packLong(0x0011223344556677L)
      }
      packer.close
      val data = packer.toByteArray

      // Boundary test
      withResource(MessagePack.newDefaultUnpacker(new InputStreamBufferInput(new ByteArrayInputStream(data), 8192))) { unpacker =>
        (0 until 1170).foreach { i =>
          unpacker.unpackLong() shouldBe 0x0011223344556677L
        }
      }

      // Boundary test for sequences of ByteBuffer, DirectByteBuffer backed MessageInput.
      for (unpacker <- unpackerCollectionWithVariousBuffers(data, 32)) {
        (0 until 1170).foreach { i =>
          unpacker.unpackLong() shouldBe 0x0011223344556677L
        }
      }
    }

    test("read string at MessageBuffer boundaries") {
      val packer = MessagePack.newDefaultBufferPacker()
      (0 until 1170).foreach { i =>
        packer.packString("hello world")
      }
      packer.close
      val data = packer.toByteArray

      // Boundary test
      withResource(MessagePack.newDefaultUnpacker(new InputStreamBufferInput(new ByteArrayInputStream(data), 8192))) { unpacker =>
        (0 until 1170).foreach { i =>
          unpacker.unpackString() shouldBe "hello world"
        }
      }

      // Boundary test for sequences of ByteBuffer, DirectByteBuffer backed MessageInput.
      for (unpacker <- unpackerCollectionWithVariousBuffers(data, 32)) {
        (0 until 1170).foreach { i =>
          unpacker.unpackString() shouldBe "hello world"
        }
      }
    }

    test("be faster than msgpack-v6 skip") {

      trait Fixture {
        val unpacker: MessageUnpacker
        def run: Unit = {
          var count = 0
          try {
            while (unpacker.hasNext) {
              unpacker.skipValue()
              count += 1
            }
          } finally {
            unpacker.close()
          }
        }
      }

      val data = testData3(10000)
      val N    = 100
      val bb   = ByteBuffer.allocate(data.length)
      bb.put(data).flip()
      val db = ByteBuffer.allocateDirect(data.length)
      db.put(data).flip()

      val t = time("skip performance", repeat = N) {
        block("v6") {
          val v6       = new org.msgpack.MessagePack()
          val unpacker = new org.msgpack.unpacker.MessagePackUnpacker(v6, new ByteArrayInputStream(data))
          var count    = 0
          try {
            while (true) {
              unpacker.skip()
              count += 1
            }
          } catch {
            case e: EOFException =>
          } finally unpacker.close()
        }

        block("v7-array") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(data)
          }.run
        }

        block("v7-array-buffer") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(bb)
          }.run
        }
        if (!universal) block("v7-direct-buffer") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(db)
          }.run
        }
      }

      t("v7-array").averageWithoutMinMax <= t("v6").averageWithoutMinMax shouldBe true
      t("v7-array-buffer").averageWithoutMinMax <= t("v6").averageWithoutMinMax shouldBe true
      if (!universal) {
        t("v7-direct-buffer").averageWithoutMinMax <= t("v6").averageWithoutMinMax shouldBe true
      }
    }

    import org.msgpack.`type`.{ValueType => ValueTypeV6}

    test("be faster than msgpack-v6 read value") {

      def readValueV6(unpacker: org.msgpack.unpacker.MessagePackUnpacker): Unit = {
        val vt = unpacker.getNextType()
        vt match {
          case ValueTypeV6.ARRAY =>
            val len = unpacker.readArrayBegin()
            var i   = 0
            while (i < len) { readValueV6(unpacker); i += 1 }
            unpacker.readArrayEnd()
          case ValueTypeV6.MAP =>
            val len = unpacker.readMapBegin()
            var i   = 0
            while (i < len) {
              readValueV6(unpacker); readValueV6(unpacker); i += 1
            }
            unpacker.readMapEnd()
          case ValueTypeV6.NIL =>
            unpacker.readNil()
          case ValueTypeV6.INTEGER =>
            unpacker.readLong()
          case ValueTypeV6.BOOLEAN =>
            unpacker.readBoolean()
          case ValueTypeV6.FLOAT =>
            unpacker.readDouble()
          case ValueTypeV6.RAW =>
            unpacker.readByteArray()
          case _ =>
            unpacker.skip()
        }
      }

      val buf = new Array[Byte](8192)

      def readValue(unpacker: MessageUnpacker): Unit = {
        val f  = unpacker.getNextFormat
        val vt = f.getValueType
        vt match {
          case ValueType.ARRAY =>
            val len = unpacker.unpackArrayHeader()
            var i   = 0
            while (i < len) { readValue(unpacker); i += 1 }
          case ValueType.MAP =>
            val len = unpacker.unpackMapHeader()
            var i   = 0
            while (i < len) { readValue(unpacker); readValue(unpacker); i += 1 }
          case ValueType.NIL =>
            unpacker.unpackNil()
          case ValueType.INTEGER =>
            unpacker.unpackLong()
          case ValueType.BOOLEAN =>
            unpacker.unpackBoolean()
          case ValueType.FLOAT =>
            unpacker.unpackDouble()
          case ValueType.STRING =>
            val len = unpacker.unpackRawStringHeader()
            unpacker.readPayload(buf, 0, len)
          case ValueType.BINARY =>
            val len = unpacker.unpackBinaryHeader()
            unpacker.readPayload(buf, 0, len)
          case _ =>
            unpacker.skipValue()
        }
      }
      trait Fixture {
        val unpacker: MessageUnpacker
        def run: Unit = {
          var count = 0
          try {
            while (unpacker.hasNext) {
              readValue(unpacker)
              count += 1
            }
          } finally unpacker.close()
        }
      }

      val data = testData3(10000)
      val N    = 100
      val bb   = ByteBuffer.allocate(data.length)
      bb.put(data).flip()
      val db = ByteBuffer.allocateDirect(data.length)
      db.put(data).flip()

      val t = time("unpack performance", repeat = N) {
        block("v6") {
          val v6       = new org.msgpack.MessagePack()
          val unpacker = new org.msgpack.unpacker.MessagePackUnpacker(v6, new ByteArrayInputStream(data))
          var count    = 0
          try {
            while (true) {
              readValueV6(unpacker)
              count += 1
            }
          } catch {
            case e: EOFException =>
          } finally unpacker.close()
        }

        block("v7-array") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(data)
          }.run
        }

        block("v7-array-buffer") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(bb)
          }.run
        }

        if (!universal) block("v7-direct-buffer") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(db)
          }.run
        }
      }

      if (t("v7-array").averageWithoutMinMax > t("v6").averageWithoutMinMax) {
        warn(s"v7-array ${t("v7-array").averageWithoutMinMax} is slower than v6 ${t("v6").averageWithoutMinMax}")
      }
      if (t("v7-array-buffer").averageWithoutMinMax > t("v6").averageWithoutMinMax) {
        warn(s"v7-array-buffer ${t("v7-array-buffer").averageWithoutMinMax} is slower than v6 ${t("v6").averageWithoutMinMax}")
      }
      if (!universal) {
        t("v7-direct-buffer").averageWithoutMinMax <= t("v6").averageWithoutMinMax shouldBe true
      }
    }

    test("be faster for reading binary than v6") {

      val bos    = new ByteArrayOutputStream()
      val packer = MessagePack.newDefaultPacker(bos)
      val L      = 10000
      val R      = 100
      (0 until R).foreach { i =>
        packer.packBinaryHeader(L)
        packer.writePayload(new Array[Byte](L))
      }
      packer.close()

      trait Fixture {
        val unpacker: MessageUnpacker
        val loop: Int
        def run: Unit = {
          var i = 0
          try {
            while (i < loop) {
              val len = unpacker.unpackBinaryHeader()
              val out = new Array[Byte](len)
              unpacker.readPayload(out, 0, len)
              i += 1
            }
          } finally unpacker.close()
        }
        def runRef: Unit = {
          var i = 0
          try {
            while (i < loop) {
              val len = unpacker.unpackBinaryHeader()
              val out = unpacker.readPayloadAsReference(len)
              i += 1
            }
          } finally unpacker.close()
        }
      }
      val b  = bos.toByteArray
      val bb = ByteBuffer.allocate(b.length)
      bb.put(b).flip()
      val db = ByteBuffer.allocateDirect(b.length)
      db.put(b).flip()

      time("unpackBinary", repeat = 100) {
        block("v6") {
          val v6       = new org.msgpack.MessagePack()
          val unpacker = new org.msgpack.unpacker.MessagePackUnpacker(v6, new ByteArrayInputStream(b))
          var i        = 0
          while (i < R) {
            val out = unpacker.readByteArray()
            i += 1
          }
          unpacker.close()
        }

        block("v7-array") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(b)
            override val loop     = R
          }.run
        }

        block("v7-array-buffer") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(bb)
            override val loop     = R
          }.run
        }

        if (!universal) block("v7-direct-buffer") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(db)
            override val loop     = R
          }.run
        }

        block("v7-ref-array") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(b)
            override val loop     = R
          }.runRef
        }

        block("v7-ref-array-buffer") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(bb)
            override val loop     = R
          }.runRef
        }

        if (!universal) block("v7-ref-direct-buffer") {
          new Fixture {
            override val unpacker = MessagePack.newDefaultUnpacker(db)
            override val loop     = R
          }.runRef
        }
      }
    }

    test("read payload as a reference") {

      val dataSizes =
        Seq(0, 1, 5, 8, 16, 32, 128, 256, 1024, 2000, 10000, 100000)

      for (s <- dataSizes) {
        test(f"data size is $s%,d") {
          val data = new Array[Byte](s)
          Random.nextBytes(data)
          val b      = new ByteArrayOutputStream()
          val packer = MessagePack.newDefaultPacker(b)
          packer.packBinaryHeader(s)
          packer.writePayload(data)
          packer.close()

          for (unpacker <- unpackers(b.toByteArray)) {
            val len = unpacker.unpackBinaryHeader()
            len shouldBe s
            val ref = unpacker.readPayloadAsReference(len)
            unpacker.close()
            ref.size() shouldBe s
            val stored = new Array[Byte](len)
            ref.getBytes(0, stored, 0, len)

            stored shouldBe data
          }
        }
      }
    }

    test("reset the internal states") {

      val data = intSeq
      val b    = createMessagePackData(packer => data foreach packer.packInt)
      for (unpacker <- unpackers(b)) {

        val unpacked = Array.newBuilder[Int]
        while (unpacker.hasNext) {
          unpacked += unpacker.unpackInt()
        }
        unpacker.close
        unpacked.result() shouldBe data

        val data2 = intSeq
        val b2    = createMessagePackData(packer => data2 foreach packer.packInt)
        val bi    = new ArrayBufferInput(b2)
        unpacker.reset(bi)
        val unpacked2 = Array.newBuilder[Int]
        while (unpacker.hasNext) {
          unpacked2 += unpacker.unpackInt()
        }
        unpacker.close
        unpacked2.result() shouldBe data2

        // reused the buffer input instance
        bi.reset(b2)
        unpacker.reset(bi)
        val unpacked3 = Array.newBuilder[Int]
        while (unpacker.hasNext) {
          unpacked3 += unpacker.unpackInt()
        }
        unpacker.close
        unpacked3.result() shouldBe data2
      }

    }

    test("improve the performance via reset method") {

      val out    = new ByteArrayOutputStream
      val packer = MessagePack.newDefaultPacker(out)
      packer.packInt(0)
      packer.flush
      val arr = out.toByteArray
      val mb  = MessageBuffer.wrap(arr)

      val N = 1000
      val t = time("unpacker", repeat = 10) {
        block("no-buffer-reset") {
          withResource(MessagePack.newDefaultUnpacker(arr)) { unpacker =>
            for (i <- 0 until N) {
              val buf = new ArrayBufferInput(arr)
              unpacker.reset(buf)
              unpacker.unpackInt
              unpacker.close
            }
          }
        }

        block("reuse-array-input") {
          withResource(MessagePack.newDefaultUnpacker(arr)) { unpacker =>
            val buf = new ArrayBufferInput(arr)
            for (i <- 0 until N) {
              buf.reset(arr)
              unpacker.reset(buf)
              unpacker.unpackInt
              unpacker.close
            }
          }
        }

        block("reuse-message-buffer") {
          withResource(MessagePack.newDefaultUnpacker(arr)) { unpacker =>
            val buf = new ArrayBufferInput(arr)
            for (i <- 0 until N) {
              buf.reset(mb)
              unpacker.reset(buf)
              unpacker.unpackInt
              unpacker.close
            }
          }
        }
      }

      // This performance comparison is too close, so we disabled it
      // t("reuse-message-buffer").averageWithoutMinMax should be <= t("no-buffer-reset").averageWithoutMinMax
      // t("reuse-array-input").averageWithoutMinMax should be <= t("no-buffer-reset").averageWithoutMinMax
    }

    test("reset ChannelBufferInput") {
      val f0 = createTempFile
      val u  = MessagePack.newDefaultUnpacker(new FileInputStream(f0).getChannel)
      checkFile(u)

      val f1 = createTempFile
      val ch = new FileInputStream(f1).getChannel
      u.reset(new ChannelBufferInput(ch))
      checkFile(u)
      u.close
    }

    test("reset InputStreamBufferInput") {
      val f0 = createTempFile
      val u  = MessagePack.newDefaultUnpacker(new FileInputStream(f0))
      checkFile(u)

      val f1 = createTempFile
      val in = new FileInputStream(f1)
      u.reset(new InputStreamBufferInput(in))
      checkFile(u)
      u.close
    }

    test("unpack large string data") {
      def createLargeData(stringLength: Int): Array[Byte] = {
        val out    = new ByteArrayOutputStream()
        val packer = MessagePack.newDefaultPacker(out)

        packer
          .packArrayHeader(2)
          .packString("l" * stringLength)
          .packInt(1)

        packer.close()

        out.toByteArray
      }

      Seq(8191, 8192, 8193, 16383, 16384, 16385).foreach { n =>
        val arr = createLargeData(n)

        for (unpacker <- unpackers(arr)) {

          unpacker.unpackArrayHeader shouldBe 2
          unpacker.unpackString.length shouldBe n
          unpacker.unpackInt shouldBe 1

          unpacker.getTotalReadBytes shouldBe arr.length

          unpacker.close()
          unpacker.getTotalReadBytes shouldBe arr.length
        }
      }
    }

    test("unpack string crossing end of buffer") {
      def check(expected: String, strLen: Int) = {
        val bytes = new Array[Byte](strLen)
        val out   = new ByteArrayOutputStream

        val packer = MessagePack.newDefaultPacker(out)
        packer.packBinaryHeader(bytes.length)
        packer.writePayload(bytes)
        packer.packString(expected)
        packer.close

        val unpacker = MessagePack.newDefaultUnpacker(new InputStreamBufferInput(new ByteArrayInputStream(out.toByteArray)))
        val len      = unpacker.unpackBinaryHeader
        unpacker.readPayload(len)
        val got = unpacker.unpackString
        unpacker.close

        got shouldBe expected
      }

      Seq("\u3042", "a\u3042", "\u3042a", "\u3042\u3044\u3046\u3048\u304A\u304B\u304D\u304F\u3051\u3053\u3055\u3057\u3059\u305B\u305D")
        .foreach { s =>
          Seq(8185, 8186, 8187, 8188, 16377, 16378, 16379, 16380).foreach { n =>
            check(s, n)
          }
        }
    }

    def readTest(input: MessageBufferInput): Unit = {
      withResource(MessagePack.newDefaultUnpacker(input)) { unpacker =>
        while (unpacker.hasNext) {
          unpacker.unpackValue()
        }
      }
    }

    test("read value length at buffer boundary") {
      val input = new SplitMessageBufferInput(
        Array(
          Array[Byte](MessagePack.Code.STR16),
          Array[Byte](0x00),
          Array[Byte](0x05), // STR16 length at the boundary
          "hello".getBytes(MessagePack.UTF8)
        )
      )
      readTest(input)

      val input2 = new SplitMessageBufferInput(
        Array(
          Array[Byte](MessagePack.Code.STR32),
          Array[Byte](0x00),
          Array[Byte](0x00, 0x00),
          Array[Byte](0x05), // STR32 length at the boundary
          "hello".getBytes(MessagePack.UTF8)
        )
      )
      readTest(input2)
    }
  }
}
