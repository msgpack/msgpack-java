package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

public class TimestampExtensionModule
{
    public static final byte EXT_TYPE = -1;
    public static final SimpleModule INSTANCE = new SimpleModule("msgpack-ext-timestamp");

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
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            // MEMO: Reusing these MessagePacker and MessageUnpacker instances would improve the performance
            try (MessagePacker packer = MessagePack.newDefaultPacker(os)) {
                packer.packTimestamp(value);
            }
            try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(os.toByteArray())) {
                ExtensionTypeHeader header = unpacker.unpackExtensionTypeHeader();
                byte[] bytes = unpacker.readPayload(header.getLength());

                MessagePackExtensionType extensionType = new MessagePackExtensionType(EXT_TYPE, bytes);
                gen.writeObject(extensionType);
            }
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
            throws IOException
        {
            MessagePackExtensionType ext = p.readValueAs(MessagePackExtensionType.class);
            if (ext.getType() != EXT_TYPE) {
                throw new RuntimeException(
                        String.format("Unexpected extension type (0x%X) for Instant object", ext.getType()));
            }

            // MEMO: Reusing this MessageUnpacker instance would improve the performance
            try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(ext.getData())) {
                return unpacker.unpackTimestamp(new ExtensionTypeHeader(EXT_TYPE, ext.getData().length));
            }
        }
    }

    private TimestampExtensionModule()
    {
    }
}
