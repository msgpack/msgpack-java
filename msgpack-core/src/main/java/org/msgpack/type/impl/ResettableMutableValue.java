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

import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.msgpack.type.Value;

public class ResettableMutableValue
        extends AbstractUnionMutableValue {
    @Override
    public void resetToNilValue() {
        super.resetToNilValue();
    }

    @Override
    public void resetToBooleanValue(boolean value) {
        super.resetToBooleanValue(value);
    }

    @Override
    public void resetToIntegerValue(long value) {
        super.resetToIntegerValue(value);
    }

    @Override
    public void resetToIntegerValue(BigInteger value) {
        super.resetToIntegerValue(value);
    }

    @Override
    public void resetToFloatValue(double value) {
        super.resetToFloatValue(value);
    }

    @Override
    public void resetToBinaryValue(ByteBuffer value) {
        super.resetToBinaryValue(value);
    }

    @Override
    public void resetToStringValue(String value) {
        super.resetToStringValue(value);
    }

    @Override
    public void resetToStringValue(ByteBuffer value) {
        super.resetToStringValue(value);
    }

    @Override
    public void resetToArrayValue(List<Value> value) {
        super.resetToArrayValue(value);
    }

    @Override
    public void resetToMapValue(Map<Value, Value> value) {
        super.resetToMapValue(value);
    }
}
