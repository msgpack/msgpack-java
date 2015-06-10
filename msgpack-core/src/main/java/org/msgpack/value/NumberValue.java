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
 * @see  org.msgpack.value.IntegerValue
 * @see  org.msgpack.value.FloatValue
 */
public interface NumberValue extends Value {
    @Override
    public ImmutableNumberValue immutableValue();

    /**
     * Returns the value as a {@code byte}, which may involve rounding or truncation.
     */
    public byte byteValue();

    /**
     * Returns the value as a {@code short}, which may involve rounding or truncation.
     */
    public short shortValue();

    /**
     * Returns the value as an {@code int}, which may involve rounding or truncation.
     */
    public int intValue();

    /**
     * Returns the value as a {@code long}, which may involve rounding or truncation.
     */
    public long longValue();

    /**
     * Returns the value as a {@code BigInteger}, which may involve rounding or truncation.
     *
     * Rounding could happen if type of this value is float or double.
     */
    public BigInteger bigIntegerValue();

    /**
     * Returns the value as a {@code float}, which may involve rounding or truncation.
     */
    public float floatValue();

    /**
     * Returns the value as a {@code double}, which may involve rounding or truncation.
     */
    public double doubleValue();
}
