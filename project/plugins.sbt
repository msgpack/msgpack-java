addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.21")
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.2.1")
// TODO: Fixes jacoco error:
// java.lang.NoClassDefFoundError: Could not initialize class org.jacoco.core.internal.flow.ClassProbesAdapter
//addSbtPlugin("com.github.sbt"   % "sbt-jacoco"      % "3.3.0")
addSbtPlugin("org.xerial.sbt"   % "sbt-jcheckstyle" % "0.2.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-osgi"        % "0.9.6")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"    % "2.5.0")
addSbtPlugin("com.github.sbt"     % "sbt-dynver"      % "5.0.1")

scalacOptions ++= Seq("-deprecation", "-feature")
