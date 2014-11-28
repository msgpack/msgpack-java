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
import org.msgpack.core.MessageTypeException;

import java.io.IOException;

/**
 * Value is an object representation of a message pack value.
 */
public interface Value {

    public ValueType getValueType();

    public NilValue asNilValue() throws MessageTypeException;
    public BooleanValue asBooleanValue() throws MessageTypeException;
    public NumberValue asNumberValue() throws MessageTypeException;
    public IntegerValue asIntegerValue() throws MessageTypeException;
    public FloatValue asFloatValue() throws MessageTypeException;
    public BinaryValue asBinaryValue() throws MessageTypeException;
    public StringValue asStringValue() throws MessageTypeException;
    public RawValue asRawValue() throws MessageTypeException;
    public ExtendedValue asExtendedValue() throws MessageTypeException;
    public ArrayValue asArrayValue() throws MessageTypeException;
    public MapValue asMapValue() throws MessageTypeException;

    public boolean isNilValue();
    public boolean isBooleanValue();
    public boolean isNumberValue();
    public boolean isIntegerValue();
    public boolean isFloatValue();
    public boolean isBinaryValue();
    public boolean isStringValue();
    public boolean isRawValue();
    public boolean isArrayValue();
    public boolean isMapValue();
    public boolean isExtendedValue();

    /**
     * Write this value into the specified packer
     * @param packer
     * @throws IOException
     */
    public void writeTo(MessagePacker packer) throws IOException;

    /**
     * Accepting a visitor
     * @param visitor
     */
    public void accept(ValueVisitor visitor);

    /**
     * Create an immutable representation of this value. If this value is already immutable, it returns self
     * @return
     */
    public Value toImmutable();

    /**
     * Test whether this value is an immutable or not
     * @return true if this value is an immutable object, otherwise false.
     */
    public boolean isImmutable();

}
