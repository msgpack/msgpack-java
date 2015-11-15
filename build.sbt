import de.johoop.findbugs4sbt.ReportType

organization := "org.msgpack"
organizationName := "MessagePack"
organizationHomepage := Some(new URL("http://msgpack.org/"))
description := "MessagePack for Java"
scalaVersion in Global := "2.11.7"
logBuffered in Test := false
autoScalaLibrary := false
crossPaths := false

// For performance testing, ensure each test run one-by-one
concurrentRestrictions in Global := Seq(
  Tags.limit(Tags.Test, 1)
)

incOptions := incOptions.value.withNameHashing(true)

// JVM options for building
scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-target:jvm-1.6", "-feature")

javaOptions in Test ++= Seq("-ea")
javacOptions in (Compile, compile) ++= Seq("-encoding", "UTF-8", "-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.6", "-target", "1.6")

// Use lenient validation mode when generating Javadoc (for Java8)
javacOptions in doc := {
  val opts = Seq("-source", "1.6")
  if (scala.util.Properties.isJavaAtLeast("1.8")) {
    opts ++ Seq("-Xdoclint:none")
  }
  else {
    opts
  }
}

val junitInterface = "com.novocode" % "junit-interface" % "0.11" % "test"

// Project settings
lazy val root = Project(id = "msgpack-java", base = file("."))
        .settings(
          // Do not publish the root project
          publishArtifact := false,
          publish := {},
          publishLocal := {},
          findbugs := {
            // do not run findbugs for the root project
          }
        ).aggregate(msgpackCore, msgpackJackson)

lazy val msgpackCore = Project(id = "msgpack-core", base = file("msgpack-core"))
        .settings(
          description := "Core library of the MessagePack for Java",
          libraryDependencies ++= Seq(
            // msgpack-core should have no external dependencies
            junitInterface,
            "org.scalatest" %% "scalatest" % "2.2.4" % "test",
            "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
            "org.xerial" % "xerial-core" % "3.3.6" % "test",
            "org.msgpack" % "msgpack" % "0.6.11" % "test",
            "commons-codec" % "commons-codec" % "1.10" % "test",
            "com.typesafe.akka" %% "akka-actor" % "2.3.9" % "test"
          )
        )

lazy val msgpackJackson = Project(id = "msgpack-jackson", base = file("msgpack-jackson"))
        .settings(
          name := "jackson-dataformat-msgpack",
          description := "Jackson extension that adds support for MessagePack",
          libraryDependencies ++= Seq(
            "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3",
            junitInterface,
            "org.apache.commons" % "commons-math3" % "3.4.1" % "test"
          ),
          testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
        ).dependsOn(msgpackCore)


// Release settings

import ReleaseTransformations._

releaseTagName := { (version in ThisBuild).value }
releaseProcess := Seq[ReleaseStep](
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
      )

// Jacoco code coverage report
jacoco.settings
parallelExecution in jacoco.Config := false

// Find bugs
findbugsSettings
findbugsReportType := Some(ReportType.FancyHtml)
findbugsReportPath := Some(crossTarget.value / "findbugs" / "report.html")

// Style check config: (sbt-jchekcstyle)
jcheckStyleConfig := "facebook"

// Run jcheckstyle both for main and test codes
compile <<= (compile in Compile) dependsOn (jcheckStyle in Compile)
compile <<= (compile in Test) dependsOn (jcheckStyle in Test)
