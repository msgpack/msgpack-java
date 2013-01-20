//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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
package org.msgpack.io;

import java.util.LinkedList;

public final class LinkedBufferOutput extends BufferedOutput {
    private static final class Link {
        final byte[] buffer;
        final int offset;
        final int size;

        Link(byte[] buffer, int offset, int size) {
            this.buffer = buffer;
            this.offset = offset;
            this.size = size;
        }
    }

    private LinkedList<Link> link;
    private int size;

    public LinkedBufferOutput(int bufferSize) {
        super(bufferSize);
        link = new LinkedList<Link>();
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[size + filled];
        int off = 0;
        for (Link l : link) {
            System.arraycopy(l.buffer, l.offset, bytes, off, l.size);
            off += l.size;
        }
        if (filled > 0) {
            System.arraycopy(buffer, 0, bytes, off, filled);
        }
        return bytes;
    }

    public int getSize() {
        return size + filled;
    }

    @Override
    protected boolean flushBuffer(byte[] b, int off, int len) {
        link.add(new Link(b, off, len));
        size += len;
        return false;
    }

    public void clear() {
        link.clear();
        size = 0;
        filled = 0;
    }

    @Override
    public void close() {
    }
}
