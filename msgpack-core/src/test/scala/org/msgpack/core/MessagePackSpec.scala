package org.msgpack.core

import org.scalatest._
import xerial.core.log.Logger
import xerial.core.util.Timer
import scala.language.implicitConversions

trait MessagePackSpec extends WordSpec with Matchers with GivenWhenThen with OptionValues with BeforeAndAfter with Timer with Logger {

  implicit def toTag(s:String) : Tag = Tag(s)

  def toHex(arr:Array[Byte]) = arr.map(x => f"$x%02x").mkString(" ")

}
