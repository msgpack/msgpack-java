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

import java.io.ByteArrayOutputStream

import org.scalatest._
import org.scalatest.prop.PropertyChecks
import xerial.core.log.{LogLevel, Logger}
import xerial.core.util.{TimeReport, Timer}

import scala.language.implicitConversions

trait MessagePackSpec extends WordSpec with Matchers with GivenWhenThen with OptionValues with BeforeAndAfter with PropertyChecks with Benchmark with Logger {

  implicit def toTag(s: String): Tag = Tag(s)

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

  val numWarmUpRuns = 10

  override protected def time[A](blockName: String, logLevel: LogLevel, repeat: Int)(f: => A): TimeReport = {
    super.time(blockName, logLevel = LogLevel.INFO, repeat)(f)
  }

  override protected def block[A](name: String, repeat: Int)(f: => A): TimeReport = {
    var i = 0
    while (i < numWarmUpRuns) {
      f
      i += 1
    }

    super.block(name, repeat)(f)
  }
}
