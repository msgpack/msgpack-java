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

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JacksonException;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.SerializableString;
import tools.jackson.core.StreamWriteCapability;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.io.IOContext;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.buffer.MessageBufferOutput;
import org.msgpack.core.buffer.OutputStreamBufferOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MessagePackGenerator
        extends GeneratorBase
{
    private static final int IN_ROOT = 0;
    private static final int IN_OBJECT = 1;
    private static final int IN_ARRAY = 2;
    private final MessagePacker messagePacker;
    // Retained heap per idle thread: ~8 KB (OutputStreamBufferOutput + internal MessageBuffer).
    // Negligible compared to Jackson's own per-thread buffer retention.
    private static final ThreadLocal<OutputStreamBufferOutput> messageBufferOutputHolder = new ThreadLocal<>();
    private final OutputStream output;
    private final MessagePack.PackerConfig packerConfig;
    private final boolean supportIntegerKeys;

    private int currentParentElementIndex = -1;
    private int currentState = IN_ROOT;
    private final List<Node> nodes;
    private boolean isElementsClosed = false;
    private MessagePackWriteContext writeContext;
    private final boolean ownsThreadLocalBuffer;

    private static final class RawUtf8String
    {
        public final byte[] bytes;
        public final int offset;
        public final int len;

        public RawUtf8String(byte[] bytes, int offset, int len)
        {
            this.bytes = bytes;
            this.offset = offset;
            this.len = len;
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

    // Internal constructor for nested serialization.
    private MessagePackGenerator(
            ObjectWriteContext writeCtxt,
            IOContext ioCtxt,
            int streamWriteFeatures,
            OutputStream out,
            MessagePack.PackerConfig packerConfig,
            boolean supportIntegerKeys)
    {
        super(writeCtxt, ioCtxt, streamWriteFeatures);
        this.output = out;
        this.messagePacker = packerConfig.newPacker(out);
        this.packerConfig = packerConfig;
        this.nodes = new ArrayList<>();
        this.supportIntegerKeys = supportIntegerKeys;
        this.writeContext = MessagePackWriteContext.createRootContext(
                StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamWriteFeatures)
                        ? DupDetector.rootDetector(this) : null);
        this.ownsThreadLocalBuffer = false;
    }

    public MessagePackGenerator(
            ObjectWriteContext writeCtxt,
            IOContext ioCtxt,
            int streamWriteFeatures,
            OutputStream out,
            MessagePack.PackerConfig packerConfig,
            boolean reuseResourceInGenerator,
            boolean supportIntegerKeys)
            throws IOException
    {
        super(writeCtxt, ioCtxt, streamWriteFeatures);
        this.output = out;
        this.messagePacker = packerConfig.newPacker(getMessageBufferOutputForOutputStream(out, reuseResourceInGenerator));
        this.packerConfig = packerConfig;
        this.nodes = new ArrayList<>();
        this.supportIntegerKeys = supportIntegerKeys;
        this.writeContext = MessagePackWriteContext.createRootContext(
                StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamWriteFeatures)
                        ? DupDetector.rootDetector(this) : null);
        this.ownsThreadLocalBuffer = reuseResourceInGenerator;
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
    public JsonGenerator writeStartArray() throws JacksonException
    {
        return writeStartArray(null);
    }

    @Override
    public JsonGenerator writeStartArray(Object currentValue) throws JacksonException
    {
        return writeStartArray(currentValue, -1);
    }

    // size is ignored: element count is determined at flush time from the actual child nodes.
    // When size >= 0, it could in principle be used to skip buffering and write the array
    // header immediately, but Jackson does not guarantee it — dynamic filters (Views,
    // @JsonFilter) evaluate entries incrementally and will pass -1 even for known-size
    // collections. Backends must handle both cases (Jackson author confirmed, see #841).
    @Override
    public JsonGenerator writeStartArray(Object currentValue, int size) throws JacksonException
    {
        _verifyValueWrite("start an array");
        writeContext = writeContext.createChildArrayContext(currentValue);
        if (currentState == IN_OBJECT) {
            Node node = nodes.get(nodes.size() - 1);
            assert node instanceof NodeEntryInObject;
            NodeEntryInObject nodeEntryInObject = (NodeEntryInObject) node;
            nodeEntryInObject.value = new NodeArray(currentParentElementIndex);
        }
        else {
            if (isElementsClosed) {
                flush();
            }
            nodes.add(new NodeArray(currentParentElementIndex));
        }
        currentParentElementIndex = nodes.size() - 1;
        currentState = IN_ARRAY;
        return this;
    }

    @Override
    public JsonGenerator writeEndArray() throws JacksonException
    {
        if (currentState != IN_ARRAY) {
            _reportError("Current context not an array but " + currentStateStr());
        }
        endCurrentContainer();
        return this;
    }

    @Override
    public JsonGenerator writeStartObject() throws JacksonException
    {
        return writeStartObject(null);
    }

    @Override
    public JsonGenerator writeStartObject(Object currentValue) throws JacksonException
    {
        return writeStartObject(currentValue, -1);
    }

    // size is ignored: same reasoning as writeStartArray(Object, int) above.
    @Override
    public JsonGenerator writeStartObject(Object forValue, int size) throws JacksonException
    {
        _verifyValueWrite("start an object");
        writeContext = writeContext.createChildObjectContext(forValue);
        if (currentState == IN_OBJECT) {
            Node node = nodes.get(nodes.size() - 1);
            assert node instanceof NodeEntryInObject;
            NodeEntryInObject nodeEntryInObject = (NodeEntryInObject) node;
            nodeEntryInObject.value = new NodeObject(currentParentElementIndex);
        }
        else {
            if (isElementsClosed) {
                flush();
            }
            nodes.add(new NodeObject(currentParentElementIndex));
        }
        currentParentElementIndex = nodes.size() - 1;
        currentState = IN_OBJECT;
        return this;
    }

    @Override
    public JsonGenerator writeEndObject() throws JacksonException
    {
        if (currentState != IN_OBJECT) {
            _reportError("Current context not an object but " + currentStateStr());
        }
        if (writeContext.isExpectingValue()) {
            _reportError("Cannot close Object, property name written but no value");
        }
        endCurrentContainer();
        return this;
    }

    private void endCurrentContainer()
    {
        writeContext = writeContext.getParent();
        Node parent = nodes.get(currentParentElementIndex);
        if (parent.parentIndex == -1) {
            isElementsClosed = true;
            currentParentElementIndex = parent.parentIndex;
            currentState = IN_ROOT;
            return;
        }

        currentParentElementIndex = parent.parentIndex;
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
        else if (v instanceof RawUtf8String) {
            RawUtf8String raw = (RawUtf8String) v;
            messagePacker.packRawStringHeader(raw.len);
            messagePacker.writePayload(raw.bytes, raw.offset, raw.len);
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
            if (bb.hasArray() && !bb.isReadOnly()) {
                messagePacker.packBinaryHeader(len);
                messagePacker.writePayload(bb.array(), bb.arrayOffset() + bb.position(), len);
            }
            else {
                byte[] data = new byte[len];
                bb.duplicate().get(data);
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
            try (MessagePackGenerator messagePackGenerator = new MessagePackGenerator(
                    objectWriteContext(), _ioContext, _streamWriteFeatures,
                    outputStream, packerConfig, supportIntegerKeys)) {
                objectWriteContext().writeValue(messagePackGenerator, v);
            }
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
            if (Double.isInfinite(doubleValue) || decimal.compareTo(BigDecimal.valueOf(doubleValue)) != 0) {
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
            _reportError("Can not write a property name, expecting a value");
        }
        nodes.add(new NodeEntryInObject(currentParentElementIndex, key));
    }

    private void addValueNode(Object value) throws IOException
    {
        if (!writeContext.writeValue()) {
            _reportError("Cannot write value: expecting a property name in Object context");
        }
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
                // Flush any buffered root container before packing a root scalar,
                // otherwise the scalar would be emitted before the container.
                if (isElementsClosed) {
                    flush();
                }
                packNonContainer(value);
                flushMessagePacker();
                break;
        }
    }

    private void writeCharArrayTextValue(char[] text, int offset, int len) throws IOException
    {
        addValueNode(new String(text, offset, len));
    }

    private void writeByteArrayTextValue(byte[] text, int offset, int len) throws IOException
    {
        addValueNode(new RawUtf8String(text, offset, len));
    }

    @Override
    public JsonGenerator writePropertyId(long id) throws JacksonException
    {
        if (this.supportIntegerKeys) {
            if (!writeContext.writeName(String.valueOf(id))) {
                _reportError("Can not write a property id, expecting a value");
            }
            addKeyNode(id);
        }
        else {
            writeName(String.valueOf(id));
        }
        return this;
    }

    @Override
    public JacksonFeatureSet<StreamWriteCapability> streamWriteCapabilities()
    {
        return DEFAULT_BINARY_WRITE_CAPABILITIES;
    }

    @Override
    public JsonGenerator writeName(String name) throws JacksonException
    {
        if (!writeContext.writeName(name)) {
            _reportError("Can not write a property name, expecting a value");
        }
        addKeyNode(name);
        return this;
    }

    @Override
    public JsonGenerator writeName(SerializableString name) throws JacksonException
    {
        if (name instanceof MessagePackSerializedString) {
            if (!writeContext.writeName(name.getValue())) {
                _reportError("Can not write a property name, expecting a value");
            }
            addKeyNode(((MessagePackSerializedString) name).getRawValue());
        }
        else {
            writeName(name.getValue());
        }
        return this;
    }

    @Override
    public JsonGenerator writeString(String text) throws JacksonException
    {
        try {
            addValueNode(text);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeString(char[] text, int offset, int len) throws JacksonException
    {
        try {
            writeCharArrayTextValue(text, offset, len);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeString(Reader reader, int len) throws JacksonException
    {
        try {
            long remaining = len < 0 ? Long.MAX_VALUE : len;
            // Cap chunk size: len is a caller hint and can be arbitrarily large.
            // Pre-allocating new StringBuilder(len) would reserve len*2 bytes upfront,
            // which is an OOM risk for large inputs. The StringBuilder grows as needed.
            int chunkSize = (int) Math.min(remaining, 8192);
            StringBuilder sb = new StringBuilder(chunkSize);
            char[] tmpBuf = new char[chunkSize];
            while (remaining > 0) {
                int read = reader.read(tmpBuf, 0, (int) Math.min(remaining, tmpBuf.length));
                if (read < 0) {
                    break;
                }
                sb.append(tmpBuf, 0, read);
                remaining -= read;
            }
            addValueNode(sb.toString());
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeString(SerializableString text) throws JacksonException
    {
        return writeString(text.getValue());
    }

    @Override
    public JsonGenerator writeRawUTF8String(byte[] text, int offset, int length) throws JacksonException
    {
        try {
            writeByteArrayTextValue(text, offset, length);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeUTF8String(byte[] text, int offset, int length) throws JacksonException
    {
        try {
            writeByteArrayTextValue(text, offset, length);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeRaw(String text) throws JacksonException
    {
        try {
            addValueNode(text);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeRaw(String text, int offset, int len) throws JacksonException
    {
        try {
            addValueNode(text.substring(offset, offset + len));
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeRaw(char[] text, int offset, int len) throws JacksonException
    {
        try {
            writeCharArrayTextValue(text, offset, len);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeRaw(char c) throws JacksonException
    {
        try {
            writeCharArrayTextValue(new char[] { c }, 0, 1);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws JacksonException
    {
        try {
            addValueNode(ByteBuffer.wrap(data, offset, len));
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(short v) throws JacksonException
    {
        return writeNumber((int) v);
    }

    @Override
    public JsonGenerator writeNumber(int v) throws JacksonException
    {
        try {
            addValueNode(v);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(long v) throws JacksonException
    {
        try {
            addValueNode(v);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(BigInteger v) throws JacksonException
    {
        try {
            addValueNode(v);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(double d) throws JacksonException
    {
        try {
            addValueNode(d);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(float f) throws JacksonException
    {
        try {
            addValueNode(f);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(BigDecimal dec) throws JacksonException
    {
        try {
            addValueNode(dec);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(String encodedValue) throws JacksonException
    {
        // There is a room to improve this API's performance while the implementation is robust.
        // If users can use other MessagePackGenerator#writeNumber APIs that accept
        // proper numeric types not String, it's better to use the other APIs instead.
        try {
            try {
                long l = Long.parseLong(encodedValue);
                addValueNode(l);
                return this;
            }
            catch (NumberFormatException ignored) {
            }

            try {
                BigInteger bi = new BigInteger(encodedValue);
                addValueNode(bi);
                return this;
            }
            catch (NumberFormatException ignored) {
            }

            try {
                BigDecimal bd = new BigDecimal(encodedValue);
                double d = bd.doubleValue();

                // Check if the double can perfectly represent the exact decimal value.
                // isInfinite guard: values like "1e309" overflow double to Infinity; keep as BigDecimal.
                if (!Double.isInfinite(d) && bd.compareTo(new BigDecimal(String.valueOf(d))) == 0) {
                    // It's a safe ordinary floating-point number.
                    addValueNode(d);
                }
                else {
                    // It has more precision than a double can handle, or overflows double range.
                    addValueNode(bd);
                }
                return this;
            }
            catch (NumberFormatException e) {
                // Fall back for NaN, Infinity, -Infinity which BigDecimal rejects.
                try {
                    double d = Double.parseDouble(encodedValue);
                    addValueNode(d);
                }
                catch (NumberFormatException ignored) {
                }
            }

            throw new NumberFormatException(encodedValue);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public JsonGenerator writeBoolean(boolean state) throws JacksonException
    {
        try {
            addValueNode(state);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNull() throws JacksonException
    {
        try {
            addValueNode(null);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    public void writeExtensionType(MessagePackExtensionType extensionType)
    {
        try {
            addValueNode(extensionType);
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public void close() throws JacksonException
    {
        if (!_closed) {
            try {
                flush();
            }
            finally {
                super.close();
            }
        }
    }

    @Override
    public void flush() throws JacksonException
    {
        if (!isElementsClosed) {
            // The whole elements are not closed yet.
            return;
        }

        try {
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
        }
        catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        nodes.clear();
        isElementsClosed = false;
    }

    private void flushMessagePacker()
            throws IOException
    {
        getMessagePacker().flush();
    }

    @Override
    public tools.jackson.core.Version version()
    {
        return PackageVersion.VERSION;
    }

    @Override
    public TokenStreamContext streamWriteContext()
    {
        return writeContext;
    }

    @Override
    public Object streamWriteOutputTarget()
    {
        return output;
    }

    @Override
    public int streamWriteOutputBuffered()
    {
        return -1;
    }

    @Override
    public Object currentValue()
    {
        return writeContext.currentValue();
    }

    @Override
    public void assignCurrentValue(Object v)
    {
        writeContext.assignCurrentValue(v);
    }

    @Override
    protected void _closeInput() throws IOException
    {
        if (StreamWriteFeature.AUTO_CLOSE_TARGET.enabledIn(_streamWriteFeatures)) {
            messagePacker.close();
        }
    }

    @Override
    protected void _releaseBuffers()
    {
        // No null check on get(): generators are single-threaded by contract so this
        // ThreadLocal is always set on the calling thread. A null here would indicate
        // cross-thread misuse; letting it NPE surfaces that bug immediately.
        if (ownsThreadLocalBuffer) {
            OutputStreamBufferOutput buf = messageBufferOutputHolder.get();
            if (buf != null) {
                try {
                    buf.reset(null);
                }
                catch (IOException e) {
                    throw _wrapIOFailure(e);
                }
            }
        }
    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws JacksonException
    {
        if (!writeContext.writeValue()) {
            _reportError("Cannot " + typeMsg + ", expecting a property name");
        }
    }

    private MessagePacker getMessagePacker()
    {
        return messagePacker;
    }
}
