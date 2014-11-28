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
package org.msgpack.value

import java.io.ByteArrayInputStream

import org.msgpack.core.{MessagePack, MessageUnpacker, MessagePackSpec}
import ValueFactory._
import scala.util.Random
import org.msgpack.value.holder.{ValueHolder, IntegerHolder}

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
      val arrCursor = cursor.next().asArrayValue()
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
      val mapCursor = cursor.next().asMapValue()
      mapCursor.size() shouldBe 1

      val mapValue = mapCursor.toImmutable
      val data = mapValue.toKeyValueArray

      data should have length 2

      data(0).asStringValue().toString shouldBe "f"
      data(1).asStringValue().toString shouldBe "x"
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
            cursor.next()
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
            val ref = cursor.next()
            val v = ref.asIntegerValue().toInt
            count += 1
          }
          cursor.close()
          count shouldBe N
        }
      }

    }

    "create immutable map" taggedAs("im-map") in {

      val m = createMessagePackData { packer =>
        packer.packMapHeader(3)

        // A -> [1, "leo"]
        packer.packString("A")
        packer.packArrayHeader(2)
        packer.packInt(1)
        packer.packString("leo")

        // B -> 10
        packer.packString("B")
        packer.packInt(10)

        // C -> {a -> 1.0f, b -> 5, c -> {cc->1}}
        packer.packString("C")
        packer.packMapHeader(3)
        packer.packString("a")
        packer.packFloat(1.0f)
        packer.packString("b")
        packer.packInt(5)

        packer.packString("c")
        packer.packMapHeader(1)
        packer.packString("cc")
        packer.packInt(1)

      }

      val unpacker = msgpack.newUnpacker(m)
      val vh = new ValueHolder
      unpacker.unpackValue(vh)
      val mapValue = vh.get().asMapValue()

      val map = mapValue.toMap
      map.size shouldBe 3

      val arr = map.get(ValueFactory.newString("A")).asArrayValue()
      arr.size shouldBe 2

      val cmap = map.get(ValueFactory.newString("C")).asMapValue()
      cmap.size shouldBe 3
      cmap.toMap.get(ValueFactory.newString("c")).asMapValue().size() shouldBe 1

      info(mapValue)
    }


  }
}
