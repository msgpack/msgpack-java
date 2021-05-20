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
package org.msgpack.core;

import org.msgpack.value.Value;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;

/**
 * MessagePack serializer that converts objects into binary.
 * You can use factory methods of {@link MessagePack} class or {@link MessagePack.PackerConfig} class to create
 * an instance.
 * <p>
 * This class provides following primitive methods to write MessagePack values. These primitive methods write
 * short bytes (1 to 7 bytes) to the internal buffer at once. There are also some utility methods for convenience.
 * <p>
 * Primitive methods:
 *
 * <table>
 *   <tr><th>Java type</th><th>Packer method</th><th>MessagePack type</th></tr>
 *   <tr><td>null</td><td>{@link #packNil()}</td><td>Nil</td></tr>
 *   <tr><td>boolean</td><td>{@link #packBoolean(boolean)}</td><td>Boolean</td></tr>
 *   <tr><td>byte</td><td>{@link #packByte(byte)}</td><td>Integer</td></tr>
 *   <tr><td>short</td><td>{@link #packShort(short)}</td><td>Integer</td></tr>
 *   <tr><td>int</td><td>{@link #packInt(int)}</td><td>Integer</td></tr>
 *   <tr><td>long</td><td>{@link #packLong(long)}</td><td>Integer</td></tr>
 *   <tr><td>BigInteger</td><td>{@link #packBigInteger(BigInteger)}</td><td>Integer</td></tr>
 *   <tr><td>float</td><td>{@link #packFloat(float)}</td><td>Float</td></tr>
 *   <tr><td>double</td><td>{@link #packDouble(double)}</td><td>Float</td></tr>
 *   <tr><td>byte[]</td><td>{@link #packBinaryHeader(int)}</td><td>Binary</td></tr>
 *   <tr><td>String</td><td>{@link #packRawStringHeader(int)}</td><td>String</td></tr>
 *   <tr><td>List</td><td>{@link #packArrayHeader(int)}</td><td>Array</td></tr>
 *   <tr><td>Map</td><td>{@link #packMapHeader(int)}</td><td>Map</td></tr>
 *   <tr><td>custom user type</td><td>{@link #packExtensionTypeHeader(byte, int)}</td><td>Extension</td></tr>
 * </table>
 *
 * <p>
 * Utility methods:
 *
 * <table>
 *   <tr><th>Java type</th><th>Packer method</th><th>MessagePack type</th></tr>
 *   <tr><td>String</td><td>{@link #packString(String)}</td><td>String</td></tr>
 *   <tr><td>{@link Value}</td><td>{@link #packValue(Value)}</td><td></td></tr>
 * </table>
 *
 * <p>
 * To write a byte array, first you call {@link #packBinaryHeader} method with length of the byte array. Then,
 * you call {@link #writePayload(byte[], int, int)} or {@link #addPayload(byte[], int, int)} method to write the
 * contents.
 *
 * <p>
 * To write a List, Collection or array, first you call {@link #packArrayHeader(int)} method with the number of
 * elements. Then, you call packer methods for each element.
 * iteration.
 *
 * <p>
 * To write a Map, first you call {@link #packMapHeader(int)} method with size of the map. Then, for each pair,
 * you call packer methods for key first, and then value. You will call packer methods twice as many time as the
 * size of the map.
 *
 * <p>
 * Note that packXxxHeader methods don't validate number of elements. You must call packer methods for correct
 * number of times to produce valid MessagePack data.
 *
 * <p>
 * When IOException is thrown, primitive methods guarantee that all data is written to the internal buffer or no data
 * is written. This is convenient behavior when you use a non-blocking output channel that may not be writable
 * immediately.
 *
 * <p>{@code MessageBufferOutput reset(MessageBufferOutput out)} has incompatibly gone.
 */
