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

import java.io.IOException;
import org.msgpack.core.ValueType;
import org.msgpack.core.Packer;

public interface Value {
    public ValueType getType();

    public ImmutableValue immutableValue();

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

    public NilValue asNilValue();

    public BooleanValue asBooleanValue();

    public NumberValue asNumberValue();

    public IntegerValue asIntegerValue();

    public FloatValue asFloatValue();

    public RawValue asRawValue();

    public BinaryValue asBinaryValue();

    public StringValue asStringValue();

    public ArrayValue asArrayValue();

    public MapValue asMapValue();

    public ExtendedValue asExtendedValue();

    public void writeTo(Packer pk) throws IOException;

    //public void accept(ValueVisitor visitor);
}
