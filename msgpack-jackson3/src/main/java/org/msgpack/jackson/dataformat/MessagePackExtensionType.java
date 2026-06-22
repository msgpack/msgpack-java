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
package org.msgpack.jackson.dataformat;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.Arrays;
import java.util.Objects;

@JsonSerialize(using = MessagePackExtensionType.Serializer.class)
public class MessagePackExtensionType
{
    private final byte type;
    private final byte[] data;

    public MessagePackExtensionType(byte type, byte[] data)
    {
        this.type = type;
        this.data = Objects.requireNonNull(data, "data");
    }

    public byte getType()
    {
        return type;
    }

    public byte[] getData()
    {
        return data;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessagePackExtensionType)) {
            return false;
        }

        MessagePackExtensionType that = (MessagePackExtensionType) o;

        if (type != that.type) {
            return false;
        }
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode()
    {
        int result = type;
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString()
    {
        return "MessagePackExtensionType(type=" + type + ", data.length=" + data.length + ")";
    }

    public static class Serializer extends StdSerializer<MessagePackExtensionType>
    {
        public Serializer()
        {
            super(MessagePackExtensionType.class);
        }

        @Override
        public void serialize(MessagePackExtensionType value, JsonGenerator gen, SerializationContext serializers)
        {
            if (gen instanceof MessagePackGenerator) {
                MessagePackGenerator msgpackGenerator = (MessagePackGenerator) gen;
                msgpackGenerator.writeExtensionType(value);
            }
            else {
                throw new IllegalStateException("'gen' is expected to be MessagePackGenerator but it's " + gen.getClass());
            }
        }
    }
}
