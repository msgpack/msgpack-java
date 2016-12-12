//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.value;

import java.nio.ByteBuffer;

/**
 * Base interface of {@link StringValue} and {@link BinaryValue} interfaces.
 * <p/>
 * MessagePack's Raw type can represent a byte array at most 2<sup>64</sup>-1 bytes.
 *
 * @see org.msgpack.value.StringValue
 * @see org.msgpack.value.BinaryValue
 */
public interface RawValue
        extends Value
{
    /**
     * Returns the value as {@code byte[]}.
     *
     * This method copies the byte array.
     */
    byte[] asByteArray();

    /**
     * Returns the value as {@code ByteBuffer}.
     *
     * Returned ByteBuffer is read-only. See also {@link java.nio.ByteBuffer#asReadOnlyBuffer()}.
     * This method doesn't copy the byte array as much as possible.
     */
    ByteBuffer asByteBuffer();

    /**
     * Returns the value as {@code String}.
     *
     * This method throws an exception if the value includes invalid UTF-8 byte sequence.
     *
     * @throws MessageStringCodingException If this value includes invalid UTF-8 byte sequence.
     */
    String asString();

    /**
     * Returns the value as {@code String}.
     *
     * This method replaces an invalid UTF-8 byte sequence with <code>U+FFFD replacement character</code>.
     */
    String toString();
}
