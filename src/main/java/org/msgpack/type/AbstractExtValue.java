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

abstract class AbstractExtValue extends AbstractValue implements ExtValue {
    @Override
    public ValueType getType() {
        return ValueType.EXT;
    }

    @Override
    public boolean isExtValue() {
        return true;
    }

    @Override
    public ExtValue asExtValue() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if (!v.isExtValue()) {
            return false;
        }

        return getObject().equals(v.asExtValue().getObject());
    }

    @Override
    public int hashCode() {
        return getObject().hashCode();
    }

    @Override
    public String toString() {
        return getObject().toString();
    }
}
