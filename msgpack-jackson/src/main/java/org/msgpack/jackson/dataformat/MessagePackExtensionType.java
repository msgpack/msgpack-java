package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        int result = (int) type;
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    public static class Serializer extends JsonSerializer<MessagePackExtensionType>
    {
        @Override
        public void serialize(MessagePackExtensionType value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException
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

    public interface Deser
    {
        Object deserialize(byte[] data)
                throws IOException;
    }

    public static class TypeBasedDeserializer
            extends UntypedObjectDeserializer.Vanilla
    {
        private final ObjectMapper objectMapper;
        private Map<Byte, Deser> deserTable = new ConcurrentHashMap<Byte, Deser>();

        public TypeBasedDeserializer()
        {
            MessagePackFactory messagePackFactory = new MessagePackFactory();
            messagePackFactory.setReuseResourceInParser(false);
            objectMapper = new ObjectMapper(messagePackFactory);
        }

        public <T> void addTargetClass(byte type, final Class<T> klass)
        {
            deserTable.put(type, new Deser() {
                @Override
                public Object deserialize(byte[] data)
                        throws IOException
                {
                    return objectMapper.readValue(data, klass);
                }
            });
        }

        public void addTargetTypeReference(byte type, final TypeReference typeReference)
        {
            deserTable.put(type, new Deser() {
                @Override
                public Object deserialize(byte[] data)
                        throws IOException
                {
                    return objectMapper.readValue(data, typeReference);
                }
            });
        }

        public void addCustomDeser(byte type, final Deser deser)
        {
            deserTable.put(type, new Deser() {
                @Override
                public Object deserialize(byte[] data)
                        throws IOException
                {
                    return deser.deserialize(data);
                }
            });
        }

        public void clearEntries()
        {
            deserTable.clear();
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException
        {
            Object obj = super.deserialize(p, ctxt);
            if (! (obj instanceof MessagePackExtensionType)) {
                return obj;
            }

            MessagePackExtensionType ext = (MessagePackExtensionType) obj;
            Deser deser = deserTable.get(ext.getType());
            if (deser == null) {
                return obj;
            }

            return deser.deserialize(ext.getData());
        }
    }
}
