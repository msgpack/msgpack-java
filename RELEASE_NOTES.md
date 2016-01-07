# Release Notes

* 0.8.1
 * MessagePack.Packer/UnpackerConfig are now immuable and configurable with withXXX methods.
 * Allow setting null to ArrayBufferInput for advanced applications that require dedicated memory management.
 * Fix MessageBufferPacker.toXXX to properly flush the output
 * Modify ValueFactory methods to produce a copy of the input data. To omit the copy, use `omitCopy` flag.
 * Improve the performance of MessagePackParser by unpacking data without using org.msgpack.value.Value.

* 0.8.0
 * Split MessagePack.Config into MessagePack.Packer/UnpackerConfig 
 * Changed MessageBuffer API 
    * It allows releasing the previously allocated buffers upon MessageBufferInput.next() call.
    * MessageBufferOutput now can read data from external byte arrays
 * MessagePacker supports addPayload(byte[]) to feed the data from an external data source 
   * This saves the cost of copying large data to the internal message buffer
 * Performance improvement of packString
 * Add MessageBufferPacker for efficiently generating byte array(s) of message packed data

* 0.7.1
 * Fix ImmutableLongValueImpl#asShort [#287](https://github.com/msgpack/msgpack-java/pull/287)

* 0.7.0
 * Support non-string key in jackson-dataformat-msgpack
 * Update the version of jackson-databind to 2.6.3
 * Several bug fixes

* 0.7.0-M6
 * Add a prototype of Value implementation
 * Apply strict coding style
 * Several bug fixes

* 0.7.0-p9
 * Fix [#217](https://github.com/msgpack/msgpack-java/issues/217) when reading from SockectInputStream 

* 0.7.0-p8
 * Support Extension type (defined in MessagePack) in msgpack-jackson
 * Support BigDecimal type (defined in Jackson) in msgpack-jackson
 * Fix MessageUnpacker#unpackString [#215](https://github.com/msgpack/msgpack-java/pull/215), [#216](https://github.com/msgpack/msgpack-java/pull/216)

* 0.7.0-p7
 * Google App Engine (GAE) support

* 0.7.0-p6
 * Add MessagePacker.getTotalWrittenBytes()

* 0.7.0-p5
 * Fix skipValue [#185](https://github.com/msgpack/msgpack-java/pull/185)

* 0.7.0-p4
 * Supporting some java6 platform and Android

