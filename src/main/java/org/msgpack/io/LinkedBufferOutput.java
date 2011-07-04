//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
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

import java.util.List;
import java.util.LinkedList;

public class LinkedBufferOutput extends BufferedOutput {
    private static class Link {
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
        this.link = new LinkedList<Link>();
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[size+filled];
        int off = 0;
        for(Link l : link) {
            System.arraycopy(l.buffer, l.offset, bytes, off, l.size);
            off += l.size;
        }
        if(filled > 0) {
            System.arraycopy(buffer, 0, bytes, off, filled);
        }
        return bytes;
    }

    public int getSize() {
        return size + filled;
    }

    public void flush() {
        if(filled > 0) {
            if(!flushBuffer(buffer, 0, filled)) {
                buffer = null;
            }
            filled = 0;
        }
    }

    protected boolean flushBuffer(byte[] buffer, int off, int len) {
        link.add(new Link(buffer, off, len));
        size += len;
        return false;
    }
}

