package org.msgpack.jackson.dataformat;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.Arrays;

@JsonSerialize(using = MessagePackExtensionType.Serializer.class)
public class MessagePackExtensionType
{
    private final byte type;
    private final byte[] data;

    public MessagePackExtensionType(byte type, byte[] data)
    {
        this.type = type;
        this.data = data;
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
        return "MessagePackExtensionType(type=" + type + ", data.length=" + (data == null ? 0 : data.length) + ")";
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
