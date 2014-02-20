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
package org.msgpack.core;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.math.BigInteger;
import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 *   <table>
 *     <tr>
 *       <th>Category</th><th>Producer</th><th>Consumer</th>
 *     </tr>
 *
 *     <tr>
 *       <th rowspan="1">At-most bulk IO</th>
 *       <td>write(src)</td><td>read(dst)</td>
 *     </tr>
 *
 *     <tr>
 *       <th rowspan="2">All bulk IO</th>
 *       <td>writeAll(src)</td><td>readAll(dst)</td>
 *     </tr>
 *     <tr><td>writeAll(b, off, len)</td><td>readAll(size)</td></tr>
 *
 *     <tr>
 *       <th rowspan="1">Direct transfer bulk IO</th>
 *       <td>transferFrom(size, channel)</td><td>transferTo(size, channel)</td>
 *     </tr>
 *     <td></td><td>transferAllTo(size, channel)</td>
 *
 *     <tr>
 *       <th rowspan="6">Composite primitive type IO</th>
 *       <td>writeByte(v)</td><td>readByte()</td>
 *     </tr>
 *     <tr><td>writeShort(v)</td><td>readShort()</td></tr>
 *     <tr><td>writeInt(v)</td><td>readInt()</td></tr>
 *     <tr><td>writeLong(v)</td><td>readLong()</td></tr>
 *     <tr><td>writeFloat(v)</td><td>readFloat()</td></tr>
 *     <tr><td>writeDouble(v)</td><td>readDouble()</td></tr>
 *
 *     <tr>
 *       <th rowspan="6">Primitive type IO</th>
 *       <td>writeByteAndByte(v)</td><td>
 *     </tr>
 *     <tr><td>writeByteAndShort(v)</td><td></tr>
 *     <tr><td>writeByteAndInt(v)</td><td></tr>
 *     <tr><td>writeByteAndLong(v)</td><td></tr>
 *     <tr><td>writeByteAndFloat(v)</td><td></tr>
 *     <tr><td>writeByteAndDouble(v)</td><td></tr>
 *
 *     <tr>
 *       <th rowspan="2">Zero-copy IO</th>
 *       <td rowspan="2">add(bb)</td><td>get()</td>
 *     </tr>
 *     <tr><td>remove()</td></tr>
 *   </table>
 *
 */
public class MessageBuffer implements MessagePackerChannel, MessageUnpackerChannel {
    public static class Builder {
        private ReadableByteChannel inputChannel = null;
        private WritableByteChannel outputChannel = null;
        private int writeBufferChunkSize = 1024;
        private int readReferenceThreshold = 1024;

        public MessageBuffer build() {
            return new MessageBuffer(this);
        }

        public ReadableByteChannel getInputChannel() {
            return inputChannel;
        }

        public void setInputChannel(ReadableByteChannel channel) {
            this.inputChannel = channel;
        }

        public Builder withInputChannel(ReadableByteChannel channel) {
            setInputChannel(channel);
            return this;
        }

        public WritableByteChannel getOutputChannel() {
            return outputChannel;
        }

        public void setOutputChannel(WritableByteChannel channel) {
            this.outputChannel = channel;
        }

        public Builder withOutputChannel(WritableByteChannel channel) {
            setOutputChannel(channel);
            return this;
        }

        public int getReadReferenceThreshold() {
            return readReferenceThreshold;
        }

        public void setReadReferenceThreshold(int size) {
            this.readReferenceThreshold = size;
        }

        public Builder withReadReferenceThreshold(int size) {
            setReadReferenceThreshold(size);
            return this;
        }

        public int getWriteBufferChunkSize() {
            return writeBufferChunkSize;
        }

        public void setWriteBufferChunkSize(int size) {
            this.writeBufferChunkSize = size;
        }

        public Builder withWriteBufferChunkSize(int size) {
            setWriteBufferChunkSize(size);
            return this;
        }
    }

