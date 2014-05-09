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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import org.msgpack.value.Value;
import org.msgpack.value.ImmutableStringValue;
import org.msgpack.value.MessageTypeStringCodingException;
import org.msgpack.core.ValueType;
import org.msgpack.core.MessagePacker;

public class ImmutableStringValueImpl
        extends AbstractImmutableValue implements ImmutableStringValue {

    // See also ResettableMutableValue

    private final String string;
    private transient ByteBuffer encodedByteBufferCache;

    public ImmutableStringValueImpl(String string) {
        this.string = string;
    }

    public ImmutableStringValueImpl(String string, ByteBuffer encodedByteBuffer) {
        this.string = string;
        this.encodedByteBufferCache = encodedByteBuffer;
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public byte[] getByteArray() {
        if (encodedByteBufferCache == null) {
            encodeString();
        }
        byte[] byteArray = new byte[encodedByteBufferCache.remaining()];
        encodedByteBufferCache.slice().get(byteArray);
        return byteArray;
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public String stringValue() {
        return string;
    }

    @Override
    public ByteBuffer byteBufferValue() {
        if (encodedByteBufferCache == null) {
            encodeString();
        }
        return encodedByteBufferCache.asReadOnlyBuffer();
    }

    private synchronized void encodeString() {
        if (encodedByteBufferCache != null) {
            return;
        }
        try {
            encodedByteBufferCache = ByteBuffer.wrap(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException neverThrown) {
            throw new AssertionError(neverThrown);
        }
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packString(string);
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
        if (!v.isStringValue()) {
            return false;
        }
        try {
            return v.asStringValue().getString().equals(string);
        } catch (MessageTypeStringCodingException ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }
}
