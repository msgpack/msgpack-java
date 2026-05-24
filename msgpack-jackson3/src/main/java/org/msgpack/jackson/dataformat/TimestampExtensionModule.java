package org.msgpack.jackson.dataformat;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;
import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
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
        public void serialize(Instant value, JsonGenerator gen, SerializationContext provider)
        {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try (MessagePacker packer = MessagePack.newDefaultPacker(os)) {
                    packer.packTimestamp(value);
                }
                try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(os.toByteArray())) {
                    ExtensionTypeHeader header = unpacker.unpackExtensionTypeHeader();
                    byte[] bytes = unpacker.readPayload(header.getLength());

                    MessagePackExtensionType extensionType = new MessagePackExtensionType(EXT_TYPE, bytes);
                    gen.writePOJO(extensionType);
                }
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
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
        {
            try {
                MessagePackExtensionType ext = p.readValueAs(MessagePackExtensionType.class);
                if (ext.getType() != EXT_TYPE) {
                    ctxt.reportInputMismatch(Instant.class,
                            "Unexpected extension type (0x%X) for Instant object", ext.getType() & 0xFF);
                    return null; // unreachable
                }
                try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(ext.getData())) {
                    return unpacker.unpackTimestamp(new ExtensionTypeHeader(EXT_TYPE, ext.getData().length));
                }
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private TimestampExtensionModule()
    {
    }
}
