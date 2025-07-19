addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.12.2")
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.3.1")
// TODO: Fixes jacoco error:
// java.lang.NoClassDefFoundError: Could not initialize class org.jacoco.core.internal.flow.ClassProbesAdapter
//addSbtPlugin("com.github.sbt"   % "sbt-jacoco"      % "3.3.0")
addSbtPlugin("org.xerial.sbt"   % "sbt-jcheckstyle" % "0.2.1")
addSbtPlugin("com.github.sbt" % "sbt-osgi"        % "0.10.0")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"    % "2.5.5")
addSbtPlugin("com.github.sbt"     % "sbt-dynver"      % "5.1.1")

scalacOptions ++= Seq("-deprecation", "-feature")
