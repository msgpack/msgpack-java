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


import de.johoop.findbugs4sbt.ReportType
import sbt._
import Keys._
import de.johoop.findbugs4sbt.FindBugs._
import de.johoop.jacoco4sbt._
import JacocoPlugin._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.ReleaseStep
import scala.util.Properties

object Build extends Build {

  val SCALA_VERSION = "2.11.6"

  lazy val buildSettings = Defaults.coreDefaultSettings ++
    releaseSettings ++
    findbugsSettings ++
    jacoco.settings ++
    Seq[Setting[_]](
      organization := "org.msgpack",
      organizationName := "MessagePack",
      organizationHomepage := Some(new URL("http://msgpack.org/")),
      description := "MessagePack for Java",
      scalaVersion in Global := SCALA_VERSION,
      logBuffered in Test := false,
      //parallelExecution in Test := false,
      autoScalaLibrary := false,
      crossPaths := false,
      concurrentRestrictions in Global := Seq(
        Tags.limit(Tags.Test, 1)
      ),
      ReleaseKeys.tagName <<= (version in ThisBuild) map (v => v),
      ReleaseKeys.releaseProcess := Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        ReleaseStep(action = Command.process("publishSigned", _)),
        setNextVersion,
        commitNextVersion,
        ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
        pushChanges
      ),
      parallelExecution in jacoco.Config := false,
      // Since sbt-0.13.2
      incOptions := incOptions.value.withNameHashing(true),
      //resolvers += Resolver.mavenLocal,
      scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-target:jvm-1.6", "-feature"),
      javaOptions in Test ++= Seq("-ea"),
      javacOptions in (Compile, compile) ++= Seq("-encoding", "UTF-8", "-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.6", "-target", "1.6"),
      javacOptions in doc := {
        val opts = Seq("-source", "1.6")
        if (Properties.isJavaAtLeast("1.8"))
          opts ++ Seq("-Xdoclint:none")
        else
          opts
      },
      findbugsReportType := Some(ReportType.FancyHtml),
      findbugsReportPath := Some(crossTarget.value / "findbugs" / "report.html")
    )

  import Dependencies._

  lazy val root = Project(
    id = "msgpack-java",
    base = file("."),
    settings = buildSettings ++ Seq(
      // Do not publish the root project
      publishArtifact := false,
      publish := {},
      publishLocal := {},
      findbugs := {
        // do not run findbugs for the root project
      }
    )
  ).aggregate(msgpackCore, msgpackJackson)

  lazy val msgpackCore = Project(
    id = "msgpack-core",
    base = file("msgpack-core"),
    settings = buildSettings ++ Seq(
      description := "Core library of the MessagePack for Java",
      libraryDependencies ++= testLib
    )
  )

  lazy val msgpackJackson = Project(
    id = "msgpack-jackson",
    base = file("msgpack-jackson"),
    settings = buildSettings ++ Seq(
      name := "jackson-dataformat-msgpack",
      description := "Jackson extension that adds support for MessagePack",
      libraryDependencies ++= jacksonLib,
      testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
    )
  ).dependsOn(msgpackCore)

  object Dependencies {

    val junitInterface = "com.novocode" % "junit-interface" % "0.11" % "test"

    val testLib = Seq(
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
      "org.xerial" % "xerial-core" % "3.3.6" % "test",
      "org.msgpack" % "msgpack" % "0.6.11" % "test",
      junitInterface,
      "commons-codec" % "commons-codec" % "1.10" % "test",
      "com.typesafe.akka" %% "akka-actor"% "2.3.9" % "test"
    )

    val jacksonLib = Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4",
      junitInterface,
      "org.apache.commons" % "commons-math3" % "3.4.1" % "test"
    )
  }

}








