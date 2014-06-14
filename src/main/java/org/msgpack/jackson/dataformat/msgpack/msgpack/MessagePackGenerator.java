package org.msgpack.jackson.dataformat.msgpack.msgpack;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.GeneratorBase;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class MessagePackGenerator extends GeneratorBase {
    protected OutputStream out;

    protected MessagePackGenerator(int features, ObjectCodec codec, OutputStream out) {
        super(features, codec);
        this.out = out;
    }

    @Override
    public void writeStartArray() throws IOException, JsonGenerationException {

    }

    @Override
    public void writeEndArray() throws IOException, JsonGenerationException {

    }

    @Override
    public void writeStartObject() throws IOException, JsonGenerationException {

    }

    @Override
    public void writeEndObject() throws IOException, JsonGenerationException {

    }

    @Override
    public void writeFieldName(String name) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeString(String text) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeRaw(String text) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeRaw(char c) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeNumber(int v) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeNumber(long v) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeNumber(BigInteger v) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeNumber(double d) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeNumber(float f) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeNumber(BigDecimal dec) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException, UnsupportedOperationException {

    }

    @Override
    public void writeBoolean(boolean state) throws IOException, JsonGenerationException {

    }

    @Override
    public void writeNull() throws IOException, JsonGenerationException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    protected void _releaseBuffers() {

    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws IOException, JsonGenerationException {

    }
}
