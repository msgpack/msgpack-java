# Release Notes

The latest release notes will be available from the [GitHub release page](https://github.com/msgpack/msgpack-java/releases)

## 0.9.3

This version supports JDK17 [#660](http://github.com/msgpack/msgpack-java/pull/660).

Important: If you need to use DirectByteBuffer (raw memory access) in JDK17 or later, specify two JVM options to allow accessing
native memory:
```
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
```
Internal updates:

* Use SPDX-ID in license name [#653](http://github.com/msgpack/msgpack-java/pull/653)
* Update airframe-json, airspec to 22.6.4 [#659](http://github.com/msgpack/msgpack-java/pull/659)
* Update akka-actor to 2.6.19 [#647](http://github.com/msgpack/msgpack-java/pull/647)

## 0.9.2

Internal updates:

* Update jackson-databind to 2.13.3 [#650](http://github.com/msgpack/msgpack-java/pull/650)
* Update akka-actor to 2.6.19 [#631](http://github.com/msgpack/msgpack-java/pull/631)
* Update airframe-json, airspec to 22.6.1 [#649](http://github.com/msgpack/msgpack-java/pull/649)
* Update scalacheck to 1.16.0 [#636](http://github.com/msgpack/msgpack-java/pull/636)
* Update scala-collection-compat to 2.7.0 [#632](http://github.com/msgpack/msgpack-java/pull/632)
* Update sbt-sonatype to 3.9.13 [#644](http://github.com/msgpack/msgpack-java/pull/644)
* Update airframe-json, airspec to 22.5.0 [#643](http://github.com/msgpack/msgpack-java/pull/643)
* Update sbt to 1.6.2 [#630](http://github.com/msgpack/msgpack-java/pull/630)

## 0.9.1

Bug fixes and improvements:

- Keep consistent read size after closing MessageUnpacker (#621) @okumin
- Fixed examples relative link in README (#622) @zbuster05
- Add an ObjectMapper shorthand @cyberdelia (#620)
- Specify the bufferSize of the ArrayBufferOutput (#597) @szh

Internal updates:

- Update akka-actor to 2.6.18 (#614) @Scala Steward
- Update airframe-json, airspec to 22.2.0 (#626) @Scala Steward
- Update junit-interface to 0.13.3 (#617) @Scala Steward
- Update sbt-scalafmt to 2.4.6 (#616) @Scala Steward
- Upgrade sbt to 1.5.6 (#610) @Taro L. Saito
- Update scala-collection-compat to 2.6.0 (#604) @Scala Steward

Known issues:
- Unpack method doesn't work in JDK17 https://github.com/msgpack/msgpack-java/issues/600

## 0.9.0

This version support reading and writing [Timestamp values](https://github.com/msgpack/msgpack/blob/master/spec.md#timestamp-extension-type).
Packer and unpacker interfaces added pack/unpackTimestamp methods.

Timestamp value in MessagePack is an extension type value whose code is -1. You can read TimestampValue object with MessgageUnapcker.unpackValue method. 
If you are using low-level unpack methods (e.g., unpackInt, unpackExtension, etc.),
you need to read unpackExtensionHeader first, and if extHeader.isTimestampType() is true, call unpackTimestamp(extHeader).

Timestamp values are represented with [java.time.Instant](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Instant.html) objects.
You can extract the unixtime value with Instant.getEpochSecond(), unixtime with milliseconds resolution with Instant.toEpochMilli(), and nano-resolution time with Instant.getNano(). 

As TimestampValue is just a sub class of ExtensionValue, no change requierd in your code that are traversing MessagePack data with MessageUnpacker.unpackValue method.

* Added Timestamp support [#565](http://github.com/msgpack/msgpack-java/pull/565) and low-level APIs [#580](https://github.com/msgpack/msgpack-java/pull/580) for
reading timestamp values.

Dependency updates:
* Update jackson-databind to 2.10.5.1 [#559](http://github.com/msgpack/msgpack-java/pull/559)

Internal updates:
* Update akka-actor to 2.6.14 [#579](http://github.com/msgpack/msgpack-java/pull/579)
* Fix for Scala 2.13 syntax [#577](http://github.com/msgpack/msgpack-java/pull/577)
* Update airframe-json, airspec to 21.6.0 [#576](http://github.com/msgpack/msgpack-java/pull/576)
* Update scala-library to 2.13.6 [#568](http://github.com/msgpack/msgpack-java/pull/568)
* Update sbt to 1.5.3 [#575](http://github.com/msgpack/msgpack-java/pull/575)

## 0.8.24

* Rebuild with JDK8 for Android compatibility [#567](https://github.com/msgpack/msgpack-java/pull/567)

## 0.8.23

* Produce stable map values [#548](https://github.com/msgpack/msgpack-java/pull/548)
* Fixes #544: Fix a bug in reading EXT32 of 2GB size [#545](https://github.com/msgpack/msgpack-java/pull/545)
* Add a warning note for the usage of MessageUnpacker.readPayloadAsReference [#546](https://github.com/msgpack/msgpack-java/pull/546)

Intenral changes:
* Add a script for releasing a new version of msgpack-java at CI
* Publish a snapshot version for every main branch commit [#556](https://github.com/msgpack/msgpack-java/pull/556)
* Use dynamic versioning with Git tags v0.x.y format [#555](https://github.com/msgpack/msgpack-java/pull/555)
* Update ScalaTest and ScalaCheck versions [#554](https://github.com/msgpack/msgpack-java/pull/554)
* Remove findbugs [#553](https://github.com/msgpack/msgpack-java/pull/553)
* Update build settings to use latest version of sbt and plugins [#552](https://github.com/msgpack/msgpack-java/pull/552)
* Run GitHub Actions for develop and main branches [#551](https://github.com/msgpack/msgpack-java/pull/551)
* Remove Travis build [#550](https://github.com/msgpack/msgpack-java/pull/550)

## 0.8.22
 * Support extension type key in Map [#535](https://github.com/msgpack/msgpack-java/pull/535)
 * Remove addTargetClass() and addTargetTypeReference() from ExtensionTypeCustomDeserializers [#539](https://github.com/msgpack/msgpack-java/pull/539)
 * Fix a bug BigDecimal serializaion fails [#540](https://github.com/msgpack/msgpack-java/pull/540)

## 0.8.21
 * Fix indexing bug in ValueFactory [#525](https://github.com/msgpack/msgpack-java/pull/525)
 * Support numeric types in MessagePackParser.getText() [#527](https://github.com/msgpack/msgpack-java/pull/527)
 * Use jackson-databind 2.10.5 for security vulnerability [#528](https://github.com/msgpack/msgpack-java/pull/528)
 * (internal) Ensure building msgpack-java for Java 7 target [#523](https://github.com/msgpack/msgpack-java/pull/523)

## 0.8.20
 * Rebuild 0.8.19 with JDK8 

## 0.8.19
 * Support JDK11
 * msgpack-jackson: Fixes [#515](https://github.com/msgpack/msgpack-java/pull/515)

## 0.8.18
 * (internal) Update sbt related dependencies [#507](https://github.com/msgpack/msgpack-java/pull/507)
 * Use jackson-databind 2.9.9.3 for security vulnerability [#511](https://github.com/msgpack/msgpack-java/pull/511)

## 0.8.17
 * Fix OOM exception for invalid msgpack messages [#500](https://github.com/msgpack/msgpack-java/pull/500)
 * Use jackson-databind 2.9.9 for security vulnerability [#505](https://github.com/msgpack/msgpack-java/pull/505)

## 0.8.16
 * Fix NPE at ObjectMapper#copy with MessagePackFactory when ExtensionTypeCustomDeserializers isn't set [#471](https://github.com/msgpack/msgpack-java/pull/471)

## 0.8.15
 * Suppress a warning in ValueFactory [#457](https://github.com/msgpack/msgpack-java/pull/457)
 * Add MessagePacker#clear() method to clear position [#459](https://github.com/msgpack/msgpack-java/pull/459)
 * Support ObjectMapper#copy with MessagePackFactory [#454](https://github.com/msgpack/msgpack-java/pull/454)
 * Use jackson-databind 2.8.11.1 for security vulnerability [#467](https://github.com/msgpack/msgpack-java/pull/467)
 * (internal) Remove "-target:jvm-1.7" from scalacOptions [#456](https://github.com/msgpack/msgpack-java/pull/456)
 * (internal) Replace sbt `test-only` command with `testOnly` [#445](https://github.com/msgpack/msgpack-java/pull/445)
 * (internal) Use JavaConverters instead of JavaConversions in unit tests [#446](https://github.com/msgpack/msgpack-java/pull/446)

## 0.8.14
 * Add MessageUnpacker.tryUnpackNil() for peeking whether the next value is nil or not.
 * Add MessageBufferPacker.getBufferSize().
 * Improved MessageUnpacker.readPayload performance [#436](https://github.com/msgpack/msgpack-java/pull/436)
 * Fixed a bug that ChannelBufferInput#next blocks until the buffer is filled. [#428](https://github.com/msgpack/msgpack-java/pull/428)
 * (internal) Upgraded to sbt-1.0.4 for better Java9 support
 * (internal) Dropped Java7 tests on TravisCI, but msgpack-java is still built for Java7 (1.7) target 

## 0.8.13
 * Fix ambiguous overload in Java 9 [#415](https://github.com/msgpack/msgpack-java/pull/415)
 * Make MessagePackParser accept a string as a byte array field [#420](https://github.com/msgpack/msgpack-java/pull/420)
 * Support MessagePackGenerator#writeNumber(String) [#422](https://github.com/msgpack/msgpack-java/pull/422)

## 0.8.12
 * Fix warnings in build.sbt [#402](https://github.com/msgpack/msgpack-java/pull/402)
 * Add ExtensionTypeCustomDeserializers and MessagePackFactory#setExtTypeCustomDesers [#408](https://github.com/msgpack/msgpack-java/pull/408)
 * Avoid a CharsetEncoder bug of Android 4.x at MessagePacker#packString [#409](https://github.com/msgpack/msgpack-java/pull/409)

## 0.8.11
 * Fixed NPE when write(add)Payload are used at the beginning [#392](https://github.com/msgpack/msgpack-java/pull/392)

## 0.8.10
 * Fixed a bug of unpackString [#387](https://github.com/msgpack/msgpack-java/pull/387) at the buffer boundary

## 0.8.9
 * Add DirectByteBuffer support
 * Add Flushable interface to MessagePacker

## 0.8.8
 * [Fix Unexpected UTF-8 encoder state](https://github.com/msgpack/msgpack-java/issues/371)
 * Make MessageUnpacker.hasNext extensible
 * Added skipValue(n)
 * [msgpack-jackson] Ignoring uknown propertiers when deserializing msgpack data in array format 

## 0.8.7
 * Fixed a problem when reading malformed UTF-8 characters in packString. This problem happens only if you are using an older version of Java (e.g., Java 6 or 7)
 * Support complex-type keys in msgpack-jackson

## 0.8.6
 * Fixed a bug that causes IndexOutOfBoundsException when reading a variable length code at the buffer boundary.

## 0.8.5
 * Add PackerConfig.withStr8FormatSupport (default: true) for backward compatibility with earier versions of msgpack v0.6, which doesn't have STR8 type.
 * msgpack-jackson now supports `@JsonFormat(shape=JsonFormat.Shape.ARRAY)` to serialize POJOs in an array format. See also https://github.com/msgpack/msgpack-java/tree/develop/msgpack-jackson#serialization-format
 * Small performance optimization of packString when the String size is larger than 512 bytes.

## 0.8.4
 * Embed bundle parameters for OSGi

## 0.8.3 
 * Fix a bug (#348), which wrongly overwrites the buffer before reading numeric data at the buffer boundary

## 0.8.2
 * Add some missing asXXX methods in Value
 * ValueFactory.MapBuilder now preserves the original element order (by using LinkedHashMap)
 * Fix ExtensionType property

## 0.8.1
 * MessagePack.Packer/UnpackerConfig are now immuable and configurable with withXXX methods.
 * Add bufferSize configuration parameter
 * Allow setting null to ArrayBufferInput for advanced applications that require dedicated memory management.
 * Fix MessageBufferPacker.toXXX to properly flush the output
 * Modify ValueFactory methods to produce a copy of the input data. To omit the copy, use `omitCopy` flag.
 * Improve the performance of MessagePackParser by unpacking data without using org.msgpack.value.Value.

## 0.8.0
 * Split MessagePack.Config into MessagePack.Packer/UnpackerConfig 
 * Changed MessageBuffer API 
    * It allows releasing the previously allocated buffers upon MessageBufferInput.next() call.
    * MessageBufferOutput now can read data from external byte arrays
 * MessagePacker supports addPayload(byte[]) to feed the data from an external data source 
   * This saves the cost of copying large data to the internal message buffer
 * Performance improvement of packString
 * Add MessageBufferPacker for efficiently generating byte array(s) of message packed data

## 0.7.1
 * Fix ImmutableLongValueImpl#asShort [#287](https://github.com/msgpack/msgpack-java/pull/287)

## 0.7.0
 * Support non-string key in jackson-dataformat-msgpack
 * Update the version of jackson-databind to 2.6.3
 * Several bug fixes

## 0.7.0-M6
 * Add a prototype of Value implementation
 * Apply strict coding style
 * Several bug fixes

## 0.7.0-p9
 * Fix [#217](https://github.com/msgpack/msgpack-java/issues/217) when reading from SockectInputStream 

## 0.7.0-p8
 * Support Extension type (defined in MessagePack) in msgpack-jackson
 * Support BigDecimal type (defined in Jackson) in msgpack-jackson
 * Fix MessageUnpacker#unpackString [#215](https://github.com/msgpack/msgpack-java/pull/215), [#216](https://github.com/msgpack/msgpack-java/pull/216)

## 0.7.0-p7
 * Google App Engine (GAE) support

## 0.7.0-p6
 * Add MessagePacker.getTotalWrittenBytes()

## 0.7.0-p5
 * Fix skipValue [#185](https://github.com/msgpack/msgpack-java/pull/185)

## 0.7.0-p4
 * Supporting some java6 platform and Android

