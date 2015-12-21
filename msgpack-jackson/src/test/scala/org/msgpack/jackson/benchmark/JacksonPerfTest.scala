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
package org.msgpack.jackson.benchmark

import java.math.BigInteger

import com.fasterxml.jackson.databind.ObjectMapper
import org.msgpack.core.MessagePackSpec
import org.msgpack.jackson.dataformat.MessagePackDataformatTestBase.{Suit, NormalPojo}
import org.msgpack.jackson.dataformat.MessagePackFactory
import xerial.core.log.LogLevel.INFO

import scala.util.Random

object JacksonPerfTest
{
  def prepareDataset(n: Int): Seq[AnyRef] =
  {
    val r = new Random(0) // Use a fixed seed
    for (i <- 0 until n) yield {
      val pojo = new NormalPojo()
      pojo.i = r.nextInt()
      pojo.l = r.nextLong()
      pojo.f = r.nextFloat()
      pojo.d = r.nextDouble()
      pojo.setS(r.nextString(10))
      pojo.bool = r.nextBoolean()
      pojo.bi = BigInteger.valueOf(r.nextLong)
      r.nextInt(4) match {
        case 0 =>
          pojo.suit = Suit.SPADE
        case 1 =>
          pojo.suit = Suit.HEART
        case 2 =>
          pojo.suit = Suit.DIAMOND
        case 3 =>
          pojo.suit = Suit.CLUB
      }
      val arr = new Array[Byte](r.nextInt(20))
      r.nextBytes(arr)
      pojo.b = arr
      pojo
    }
  }
}

class JacksonPerfTest
        extends MessagePackSpec
{
  import JacksonPerfTest._

  "msgpack-jackson" should {

    "be faster than msgpack-json binding" in {
      val jsonMapper = new ObjectMapper()
      val msgpackMapper = new ObjectMapper(new MessagePackFactory())

      val inputData = prepareDataset(10).toIndexedSeq

      val N = 100
      val R = 10
      val t = time("benchmark", repeat = N, logLevel = INFO) {
        block("json", repeat = R) {
          var i = 0
          while (i < inputData.size) {
            jsonMapper.writeValueAsBytes(inputData(i))
            i += 1
          }
        }

        block("msgpack", repeat = R) {
          var i = 0
          while (i < inputData.size) {
            // TODO Use byte[] based API
            msgpackMapper.writeValueAsBytes(inputData(i))
            i += 1
          }
        }
      }

      t("msgpack").averageWithoutMinMax should be <= t("json").averageWithoutMinMax
    }
  }
}
