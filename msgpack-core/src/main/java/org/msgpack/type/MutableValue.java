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
package org.msgpack.type;

public interface MutableValue
        extends Value {
    @Override
    public MutableNilValue asNilValue();

    @Override
    public MutableBooleanValue asBooleanValue();

    @Override
    public MutableIntegerValue asIntegerValue();

    @Override
    public MutableFloatValue asFloatValue();

    @Override
    public MutableArrayValue asArrayValue();

    @Override
    public MutableMapValue asMapValue();

    @Override
    public MutableRawValue asRawValue();

    @Override
    public MutableBinaryValue asBinaryValue();

    @Override
    public MutableStringValue asStringValue();
}
