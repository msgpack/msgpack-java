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

class FalseValueImpl extends AbstractBooleanValue {
    private FalseValueImpl() {
    }

    private static FalseValueImpl instance = new FalseValueImpl();

    static FalseValueImpl getInstance() {
        return instance;
    }

    @Override
    public boolean getBoolean() {
        return false;
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
        pk.write(false);
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

        return v.asBooleanValue().getBoolean() == false;
    }

    @Override
    public int hashCode() {
        return 1237;
    }

    @Override
    public String toString() {
        return "false";
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append("false");
    }
}