    private static final class Chunk {
        ByteBuffer bb;
        boolean reusable;

        Chunk(ByteBuffer bb, boolean reusable) {
            this.bb = bb;
            this.reusable = reusable;
        }
    }

    private final LinkedList<Chunk> list = new LinkedList<Chunk>();
    private ByteBuffer lastWritableBuffer = null;
    private int lastWritableBufferReadPosition = 0;

    private final byte[] castBuffer = new byte[8];
    private final ByteBuffer castByteBuffer = ByteBuffer.wrap(castBuffer);

    protected ReadableByteChannel inputChannel;
    protected WritableByteChannel outputChannel;

    private int writeBufferChunkSize;
    private int readReferenceThreshold;

    // TODO configurable buffer allocator

    public MessageBuffer() {
        this(new Builder());
    }

    MessageBuffer(Builder builder) {
        this.inputChannel = builder.getInputChannel();
        this.outputChannel = builder.getOutputChannel();
        this.writeBufferChunkSize = builder.getWriteBufferChunkSize();
        this.readReferenceThreshold = builder.getReadReferenceThreshold();
    }

    public void close() throws IOException {
        if (inputChannel != null) {
            inputChannel.close();
        }
        if (outputChannel != null) {
            if (outputChannel.isOpen()) {
                flush();
            }
            outputChannel.close();
        }
    }

    public boolean isOpen() {
        if (inputChannel != null && !inputChannel.isOpen()) {
            return false;
        }
        if (outputChannel != null && !outputChannel.isOpen()) {
            return false;
        }
        return true;
    }

    private static int transferByteBuffer(ByteBuffer src, ByteBuffer dst) {
        int pos = dst.position();

        int srcrem = src.remaining();
        int dstrem = dst.remaining();
        if (dstrem < srcrem) {
            int lim = src.limit();
            try {
                src.limit(src.position() + dstrem);
                dst.put(src);
            } finally {
                src.limit(lim);
            }
        } else {
            dst.put(src);
        }

        return dst.position() - pos;
    }

    private void consumedFirstChunk() {
        Chunk c = list.removeFirst();
        if (c.reusable) {
            if (lastWritableBuffer == null) {
                lastWritableBuffer = c.bb;
                lastWritableBuffer.position(0);
                lastWritableBuffer.limit(lastWritableBuffer.capacity());
                lastWritableBufferReadPosition = 0;
            }
        }
    }

    public void add(ByteBuffer bb) throws IOException {
        list.addLast(new Chunk(bb, true));
    }

    public ByteBuffer get() throws IOException {
        if (list.isEmpty() && !tryReadMore()) {
            return null;
        }
        return list.getFirst().bb;
    }

    public ByteBuffer remove() throws IOException {
        if (list.isEmpty() && !tryReadMore()) {
            return null;
        }
        Chunk c = list.getFirst();
        c.reusable = false;
        consumedFirstChunk();
        return c.bb;
    }

    private void producedLastChunk() throws IOException {
        lastWritableBuffer.limit(lastWritableBuffer.position());
        lastWritableBuffer.position(lastWritableBufferReadPosition);
        list.addLast(new Chunk(lastWritableBuffer, true));
        lastWritableBuffer = null;
        tryFlush();
    }

    private ByteBuffer allocateLastWritableBuffer(int minSize) throws IOException {
        if (lastWritableBuffer != null) {
            producedLastChunk();
            if (lastWritableBuffer != null && lastWritableBuffer.remaining() >= size) {
                return lastWritableBuffer;
            }
        }
        int size = writeBufferChunkSize;
        while (size < minSize) {
            size *= 2;
        }
        final ByteBuffer bb = allocateBuffer(size);
        lastWritableBuffer = bb;
        lastWritableBufferReadPosition = 0;
        return bb;
    }

    private ByteBuffer allocateBuffer(int size) {
        return ByteBuffer.allocate(size);
    }

