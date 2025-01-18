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
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.SerializedString;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.annotations.Nullable;
import org.msgpack.core.buffer.MessageBufferOutput;
import org.msgpack.core.buffer.OutputStreamBufferOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.msgpack.jackson.dataformat.JavaInfo.STRING_VALUE_FIELD_IS_CHARS;

public class MessagePackGenerator
        extends GeneratorBase
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int IN_ROOT = 0;
    private static final int IN_OBJECT = 1;
    private static final int IN_ARRAY = 2;
    private final MessagePacker messagePacker;
    private static final ThreadLocal<OutputStreamBufferOutput> messageBufferOutputHolder = new ThreadLocal<>();
    private final OutputStream output;
    private final MessagePack.PackerConfig packerConfig;

    private int currentParentElementIndex = -1;
    private int currentState = IN_ROOT;
    private final List<Node> nodes;
    private boolean isElementsClosed = false;

    private static final class AsciiCharString
    {
        public final byte[] bytes;

        public AsciiCharString(byte[] bytes)
        {
            this.bytes = bytes;
        }
    }

    private abstract static class Node
    {
        // Root containers have -1.
        final int parentIndex;

        public Node(int parentIndex)
        {
            this.parentIndex = parentIndex;
        }

        abstract void incrementChildCount();

        abstract int currentStateAsParent();
    }

    private abstract static class NodeContainer extends Node
    {
        // Only for containers.
        int childCount;

        public NodeContainer(int parentIndex)
        {
            super(parentIndex);
        }

        @Override
        void incrementChildCount()
        {
            childCount++;
        }
    }

    private static final class NodeArray extends NodeContainer
    {
        public NodeArray(int parentIndex)
        {
            super(parentIndex);
        }

        @Override
        int currentStateAsParent()
        {
            return IN_ARRAY;
        }
    }

    private static final class NodeObject extends NodeContainer
    {
        public NodeObject(int parentIndex)
        {
            super(parentIndex);
        }

        @Override
        int currentStateAsParent()
        {
            return IN_OBJECT;
        }
    }

    private static final class NodeEntryInArray extends Node
    {
        final Object value;

        public NodeEntryInArray(int parentIndex, Object value)
        {
            super(parentIndex);
            this.value = value;
        }

        @Override
        void incrementChildCount()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        int currentStateAsParent()
        {
            throw new UnsupportedOperationException();
        }
    }

    private static final class NodeEntryInObject extends Node
    {
        final Object key;
        // Lazily initialized.
        Object value;

        public NodeEntryInObject(int parentIndex, Object key)
        {
            super(parentIndex);
            this.key = key;
        }

        @Override
        void incrementChildCount()
        {
            assert value instanceof NodeContainer;
            ((NodeContainer) value).childCount++;
        }

        @Override
        int currentStateAsParent()
        {
            if (value instanceof NodeObject) {
                return IN_OBJECT;
            }
            else if (value instanceof NodeArray) {
                return IN_ARRAY;
            }
            else {
                throw new AssertionError();
            }
        }
    }

    // This is an internal constructor for nested serialization.
    private MessagePackGenerator(
            int features,
            ObjectCodec codec,
            OutputStream out,
            MessagePack.PackerConfig packerConfig)
    {
        super(features, codec);
        this.output = out;
        this.messagePacker = packerConfig.newPacker(out);
        this.packerConfig = packerConfig;
        this.nodes = new ArrayList<>();
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
        this.messagePacker = packerConfig.newPacker(getMessageBufferOutputForOutputStream(out, reuseResourceInGenerator));
        this.packerConfig = packerConfig;
        this.nodes = new ArrayList<>();
    }

    private MessageBufferOutput getMessageBufferOutputForOutputStream(
            OutputStream out,
            boolean reuseResourceInGenerator)
            throws IOException
    {
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
        return messageBufferOutput;
    }

    private String currentStateStr()
    {
        switch (currentState) {
            case IN_OBJECT:
                return "IN_OBJECT";
            case IN_ARRAY:
                return "IN_ARRAY";
            default:
                return "IN_ROOT";
        }
    }

    @Override
    public void writeStartArray()
    {
        if (currentState == IN_OBJECT) {
            Node node = nodes.get(nodes.size() - 1);
            assert node instanceof NodeEntryInObject;
            NodeEntryInObject nodeEntryInObject = (NodeEntryInObject) node;
            nodeEntryInObject.value = new NodeArray(currentParentElementIndex);
        }
        else {
            nodes.add(new NodeArray(currentParentElementIndex));
        }
        currentParentElementIndex = nodes.size() - 1;
        currentState = IN_ARRAY;
    }

    @Override
    public void writeEndArray()
            throws IOException
    {
        if (currentState != IN_ARRAY) {
            _reportError("Current context not an array but " + currentStateStr());
        }
        endCurrentContainer();
    }

    @Override
    public void writeStartObject()
    {
        if (currentState == IN_OBJECT) {
            Node node = nodes.get(nodes.size() - 1);
            assert node instanceof NodeEntryInObject;
            NodeEntryInObject nodeEntryInObject = (NodeEntryInObject) node;
            nodeEntryInObject.value = new NodeObject(currentParentElementIndex);
        }
        else {
            nodes.add(new NodeObject(currentParentElementIndex));
        }
        currentParentElementIndex = nodes.size() - 1;
        currentState = IN_OBJECT;
    }

    @Override
    public void writeEndObject()
            throws IOException
    {
        if (currentState != IN_OBJECT) {
            _reportError("Current context not an object but " + currentStateStr());
        }
        endCurrentContainer();
    }

    private void endCurrentContainer()
    {
        Node parent = nodes.get(currentParentElementIndex);
        if (currentParentElementIndex == 0) {
            isElementsClosed = true;
            currentParentElementIndex = parent.parentIndex;
            return;
        }

        currentParentElementIndex = parent.parentIndex;
        assert currentParentElementIndex >= 0;
        Node currentParent = nodes.get(currentParentElementIndex);
        currentParent.incrementChildCount();
        currentState = currentParent.currentStateAsParent();
    }

    private void packNonContainer(Object v)
            throws IOException
    {
        MessagePacker messagePacker = getMessagePacker();
        if (v instanceof String) {
            messagePacker.packString((String) v);
        }
        else if (v instanceof AsciiCharString) {
            byte[] bytes = ((AsciiCharString) v).bytes;
            messagePacker.packRawStringHeader(bytes.length);
            messagePacker.writePayload(bytes);
        }
        else if (v instanceof Integer) {
            messagePacker.packInt((Integer) v);
        }
        else if (v == null) {
            messagePacker.packNil();
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
            packBigDecimal((BigDecimal) v);
        }
        else if (v instanceof Boolean) {
            messagePacker.packBoolean((Boolean) v);
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
        else if (v instanceof MessagePackExtensionType) {
            MessagePackExtensionType extensionType = (MessagePackExtensionType) v;
            byte[] extData = extensionType.getData();
            messagePacker.packExtensionTypeHeader(extensionType.getType(), extData.length);
            messagePacker.writePayload(extData);
        }
        else {
            messagePacker.flush();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MessagePackGenerator messagePackGenerator = new MessagePackGenerator(getFeatureMask(), getCodec(), outputStream, packerConfig);
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
        catch (ArithmeticException | IllegalArgumentException e) {
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

    private void packObject(NodeObject container)
            throws IOException
    {
        MessagePacker messagePacker = getMessagePacker();
        messagePacker.packMapHeader(container.childCount);
    }

    private void packArray(NodeArray container)
            throws IOException
    {
        MessagePacker messagePacker = getMessagePacker();
        messagePacker.packArrayHeader(container.childCount);
    }

    private void addKeyNode(Object key)
    {
        if (currentState != IN_OBJECT) {
            throw new IllegalStateException();
        }
        Node node = new NodeEntryInObject(currentParentElementIndex, key);
        nodes.add(node);
    }

    private void addValueNode(Object value) throws IOException
    {
        switch (currentState) {
            case IN_OBJECT: {
                Node node = nodes.get(nodes.size() - 1);
                assert node instanceof NodeEntryInObject;
                NodeEntryInObject nodeEntryInObject = (NodeEntryInObject) node;
                nodeEntryInObject.value = value;
                nodes.get(node.parentIndex).incrementChildCount();
                break;
            }
            case IN_ARRAY: {
                Node node = new NodeEntryInArray(currentParentElementIndex, value);
                nodes.add(node);
                nodes.get(node.parentIndex).incrementChildCount();
                break;
            }
            default:
                packNonContainer(value);
                flushMessagePacker();
                break;
        }
    }

    @Nullable
    private byte[] getBytesIfAscii(char[] chars, int offset, int len)
    {
        byte[] bytes = new byte[len];
        for (int i = offset; i < offset + len; i++) {
            char c = chars[i];
            if (c >= 0x80) {
                return null;
            }
            bytes[i] = (byte) c;
        }
        return bytes;
    }

    private boolean areAllAsciiBytes(byte[] bytes, int offset, int len)
    {
        for (int i = offset; i < offset + len; i++) {
            if ((bytes[i] & 0x80) != 0) {
                return false;
            }
        }
        return true;
    }

    private void writeCharArrayTextKey(char[] text, int offset, int len)
    {
        byte[] bytes = getBytesIfAscii(text, offset, len);
        if (bytes != null) {
            addKeyNode(new AsciiCharString(bytes));
            return;
        }
        addKeyNode(new String(text, offset, len));
    }

    private void writeCharArrayTextValue(char[] text, int offset, int len) throws IOException
    {
        byte[] bytes = getBytesIfAscii(text, offset, len);
        if (bytes != null) {
            addValueNode(new AsciiCharString(bytes));
            return;
        }
        addValueNode(new String(text, offset, len));
    }

    private void writeByteArrayTextValue(byte[] text, int offset, int len) throws IOException
    {
        if (areAllAsciiBytes(text, offset, len)) {
            addValueNode(new AsciiCharString(text));
            return;
        }
        addValueNode(new String(text, offset, len, DEFAULT_CHARSET));
    }

    private void writeByteArrayTextKey(byte[] text, int offset, int len) throws IOException
    {
        if (areAllAsciiBytes(text, offset, len)) {
            addValueNode(new AsciiCharString(text));
            return;
        }
        addValueNode(new String(text, offset, len, DEFAULT_CHARSET));
    }

    // TODO: Uncomment
    //@Override
    //public void writeFieldId(long id) throws IOException
    //{
    //    addKeyToStackTop(id);
    //}

    @Override
    public void writeFieldName(String name)
    {
        if (STRING_VALUE_FIELD_IS_CHARS.get()) {
            char[] chars = name.toCharArray();
            writeCharArrayTextKey(chars, 0, chars.length);
        }
        else {
            addKeyNode(name);
        }
    }

    @Override
    public void writeFieldName(SerializableString name)
    {
        if (name instanceof SerializedString) {
            writeFieldName(name.getValue());
        }
        else if (name instanceof MessagePackSerializedString) {
            addKeyNode(((MessagePackSerializedString) name).getRawValue());
        }
        else {
            throw new IllegalArgumentException("Unsupported key: " + name);
        }
    }

    @Override
    public void writeString(String text)
            throws IOException
    {
        if (STRING_VALUE_FIELD_IS_CHARS.get()) {
            char[] chars = text.toCharArray();
            writeCharArrayTextValue(chars, 0, chars.length);
        }
        else {
            addValueNode(text);
        }
    }

    @Override
    public void writeString(char[] text, int offset, int len)
            throws IOException
    {
        writeCharArrayTextValue(text, offset, len);
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length)
            throws IOException
    {
        writeByteArrayTextValue(text, offset, length);
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length)
            throws IOException
    {
        writeByteArrayTextValue(text, offset, length);
    }

    @Override
    public void writeRaw(String text)
            throws IOException
    {
        if (STRING_VALUE_FIELD_IS_CHARS.get()) {
            char[] chars = text.toCharArray();
            writeCharArrayTextValue(chars, 0, chars.length);
        }
        else {
            addValueNode(text);
        }
    }

    @Override
    public void writeRaw(String text, int offset, int len)
            throws IOException
    {
        // TODO: There is room to optimize this.
        char[] chars = text.toCharArray();
        writeCharArrayTextValue(chars, offset, len);
    }

    @Override
    public void writeRaw(char[] text, int offset, int len)
            throws IOException
    {
        writeCharArrayTextValue(text, offset, len);
    }

    @Override
    public void writeRaw(char c)
            throws IOException
    {
        writeCharArrayTextValue(new char[] { c }, 0, 1);
    }

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
            throws IOException
    {
        addValueNode(ByteBuffer.wrap(data, offset, len));
    }

    @Override
    public void writeNumber(int v)
            throws IOException
    {
        addValueNode(v);
    }

    @Override
    public void writeNumber(long v)
            throws IOException
    {
        addValueNode(v);
    }

    @Override
    public void writeNumber(BigInteger v)
            throws IOException
    {
        addValueNode(v);
    }

    @Override
    public void writeNumber(double d)
            throws IOException
    {
        addValueNode(d);
    }

    @Override
    public void writeNumber(float f)
            throws IOException
    {
        addValueNode(f);
    }

    @Override
    public void writeNumber(BigDecimal dec)
            throws IOException
    {
        addValueNode(dec);
    }

    @Override
    public void writeNumber(String encodedValue)
            throws IOException, UnsupportedOperationException
    {
        // There is a room to improve this API's performance while the implementation is robust.
        // If users can use other MessagePackGenerator#writeNumber APIs that accept
        // proper numeric types not String, it's better to use the other APIs instead.
        try {
            long l = Long.parseLong(encodedValue);
            addValueNode(l);
            return;
        }
        catch (NumberFormatException ignored) {
        }

        try {
            double d = Double.parseDouble(encodedValue);
            addValueNode(d);
            return;
        }
        catch (NumberFormatException ignored) {
        }

        try {
            BigInteger bi = new BigInteger(encodedValue);
            addValueNode(bi);
            return;
        }
        catch (NumberFormatException ignored) {
        }

        try {
            BigDecimal bc = new BigDecimal(encodedValue);
            addValueNode(bc);
            return;
        }
        catch (NumberFormatException ignored) {
        }

        throw new NumberFormatException(encodedValue);
    }

    @Override
    public void writeBoolean(boolean state)
            throws IOException
    {
        addValueNode(state);
    }

    @Override
    public void writeNull()
            throws IOException
    {
        addValueNode(null);
    }

    public void writeExtensionType(MessagePackExtensionType extensionType)
            throws IOException
    {
        addValueNode(extensionType);
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
        if (!isElementsClosed) {
            // The whole elements are not closed yet.
            return;
        }

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof NodeEntryInObject) {
                NodeEntryInObject nodeEntry = (NodeEntryInObject) node;
                packNonContainer(nodeEntry.key);
                if (nodeEntry.value instanceof NodeObject) {
                    packObject((NodeObject) nodeEntry.value);
                }
                else if (nodeEntry.value instanceof NodeArray) {
                    packArray((NodeArray) nodeEntry.value);
                }
                else {
                    packNonContainer(nodeEntry.value);
                }
            }
            else if (node instanceof NodeObject) {
                packObject((NodeObject) node);
            }
            else if (node instanceof NodeEntryInArray) {
                packNonContainer(((NodeEntryInArray) node).value);
            }
            else if (node instanceof NodeArray) {
                packArray((NodeArray) node);
            }
            else {
                throw new AssertionError();
            }
        }
        flushMessagePacker();
        nodes.clear();
        isElementsClosed = false;
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
        try {
            messagePacker.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to close MessagePacker", e);
        }
    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws IOException
    {
        // FIXME?
    }

    private MessagePacker getMessagePacker()
    {
        return messagePacker;
    }
}
