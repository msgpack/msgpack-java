addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")
// TODO: Fixes jacoco error:
// java.lang.NoClassDefFoundError: Could not initialize class org.jacoco.core.internal.flow.ClassProbesAdapter
//addSbtPlugin("com.github.sbt"   % "sbt-jacoco"      % "3.3.0")
addSbtPlugin("org.xerial.sbt" % "sbt-jcheckstyle" % "0.3.0")
addSbtPlugin("com.github.sbt" % "sbt-osgi"        % "0.11.0-RC1")
addSbtPlugin("org.scalameta"  % "sbt-scalafmt"    % "2.6.2")
addSbtPlugin("com.github.sbt" % "sbt-dynver"      % "5.1.1")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"     % "0.4.8")

scalacOptions ++= Seq("-deprecation", "-feature")
