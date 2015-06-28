package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.nio.ByteBuffer;

@JsonSerialize(using = MessagePackExtensionType.Serializer.class)
public class MessagePackExtensionType
{
    private final int extType;
    private final ByteBuffer byteBuffer;

    public MessagePackExtensionType(int extType, ByteBuffer byteBuffer) {
        this.extType = extType;
        this.byteBuffer = byteBuffer.isReadOnly() ?
                byteBuffer : byteBuffer.asReadOnlyBuffer();
    }

    public int extType() {
        return extType;
    }

    public ByteBuffer byteBuffer() {
        return byteBuffer;
    }

    public static class Serializer extends JsonSerializer<MessagePackExtensionType> {
        @Override
        public void serialize(MessagePackExtensionType value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {
            if (gen instanceof MessagePackGenerator) {
                MessagePackGenerator msgpackGenerator = (MessagePackGenerator)gen;
                msgpackGenerator.writeExtendedType(value);
            }
            else {
                throw new IllegalStateException("'gen' is expected to be MessagePackGenerator but it's " + gen.getClass());
            }
        }
    }
}
