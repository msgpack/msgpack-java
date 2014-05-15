/*
 * Copyright 2012 Taro L. Saito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import sbt._
import Keys._
import xerial.sbt.Sonatype._

object Build extends Build {

  val SCALA_VERSION = "2.10.3"

  lazy val buildSettings = Defaults.defaultSettings ++ Seq[Setting[_]](
    organization := "org.msgpack",
    organizationName := "MessagePack",
    organizationHomepage := Some(new URL("http://msgpack.org/")),
    description := "MessagePack for Java",
    scalaVersion in Global := SCALA_VERSION,
    sbtVersion in Global := "0.13.2-M1",
    logBuffered in Test := false,
    //parallelExecution in Test := false,
    autoScalaLibrary := false,
    crossPaths := false,
    // Since sbt-0.13.2
    incOptions := incOptions.value.withNameHashing(true),
    //resolvers += Resolver.mavenLocal,
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-target:jvm-1.6", "-feature"),
    javacOptions in (Compile, compile) ++= Seq("-encoding", "UTF-8", "-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.6", "-target", "1.6"),
    pomExtra := {
      <url>http://msgpack.org/</url>
        <licenses>
          <license>
            <name>Apache 2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
          </license>
        </licenses>
        <scm>
          <connection>scm:git:github.com/msgpack/msgpack-java.git</connection>
          <developerConnection>scm:git:git@github.com:msgpack/msgpack-java.git</developerConnection>
          <url>github.com/msgpack/msgpack-java.git</url>
        </scm>
        <properties>
          <scala.version>{SCALA_VERSION}</scala.version>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        </properties>
    }
  )

  import Dependencies._


  lazy val root = Project(
    id = "msgpack-java",
    base = file("."),
    settings = buildSettings ++ sonatypeSettings
  ) aggregate(msgpackCore, msgpackValue)


  lazy val msgpackCore = Project(
    id = "msgpack-core",
    base = file("msgpack-core"),
    settings = buildSettings ++ Seq(
      description := "Core library of the MessagePack for Java",
      libraryDependencies ++= testLib
    )
  )

  lazy val msgpackValue = Project(
    id = "msgpack-value",
    base = file("msgpack-value"),
    settings = buildSettings ++ Seq(
      description := "Value reader/writer library of the MessagePack for Java",
        libraryDependencies ++= testLib
    )
  ) dependsOn(msgpackCore)


  object Dependencies {

    val testLib = Seq(
      "org.scalatest" % "scalatest_2.10" % "2.1.0-RC2" % "test",
      "org.scalacheck" % "scalacheck_2.10" % "1.11.3" % "test",
      "org.xerial" % "xerial-core" % "3.2.3" % "test",
      "org.msgpack" % "msgpack" % "0.6.9" % "test",
      "com.novocode" % "junit-interface" % "0.10" % "test"
    )
  }

}