    private boolean tryReadMore() throws IOException {
        if (inputChannel != null && (lastWritableBuffer == null || lastWritableBuffer.position() == 0)) {
            final ByteBuffer bb = allocateBuffer(writeBufferChunkSize);
            lastWritableBuffer = bb;
            lastWritableBufferReadPosition = 0;
            if (inputChannel.read(bb) == 0) {
                // read == 0 doesn't mean EOF
                list.addLast(new Chunk(ByteBuffer.allocate(0), false));
                return true;
            }
        }

        if (lastWritableBuffer != null) {
            final int pos = lastWritableBuffer.position();
            if (pos > lastWritableBufferReadPosition) {
                // TODO optimize to call producedLastChunk if almost fully filled
                lastWritableBuffer.position(lastWritableBufferReadPosition);
                lastWritableBuffer.limit(pos);
                final ByteBuffer bb = lastWritableBuffer.slice();
                final Chunk c = new Chunk(bb, false);
                lastWritableBufferReadPosition = pos;
                lastWritableBuffer.position(pos);
                lastWritableBuffer.limit(lastWritableBuffer.capacity());
                list.addLast(c);
                return true;
            }
        }

        return false;
    }

    public int read(ByteBuffer dst) throws IOException {
        if (list.isEmpty() && !tryReadMore()) {
            return -1;
        }

        Chunk c = list.getFirst();
        int n = transferByteBuffer(c.bb, dst);
        if (!c.bb.hasRemaining()) {
            consumedFirstChunk();
        }
        return n;
    }

    public int skip(int maxSize) throws IOException {
        if (list.isEmpty() && !tryReadMore()) {
            return 0;
        }

        int size = maxSize;
        Iterator<Chunk> ite = list.iterator();
        while (ite.hasNext()) {
            Chunk c = ite.next();
            int rem = c.bb.remaining();
            if (rem < size) {
                // skip all data from this chunk
                size -= rem;
                ite.remove();
            } else if (rem == size) {
                // skip all data from this chunk
                // & end of iteration
                ite.remove();
                return maxSize;
            } else {
                // skip partial data from this chunk
                // & end of iteration
                c.bb.position(c.bb.position() + size);
                return maxSize;
            }
        }
        return maxSize - size;
    }

    private Chunk readIteratorNext(Iterator<Chunk> ite) throws IOException {
        if (ite.hasNext()) {
            return ite.next();
        } else if (tryReadMore()) {
            return list.getLast();
        } else {
            return null;
        }
    }

    private ByteBuffer allocateReadResultBuffer(int size) {
        return ByteBuffer.allocate(size);
    }

    private ByteBuffer sliceBuffer(Chunk c, int size) {
        if (size >= readReferenceThreshold) {
            // zero-copy reference
            int lim = c.bb.limit();
            try {
                c.bb.limit(c.bb.position() + size);
                c.reusable = false;
                return c.bb.slice();
            } finally {
                c.bb.limit(lim);
            }

        } else {
            // copy
            ByteBuffer bb = allocateReadResultBuffer(size);
            transferByteBuffer(c.bb, bb);
            bb.position(0);
            return bb;
        }
    }

    public ByteBuffer readAll(int size) throws IOException {
        final int totalSize = size;
        int chunkCount = 0;
        Iterator<Chunk> ite = list.iterator();
        Chunk c;
        while ((c = readIteratorNext(ite)) != null) {
            int rem = c.bb.remaining();
            if (rem < size) {
                // read all data from this chunk
                size -= rem;
                chunkCount++;
            } else {
                // read all or partial data from this chunk
                // & end of iteration
                final ByteBuffer bb;
                if (chunkCount == 0) {
                    // all data is in a single buffer chunk.
                    // try zero-copy slicing.
                    bb = sliceBuffer(c, totalSize);
                } else {
                    // copy data from multiple chunks
                    bb = allocateReadResultBuffer(totalSize);
                    for (int i=0; i < chunkCount; i++) {
                        bb.put(list.getFirst().bb);
                        consumedFirstChunk();
                    }
                    transferByteBuffer(c.bb, bb);
                }
                if (rem == size) {
                    // read all data from this chunk
                    consumedFirstChunk();
                }
                return bb;
            }
        }
        throw new EOFException();
    }

