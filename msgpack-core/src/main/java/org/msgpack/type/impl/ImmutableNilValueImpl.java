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
package org.msgpack.type.impl;

import java.io.IOException;

import org.msgpack.type.Value;
import org.msgpack.type.ValueType;
import org.msgpack.type.ImmutableNilValue;
import org.msgpack.packer.Packer;

public class ImmutableNilValueImpl
        extends AbstractImmutableValue implements ImmutableNilValue {

    private ImmutableNilValueImpl() {
    }

    private static ImmutableNilValueImpl instance = new ImmutableNilValueImpl();

    public static ImmutableNilValueImpl getInstance() {
        return instance;
    }

    @Override
    public ValueType getType() {
        return ValueType.NIL;
    }

    @Override
    public ImmutableNilValue asNilValue() {
        return this;
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
        pk.writeNil();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        return ((Value) o).isNilValue();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "null";
    }
}
