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
import wvlet.airspec.AirSpec

class MessageBufferCopyTest extends AirSpec:
  private def byteBuffer(data: Array[Byte], direct: Boolean): ByteBuffer =
    if direct then
      val buffer = ByteBuffer.allocateDirect(data.length)
      buffer.put(data)
      buffer.flip()
      buffer
    else
      ByteBuffer.wrap(data)

  for
    directSource      <- Seq(false, true)
    readOnly          <- Seq(false, true)
    directDestination <- Seq(false, true)
    sourceOffset      <- Seq(0, 2)
    destinationOffset <- Seq(0, 3)
    length            <- Seq(0, 3)
  do
    test(
      s"copy directSource=$directSource readOnly=$readOnly directDestination=$directDestination sourceOffset=$sourceOffset destinationOffset=$destinationOffset length=$length"
    ) {
      val data         = Array.tabulate[Byte](12)(i => (10 + i).toByte)
      val sourceBuffer = byteBuffer(data, directSource)
      sourceBuffer.position(sourceOffset)
      val sourceSlice = sourceBuffer.slice()
      val source      =
        if readOnly then
          sourceSlice.asReadOnlyBuffer()
        else
          sourceSlice
      source.position(1)
      source.limit(6)
      source.mark()

      val destination = MessageBuffer.wrap(byteBuffer(Array.fill[Byte](12)(-1), directDestination))
      val destinationSlice = destination.slice(destinationOffset, 8)
      destinationSlice.putByteBuffer(2, source, length)

      val expected = Array.fill[Byte](12)(-1)
      Array.copy(data, sourceOffset + 1, expected, destinationOffset + 2, length)
      destination.toByteArray() shouldBe expected
      source.position() shouldBe 1 + length
      source.limit() shouldBe 6
      source.reset()
      source.position() shouldBe 1
      sourceBuffer.position() shouldBe sourceOffset
    }

  end for

end MessageBufferCopyTest
