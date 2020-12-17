//
// MessagePack for Java
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.jackson.dataformat;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.OutputStreamBufferOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MessagePackGenerator
        extends GeneratorBase
{
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private final MessagePacker messagePacker;
    private static ThreadLocal<OutputStreamBufferOutput> messageBufferOutputHolder = new ThreadLocal<OutputStreamBufferOutput>();
    private final OutputStream output;
    private final MessagePack.PackerConfig packerConfig;
    private LinkedList<StackItem> stack;
    private StackItem rootStackItem;

    private abstract static class StackItem
    {
        protected List<Object> objectKeys = new ArrayList<Object>();
        protected List<Object> objectValues = new ArrayList<Object>();

        abstract void addKey(Object key);

        void addValue(Object value)
        {
            objectValues.add(value);
        }

        abstract List<Object> getKeys();

        List<Object> getValues()
        {
            return objectValues;
        }
    }

    private static class StackItemForObject
            extends StackItem
    {
        @Override
        void addKey(Object key)
        {
            objectKeys.add(key);
        }

        @Override
        List<Object> getKeys()
        {
            return objectKeys;
        }
    }

    private static class StackItemForArray
            extends StackItem
    {
        @Override
        void addKey(Object key)
        {
            throw new IllegalStateException("This method shouldn't be called");
        }

        @Override
        List<Object> getKeys()
        {
            throw new IllegalStateException("This method shouldn't be called");
        }
    }

    public MessagePackGenerator(
            int features,
            ObjectCodec codec,
            OutputStream out,
            MessagePack.PackerConfig packerConfig,
            boolean reuseResourceInGenerator)
            throws IOException
    {
        super(features, codec);
        this.output = out;

        OutputStreamBufferOutput messageBufferOutput;
        if (reuseResourceInGenerator) {
            messageBufferOutput = messageBufferOutputHolder.get();
            if (messageBufferOutput == null) {
                messageBufferOutput = new OutputStreamBufferOutput(out);
                messageBufferOutputHolder.set(messageBufferOutput);
            }
            else {
                messageBufferOutput.reset(out);
            }
        }
        else {
            messageBufferOutput = new OutputStreamBufferOutput(out);
        }
        this.messagePacker = packerConfig.newPacker(messageBufferOutput);

        this.packerConfig = packerConfig;

        this.stack = new LinkedList<StackItem>();
    }

    @Override
    public void writeStartArray()
            throws IOException, JsonGenerationException
    {
        _writeContext = _writeContext.createChildArrayContext();
        stack.push(new StackItemForArray());
    }

    @Override
    public void writeEndArray()
            throws IOException, JsonGenerationException
    {
        if (!_writeContext.inArray()) {
            _reportError("Current context not an array but " + _writeContext.getTypeDesc());
        }

        getStackTopForArray();

        _writeContext = _writeContext.getParent();

        popStackAndStoreTheItemAsValue();
    }

    @Override
    public void writeStartObject()
            throws IOException, JsonGenerationException
    {
        _writeContext = _writeContext.createChildObjectContext();
        stack.push(new StackItemForObject());
    }

    @Override
    public void writeEndObject()
            throws IOException, JsonGenerationException
    {
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

    private void pack(Object v)
            throws IOException
    {
        MessagePacker messagePacker = getMessagePacker();
        if (v == null) {
            messagePacker.packNil();
        }
        else if (v instanceof Integer) {
            messagePacker.packInt((Integer) v);
        }
        else if (v instanceof ByteBuffer) {
            ByteBuffer bb = (ByteBuffer) v;
            int len = bb.remaining();
            if (bb.hasArray()) {
                messagePacker.packBinaryHeader(len);
                messagePacker.writePayload(bb.array(), bb.arrayOffset(), len);
            }
            else {
                byte[] data = new byte[len];
                bb.get(data);
                messagePacker.packBinaryHeader(len);
                messagePacker.addPayload(data);
            }
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
            packBigDecimal((BigDecimal) v);
        }
        else if (v instanceof Boolean) {
            messagePacker.packBoolean((Boolean) v);
        }
        else if (v instanceof MessagePackExtensionType) {
            MessagePackExtensionType extensionType = (MessagePackExtensionType) v;
            byte[] extData = extensionType.getData();
            messagePacker.packExtensionTypeHeader(extensionType.getType(), extData.length);
            messagePacker.writePayload(extData);
        }
        else {
            messagePacker.flush();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MessagePackGenerator messagePackGenerator = new MessagePackGenerator(getFeatureMask(), getCodec(), outputStream, packerConfig, false);
            getCodec().writeValue(messagePackGenerator, v);
            output.write(outputStream.toByteArray());
        }
    }

    private void packBigDecimal(BigDecimal decimal)
            throws IOException
    {
        MessagePacker messagePacker = getMessagePacker();
        boolean failedToPackAsBI = false;
        try {
            //Check to see if this BigDecimal can be converted to BigInteger
            BigInteger integer = decimal.toBigIntegerExact();
            messagePacker.packBigInteger(integer);
        }
        catch (ArithmeticException e) {
            failedToPackAsBI = true;
        }
        catch (IllegalArgumentException e) {
            failedToPackAsBI = true;
        }

        if (failedToPackAsBI) {
            double doubleValue = decimal.doubleValue();
            //Check to make sure this BigDecimal can be represented as a double
            if (!decimal.stripTrailingZeros().toEngineeringString().equals(
                    BigDecimal.valueOf(doubleValue).stripTrailingZeros().toEngineeringString())) {
                throw new IllegalArgumentException("MessagePack cannot serialize a BigDecimal that can't be represented as double. " + decimal);
            }
            messagePacker.packDouble(doubleValue);
        }
    }

    private void packObject(StackItemForObject stackItem)
            throws IOException
    {
        List<Object> keys = stackItem.getKeys();
        List<Object> values = stackItem.getValues();

        MessagePacker messagePacker = getMessagePacker();
        messagePacker.packMapHeader(keys.size());

        for (int i = 0; i < keys.size(); i++) {
            pack(keys.get(i));
            pack(values.get(i));
        }
    }

    private void packArray(StackItemForArray stackItem)
            throws IOException
    {
        List<Object> values = stackItem.getValues();

        MessagePacker messagePacker = getMessagePacker();
        messagePacker.packArrayHeader(values.size());

        for (int i = 0; i < values.size(); i++) {
            Object v = values.get(i);
            pack(v);
        }
    }

    @Override
    public void writeFieldName(String name)
            throws IOException, JsonGenerationException
    {
        addKeyToStackTop(name);
    }

    @Override
    public void writeFieldName(SerializableString name)
            throws IOException
    {
        if (name instanceof MessagePackSerializedString) {
            addKeyToStackTop(((MessagePackSerializedString) name).getRawValue());
        }
        else if (name instanceof SerializedString) {
            addKeyToStackTop(name.getValue());
        }
        else {
            System.out.println(name.getClass());
            throw new IllegalArgumentException("Unsupported key: " + name);
        }
    }

    @Override
    public void writeString(String text)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(new String(text, offset, len));
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(new String(text, offset, length, DEFAULT_CHARSET));
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(new String(text, offset, length, DEFAULT_CHARSET));
    }

    @Override
    public void writeRaw(String text)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(text.substring(0, len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(new String(text, offset, len));
    }

    @Override
    public void writeRaw(char c)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(String.valueOf(c));
    }

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(ByteBuffer.wrap(data, offset, len));
    }

    @Override
    public void writeNumber(int v)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(Integer.valueOf(v));
    }

    @Override
    public void writeNumber(long v)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(Long.valueOf(v));
    }

    @Override
    public void writeNumber(BigInteger v)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(v);
    }

    @Override
    public void writeNumber(double d)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(Double.valueOf(d));
    }

    @Override
    public void writeNumber(float f)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(Float.valueOf(f));
    }

    @Override
    public void writeNumber(BigDecimal dec)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(dec);
    }

    @Override
    public void writeNumber(String encodedValue)
            throws IOException, JsonGenerationException, UnsupportedOperationException
    {
        // There is a room to improve this API's performance while the implementation is robust.
        // If users can use other MessagePackGenerator#writeNumber APIs that accept
        // proper numeric types not String, it's better to use the other APIs instead.
        try {
            long l = Long.parseLong(encodedValue);
            addValueToStackTop(l);
            return;
        }
        catch (NumberFormatException e) {
        }

        try {
            double d = Double.parseDouble(encodedValue);
            addValueToStackTop(d);
            return;
        }
        catch (NumberFormatException e) {
        }

        try {
            BigInteger bi = new BigInteger(encodedValue);
            addValueToStackTop(bi);
            return;
        }
        catch (NumberFormatException e) {
        }

        try {
            BigDecimal bc = new BigDecimal(encodedValue);
            addValueToStackTop(bc);
            return;
        }
        catch (NumberFormatException e) {
        }

        throw new NumberFormatException(encodedValue);
    }

    @Override
    public void writeBoolean(boolean state)
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(Boolean.valueOf(state));
    }

    @Override
    public void writeNull()
            throws IOException, JsonGenerationException
    {
        addValueToStackTop(null);
    }

    public void writeExtensionType(MessagePackExtensionType extensionType)
            throws IOException
    {
        addValueToStackTop(extensionType);
    }

    @Override
    public void close()
            throws IOException
    {
        try {
            flush();
        }
        finally {
            if (isEnabled(Feature.AUTO_CLOSE_TARGET)) {
                MessagePacker messagePacker = getMessagePacker();
                messagePacker.close();
            }
        }
    }

    @Override
    public void flush()
            throws IOException
    {
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
            rootStackItem = null;
            flushMessagePacker();
        }
    }

    private void flushMessagePacker()
            throws IOException
    {
        MessagePacker messagePacker = getMessagePacker();
        messagePacker.flush();
    }

    @Override
    protected void _releaseBuffers()
    {
    }

    @Override
    protected void _verifyValueWrite(String typeMsg)
            throws IOException, JsonGenerationException
    {
        int status = _writeContext.writeValue();
        if (status == JsonWriteContext.STATUS_EXPECT_NAME) {
            _reportError("Can not " + typeMsg + ", expecting field name");
        }
    }

    private StackItem getStackTop()
    {
        if (stack.isEmpty()) {
            throw new IllegalStateException("The stack is empty");
        }
        return stack.getFirst();
    }

    private StackItemForObject getStackTopForObject()
    {
        StackItem stackTop = getStackTop();
        if (!(stackTop instanceof StackItemForObject)) {
            throw new IllegalStateException("The stack top should be Object: " + stackTop);
        }
        return (StackItemForObject) stackTop;
    }

    private StackItemForArray getStackTopForArray()
    {
        StackItem stackTop = getStackTop();
        if (!(stackTop instanceof StackItemForArray)) {
            throw new IllegalStateException("The stack top should be Array: " + stackTop);
        }
        return (StackItemForArray) stackTop;
    }

    private void addKeyToStackTop(Object key)
    {
        getStackTop().addKey(key);
    }

    private void addValueToStackTop(Object value)
            throws IOException
    {
        if (stack.isEmpty()) {
            pack(value);
            flushMessagePacker();
        }
        else {
            getStackTop().addValue(value);
        }
    }

    private void popStackAndStoreTheItemAsValue()
            throws IOException
    {
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

    private MessagePacker getMessagePacker()
    {
        return messagePacker;
    }
}
