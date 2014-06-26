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

import org.msgpack.core.ValueType;

public interface BasicValue {
    public ValueType getType();

    /**
     * Returns immutable copy of this value
     */
    public Value immutableValue();

    public boolean isNilValue();

    public boolean isBooleanValue();

    public boolean isNumberValue();

    public boolean isIntegerValue();

    public boolean isFloatValue();

    public boolean isRawValue();

    public boolean isBinaryValue();

    public boolean isStringValue();

    public boolean isArrayValue();

    public boolean isMapValue();

    public boolean isExtendedValue();

    public BasicNilValue asNilValue();

    public BasicBooleanValue asBooleanValue();

    public BasicNumberValue asNumberValue();

    public BasicIntegerValue asIntegerValue();

    public BasicFloatValue asFloatValue();

    public BasicRawValue asRawValue();

    public BasicBinaryValue asBinaryValue();

    public BasicStringValue asStringValue();

    /*
    public BasicArrayValue asArrayValue();

    public BasicMapValue asMapValue();

    public BasicExtendedValue asExtendedValue();
    */

    //// TODO
    //public void accept(BasicValueVisitor visitor);
}
