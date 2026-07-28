addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")
// TODO: Fixes jacoco error:
// java.lang.NoClassDefFoundError: Could not initialize class org.jacoco.core.internal.flow.ClassProbesAdapter
//addSbtPlugin("com.github.sbt"   % "sbt-jacoco"      % "3.3.0")
addSbtPlugin("org.xerial.sbt" % "sbt-jcheckstyle" % "0.3.0")
addSbtPlugin("com.github.sbt" % "sbt-osgi"        % "0.11.0-RC1")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"    % "2.6.2")
addSbtPlugin("com.github.sbt" % "sbt-dynver"      % "5.1.1")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"     % "0.4.8")
// Runs JUnit 5 (Jupiter) tests from sbt; without this, JUnit 5 tests are silently skipped.
// Pinned to 0.15.x: 0.17+ upgrades to JUnit 6, which requires Java 17 and would break
// running msgpack-jackson2 tests on JDK 8.
addSbtPlugin("com.github.sbt.junit" % "sbt-jupiter-interface" % "0.15.2")

scalacOptions ++= Seq("-deprecation", "-feature")
