//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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
package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.type.ValueType;

public interface Value {
    public ValueType getType();

    public boolean isNilValue();

    public boolean isBooleanValue();

    public boolean isIntegerValue();

    public boolean isFloatValue();

    public boolean isArrayValue();

    public boolean isMapValue();

    public boolean isRawValue();

    public NilValue asNilValue();

    public BooleanValue asBooleanValue();

    public IntegerValue asIntegerValue();

    public FloatValue asFloatValue();

    public ArrayValue asArrayValue();

    public MapValue asMapValue();

    public RawValue asRawValue();

    public void writeTo(Packer pk) throws IOException;

    public StringBuilder toString(StringBuilder sb);
}
