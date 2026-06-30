import scala.language.implicitConversions

Global / onChangedBuildSource := ReloadOnSourceChanges

// For performance testing, ensure each test run one-by-one
Global / concurrentRestrictions := Seq(Tags.limit(Tags.Test, 1))

val AIRFRAME_VERSION = "2026.1.7"

// Use dynamic snapshot version strings for non tagged versions
ThisBuild / dynverSonatypeSnapshots := true
// Use coursier friendly version separator
ThisBuild / dynverSeparator := "-"

// Publishing metadata
ThisBuild / homepage := Some(url("https://msgpack.org/"))
ThisBuild / licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / scmInfo  :=
  Some(
    ScmInfo(
      url("https://github.com/msgpack/msgpack-java"),
      "scm:git@github.com:msgpack/msgpack-java.git"
    )
  )

ThisBuild / developers :=
  List(
    Developer(
      id = "frsyuki",
      name = "Sadayuki Furuhashi",
      email = "frsyuki@users.sourceforge.jp",
      url = url("https://github.com/frsyuki")
    ),
    Developer(
      id = "muga",
      name = "Muga Nishizawa",
      email = "muga.nishizawa@gmail.com",
      url = url("https://github.com/muga")
    ),
    Developer(
      id = "oza",
      name = "Tsuyoshi Ozawa",
      email = "ozawa.tsuyoshi@gmail.com",
      url = url("https://github.com/oza")
    ),
    Developer(
      id = "komamitsu",
      name = "Mitsunori Komatsu",
      email = "komamitsu@gmail.com",
      url = url("https://github.com/komamitsu")
    ),
    Developer(
      id = "xerial",
      name = "Taro L. Saito",
      email = "leo@xerial.org",
      url = url("https://github.com/xerial")
    )
  )

val buildSettings = Seq[Setting[?]](
  organization         := "org.msgpack",
  organizationName     := "MessagePack",
  organizationHomepage := Some(url("http://msgpack.org/")),
  description          := "MessagePack for Java",
  scalaVersion         := "3.7.1",
  Test / logBuffered   := false,
  // msgpack-java should be a pure-java library, so remove Scala specific configurations
  autoScalaLibrary  := false,
  crossPaths        := false,
  publishMavenStyle := true,
  // JVM options for building
  scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature"),
  Test / javaOptions ++= Seq("-ea"),
  // sbt 2 itself requires JDK 17+ to run, but each CI lane still needs to compile and
  // test against its own target JDK (e.g. 8) to faithfully reproduce runtime behavior:
  // javac resolves API calls against whichever JDK actually runs it (-source/-target
  // only constrain language level and bytecode version, not API resolution), so e.g.
  // compiling on JDK9+ can bind to covariant overloads like ByteBuffer.flip():
  // ByteBuffer that don't exist on a real JDK8 at runtime. When TEST_JAVA_HOME is set,
  // fork both compilation and test execution onto that JDK; otherwise use the JDK
  // running sbt, as before.
  javaHome    := sys.env.get("TEST_JAVA_HOME").map(file),
  Test / fork := true,
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  Compile / compile / javacOptions ++=
    Seq("-encoding", "UTF-8", "-Xlint:unchecked", "-Xlint:deprecation"),
  // Use lenient validation mode when generating Javadoc (for Java8)
  doc / javacOptions := {
    val opts = Seq("-source", "1.8")
    if (scala.util.Properties.isJavaAtLeast("1.8")) {
      opts ++ Seq("-Xdoclint:none")
    } else {
      opts
    }
  },
  // Add sonatype repository settings
  publishTo := {
    val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
    if (isSnapshot.value)
      Some("central-snapshots" at centralSnapshots)
    else
      localStaging.value
  },
  // Style check config: (sbt-jchekcstyle)
  jcheckStyleConfig := "facebook",
  // Run jcheckstyle both for main and test codes
  Compile / compile := Def.uncached((Compile / compile).dependsOn(Compile / jcheckStyle).value),
  Test / compile    := Def.uncached((Test / compile).dependsOn(Test / jcheckStyle).value)
)

val junitJupiter = "org.junit.jupiter" % "junit-jupiter"        % "5.14.4" % "test"
val junitVintage = "org.junit.vintage" % "junit-vintage-engine" % "5.14.4" % "test"

// Project settings
lazy val root = Project(id = "msgpack-java", base = file("."))
  .settings(
    buildSettings,
    // Do not publish the root project
    publishArtifact := false,
    publish         := {},
    publishLocal    := {}
  )
  .aggregate(msgpackCore, msgpackJackson)

lazy val msgpackCore = Project(id = "msgpack-core", base = file("msgpack-core"))
  .enablePlugins(SbtOsgi)
  .settings(
    buildSettings,
    description                 := "Core library of the MessagePack for Java",
    OsgiKeys.bundleSymbolicName := "org.msgpack.msgpack-core",
    OsgiKeys.exportPackage      :=
      Seq(
        // TODO enumerate used packages automatically
        "org.msgpack.core",
        "org.msgpack.core.annotations",
        "org.msgpack.core.buffer",
        "org.msgpack.value",
        "org.msgpack.value.impl"
      ),
    OsgiKeys.importPackage := Seq("!android.os", "!sun.*"),
    testFrameworks += new TestFramework("wvlet.airspec.Framework"),
    Test / javaOptions ++=
      Seq(
        // --add-opens is not available in JDK8
        "-XX:+IgnoreUnrecognizedVMOptions",
        "--add-opens=java.base/java.nio=ALL-UNNAMED",
        "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
      ),
    libraryDependencies ++=
      Seq(
        // msgpack-core should have no external dependencies
        junitJupiter,
        junitVintage,
        "org.wvlet.airframe" %% "airframe-json" % AIRFRAME_VERSION % "test",
        "org.wvlet.airframe" %% "airspec"       % AIRFRAME_VERSION % "test",
        // Add property testing support with forAll methods
        "org.scalacheck" %% "scalacheck" % "1.19.0" % "test",
        // For performance comparison with msgpack v6
        "org.msgpack" % "msgpack" % "0.6.12" % "test",
        // For integration test with Akka
        "com.typesafe.akka"      %% "akka-actor"              % "2.6.20" % "test",
        "org.scala-lang.modules" %% "scala-collection-compat" % "2.14.0" % "test"
      )
  )

lazy val msgpackJackson = Project(id = "msgpack-jackson", base = file("msgpack-jackson"))
  .enablePlugins(SbtOsgi)
  .settings(
    buildSettings,
    name                        := "jackson-dataformat-msgpack",
    description                 := "Jackson extension that adds support for MessagePack",
    OsgiKeys.bundleSymbolicName := "org.msgpack.msgpack-jackson",
    OsgiKeys.exportPackage      := Seq("org.msgpack.jackson", "org.msgpack.jackson.dataformat"),
    libraryDependencies ++=
      Seq(
        "com.fasterxml.jackson.core" % "jackson-databind" % "2.20.0",
        junitJupiter,
        junitVintage,
        "org.apache.commons" % "commons-math3" % "3.6.1" % "test"
      ),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
  )
  .dependsOn(msgpackCore)
