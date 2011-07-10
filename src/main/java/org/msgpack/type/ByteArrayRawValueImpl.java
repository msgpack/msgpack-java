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
package org.msgpack.type;

import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;

class ByteArrayRawValueImpl extends AbstractRawValue {
    private static ByteArrayRawValueImpl emptyInstance = new ByteArrayRawValueImpl(new byte[0], true);

    public static RawValue getEmptyInstance() {
        return emptyInstance;
    }

    private byte[] bytes;

    ByteArrayRawValueImpl(byte[] bytes, boolean gift) {
        if(gift) {
            this.bytes = bytes;
        } else {
            this.bytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
        }
    }

    ByteArrayRawValueImpl(byte[] b, int off, int len) {
        this.bytes = new byte[len];
        System.arraycopy(b, off, this.bytes, 0, len);
    }

    public byte[] getByteArray() {
        return bytes;
    }

    public String getString() {
        // TODO encoding error
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new MessageTypeException();
        }
    }

    public void writeTo(Packer pk) throws IOException {
        pk.writeByteArray(bytes);
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof RawValue)) {
            return false;
        }

        return Arrays.equals(bytes, ((RawValue) o).getByteArray());
    }

    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}

