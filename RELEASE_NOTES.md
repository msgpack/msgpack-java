# Release Notes

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

