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

import org.msgpack.core.MessagePacker;

import java.io.IOException;

/**
 * Value is an implementation of MessagePack type system. To retrieve values from a Value object,
 * You need to check its {@link ValueType} then call an appropriate asXXXValue method.
 *
 *
 *
 */
public interface Value
{
    /**
     * Returns type of this value.
     *
     * Note that you can't use <code>instanceof</code> to check type of a value because type of a mutable value is variable.
     */
    ValueType getValueType();

    /**
     * Returns immutable copy of this value.
     *
     * This method simply returns <code>this</code> without copying the value if this value is already immutable.
     */
    ImmutableValue immutableValue();

    /**
     * Returns true if type of this value is Nil.
     *
     * If this method returns true, {@code asNilValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((NilValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isNilValue();

    /**
     * Returns true if type of this value is Boolean.
     *
     * If this method returns true, {@code asBooleanValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((BooleanValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isBooleanValue();

    /**
     * Returns true if type of this value is Integer or Float.
     *
     * If this method returns true, {@code asNumberValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((NumberValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isNumberValue();

    /**
     * Returns true if type of this value is Integer.
     *
     * If this method returns true, {@code asIntegerValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((IntegerValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isIntegerValue();

    /**
     * Returns true if type of this value is Float.
     *
     * If this method returns true, {@code asFloatValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((FloatValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isFloatValue();

    /**
     * Returns true if type of this value is String or Binary.
     *
     * If this method returns true, {@code asRawValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((RawValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isRawValue();

    /**
     * Returns true if type of this value is Binary.
     *
     * If this method returns true, {@code asBinaryValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((BinaryValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isBinaryValue();

    /**
     * Returns true if type of this value is String.
     *
     * If this method returns true, {@code asStringValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((StringValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isStringValue();

    /**
     * Returns true if type of this value is Array.
     *
     * If this method returns true, {@code asArrayValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((ArrayValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isArrayValue();

    /**
     * Returns true if type of this value is Map.
     *
     * If this method returns true, {@code asMapValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((MapValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    boolean isMapValue();

    /**
     * Returns true if type of this an Extension.
     *
     * If this method returns true, {@code asExtensionValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((ExtensionValue) thisValue)</code> to check type of a value because
     * type of a mutable value is variable.
     */
    boolean isExtensionValue();

    /**
     * Returns the value as {@code NilValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((NilValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Nil.
     */
    NilValue asNilValue();

    /**
     * Returns the value as {@code BooleanValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((BooleanValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Boolean.
     */
    BooleanValue asBooleanValue();

    /**
     * Returns the value as {@code NumberValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((NumberValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Integer or Float.
     */
    NumberValue asNumberValue();

    /**
     * Returns the value as {@code IntegerValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((IntegerValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Integer.
     */
    IntegerValue asIntegerValue();

    /**
     * Returns the value as {@code FloatValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((FloatValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Float.
     */
    FloatValue asFloatValue();

    /**
     * Returns the value as {@code RawValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((RawValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Binary or String.
     */
    RawValue asRawValue();

    /**
     * Returns the value as {@code BinaryValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((BinaryValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Binary.
     */
    BinaryValue asBinaryValue();

    /**
     * Returns the value as {@code StringValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((StringValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not String.
     */
    StringValue asStringValue();

    /**
     * Returns the value as {@code ArrayValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((ArrayValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Array.
     */
    ArrayValue asArrayValue();

    /**
     * Returns the value as {@code MapValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((MapValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not Map.
     */
    MapValue asMapValue();

    /**
     * Returns the value as {@code ExtensionValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((ExtensionValue) thisValue)</code> to check type of a value
     * because type of a mutable value is variable.
     *
     * @throws MessageTypeCastException If type of this value is not an Extension.
     */
    ExtensionValue asExtensionValue();

    /**
     * Serializes the value using the specified {@code MessagePacker}
     *
     * @see MessagePacker
     */
    void writeTo(MessagePacker pk)
            throws IOException;

    /**
     * Compares this value to the specified object.
     *
     * This method returns {@code true} if type and value are equivalent.
     * If this value is {@code MapValue} or {@code ArrayValue}, this method check equivalence of elements recursively.
     */
    boolean equals(Object obj);

    /**
     * Returns json representation of this Value.
     *
     * Following behavior is not configurable at this release and they might be changed at future releases:
     *
     * * if a key of MapValue is not string, the key is converted to a string using toString method.
     * * NaN and Infinity of DoubleValue are converted to null.
     * * ExtensionValue is converted to a 2-element array where first element is a number and second element is the data encoded in hex.
     * * BinaryValue is converted to a string using UTF-8 encoding. Invalid byte sequence is replaced with <code>U+FFFD replacement character</code>.
     * * Invalid UTF-8 byte sequences in StringValue is replaced with <code>U+FFFD replacement character</code>
     */
    String toJson();
}
