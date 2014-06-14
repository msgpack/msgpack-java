package org.msgpack.jackson.dataformat.msgpack.msgpack;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import org.msgpack.core.MessagePacker;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MessagePackGenerator extends GeneratorBase {
    private MessagePacker messagePacker;
    // TODO: manage these collection as a stack
    private List<String> objectKeys;
    private List<Object> objectValues;

    public MessagePackGenerator(int features, ObjectCodec codec, OutputStream out) {
        super(features, codec);
        this.messagePacker = new MessagePacker(out);
    }

    @Override
    public void writeStartArray() throws IOException, JsonGenerationException {

    }

    @Override
    public void writeEndArray() throws IOException, JsonGenerationException {

    }

    @Override
    public void writeStartObject() throws IOException, JsonGenerationException {
        _verifyValueWrite("start an object");
        _writeContext = _writeContext.createChildObjectContext();
        if (objectKeys != null || objectValues != null) {
            throw new IllegalStateException("objectKeys or objectValues is not null");
        }
        objectKeys = new ArrayList<String>();
        objectValues = new ArrayList<Object>();
    }

    @Override
    public void writeEndObject() throws IOException, JsonGenerationException {
        if (!_writeContext.inObject()) {
            _reportError("Current context not an object but "+_writeContext.getTypeDesc());
        }

        assertObjectKeysIsNotNull();
        assertObjectValuesIsNotNull();

        if (objectKeys.size() != objectValues.size()) {
            throw new IllegalStateException(
                    String.format(
                            "objectKeys.size() and objectValues.size() is not same: key=%d, value=%d",
                            objectKeys.size(), objectValues.size()));
        }
        int len = objectKeys.size();
        
        messagePacker.packMapHeader(len);
        for (int i = 0; i < len; i++) {
            messagePacker.packString(objectKeys.get(i));
            Object v = objectValues.get(i);
            if (v instanceof Integer) {
                messagePacker.packInt((Integer) v);
            }
            else if (v instanceof String) {
                messagePacker.packString((String) v);
            }
            else if (v instanceof Float) {
                messagePacker.packFloat((Float) v);
            }
            else if (v instanceof Long) {
                messagePacker.packLong((Long) v);
            }
            else if (v instanceof Double) {
                messagePacker.packDouble((Double) v);
            }
            else if (v instanceof BigInteger) {
                messagePacker.packBigInteger((BigInteger) v);
            }
            else if (v instanceof BigDecimal) {
                // TODO
                throw new NotImplementedException();
            }
            // TODO: for Map and Array
        }
        
        _writeContext = _writeContext.getParent();
    }

    @Override
    public void writeFieldName(String name) throws IOException, JsonGenerationException {
        assertObjectKeysIsNotNull();

        objectKeys.add(name);
    }

    @Override
    public void writeString(String text) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(new String(text, offset, len));
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(new String(text, offset, length));
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(new String(text, offset, length));
    }

    @Override
    public void writeRaw(String text) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(text.substring(0, len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(new String(text, offset, len));
    }

    @Override
    public void writeRaw(char c) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(String.valueOf(c));
    }

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        throw new NotImplementedException();
    }

    @Override
    public void writeNumber(int v) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(Integer.valueOf(v));
    }

    @Override
    public void writeNumber(long v) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(Long.valueOf(v));
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(v);
    }

    @Override
    public void writeNumber(double d) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(Double.valueOf(d));
    }

    @Override
    public void writeNumber(float f) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(Float.valueOf(f));
    }

    @Override
    public void writeNumber(BigDecimal dec) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(dec);
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException, UnsupportedOperationException {
        assertObjectValuesIsNotNull();

        throw new NotImplementedException();
    }

    @Override
    public void writeBoolean(boolean state) throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(Boolean.valueOf(state));
    }

    @Override
    public void writeNull() throws IOException, JsonGenerationException {
        assertObjectValuesIsNotNull();

        objectValues.add(null);
    }

    @Override
    public void close() throws IOException {
        flush();
    }

    @Override
    public void flush() throws IOException {
        messagePacker.flush();
    }

    @Override
    protected void _releaseBuffers() {

    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws IOException, JsonGenerationException {
        int status = _writeContext.writeValue();
        if (status == JsonWriteContext.STATUS_EXPECT_NAME) {
            _reportError("Can not "+typeMsg+", expecting field name");
        }
    }

    private void assertObjectKeysIsNotNull() {
        if (objectKeys == null) {
            throw new IllegalStateException("objectKeys is null");
        }
    }

    private void assertObjectValuesIsNotNull() {
        if (objectValues == null) {
            throw new IllegalStateException("objectValues is null");
        }
    }
}
