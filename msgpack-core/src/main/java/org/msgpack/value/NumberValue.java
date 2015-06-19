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

import java.math.BigInteger;

/**
 * The interface {@code NumberValue} is the interface of {@code IntegerValue} and {@code FloatValue}.
 *
 * @see org.msgpack.value.IntegerValue
 * @see org.msgpack.value.FloatValue
 */
public interface NumberValue extends Value {

    /**
     * Convert this value into a byte value. If this value is not within the range of Byte value, it will truncate or round the value.
     */
    byte toByte();

    /**
     * Convert this value into a short value. If this value is not within the range of Short value, it will truncate or round the value.
     */
    short toShort();

    /**
     * Convert this value into an int value. If this value is not within the range of Int value, it will truncate or round the value.
     */
    int toInt();

    /**
     * Convert this value into a long value. If this value is not within the range of Long value, it will truncate or round the value.
     */
    long toLong();

    /**
     * Convert this value into a BigInteger. If value is Float type, it will round the value
     */
    BigInteger toBigInteger();

    /**
     * Converts this value into a 32-bit float
     */
    float toFloat();

    /**
     * Converts this value into a 64-bit double
     */
    double toDouble();
}
