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

import org.msgpack.core.MessageTypeFamily;
import org.msgpack.value.Value;
import org.msgpack.value.StringValue;
import org.msgpack.value.ImmutableStringValue;
import org.msgpack.core.MessagePacker;

public class ImmutableRawStringValueImpl
        extends AbstractImmutableRawValue implements ImmutableStringValue {
    public ImmutableRawStringValueImpl(ByteBuffer byteBuffer) {
        super(byteBuffer);
    }

    @Override
    public MessageTypeFamily getType() {
        return MessageTypeFamily.STRING;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        pk.packRawStringHeader(byteBuffer.remaining());
        pk.writePayload(byteBuffer.asReadOnlyBuffer());
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
        StringValue sv = v.asStringValue();
        return sv.byteBufferValue().equals(byteBuffer);
    }

    @Override
    public int hashCode() {
        return stringValue().hashCode();
    }
}