    public void skipAll(int size) throws IOException {
        final int totalSize = size;
        int chunkCount = 0;
        Iterator<Chunk> ite = list.iterator();
        Chunk c;
        while ((c = readIteratorNext(ite)) != null) {
            int rem = c.bb.remaining();
            if (rem < size) {
                // skip all data from this chunk
                size -= rem;
                chunkCount++;
            } else {
                // skip all or partial data from this chunk
                // & end of iteration
                for (int i=0; i < chunkCount; i++) {
                    consumedFirstChunk();
                }
                if (rem == size) {
                    // skip all data from this chunk
                    consumedFirstChunk();
                } else {
                    c.bb.position(c.bb.position() + size);
                }
                return;
            }
        }
        throw new EOFException();
    }

    public int size() {
        int total = 0;
        for (Chunk c : list) {
            total += c.bb.remaining();
        }
        if (lastWritableBuffer != null) {
            total += lastWritableBuffer.position();
        }
        return total;
    }

    public byte[] toByteArray() {
        byte[] result = new byte[size()];
        int off = 0;
        for (Chunk c : list) {
            ByteBuffer bb = c.bb;
            int pos = bb.position();
            try {
                int rem = bb.remaining();
                bb.get(result, off, rem);
                off += rem;
            } finally {
                bb.position(pos);
            }
        }
        if (lastWritableBuffer != null) {
            ByteBuffer bb = lastWritableBuffer;
            int pos = bb.position();
            bb.position(0);
            try {
                bb.get(result, off, pos);
                off += pos;
            } finally {
                bb.position(pos);
            }
        }
        return result;
    }

    //public int transferFrom(int size, ReadableByteChannel out) throws IOException {
    //  TODO
    //}

    //public int transferTo(int size, WritableByteChannel out) throws IOException {
    //  TODO
    //}

    //public int transferAllTo(int size, WritableByteChannel out) throws IOException {
    //  TODO
    //}

    private void tryFlush() throws IOException {
        if (outputChannel == null) {
            return;
        }

        while (!list.isEmpty()) {
            ByteBuffer bb = list.getFirst().bb;
            while (bb.hasRemaining()) {
                outputChannel.write(bb);
            }
            consumedFirstChunk();
        }
    }

    public void flush() throws IOException {
        if (lastWritableBuffer != null) {
            producedLastChunk();
        }
        tryFlush();
    }

    private ByteBuffer prepareForRead(int size) throws IOException {
        if (list.isEmpty() && !tryReadMore()) {
            throw new EOFException();
        }
        final ByteBuffer bb = list.getFirst().bb;
        if (bb.remaining() > size) {
            return bb;
        }
        return fillCastBuffer(size);
    }

    private ByteBuffer fillCastBuffer(int size) throws IOException {
        // collect data from multiple chunks
        final int totalSize = size;
        int chunkCount = 0;
        Iterator<Chunk> ite = list.iterator();
        Chunk c;
        while ((c = readIteratorNext(ite)) != null) {
            int rem = c.bb.remaining();
            if (rem < size) {
                // read all data from this chunk
                size -= rem;
                chunkCount++;
            } else {
                // read all or partial data from this chunk
                // & end of iteration
                castByteBuffer.position(0);
                castByteBuffer.limit(totalSize);
                for (int i=0; i < chunkCount; i++) {
                    castByteBuffer.put(list.getFirst().bb);
                    consumedFirstChunk();
                }
                transferByteBuffer(c.bb, castByteBuffer);
                if (rem == size) {
                    // read all data from this chunk
                    consumedFirstChunk();
                }
                return castByteBuffer;
            }
        }
        throw new EOFException();
    }

