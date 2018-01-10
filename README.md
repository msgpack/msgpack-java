MessagePack for Java 
=== 

[MessagePack](http://msgpack.org) is a binary serialization format. If you need a fast and compact alternative of JSON, MessagePack is your friend. For example, a small integer can be encoded in a single byte, and short strings only need a single byte prefix + the original byte array. MessagePack implementation is already available in various lanaguages (See also the list in http://msgpack.org) and works as a universal data format.

 * Message Pack specification: <https://github.com/msgpack/msgpack/blob/master/spec.md>

MessagePack v7 (or later) is a faster implementation of the previous version [v06](https://github.com/msgpack/msgpack-java/tree/v06), and
supports all of the message pack types, including [extension format](https://github.com/msgpack/msgpack/blob/master/spec.md#formats-ext).

[JavaDoc is available at javadoc.io](https://www.javadoc.io/doc/org.msgpack/msgpack-core).

## Quick Start

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.msgpack/msgpack-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.msgpack/msgpack-core/)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.msgpack/msgpack-core/badge.svg)](http://www.javadoc.io/doc/org.msgpack/msgpack-core)

For Maven users:
```
<dependency>
   <groupId>org.msgpack</groupId>
   <artifactId>msgpack-core</artifactId>
   <version>(version)</version>
</dependency>
```

For sbt users:
```
libraryDependencies += "org.msgpack" % "msgpack-core" % "(version)"
```

For gradle users:
```
repositories {
    mavenCentral()
}

dependencies {
    compile 'org.msgpack:msgpack-core:(version)'
}
```

- [Usage examples](msgpack-core/src/test/java/org/msgpack/core/example/MessagePackExample.java)

msgpack-java supports serialization and deserialization of Java objects through [jackson-databind](https://github.com/FasterXML/jackson-databind).
For details, see [msgpack-jackson/README.md](msgpack-jackson/README.md). The template-based serialization mechanism used in v06 is deprecated.

- [Release Notes](RELEASE_NOTES.md)

## For MessagePack Developers [![Travis CI](https://travis-ci.org/msgpack/msgpack-java.svg?branch=v07-develop)](https://travis-ci.org/msgpack/msgpack-java)

msgpack-java uses [sbt](http://www.scala-sbt.org/) for building the projects. For the basic usage of sbt, see:
 * [Building Java projects with sbt](http://xerial.org/blog/2014/03/24/sbt/)

Coding style
 * msgpack-java uses [the same coding style](https://github.com/airlift/codestyle) with Facebook Presto
  * [IntelliJ setting file](https://raw.githubusercontent.com/airlift/codestyle/master/IntelliJIdea14/Airlift.xml)

### Basic sbt commands
Enter the sbt console:
```
$ ./sbt
```

Here is a list of sbt commands for daily development:
```
> ~compile                                 # Compile source codes
> ~test:compile                            # Compile both source and test codes
> ~test                                    # Run tests upon source code change
> ~testOnly *MessagePackTest               # Run tests in the specified class
> ~testOnly *MessagePackTest -- -n prim    # Run the test tagged as "prim"
> project msgpack-core                     # Focus on a specific project
> package                                  # Create a jar file in the target folder of each project
> findbugs                                 # Produce findbugs report in target/findbugs
> jacoco:cover                             # Report the code coverage of tests to target/jacoco folder
> jcheckStyle                              # Run check style
> ;scalafmt;test:scalafmt;scalafmtSbt      # Reformat Scala codes
```

### Publishing

```
> publishLocal            # Install to local .ivy2 repository
> publishM2               # Install to local .m2 Maven repository
> publish                 # Publishing a snapshot version to the Sonatype repository

> release                 # Run the release procedure (set a new version, run tests, upload artifacts, then deploy to Sonatype)

# If you need to perform the individual release steps manually, use the following commands:
> publishSigned           # Publish GPG signed artifacts to the Sonatype repository
> sonatypeRelease         # Publish to the Maven Central (It will be synched within less than 4 hours)
```

For publishing to Maven central, msgpack-java uses [sbt-sonatype](https://github.com/xerial/sbt-sonatype) plugin. Set Sonatype account information (user name and password) in the global sbt settings. To protect your password, never include this file in your project.

___$HOME/.sbt/(sbt-version)/sonatype.sbt___

```
credentials += Credentials("Sonatype Nexus Repository Manager",
        "oss.sonatype.org",
        "(Sonatype user name)",
        "(Sonatype password)")
```

### Project Structure

```
msgpack-core                 # Contains packer/unpacker implementation that never uses third-party libraries
msgpack-jackson              # Contains jackson-dataformat-java implementation
```
