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
import org.msgpack.core.MessageTypeCastException;

import java.io.IOException;


/**
 * Value is an implementation of MessagePack type system.
 */
public interface Value {
    /**
     * Returns type of this value.
     *
     * Note that you can't use <code>instanceof</code> to check type of a value because type of a mutable value is variable.
     */
    public ValueType getValueType();

    /**
     * Returns immutable copy of this value.
     *
     * This method simply returns <code>this</code> without copying the value if this value is already immutable.
     */
    public ImmutableValue immutableValue();

    /**
     * Returns true if type of this value is Nil.
     *
     * If this method returns true, {@code asNilValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((NilValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isNilValue();

    /**
     * Returns true if type of this value is Boolean.
     *
     * If this method returns true, {@code asBooleanValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((BooleanValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isBooleanValue();

    /**
     * Returns true if type of this value is Integer or Float.
     *
     * If this method returns true, {@code asNumberValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((NumberValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isNumberValue();

    /**
     * Returns true if type of this value is Integer.
     *
     * If this method returns true, {@code asIntegerValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((IntegerValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isIntegerValue();

    /**
     * Returns true if type of this value is Float.
     *
     * If this method returns true, {@code asFloatValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((FloatValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isFloatValue();

    /**
     * Returns true if type of this value is String or Binary.
     *
     * If this method returns true, {@code asRawValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((RawValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isRawValue();

    /**
     * Returns true if type of this value is Binary.
     *
     * If this method returns true, {@code asBinaryValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((BinaryValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isBinaryValue();

    /**
     * Returns true if type of this value is String.
     *
     * If this method returns true, {@code asStringValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((StringValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isStringValue();

    /**
     * Returns true if type of this value is Array.
     *
     * If this method returns true, {@code asArrayValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((ArrayValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isArrayValue();

    /**
     * Returns true if type of this value is Map.
     *
     * If this method returns true, {@code asMapValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((MapValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     */
    public boolean isMapValue();

    /**
     * Returns true if type of this an Extension.
     *
     * If this method returns true, {@code asExtensionValue} never throws exceptions.
     * Note that you can't use <code>instanceof</code> or cast <code>((ExtensionValue) thisValue)</code> to check type of a value because
     * type of a mutable value is variable.
     */
    public boolean isExtensionValue();

    /**
     * Returns the value as {@code NilValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((NilValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Nil.
     */
    public NilValue asNilValue();

    /**
     * Returns the value as {@code BooleanValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((BooleanValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Boolean.
     */
    public BooleanValue asBooleanValue();

    /**
     * Returns the value as {@code NumberValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((NumberValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Integer or Float.
     */
    public NumberValue asNumberValue();

    /**
     * Returns the value as {@code IntegerValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((IntegerValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Integer.
     */
    public IntegerValue asIntegerValue();

    /**
     * Returns the value as {@code FloatValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((FloatValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Float.
     */
    public FloatValue asFloatValue();

    /**
     * Returns the value as {@code RawValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((RawValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Binary or String.
     */
    public RawValue asRawValue();

    /**
     * Returns the value as {@code BinaryValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((BinaryValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Binary.
     */
    public BinaryValue asBinaryValue();

    /**
     * Returns the value as {@code StringValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((StringValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not String.
     */
    public StringValue asStringValue();

    /**
     * Returns the value as {@code ArrayValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((ArrayValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Array.
     */
    public ArrayValue asArrayValue();

    /**
     * Returns the value as {@code MapValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((MapValue) thisValue)</code> to check type of a value because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not Map.
     */
    public MapValue asMapValue();

    /**
     * Returns the value as {@code ExtensionValue}. Otherwise throws {@code MessageTypeCastException}.
     *
     * Note that you can't use <code>instanceof</code> or cast <code>((ExtensionValue) thisValue)</code> to check type of a value
     * because type of a mutable value is variable.
     *
     * @throws  MessageTypeCastException
     *          If type of this value is not an Extension.
     */
    public ExtensionValue asExtensionValue();

    /**
     * Serializes the value using the specified {@code MessagePacker}
     *
     * @see  MessagePacker
     */
    public void writeTo(MessagePacker pk) throws IOException;

    /**
     * Compares this value to the specified object.
     *
     * This method returns {@code true} if type and value are equivalent.
     * If this value is {@code MapValue} or {@code ArrayValue}, this method check equivalence of elements recursively.
     */
    public boolean equals(Object obj);
}
