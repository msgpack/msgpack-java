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

import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;

class StringRawValueImpl extends AbstractRawValue {
    private String string;

    StringRawValueImpl(String string) {
        this.string = string;
    }

    @Override
    public byte[] getByteArray() {
        try {
            // TODO encoding error?
            return string.getBytes(UTF8);
        } catch (UnsupportedEncodingException ex) {
            throw new MessageTypeException(ex);
        }
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
        pk.write(string);
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
        if (!v.isRawValue()) {
            return false;
        }

        if (v.getClass() == StringRawValueImpl.class) {
            return string.equals(((StringRawValueImpl) v).string);
        }

        return Arrays.equals(getByteArray(), v.asRawValue().getByteArray());
    }
}
