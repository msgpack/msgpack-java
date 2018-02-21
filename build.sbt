import ReleaseTransformations._

val buildSettings = Seq[Setting[_]](
  organization := "org.msgpack",
  organizationName := "MessagePack",
  organizationHomepage := Some(new URL("http://msgpack.org/")),
  description := "MessagePack for Java",
  scalaVersion := "2.12.4",
  logBuffered in Test := false,
  // msgpack-java should be a pure-java library, so remove Scala specific configurations
  autoScalaLibrary := false,
  crossPaths := false,
  // For performance testing, ensure each test run one-by-one
  concurrentRestrictions in Global := Seq(
    Tags.limit(Tags.Test, 1)
  ),
  // JVM options for building
  scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature"),
  javaOptions in Test ++= Seq("-ea"),
  javacOptions in (Compile, compile) ++= Seq("-encoding", "UTF-8", "-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.7", "-target", "1.7"),
  // Use lenient validation mode when generating Javadoc (for Java8)
  javacOptions in doc := {
    val opts = Seq("-source", "1.7")
    if (scala.util.Properties.isJavaAtLeast("1.8")) {
      opts ++ Seq("-Xdoclint:none")
    } else {
      opts
    }
  },
  // Release settings
  releaseTagName := { (version in ThisBuild).value },
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommand("publishSigned"),
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
  ),
  // Add sonatype repository settings
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),
  // Find bugs
  findbugsReportType := Some(FindbugsReport.FancyHtml),
  findbugsReportPath := Some(crossTarget.value / "findbugs" / "report.html"),
  // Style check config: (sbt-jchekcstyle)
  jcheckStyleConfig := "facebook",
  // Run jcheckstyle both for main and test codes
  (compile in Compile) := ((compile in Compile) dependsOn (jcheckStyle in Compile)).value,
  (compile in Test) := ((compile in Test) dependsOn (jcheckStyle in Test)).value
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
      "org.scalatest"     %% "scalatest"    % "3.0.3"  % "test",
      "org.scalacheck"    %% "scalacheck"   % "1.13.5" % "test",
      "org.xerial"        %% "xerial-core"  % "3.6.0"  % "test",
      "org.msgpack"       % "msgpack"       % "0.6.12" % "test",
      "commons-codec"     % "commons-codec" % "1.10"   % "test",
      "com.typesafe.akka" %% "akka-actor"   % "2.5.7"  % "test"
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
        "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.11.1",
        junitInterface,
        "org.apache.commons" % "commons-math3" % "3.6.1" % "test"
      ),
      testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
    )
    .dependsOn(msgpackCore)
