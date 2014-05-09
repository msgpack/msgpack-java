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
package org.msgpack.value.impl;

import java.io.IOException;

import org.msgpack.core.MessageTypeFamily;
import org.msgpack.value.Value;
import org.msgpack.value.ImmutableBooleanValue;
import org.msgpack.core.MessagePacker;

public class ImmutableFalseValueImpl
        extends AbstractImmutableValue implements ImmutableBooleanValue {

    private ImmutableFalseValueImpl() {
    }

    private static ImmutableFalseValueImpl instance = new ImmutableFalseValueImpl();

    public static ImmutableFalseValueImpl getInstance() {
        return instance;
    }

    @Override
    public MessageTypeFamily getType() {
        return MessageTypeFamily.BOOLEAN;
    }

    @Override
    public ImmutableBooleanValue asBooleanValue() {
        return this;
    }

    @Override
    public boolean booleanValue() {
        return false;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packBoolean(false);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if (!v.isBooleanValue()) {
            return false;
        }
        return v.asBooleanValue().booleanValue() == false;
    }

    @Override
    public int hashCode() {
        return 1237;
    }

    @Override
    public String toString() {
        return "false";
    }
}
