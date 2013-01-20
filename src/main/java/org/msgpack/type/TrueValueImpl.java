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

class TrueValueImpl extends AbstractBooleanValue {
    private TrueValueImpl() {
    }

    private static TrueValueImpl instance = new TrueValueImpl();

    static TrueValueImpl getInstance() {
        return instance;
    }

    @Override
    public boolean getBoolean() {
        return true;
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
        pk.write(true);
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

        return v.asBooleanValue().getBoolean() == true;
    }

    @Override
    public int hashCode() {
        return 1231;
    }

    @Override
    public String toString() {
        return "true";
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append("true");
    }
}
