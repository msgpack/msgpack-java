package org.msgpack.jackson.dataformat;

    import java.nio.ByteBuffer;

/**
 * Created by komamitsu on 3/7/15.
 */
public class MessagePackExtensionType {
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
}
