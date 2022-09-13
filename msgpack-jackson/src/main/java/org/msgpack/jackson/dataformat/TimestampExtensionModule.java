package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;

public class TimestampExtensionModule
{
    public static final byte EXT_TYPE = -1;
    public static final SimpleModule INSTANCE = new SimpleModule("msgpack-ext-timestamp");
    private static final int SIZE_OF_NANOS_IN_BYTES = Integer.SIZE / 8;
    private static final int SIZE_OF_EPOCH_SECONDS_IN_BYTES = Long.SIZE / 8;


    static {
        INSTANCE.addSerializer(Instant.class, new InstantSerializer(Instant.class));
        INSTANCE.addDeserializer(Instant.class, new InstantDeserializer(Instant.class));
    }

    private static class InstantSerializer extends StdSerializer<Instant>
    {
        protected InstantSerializer(Class<Instant> t)
        {
            super(t);
        }

        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider)
            throws IOException
        {
            int nanos = value.getNano();
            long epochSeconds = value.getEpochSecond();

            byte[] bytes = new byte[SIZE_OF_NANOS_IN_BYTES + SIZE_OF_EPOCH_SECONDS_IN_BYTES];

            for (int i = 0; i < SIZE_OF_NANOS_IN_BYTES; i++) {
                bytes[i] = (byte) (nanos >> ((SIZE_OF_NANOS_IN_BYTES - 1) - i) * 8);
            }

            for (int i = 0; i < SIZE_OF_EPOCH_SECONDS_IN_BYTES; i++) {
                bytes[i + SIZE_OF_NANOS_IN_BYTES] = (byte) (epochSeconds >> ((SIZE_OF_EPOCH_SECONDS_IN_BYTES - 1) - i) * 8);
            }

            MessagePackExtensionType extensionType = new MessagePackExtensionType(EXT_TYPE, bytes);

            gen.writeObject(extensionType);
        }
    }

    private static class InstantDeserializer extends StdDeserializer<Instant>
    {
        protected InstantDeserializer(Class<?> vc)
        {
            super(vc);
        }

        @Override
        public Instant deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
        {
            // TODO: Check header for all possible cases
            MessagePackExtensionType ext = p.readValueAs(MessagePackExtensionType.class);
            if (ext.getType() != EXT_TYPE) {
                throw new RuntimeException(
                        String.format("Unexpected extension type (0x%X) for Instant object", ext.getType()));
            }

            byte[] bytes = ext.getData();

            int nanoSeconds = 0;
            for (int i = 0; i < SIZE_OF_NANOS_IN_BYTES; i++) {
                nanoSeconds += ((int) bytes[i] & 0xFF) << (((SIZE_OF_NANOS_IN_BYTES - 1) - i) * 8);
            }

            long epochSeconds = 0;
            for (int i = 0; i < SIZE_OF_EPOCH_SECONDS_IN_BYTES; i++) {
                epochSeconds += ((long) bytes[i + SIZE_OF_NANOS_IN_BYTES] & 0xFF) << (((SIZE_OF_EPOCH_SECONDS_IN_BYTES - 1) - i) * 8);
            }

            return Instant.ofEpochSecond(epochSeconds, nanoSeconds);
        }
    }
}
