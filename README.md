MessagePack for Java 
=== 

[MessagePack](http://msgpack.org) is an efficient binary serialization format. It lets you exchange data among multiple languages like JSON. But it's faster and smaller. Small integers are encoded into a single byte, and typical short strings require only one extra byte in addition to the strings themselves.

 * Message Pack specification: <https://github.com/msgpack/msgpack/blob/master/spec.md>

MessagePack v7 (0.7.x) is a faster implementation of the previous version [v06](https://github.com/msgpack/msgpack-java/tree/v06), and supports all of the message pack types, including [extended format](https://github.com/msgpack/msgpack/blob/master/spec.md#formats-ext).

## Quick Start

For Maven users:
```
<dependency>
   <groupId>org.msgpack</groupId>
   <artifactId>msgpack-core</artifactId>
   <version>0.7.0-M5</version>
</dependency>
```

For sbt users:
```
libraryDependencies += "org.msgpack" % "msgpack-core" % "0.7.0-M5"
```

If you want to use MessagePack through [jackson-databind](https://github.com/FasterXML/jackson-databind), you can use *jackson-dataformat-msgpack*. Please read [README](msgpack-jackson/README.md) to know how to use it.
- [Usage examples](msgpack-core/src/main/java/org/msgpack/core/example/MessagePackExample.java)
- TODO: jackson-databind-msgpack usage

## For Developers [![Travis CI](https://travis-ci.org/msgpack/msgpack-java.svg?branch=v07-develop)](https://travis-ci.org/msgpack/msgpack-java)

msgpack-java uses [sbt](http://www.scala-sbt.org/) for building the projects. For the basic usage of sbt, see:
 * [Building Java projects with sbt](http://xerial.org/blog/2014/03/24/sbt/)

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
> ~test-only *MessagePackTest              # Run tests in the specified class
> ~test-only *MessagePackTest -- -n prim   # Run the test tagged as "prim"
> project msgpack-core                     # Focus on a specific project
> package                                  # Create a jar file in the target folder of each project
> findbugs                                 # Produce findbugs report in target/findbugs
> jacoco:cover                             # Report the code coverage of tests to target/jacoco folder
```

### Publishing

```
> publishLocal            # Install to local .ivy2 repository
> publishM2               # Install to local .m2 Maven repository
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