    public byte readByte() throws IOException {
        return prepareForRead(1).get();
    }

    public short readShort() throws IOException {
        return prepareForRead(2).getShort();
    }

    public int readInt() throws IOException {
        return prepareForRead(4).getInt();
    }

    public long readLong() throws IOException {
        return prepareForRead(8).getLong();
    }

    public float readFloat() throws IOException {
        return prepareForRead(4).getFloat();
    }

    public double readDouble() throws IOException {
        return prepareForRead(8).getDouble();
    }

    public int write(ByteBuffer src) throws IOException {
        if (!src.hasRemaining()) {
            return 0;
        }

        ByteBuffer wb = lastWritableBuffer;
        if (wb == null) {
            wb = allocateLastWritableBuffer(src.remaining());
        }
        int n = transferByteBuffer(wb, src);

        if (!wb.hasRemaining()) {
            producedLastChunk();
        }
        return n;
    }

    public void writeAll(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }

        ByteBuffer wb = lastWritableBuffer;
        if (wb != null) {
            final int rem = wb.remaining();
            if (len <= rem) {
                // this single buffer can contain all data
                wb.put(b, off, len);
                if (len == rem) {
                    producedLastChunk();
                }
                return;
            }
            // this buffer can contain partial data
            wb.put(b, off, rem);
            producedLastChunk();
            off += rem;
            len -= rem;
        }

        wb = allocateLastWritableBuffer(len);
        wb.put(b, off, len);
        if (!wb.hasRemaining()) {
            producedLastChunk();
        }
    }

    public void writeAll(ByteBuffer src) throws IOException {
        if (!src.hasRemaining()) {
            return;
        }

        ByteBuffer wb = lastWritableBuffer;
        if (wb != null) {
            transferByteBuffer(src, wb);
            if (!wb.hasRemaining()) {
                producedLastChunk();
            }
            if (!src.hasRemaining()) {
                return;
            }
        }

        wb = allocateLastWritableBuffer(src.remaining());
        wb.put(src);
        if (!wb.hasRemaining()) {
            producedLastChunk();
        }
    }

    private ByteBuffer prepareForWrite(int size) throws IOException {
        ByteBuffer wb = lastWritableBuffer;
        if (wb != null && wb.remaining() >= size) {
            return wb;
        }
        return allocateLastWritableBuffer(size);
    }

    public void writeByte(byte v) throws IOException {
        prepareForWrite(1).put(v);
    }

    public void writeShort(short v) throws IOException {
        prepareForWrite(2).putShort(v);
    }

    public void writeInt(int v) throws IOException {
        prepareForWrite(4).putInt(v);
    }

    public void writeLong(long v) throws IOException {
        prepareForWrite(8).putLong(v);
    }

    public void writeFloat(float v) throws IOException {
        prepareForWrite(4).putFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        prepareForWrite(8).putDouble(v);
    }

    public void writeByteAndByte(byte b, byte v) throws IOException {
        ByteBuffer bb = prepareForWrite(2);
        bb.put(b);
        bb.put(v);
    }

    public void writeByteAndShort(byte b, short v) throws IOException {
        ByteBuffer bb = prepareForWrite(3);
        bb.put(b);
        bb.putShort(v);
    }

    public void writeByteAndInt(byte b, int v) throws IOException {
        ByteBuffer bb = prepareForWrite(5);
        bb.put(b);
        bb.putInt(v);
    }

    public void writeByteAndLong(byte b, long v) throws IOException {
        ByteBuffer bb = prepareForWrite(9);
        bb.put(b);
        bb.putLong(v);
    }

    public void writeByteAndFloat(byte b, float v) throws IOException {
        ByteBuffer bb = prepareForWrite(5);
        bb.put(b);
        bb.putFloat(v);
    }

    public void writeByteAndDouble(byte b, double v) throws IOException {
        ByteBuffer bb = prepareForWrite(9);
        bb.put(b);
        bb.putDouble(v);
    }
}
