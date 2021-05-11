addSbtPlugin("com.github.sbt"   % "sbt-release"     % "1.0.15")
addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"    % "3.9.7")
addSbtPlugin("com.github.sbt"   % "sbt-pgp"         % "2.1.2")
// TODO: Fixes jacoco error:
// java.lang.NoClassDefFoundError: Could not initialize class org.jacoco.core.internal.flow.ClassProbesAdapter
//addSbtPlugin("com.github.sbt"   % "sbt-jacoco"      % "3.3.0")
addSbtPlugin("org.xerial.sbt"   % "sbt-jcheckstyle" % "0.2.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-osgi"        % "0.9.5")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"    % "2.4.2")

scalacOptions ++= Seq("-deprecation", "-feature")
