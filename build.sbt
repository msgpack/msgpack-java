import ReleaseTransformations._

Global / onChangedBuildSource := ReloadOnSourceChanges

// For performance testing, ensure each test run one-by-one
Global / concurrentRestrictions := Seq(
  Tags.limit(Tags.Test, 1)
)

val buildSettings = Seq[Setting[_]](
  organization := "org.msgpack",
  organizationName := "MessagePack",
  organizationHomepage := Some(new URL("http://msgpack.org/")),
  description := "MessagePack for Java",
  scalaVersion := "2.12.13",
  Test / logBuffered := false,
  // msgpack-java should be a pure-java library, so remove Scala specific configurations
  autoScalaLibrary := false,
  crossPaths := false,
  // JVM options for building
  scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature"),
  Test / javaOptions ++= Seq("-ea"),
  javacOptions ++= Seq("-source", "1.7", "-target", "1.7"),
  Compile / compile / javacOptions ++= Seq("-encoding", "UTF-8", "-Xlint:unchecked", "-Xlint:deprecation"),
  // Use lenient validation mode when generating Javadoc (for Java8)
  doc / javacOptions := {
    val opts = Seq("-source", "1.7")
    if (scala.util.Properties.isJavaAtLeast("1.8")) {
      opts ++ Seq("-Xdoclint:none")
    } else {
      opts
    }
  },
  // Release settings
  releaseTagName := { (ThisBuild / version).value },
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommand("publishSigned"),
    releaseStepCommand("sonatypeBundleRelease"),
    setNextVersion,
    commitNextVersion,
    pushChanges
  ),
  // Add sonatype repository settings
  publishTo := sonatypePublishToBundle.value,
  // Find bugs
  findbugsReportType := Some(FindbugsReport.FancyHtml),
  findbugsReportPath := Some(crossTarget.value / "findbugs" / "report.html"),
  // Style check config: (sbt-jchekcstyle)
  jcheckStyleConfig := "facebook",
  // Run jcheckstyle both for main and test codes
  Compile / compile := ((Compile / compile) dependsOn (Compile / jcheckStyle)).value,
  Test / compile := ((Test / compile) dependsOn (Test / jcheckStyle)).value
)

val junitInterface = "com.novocode" % "junit-interface" % "0.11" % "test"

// Project settings
lazy val root = Project(id = "msgpack-java", base = file("."))
  .settings(
    buildSettings,
    // Do not publish the root project
    publishArtifact := false,
    publish := {},
    publishLocal := {},
    findbugs := {
      // do not run findbugs for the root project
    }
  )
  .aggregate(msgpackCore, msgpackJackson)

lazy val msgpackCore = Project(id = "msgpack-core", base = file("msgpack-core"))
  .enablePlugins(SbtOsgi)
  .settings(
    buildSettings,
    description := "Core library of the MessagePack for Java",
    OsgiKeys.bundleSymbolicName := "org.msgpack.msgpack-core",
    OsgiKeys.exportPackage := Seq(
      // TODO enumerate used packages automatically
      "org.msgpack.core",
      "org.msgpack.core.annotations",
      "org.msgpack.core.buffer",
      "org.msgpack.value",
      "org.msgpack.value.impl"
    ),
    libraryDependencies ++= Seq(
      // msgpack-core should have no external dependencies
      junitInterface,
      "org.scalatest"     %% "scalatest"    % "3.0.8"  % "test",
      "org.scalacheck"    %% "scalacheck"   % "1.14.0" % "test",
      "org.xerial"        %% "xerial-core"  % "3.6.0"  % "test",
      "org.msgpack"       % "msgpack"       % "0.6.12" % "test",
      "commons-codec"     % "commons-codec" % "1.12"   % "test",
      "com.typesafe.akka" %% "akka-actor"   % "2.5.23" % "test"
    )
  )

lazy val msgpackJackson =
  Project(id = "msgpack-jackson", base = file("msgpack-jackson"))
    .enablePlugins(SbtOsgi)
    .settings(
      buildSettings,
      name := "jackson-dataformat-msgpack",
      description := "Jackson extension that adds support for MessagePack",
      OsgiKeys.bundleSymbolicName := "org.msgpack.msgpack-jackson",
      OsgiKeys.exportPackage := Seq(
        "org.msgpack.jackson",
        "org.msgpack.jackson.dataformat"
      ),
      libraryDependencies ++= Seq(
        "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.5",
        junitInterface,
        "org.apache.commons" % "commons-math3" % "3.6.1" % "test"
      ),
      testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
    )
    .dependsOn(msgpackCore)
