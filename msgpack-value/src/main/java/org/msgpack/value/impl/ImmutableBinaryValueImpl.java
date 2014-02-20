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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.CharacterCodingException;

import org.msgpack.value.Value;
import org.msgpack.value.BinaryValue;
import org.msgpack.value.ImmutableBinaryValue;
import org.msgpack.core.ValueType;
import org.msgpack.core.Packer;

public class ImmutableBinaryValueImpl
        extends AbstractImmutableRawValue implements ImmutableBinaryValue {
    public ImmutableBinaryValueImpl(ByteBuffer byteBuffer) {
        super(byteBuffer);
    }

    @Override
    public ValueType getType() {
        return ValueType.BINARY;
    }

    @Override
    public void writeTo(Packer pk) throws IOException {
        pk.writeBinaryLength(byteBuffer.remaining());
        pk.rawWrite(byteBuffer.asReadOnlyBuffer());
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
        if (!v.isBinaryValue()) {
            return false;
        }
        BinaryValue bv = v.asBinaryValue();
        return bv.byteBufferValue().equals(byteBuffer);
    }

    @Override
    public int hashCode() {
        return byteBuffer.hashCode();
    }
}
