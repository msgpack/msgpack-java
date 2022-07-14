Global / onChangedBuildSource := ReloadOnSourceChanges

// For performance testing, ensure each test run one-by-one
Global / concurrentRestrictions := Seq(
  Tags.limit(Tags.Test, 1)
)

val AIRFRAME_VERSION = "22.7.2"

// Use dynamic snapshot version strings for non tagged versions
ThisBuild / dynverSonatypeSnapshots := true
// Use coursier friendly version separator
ThisBuild / dynverSeparator := "-"

val buildSettings = Seq[Setting[_]](
  organization := "org.msgpack",
  organizationName := "MessagePack",
  organizationHomepage := Some(new URL("http://msgpack.org/")),
  description := "MessagePack for Java",
  scalaVersion := "2.13.6",
  Test / logBuffered := false,
  // msgpack-java should be a pure-java library, so remove Scala specific configurations
  autoScalaLibrary := false,
  crossPaths := false,
  publishMavenStyle := true,
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
  // Add sonatype repository settings
  publishTo := sonatypePublishToBundle.value,
  // Style check config: (sbt-jchekcstyle)
  jcheckStyleConfig := "facebook",
  // Run jcheckstyle both for main and test codes
  Compile / compile := ((Compile / compile) dependsOn (Compile / jcheckStyle)).value,
  Test / compile := ((Test / compile) dependsOn (Test / jcheckStyle)).value
)

val junitInterface = "com.github.sbt" % "junit-interface" % "0.13.3" % "test"

// Project settings
lazy val root = Project(id = "msgpack-java", base = file("."))
  .settings(
    buildSettings,
    // Do not publish the root project
    publishArtifact := false,
    publish := {},
    publishLocal := {}
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
    testFrameworks += new TestFramework("wvlet.airspec.Framework"),
    Test / javaOptions ++= Seq(
      // --add-opens is not available in JDK8
      "-XX:+IgnoreUnrecognizedVMOptions",
      "--add-opens=java.base/java.nio=ALL-UNNAMED",
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
    ),
    Test / fork := true,
    libraryDependencies ++= Seq(
      // msgpack-core should have no external dependencies
      junitInterface,
      "org.wvlet.airframe" %% "airframe-json" % AIRFRAME_VERSION % "test",
      "org.wvlet.airframe" %% "airspec"       % AIRFRAME_VERSION % "test",
      // Add property testing support with forAll methods
      "org.scalacheck" %% "scalacheck" % "1.16.0" % "test",
      // For performance comparison with msgpack v6
      "org.msgpack" % "msgpack" % "0.6.12" % "test",
      // For integration test with Akka
      "com.typesafe.akka"      %% "akka-actor"              % "2.6.19" % "test",
      "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.0"  % "test"
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
        "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.3",
        junitInterface,
        "org.apache.commons" % "commons-math3" % "3.6.1" % "test"
      ),
      testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
    )
    .dependsOn(msgpackCore)
