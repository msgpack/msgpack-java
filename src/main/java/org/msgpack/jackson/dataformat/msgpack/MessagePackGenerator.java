package org.msgpack.jackson.dataformat.msgpack;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.MessageBufferOutput;
import org.msgpack.core.buffer.OutputStreamBufferOutput;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MessagePackGenerator extends GeneratorBase {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static ThreadLocal<MessagePacker> messagePackersHolder = new ThreadLocal<MessagePacker>();
    private static ThreadLocal<OutputStreamBufferOutput> messageBufferOutputHolder = new ThreadLocal<OutputStreamBufferOutput>();
    private LinkedList<StackItem> stack;
    private StackItem rootStackItem;


    private static abstract class StackItem {
        protected List<String> objectKeys = new ArrayList<String>();
        protected List<Object> objectValues = new ArrayList<Object>();

        abstract void addKey(String key);

        void addValue(Object value) {
            objectValues.add(value);
        }

        abstract List<String> getKeys();

        List<Object> getValues() {
            return objectValues;
        }
    }

    private static class StackItemForObject extends StackItem {
        @Override
        void addKey(String key) {
            objectKeys.add(key);
        }

        @Override
        List<String> getKeys() {
            return objectKeys;
        }
    }

    private static class StackItemForArray extends StackItem {
        @Override
        void addKey(String key) {
            throw new IllegalStateException("This method shouldn't be called");
        }

        @Override
        List<String> getKeys() {
            throw new IllegalStateException("This method shouldn't be called");
        }
    }

    public MessagePackGenerator(int features, ObjectCodec codec, OutputStream out) throws IOException {
        super(features, codec);
        MessagePacker messagePacker = messagePackersHolder.get();
        OutputStreamBufferOutput messageBufferOutput = messageBufferOutputHolder.get();
        if (messageBufferOutput == null) {
            messageBufferOutput = new OutputStreamBufferOutput(out);
        }
        else {
            messageBufferOutput.reset(out);
        }
        messageBufferOutputHolder.set(messageBufferOutput);

        if (messagePacker == null) {
            messagePacker = new MessagePacker(messageBufferOutput);
        }
        else {
            messagePacker.reset(messageBufferOutput);
        }
        messagePackersHolder.set(messagePacker);

        this.stack = new LinkedList<StackItem>();
    }

    @Override
    public void writeStartArray() throws IOException, JsonGenerationException {
        _writeContext = _writeContext.createChildArrayContext();
        stack.push(new StackItemForArray());
    }

    @Override
    public void writeEndArray() throws IOException, JsonGenerationException {
        if (!_writeContext.inArray()) {
            _reportError("Current context not an array but " + _writeContext.getTypeDesc());
        }

        getStackTopForArray();

        _writeContext = _writeContext.getParent();

        popStackAndStoreTheItemAsValue();
    }

    @Override
    public void writeStartObject() throws IOException, JsonGenerationException {
        _writeContext = _writeContext.createChildObjectContext();
        stack.push(new StackItemForObject());
    }

    @Override
    public void writeEndObject() throws IOException, JsonGenerationException {
        if (!_writeContext.inObject()) {
            _reportError("Current context not an object but " + _writeContext.getTypeDesc());
        }

        StackItemForObject stackTop = getStackTopForObject();

        if (stackTop.getKeys().size() != stackTop.getValues().size()) {
            throw new IllegalStateException(
                    String.format(
                            "objectKeys.size() and objectValues.size() is not same: depth=%d, key=%d, value=%d",
                            stack.size(), stackTop.getKeys().size(), stackTop.getValues().size()));
        }
        _writeContext = _writeContext.getParent();

        popStackAndStoreTheItemAsValue();
    }

    private void packValue(Object v) throws IOException {
        MessagePacker messagePacker = getMessagePacker();
        if (v == null) {
            messagePacker.packNil();
        }
        else if (v instanceof Integer) {
            messagePacker.packInt((Integer) v);
        }
        else if (v instanceof ByteBuffer) {
            ByteBuffer bb = (ByteBuffer) v;
            messagePacker.packBinaryHeader(bb.limit());
            messagePacker.writePayload(bb);
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
        else if (v instanceof StackItemForObject) {
            packObject((StackItemForObject) v);
        }
        else if (v instanceof StackItemForArray) {
            packArray((StackItemForArray) v);
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
        else {
            throw new IllegalArgumentException(v.toString());
        }
    }

    private void packObject(StackItemForObject stackItem) throws IOException {
        List<String> keys = stackItem.getKeys();
        List<Object> values = stackItem.getValues();

        MessagePacker messagePacker = getMessagePacker();
        messagePacker.packMapHeader(keys.size());

        for (int i = 0; i < keys.size(); i++) {
            messagePacker.packString(keys.get(i));
            Object v = values.get(i);
            packValue(v);
        }
    }

    private void packArray(StackItemForArray stackItem) throws IOException {
        List<Object> values = stackItem.getValues();

        MessagePacker messagePacker = getMessagePacker();
        messagePacker.packArrayHeader(values.size());

        for (int i = 0; i < values.size(); i++) {
            Object v = values.get(i);
            packValue(v);
        }
    }

    @Override
    public void writeFieldName(String name) throws IOException, JsonGenerationException {
        addKeyToStackTop(name);
    }

    @Override
    public void writeString(String text) throws IOException, JsonGenerationException {
        addValueToStackTop(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        addValueToStackTop(new String(text, offset, len));
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        addValueToStackTop(new String(text, offset, length, DEFAULT_CHARSET));
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException, JsonGenerationException {
        addValueToStackTop(new String(text, offset, length, DEFAULT_CHARSET));
    }

    @Override
    public void writeRaw(String text) throws IOException, JsonGenerationException {
        addValueToStackTop(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException, JsonGenerationException {
        addValueToStackTop(text.substring(0, len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException {
        addValueToStackTop(new String(text, offset, len));
    }

    @Override
    public void writeRaw(char c) throws IOException, JsonGenerationException {
        addValueToStackTop(String.valueOf(c));
    }

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        addValueToStackTop(ByteBuffer.wrap(data, offset, len));
    }

    @Override
    public void writeNumber(int v) throws IOException, JsonGenerationException {
        addValueToStackTop(Integer.valueOf(v));
    }

    @Override
    public void writeNumber(long v) throws IOException, JsonGenerationException {
        addValueToStackTop(Long.valueOf(v));
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException, JsonGenerationException {
        addValueToStackTop(v);
    }

    @Override
    public void writeNumber(double d) throws IOException, JsonGenerationException {
        addValueToStackTop(Double.valueOf(d));
    }

    @Override
    public void writeNumber(float f) throws IOException, JsonGenerationException {
        addValueToStackTop(Float.valueOf(f));
    }

    @Override
    public void writeNumber(BigDecimal dec) throws IOException, JsonGenerationException {
        addValueToStackTop(dec);
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException, JsonGenerationException, UnsupportedOperationException {
        throw new NotImplementedException();
    }

    @Override
    public void writeBoolean(boolean state) throws IOException, JsonGenerationException {
        addValueToStackTop(Boolean.valueOf(state));
    }

    @Override
    public void writeNull() throws IOException, JsonGenerationException {
        addValueToStackTop(null);
    }

    @Override
    public void close() throws IOException {
        flush();
    }

    @Override
    public void flush() throws IOException {
        if (rootStackItem != null) {
            if (rootStackItem instanceof StackItemForObject) {
                packObject((StackItemForObject) rootStackItem);
            }
            else if (rootStackItem instanceof StackItemForArray) {
                packArray((StackItemForArray) rootStackItem);
            }
            else {
                throw new IllegalStateException("Unexpected rootStackItem: " + rootStackItem);
            }
            MessagePacker messagePacker = getMessagePacker();
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

    private StackItem getStackTop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("The stack is empty");
        }
        return stack.getFirst();
    }

    private StackItemForObject getStackTopForObject() {
        StackItem stackTop = getStackTop();
        if (!(stackTop instanceof StackItemForObject)) {
            throw new IllegalStateException("The stack top should be Object: " + stackTop);
        }
        return (StackItemForObject) stackTop;
    }

    private StackItemForArray getStackTopForArray() {
        StackItem stackTop = getStackTop();
        if (!(stackTop instanceof StackItemForArray)) {
            throw new IllegalStateException("The stack top should be Array: " + stackTop);
        }
        return (StackItemForArray) stackTop;
    }

    private void addKeyToStackTop(String key) {
        getStackTop().addKey(key);
    }

    private void addValueToStackTop(Object value) {
        getStackTop().addValue(value);
    }

    private void popStackAndStoreTheItemAsValue() {
        StackItem child = stack.pop();
        if (stack.size() > 0) {
            addValueToStackTop(child);
        }
        else {
            if (rootStackItem != null) {
                throw new IllegalStateException("rootStackItem is not null");
            }
            else {
                rootStackItem = child;
            }
        }
    }

    private MessagePacker getMessagePacker() {
        MessagePacker messagePacker = messagePackersHolder.get();
        if (messagePacker == null) {
            throw new IllegalStateException("messagePacker is null");
        }
        return messagePacker;
    }
}
