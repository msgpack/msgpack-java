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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MessagePackGenerator extends GeneratorBase {
    private MessagePacker messagePacker;
    private LinkedList<StackItem> stack;
    private StackItem lastItem;

    private static class StackItem {
        List<String> objectKeys = new ArrayList<String>();
        List<Object> objectValues = new ArrayList<Object>();
    }

    public MessagePackGenerator(int features, ObjectCodec codec, OutputStream out) {
        super(features, codec);
        this.messagePacker = new MessagePacker(out);
        this.stack = new LinkedList<StackItem>();
    }

    @Override
    public void writeStartArray() throws IOException, JsonGenerationException {

    }

    @Override
    public void writeEndArray() throws IOException, JsonGenerationException {

    }

    @Override
    public void writeStartObject() throws IOException, JsonGenerationException {
        _writeContext = _writeContext.createChildObjectContext();
        stack.push(new StackItem());
    }

    @Override
    public void writeEndObject() throws IOException, JsonGenerationException {
        if (!_writeContext.inObject()) {
            _reportError("Current context not an object but " + _writeContext.getTypeDesc());
        }

        if (getCurrentObjectKeys().size() != getCurrentObjectValues().size()) {
            throw new IllegalStateException(
                    String.format(
                            "objectKeys.size() and objectValues.size() is not same: depth=%d, key=%d, value=%d",
                            stack.size(), getCurrentObjectKeys().size(), getCurrentObjectValues().size()));
        }
        _writeContext = _writeContext.getParent();
        StackItem child = stack.pop();
        if (stack.size() > 0) {
            getCurrentObjectValues().add(child);
        }
        else {
            if (lastItem != null) {
                throw new IllegalStateException("lastItem is not null");
            }
            else {
                lastItem = child;
            }
        }
    }

    private void packObject(StackItem stackItem) throws IOException {
        List<String> keys = stackItem.objectKeys;
        List<Object> values = stackItem.objectValues;

        messagePacker.packMapHeader(keys.size());

        for (int i = 0; i < keys.size(); i++) {
            messagePacker.packString(keys.get(i));
            Object v = values.get(i);
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
            else if (v instanceof StackItem) {
                // TODO: for now, this is as a Object
                packObject((StackItem) v);
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
            // TODO: for Array
        }
    }

    @Override
    public void writeFieldName(String name) throws IOException, JsonGenerationException {
        getCurrentObjectKeys().add(name);
    }

    @Override
    public void writeString(String text) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(new String(text, offset, len));
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(new String(text, offset, length));
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(new String(text, offset, length));
    }

    @Override
    public void writeRaw(String text) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(text.substring(0, len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(new String(text, offset, len));
    }

    @Override
    public void writeRaw(char c) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(String.valueOf(c));
    }

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        throw new NotImplementedException();
    }

    @Override
    public void writeNumber(int v) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(Integer.valueOf(v));
    }

    @Override
    public void writeNumber(long v) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(Long.valueOf(v));
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(v);
    }

    @Override
    public void writeNumber(double d) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(Double.valueOf(d));
    }

    @Override
    public void writeNumber(float f) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(Float.valueOf(f));
    }

    @Override
    public void writeNumber(BigDecimal dec) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(dec);
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException, UnsupportedOperationException {
        throw new NotImplementedException();
    }

    @Override
    public void writeBoolean(boolean state) throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(Boolean.valueOf(state));
    }

    @Override
    public void writeNull() throws IOException, JsonGenerationException {
        getCurrentObjectValues().add(null);
    }

    @Override
    public void close() throws IOException {
        flush();
    }

    @Override
    public void flush() throws IOException {
        if (lastItem != null) {
            packObject(lastItem);
            messagePacker.flush();
        }
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

    private StackItem getCurrentStackItem() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("The stack is empty");
        }
        return stack.getFirst();
    }

    private List<String> getCurrentObjectKeys() {
        return getCurrentStackItem().objectKeys;
    }

    private List<Object> getCurrentObjectValues() {
        return getCurrentStackItem().objectValues;
    }
}
