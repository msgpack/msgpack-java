package org.msgpack.value.holder;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageStringCodingException;
import org.msgpack.core.buffer.MessageBuffer;
import org.msgpack.value.*;
import org.msgpack.value.impl.AbstractValue;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.msgpack.core.MessagePackException.UNREACHABLE;


class RawHolderImpl extends AbstractValue implements RawValue {

    public static enum Type {
        STRING,
        BINARY
    }

    protected Type tpe;
    protected MessageBuffer buf;

    public void setString(MessageBuffer buf) {
        this.tpe = Type.STRING;
        this.buf = buf;
    }

    public void setBinary(MessageBuffer buf) {
        this.tpe = Type.BINARY;
        this.buf = buf;
    }

    public MessageBuffer getBuffer() { return buf; }

    @Override
    public byte[] toByteArray() {
        switch(tpe) {
            case STRING:
            case BINARY:
                return buf.toByteArray();
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public ByteBuffer toByteBuffer() {
        switch(tpe) {
            case STRING:
            case BINARY:
                return buf.toByteBuffer();
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public MessageBuffer toMessageBuffer() {
        return buf;
    }

    @Override
    public String toString() throws MessageStringCodingException {
        switch(tpe) {
            case STRING:
                return new String(buf.toByteArray(), MessagePack.UTF8);
            case BINARY:
                return buf.toHexString(0, buf.size());
            default:
                throw UNREACHABLE;
        }
    }


    @Override
    public ValueType getValueType() {
        switch(tpe) {
            case STRING:
                return ValueType.STRING;
            case BINARY:
                return ValueType.BINARY;
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        switch(tpe) {
            case STRING:
                packer.packRawStringHeader(buf.size()).writePayload(buf.toByteBuffer());
                break;
            case BINARY:
                packer.packBinaryHeader(buf.size()).writePayload(buf.toByteBuffer());
                break;
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public void accept(ValueVisitor visitor) {
        switch(tpe) {
            case STRING:
                visitor.visitString(this.asStringValue());
                break;
            case BINARY:
                visitor.visitBinary(this.asBinaryValue());
                break;
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public RawValue toImmutable() {
        switch(tpe) {
            case STRING:
                return ValueFactory.newRawString(buf.toByteArray());
            case BINARY:
                return ValueFactory.newBinary(buf.toByteArray());
            default:
                throw UNREACHABLE;
        }
    }

}


/**
 * Holder of the raw values
 */
public class RawHolder extends RawHolderImpl {

    private static class StringValueWrap extends RawHolderImpl implements StringValue {
        @Override
        public StringValue toImmutable() {
            return ValueFactory.newRawString(buf.toByteArray());
        }
    }

    private static class BinaryValueWrap extends RawHolderImpl implements BinaryValue {
        @Override
        public BinaryValue toValue() {
            return ValueFactory.newBinary(buf.toByteArray());
        }
    }

    private final StringValueWrap stringWrap = new StringValueWrap();
    private final BinaryValueWrap binaryWrap = new BinaryValueWrap();

    @Override
    public void setString(MessageBuffer buf) {
        this.tpe = Type.STRING;
        this.buf = buf;
        stringWrap.setString(buf);
    }

    @Override
    public void setBinary(MessageBuffer buf) {
        this.tpe = Type.BINARY;
        this.buf = buf;
        binaryWrap.setBinary(buf);
    }

    public MessageBuffer getBuffer() { return buf; }

    @Override
    public byte[] toByteArray() {
        switch(tpe) {
            case STRING:
            case BINARY:
                return buf.toByteArray();
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public ByteBuffer toByteBuffer() {
        switch(tpe) {
            case STRING:
            case BINARY:
                return buf.toByteBuffer();
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public MessageBuffer toMessageBuffer() {
        return buf;
    }

    @Override
    public String toString() throws MessageStringCodingException {
        switch(tpe) {
            case STRING:
                return new String(buf.toByteArray(), MessagePack.UTF8);
            case BINARY:
                return buf.toHexString(0, buf.size());
            default:
                throw UNREACHABLE;
        }
    }


    @Override
    public ValueType getValueType() {
        switch(tpe) {
            case STRING:
                return ValueType.STRING;
            case BINARY:
                return ValueType.BINARY;
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public void writeTo(MessagePacker packer) throws IOException {
        switch(tpe) {
            case STRING:
                packer.packRawStringHeader(buf.size()).writePayload(buf.toByteBuffer());
                break;
            case BINARY:
                packer.packBinaryHeader(buf.size()).writePayload(buf.toByteBuffer());
                break;
            default:
                throw UNREACHABLE;
        }
    }

    @Override
    public void accept(ValueVisitor visitor) {
        switch(tpe) {
            case STRING:
                visitor.visitString(this.asStringValue());
                break;
            case BINARY:
                visitor.visitBinary(this.asBinaryValue());
                break;
            default:
                throw UNREACHABLE;
        }
    }
    
    @Override
    public RawValue toImmutable() {
        switch(tpe) {
            case STRING:
                return ValueFactory.newRawString(buf.toByteArray());
            case BINARY:
                return ValueFactory.newBinary(buf.toByteArray());
            default:
                throw UNREACHABLE;
        }
     }


    @Override
    public StringValue asStringValue() {
        return stringWrap;
    }

    @Override
    public BinaryValue asBinaryValue() {
        return binaryWrap;
    }

}
