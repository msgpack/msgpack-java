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
import java.nio.ByteBuffer;

public class MessageBuffer {
    private static final class Chunk {
        private ByteBuffer bb;
        private boolean referenced;

        boolean isEmpty() {
            // TODO
            return bb.remaining() == 0;
        }

        int size() {
            return bb.remaining();
        }

        ByteBuffer slice(int size) {
            // TODO
            referenced = true;
            return null;
        }

        void skip(int n) {
            // TODO
        }

        void read(ByteBuffer bb) {
            // TODO
        }

        boolean isReferenced() {
            return referenced;
        }

        ByteBuffer get() {
            return bb;
        }
    }

    private LinkedList<Chunk> list = new LinkedList<Chunk>();

    private ByteBuffer castBuffer = ByteBuffer.allocate(8);

    public MessageBuffer() {
    }

    public int read(ByteBuffer dst) {
        Chunk c = list.getFirst();
        c.read(dst);
        if (c.isEmpty()) {
            removeFirstChunk();
        }
        return 0;  // TODO
    }

    public int skip(int maxSize) {
        int rem = maxSize;
        Iterator<Chunk> ite = list.iterator();
        while (ite.hasNext()) {
            Chunk c = ite.next();
            int sz = c.size();
            if (sz < rem) {
                rem -= sz;
                ite.remove();
            } else if (sz == rem) {
                ite.remove();
                return maxSize;
            } else {
                c.skip(rem);
                return maxSize;
            }
        }
        return maxSize - rem;
    }

    public ByteBuffer readAll(int size) {
        int chunkCount = 0;
        int rem = size;
        for (Chunk c : list) {
            int sz = c.size();
            if (sz < rem) {
                rem -= sz;
                chunkCount++;
            } else {
                final ByteBuffer bb;
                if (chunkCount == 0) {
                    bb = c.slice(size);
                } else {
                    bb = ByteBuffer.allocate(size);
                    for (int i=0; i < chunkCount; i++) {
                        list.getFirst().read(bb);
                        removeFirstChunk();
                    }
                    c.read(bb);
                }
                if (sz == rem) {
                    removeFirstChunk();
                }
                return bb;
            }
        }
        return null;
    }

    public void skipAll(int size) {
        int chunkCount = 0;
        int rem = size;
        for (Chunk c : list) {
            int sz = c.size();
            if (sz < rem) {
                rem -= sz;
                chunkCount++;
            } else {
                for (int i=0; i < chunkCount; i++) {
                    removeFirstChunk();
                }
                if (sz == rem) {
                    removeFirstChunk();
                } else {
                    c.skip(rem);
                }
                return;
            }
        }
    }

    private void fillCastBuffer(int n) {
        // TODO
    }

    private void removeFirstChunk() {
        Chunk c = list.removeFirst();
        if (!c.isReferenced()) {
            // TODO reuse this chunk
        }
    }

    private ByteBuffer require(int n) {
        Chunk c = list.getFirst();
        final int sz = c.size();
        if (sz >= n) {
            if (sz == n) {
                list.removeFirst();
            }
            return c.get();
        }
        fillCastBuffer(n);
        return castBuffer;
    }

    public byte readByte() {
        return require(1).get();
    }

    public short readShort() {
        return require(2).getShort();
    }

    public int readInt() {
        return require(4).getInt();
    }

    public long readLong() {
        return require(8).getLong();
    }

    public float readFloat() {
        return require(4).getFloat();
    }

    public double readDouble() {
        return require(8).getDouble();
    }
}