public abstract class MessagePacker
        implements Closeable, Flushable
{
    /**
     * Returns total number of written bytes.
     * <p>
     * This method returns total of amount of data flushed to the underlying output plus size of current
     * internal buffer.
     */
    public abstract long getTotalWrittenBytes();

    /**
     * Clears the written data.
     */
    public abstract void clear();

    /**
     * Flushes internal buffer to the underlying output.
     * <p>
     * This method also calls flush method of the underlying output after writing internal buffer.
     */
    @Override
    public abstract void flush()
            throws IOException;

    /**
     * Closes underlying output.
     * <p>
     * This method flushes internal buffer before closing.
     */
    @Override
    public abstract void close()
            throws IOException;

    /**
     * Writes a Nil value.
     *
     * This method writes a nil byte.
     *
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packNil()
            throws IOException;

    /**
     * Writes a Boolean value.
     *
     * This method writes a true byte or a false byte.
     *
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packBoolean(boolean b)
            throws IOException;

    /**
     * Writes an Integer value.
     *
     * <p>
     * This method writes an integer using the smallest format from the int format family.
     *
     * @param b the integer to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packByte(byte b)
            throws IOException;

    /**
     * Writes an Integer value.
     *
     * <p>
     * This method writes an integer using the smallest format from the int format family.
     *
     * @param v the integer to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packShort(short v)
            throws IOException;

    /**
     * Writes an Integer value.
     *
     * <p>
     * This method writes an integer using the smallest format from the int format family.
     *
     * @param r the integer to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packInt(int r)
            throws IOException;

    /**
     * Writes an Integer value.
     *
     * <p>
     * This method writes an integer using the smallest format from the int format family.
     *
     * @param v the integer to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packLong(long v)
            throws IOException;

    /**
     * Writes an Integer value.
     *
     * <p>
     * This method writes an integer using the smallest format from the int format family.
     *
     * @param bi the integer to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packBigInteger(BigInteger bi)
            throws IOException;

    /**
     * Writes a Float value.
     *
     * <p>
     * This method writes a float value using float format family.
     *
     * @param v the value to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packFloat(float v)
            throws IOException;

    /**
     * Writes a Float value.
     *
     * <p>
     * This method writes a float value using float format family.
     *
     * @param v the value to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packDouble(double v)
            throws IOException;

    /**
     * Writes a String vlaue in UTF-8 encoding.
     *
     * <p>
     * This method writes a UTF-8 string using the smallest format from the str format family by default. If {@link MessagePack.PackerConfig#withStr8FormatSupport(boolean)} is set to false, smallest format from the str format family excepting str8 format.
     *
     * @param s the string to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packString(String s)
            throws IOException;

    /**
     * Writes a Timestamp value.
     *
     * <p>
     * This method writes a timestamp value using timestamp format family.
     *
     * @param instant the timestamp to be written
     * @return this packer
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packTimestamp(Instant instant)
            throws IOException;

    /**
     * Writes a Timesamp value using a millisecond value (e.g., System.currentTimeMillis())
     * @param millis the millisecond value
     * @return this packer
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packTimestamp(long millis)
            throws IOException;

    /**
     * Writes a Timestamp value.
     *
     * <p>
     * This method writes a timestamp value using timestamp format family.
     *
     * @param epochSecond the number of seconds from 1970-01-01T00:00:00Z
     * @param nanoAdjustment the nanosecond adjustment to the number of seconds, positive or negative
     * @return this
     * @throws IOException when underlying output throws IOException
     * @throws ArithmeticException when epochSecond plus nanoAdjustment in seconds exceeds the range of long
     */
    public abstract MessagePacker packTimestamp(long epochSecond, int nanoAdjustment)
            throws IOException, ArithmeticException;

    /**
     * Writes header of an Array value.
     * <p>
     * You will call other packer methods for each element after this method call.
     * <p>
     * You don't have to call anything at the end of iteration.
     *
     * @param arraySize number of elements to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packArrayHeader(int arraySize)
            throws IOException;

    /**
     * Writes header of a Map value.
     * <p>
     * After this method call, for each key-value pair, you will call packer methods for key first, and then value.
     * You will call packer methods twice as many time as the size of the map.
     * <p>
     * You don't have to call anything at the end of iteration.
     *
     * @param mapSize number of pairs to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packMapHeader(int mapSize)
            throws IOException;

    /**
     * Writes a dynamically typed value.
     *
     * @param v the value to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packValue(Value v)
            throws IOException;

    /**
     * Writes header of an Extension value.
     * <p>
     * You MUST call {@link #writePayload(byte[])} or {@link #addPayload(byte[])} method to write body binary.
     *
     * @param extType the extension type tag to be written
     * @param payloadLen number of bytes of a payload binary to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packExtensionTypeHeader(byte extType, int payloadLen)
            throws IOException;

    /**
     * Writes header of a Binary value.
     * <p>
     * You MUST call {@link #writePayload(byte[])} or {@link #addPayload(byte[])} method to write body binary.
     *
     * @param len number of bytes of a binary to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packBinaryHeader(int len)
            throws IOException;

    /**
     * Writes header of a String value.
     * <p>
     * Length must be number of bytes of a string in UTF-8 encoding.
     * <p>
     * You MUST call {@link #writePayload(byte[])} or {@link #addPayload(byte[])} method to write body of the
     * UTF-8 encoded string.
     *
     * @param len number of bytes of a UTF-8 string to be written
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker packRawStringHeader(int len)
            throws IOException;

    /**
     * Writes a byte array to the output.
     * <p>
     * This method is used with {@link #packRawStringHeader(int)} or {@link #packBinaryHeader(int)} methods.
     *
     * @param src the data to add
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker writePayload(byte[] src)
            throws IOException;

    /**
     * Writes a byte array to the output.
     * <p>
     * This method is used with {@link #packRawStringHeader(int)} or {@link #packBinaryHeader(int)} methods.
     *
     * @param src the data to add
     * @param off the start offset in the data
     * @param len the number of bytes to add
     * @return this
     * @throws IOException when underlying output throws IOException
     */
    public abstract MessagePacker writePayload(byte[] src, int off, int len)
            throws IOException;

    /**
     * Writes a byte array to the output.
     * <p>
     * This method is used with {@link #packRawStringHeader(int)} or {@link #packBinaryHeader(int)} methods.
     * <p>
     * Unlike {@link #writePayload(byte[])} method, this method does not make a defensive copy of the given byte
     * array, even if it is shorter than {@link MessagePack.PackerConfig#withBufferFlushThreshold(int)}. This is
     * faster than {@link #writePayload(byte[])} method but caller must not modify the byte array after calling
     * this method.
     *
     * @param src the data to add
     * @return this
     * @throws IOException when underlying output throws IOException
     * @see #writePayload(byte[])
     */
    public abstract MessagePacker addPayload(byte[] src)
            throws IOException;

    /**
     * Writes a byte array to the output.
     * <p>
     * This method is used with {@link #packRawStringHeader(int)} or {@link #packBinaryHeader(int)} methods.
     * <p>
     * Unlike {@link #writePayload(byte[], int, int)} method, this method does not make a defensive copy of the
     * given byte array, even if it is shorter than {@link MessagePack.PackerConfig#withBufferFlushThreshold(int)}.
     * This is faster than {@link #writePayload(byte[])} method but caller must not modify the byte array after
     * calling this method.
     *
     * @param src the data to add
     * @param off the start offset in the data
     * @param len the number of bytes to add
     * @return this
     * @throws IOException when underlying output throws IOException
     * @see #writePayload(byte[], int, int)
     */
    public abstract MessagePacker addPayload(byte[] src, int off, int len)
            throws IOException;
}
