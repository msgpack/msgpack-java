package org.msgpack.core

import org.scalatest._
import xerial.core.log.{LogLevel, Logger}
import xerial.core.util.{TimeReport, Timer}
import scala.language.implicitConversions
import org.scalatest.prop.PropertyChecks
import java.io.ByteArrayOutputStream

trait MessagePackSpec
  extends WordSpec
  with Matchers
  with GivenWhenThen
  with OptionValues
  with BeforeAndAfter
  with PropertyChecks
  with Benchmark
  with Logger {

  implicit def toTag(s:String) : Tag = Tag(s)

  def toHex(arr:Array[Byte]) = arr.map(x => f"$x%02x").mkString(" ")


  def createMessagePackData(f: MessagePacker => Unit) : Array[Byte] = {
    val b = new ByteArrayOutputStream()
    val packer = new MessagePacker(b)
    f(packer)
    packer.close()
    b.toByteArray
  }




}


trait Benchmark extends Timer {

  val numWarmUpRuns = 10


  override protected def time[A](blockName: String, logLevel: LogLevel, repeat: Int)(f: => A): TimeReport = {
    super.time(blockName, logLevel=LogLevel.INFO, repeat)(f)
  }

  override protected def block[A](name: String, repeat: Int)(f: => A): TimeReport = {
    var i = 0
    while(i < numWarmUpRuns) {
      f
      i += 1
    }

    super.block(name, repeat)(f)

  }

}