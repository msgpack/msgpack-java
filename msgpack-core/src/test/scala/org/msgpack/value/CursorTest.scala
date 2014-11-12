package org.msgpack.value

import org.msgpack.core.{MessagePack, MessageUnpacker, MessagePackSpec}
import ValueFactory._
import scala.util.Random
import org.msgpack.value.holder.IntegerHolder

/**
 * Created on 6/13/14.
 */
class CursorTest extends MessagePackSpec {

  val msgpack = MessagePack.DEFAULT

  def sampleData = createMessagePackData { packer =>
    packer.packValue(
      ValueFactory.newArray(
        newInt(10),
        newBinary("message pack".getBytes(MessagePack.UTF8)),
        newString("hello")
      )
    )
  }

  def intSeq(n:Int) = createMessagePackData { packer =>
    (0 until n).foreach { i =>
      packer.packInt(Random.nextInt(65536))
    }
  }
  def binSeq(n:Int) = createMessagePackData { packer =>
    (0 until n).foreach { i =>
      val len = Random.nextInt(256)
      val b = new Array[Byte](len)
      Random.nextBytes(b)
      packer.packBinaryHeader(b.length).writePayload(b)
    }
  }


  "Cursor" should {

    "have array cursor" taggedAs("array") in {

      val cursor = msgpack.newUnpacker(sampleData).getCursor
      // Traverse as references
      val arrCursor = cursor.nextRef().getArrayCursor
      arrCursor.size() shouldBe 3

      import scala.collection.JavaConversions._
      for(v <- arrCursor) {
        info(s"[${v.getValueType}]\t${v}")
      }
    }

    "have map cursor" taggedAs("map") in {
      val packedData = createMessagePackData { packer =>
        packer packMapHeader(1) packString("f") packString("x")
      }

      val cursor = msgpack.newUnpacker(packedData).getCursor
      val mapCursor = cursor.nextRef().getMapCursor
      mapCursor.size() shouldBe 1

      val mapValue = mapCursor.toValue
      val data = mapValue.toKeyValueSeq

      data should have length 2

      data(0).asString().toString shouldBe "f"
      data(1).asString().toString shouldBe "x"
    }

    "traverse ValueRef faster than traversing Value" taggedAs("ref") in {
      val N = 10000
      val data = binSeq(N)

      time("traversal", repeat=100) {
        block("value") {
          val cursor = msgpack.newUnpacker(data).getCursor
          while(cursor.hasNext) {
            cursor.next()
          }
          cursor.close()
        }
        block("value-ref") {
          val cursor = msgpack.newUnpacker(data).getCursor
          while(cursor.hasNext) {
            cursor.nextRef()
          }
          cursor.close()
        }
      }

    }

    "have negligible overhead" taggedAs("perf") in {
      val N = 10000
      val data = intSeq(N)
      time("scan int-seq", repeat=1000) {
        block("unpacker") {
          val unpacker = msgpack.newUnpacker(data)
          val intHolder = new IntegerHolder()
          var count = 0
          while(unpacker.hasNext) {
            val vt = unpacker.getNextFormat.getValueType
            if(vt.isIntegerType) {
              unpacker.unpackInteger(intHolder);
              count += 1
            }
            else {
              throw new IllegalStateException(s"invalid format: ${vt}")
            }
          }
          unpacker.close()
          count shouldBe N
        }
        block("cursor") {
          var count = 0
          val cursor = msgpack.newUnpacker(data).getCursor
          while(cursor.hasNext) {
            val ref = cursor.nextRef()
            val v = ref.asInteger().toInt
            count += 1
          }
          cursor.close()
          count shouldBe N
        }
      }



    }


  }
}
