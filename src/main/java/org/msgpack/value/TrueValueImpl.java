//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
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

class TrueValueImpl extends AbstractBooleanValue {
    private TrueValueImpl() { }

    private static TrueValueImpl instance = new TrueValueImpl();

    static TrueValueImpl getInstance() {
        return instance;
    }

    public boolean getBoolean() {
        return true;
    }

    public BooleanValue asBooleanValue() {
        return this;
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof BooleanValue)) {
            return false;
        }

        return ((BooleanValue) o).getBoolean() == true;
    }

	public int hashCode() {
        return 1231;
    }
}

