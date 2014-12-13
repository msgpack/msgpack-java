package org.msgpack.value.impl;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageStringCodingException;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.value.BinaryValue;
import org.msgpack.value.RawValue;
import org.msgpack.value.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.*;

/**
* Created on 5/30/14.
*/
public abstract class RawValueImpl extends AbstractValue implements RawValue {

    protected final ByteBuffer byteBuffer;
    private transient String decodedStringCache;
    private transient MessageStringCodingException codingException;

    public RawValueImpl(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer.slice();
    }

    @Override
    public byte[] toByteArray() {
        byte[] byteArray = new byte[byteBuffer.remaining()];
        byteBuffer.slice().get(byteArray);
        return byteArray;
    }

    @Override
    public RawValue toValue() {
        return this;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        return byteBuffer.asReadOnlyBuffer();
    }

    @Override
    public MessageBuffer toMessageBuffer() {
        return MessageBuffer.wrap(byteBuffer);
    }

    @Override
    public String toString() {
        if (decodedStringCache == null) {
            decodeString();
        }
        if (codingException != null) {
            throw codingException;
        }
        return decodedStringCache;
    }


    private synchronized void decodeString() {
        if (decodedStringCache != null) {
            return;
        }
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        readOnlyBuffer.position(0);
        try {
            CharsetDecoder reportDecoder = Charset.forName("UTF-8").newDecoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
            decodedStringCache = reportDecoder.decode(readOnlyBuffer).toString();
        } catch (UnsupportedCharsetException neverThrown) {
            throw new AssertionError(neverThrown);
        } catch (CharacterCodingException ex) {
            codingException = new MessageStringCodingException(ex);
            try {
                CharsetDecoder replaceDecoder = Charset.forName("UTF-8").newDecoder()
                        .onMalformedInput(CodingErrorAction.REPLACE)
                        .onUnmappableCharacter(CodingErrorAction.REPLACE);
                decodedStringCache = replaceDecoder.decode(readOnlyBuffer).toString();
            } catch (UnsupportedCharsetException neverThrown) {
                throw new AssertionError(neverThrown);
            } catch (CharacterCodingException neverThrown) {
                throw new AssertionError(neverThrown);
            }
        }
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
        if (!v.isBinary()) {
            return false;
        }
        BinaryValue bv = v.asBinary();
        return bv.toByteBuffer().equals(byteBuffer);
    }

    @Override
    public int hashCode() {
        return byteBuffer.hashCode();
    }

    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        packer.packBinaryHeader(byteBuffer.remaining());
        packer.writePayload(byteBuffer);
    }
}
