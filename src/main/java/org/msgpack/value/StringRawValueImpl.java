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

import java.util.Arrays;

class StringRawValueImpl extends AbstractRawValue {
    private String string;

    StringRawValueImpl(String string) {
        this.string = string;
    }

    public byte[] getByteArray() {
        return string.getBytes("UTF-8");
    }

    public String getString() {
        return string;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof RawValue)) {
            return false;
        }

        if(o.getClass() == StringRawValueImpl.class) {
            return string.equals(((StringRawValueImpl) o).string);
        }

        return Arrays.equals(getByteArray(), ((RawValue) o).getByteArray());
    }
}

