import xerial.sbt.Sonatype._

ThisBuild / sonatypeProfileName := "org.msgpack"
ThisBuild / homepage := Some(url("https://msgpack.org/"))
ThisBuild / licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/msgpack/msgpack-java"),
    "scm:git@github.com:msgpack/msgpack-java.git"
  )
)
ThisBuild / developers := List(
  Developer(id = "frsyuki", name = "Sadayuki Furuhashi", email = "frsyuki@users.sourceforge.jp", url = url("https://github.com/frsyuki")),
  Developer(id = "muga", name = "Muga Nishizawa", email = "muga.nishizawa@gmail.com", url = url("https://github.com/muga")),
  Developer(id = "oza", name = "Tsuyoshi Ozawa", email = "ozawa.tsuyoshi@gmail.com", url = url("https://github.com/oza")),
  Developer(id = "komamitsu", name = "Mitsunori Komatsu", email = "komamitsu@gmail.com", url = url("https://github.com/komamitsu")),
  Developer(id = "xerial", name = "Taro L. Saito", email = "leo@xerial.org", url = url("https://github.com/xerial"))
)
