# Release Notes

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
 * Embed bundle paramters for OSGi

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

