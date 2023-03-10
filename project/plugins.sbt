addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.18")
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.2.1")
// TODO: Fixes jacoco error:
// java.lang.NoClassDefFoundError: Could not initialize class org.jacoco.core.internal.flow.ClassProbesAdapter
//addSbtPlugin("com.github.sbt"   % "sbt-jacoco"      % "3.3.0")
addSbtPlugin("org.xerial.sbt"   % "sbt-jcheckstyle" % "0.2.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-osgi"        % "0.9.6")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"    % "2.5.0")
addSbtPlugin("com.dwijnand"     % "sbt-dynver"      % "4.1.1")

scalacOptions ++= Seq("-deprecation", "-feature")
