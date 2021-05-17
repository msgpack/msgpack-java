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

import wvlet.log.LogLevel
import wvlet.log.io.{TimeReport, Timer}

import java.io.ByteArrayOutputStream

object MessagePackSpec {
  def toHex(arr: Array[Byte]) = arr.map(x => f"$x%02x").mkString(" ")
  def createMessagePackData(f: MessagePacker => Unit): Array[Byte] = {
    val b      = new ByteArrayOutputStream()
    val packer = MessagePack.newDefaultPacker(b)
    f(packer)
    packer.close()
    b.toByteArray
  }
}

trait Benchmark extends Timer {
  private val numWarmUpRuns = 10

  override protected def time[A](blockName: String, logLevel: LogLevel = LogLevel.INFO, repeat: Int = 1, blockRepeat: Int = 1)(f: => A): TimeReport = {
    super.time(blockName, logLevel = LogLevel.INFO, repeat)(f)
  }

  override protected def block[A](name: String)(f: => A): TimeReport = {
    var i = 0
    while (i < numWarmUpRuns) {
      f
      i += 1
    }

    super.block(name)(f)
  }
}
