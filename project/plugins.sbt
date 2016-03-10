
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.3.0")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6")

addSbtPlugin("org.xerial.sbt" % "sbt-jcheckstyle" % "0.1.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-osgi" % "0.7.0")

scalacOptions ++= Seq("-deprecation", "-feature")
