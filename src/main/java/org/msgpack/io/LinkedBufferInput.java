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

import java.io.EOFException;
import java.util.List;
import java.util.LinkedList;
import java.nio.ByteBuffer;

public class LinkedBufferInput implements Input {
    private static class Link {
        final byte[] buffer;
        int offset;
        int size;
        int writable;

        Link(byte[] buffer, int offset, int size, int writable) {
            this.buffer = buffer;
            this.offset = offset;
            this.size = size;
            this.writable = writable;
        }
    }

    private LinkedList<Link> link;

    private byte[] topBuffer;
    private int topOffset;
    private int topAvailable;
    private ByteBuffer castByteBuffer;
    private int filled;

    private final int bufferSize;

    public LinkedBufferInput(int bufferSize) {
        this.link = new LinkedList<Link>();
        this.bufferSize = bufferSize;
    }

    public int read(byte[] b, int off, int len) throws EOFException {
        if(len < topAvailable) {
            System.arraycopy(topBuffer, topOffset, b, off, len);
            consume(len);
            return len;
        }
        return readMore(b, off, len);
    }

    public int readMore(byte[] b, int off, int len) throws EOFException {
        if(topAvailable <= 0 && link.isEmpty()) {
            // TODO MoreBufferError
            throw new EOFException();
        }

        System.arraycopy(topBuffer, topOffset, b, off, topAvailable);
        int sum = topAvailable;
        len -= topAvailable;
        topAvailable = 0;

        while(sum < len) {
            int req = len - sum;
            Link l = link.peek();
            if(l == null) {
                return sum;
            } else if(req < l.size) {
                System.arraycopy(l.buffer, l.offset, b, off+sum, req);
                sum += req;
                l.offset += req;
                l.size -= req;
                break;
            } else if(req == l.size) {
                System.arraycopy(l.buffer, l.offset, b, off+sum, req);
                link.poll();
                break;
            } else {
                sum += l.size;
                link.poll();
            }
        }
        Link l = link.peek();
        if(l != null) {
            topBuffer = l.buffer;
            topOffset = l.offset;
            topAvailable = l.size;
            castByteBuffer = ByteBuffer.wrap(topBuffer);
        }

        return sum;
    }

    public byte readByte() throws EOFException {
        if(topAvailable <= 0) {
            // TODO MoreBufferError
            throw new EOFException();
        }
        byte b = topBuffer[0];
        consume(1);
        return b;
    }

    private boolean require(int n) throws EOFException {
        if(n <= topAvailable) {
            filled = n;
            return true;
        }
        requireMore(n);
        return false;
    }

    private void requireMore(int n) throws EOFException {
        if(!link.isEmpty()) {
            n -= topAvailable;
            for(Link l : link) {
                if(n <= l.size) {
                    filled = n;
                    return;
                }
                n -= l.size;
            }
        }
        // TODO MoreBufferError
        throw new EOFException();
    }

    private void consume(int n) {
        if(n <= topAvailable) {
            topAvailable -= n;
            topOffset += n;
            if(topAvailable > 0) {
                return;
            }
        } else {
            n -= topAvailable;
            topAvailable = 0;
            while(true) {
                Link l = link.peek();
                if(l == null) {
                    break;
                } else if(n < l.size) {
                    l.offset += n;
                    l.size -= n;
                    break;
                } else if(n == l.size) {
                    link.poll();
                    break;
                } else {
                    n -= l.size;
                    link.poll();
                }
            }
        }
        Link l = link.peek();
        if(l != null) {
            topBuffer = l.buffer;
            topOffset = l.offset;
            topAvailable = l.size;
            castByteBuffer = ByteBuffer.wrap(topBuffer);
        }
    }

    public void advance() {
        consume(filled);
        filled = 0;
    }

    public byte getByte() throws EOFException {
        if(require(1)) {
            return topBuffer[0];
        }
        // FIXME not implemented
        return 0;
    }

    public short getShort() throws EOFException {
        if(require(2)) {
            return castByteBuffer.getShort(0);
        }
        // FIXME not implemented
        return 0;
    }

    public int getInt() throws EOFException {
        if(require(4)) {
            return castByteBuffer.getInt(0);
        }
        // FIXME not implemented
        return 0;
    }

    public long getLong() throws EOFException {
        if(require(8)) {
            return castByteBuffer.getLong(0);
        }
        // FIXME not implemented
        return 0;
    }

    public float getFloat() throws EOFException {
        if(require(4)) {
            return castByteBuffer.getFloat(0);
        }
        // FIXME not implemented
        return 0.0f;
    }

    public double getDouble() throws EOFException {
        if(require(8)) {
            return castByteBuffer.getDouble(0);
        }
        // FIXME not implemented
        return 0.0;
    }

    public void feed(byte[] b) {
        feed(b, 0, b.length, false);
    }

    public void feed(byte[] b, boolean gift) {
        feed(b, 0, b.length, gift);
    }

    public void feed(byte[] b, int off, int len) {
        feed(b, off, len, false);
    }

    public void feed(byte[] b, int off, int len, boolean gift) {
        if(gift) {
            // TODO copy if len < xxx
            link.add(new Link(b, off, len, 0));
        } else {
            // TODO
            Link l = link.peekLast();
            if(len <= l.writable) {
                System.arraycopy(b, off, l.buffer, l.offset+l.size, len);
                l.size += len;
                l.writable -= len;
            } else if(len < bufferSize) {
                byte[] buffer = new byte[bufferSize];
                System.arraycopy(b, off, buffer, 0, len);
                link.add(new Link(buffer, 0, off, bufferSize-len));
            } else {
                byte[] buffer = new byte[len];
                System.arraycopy(b, off, buffer, 0, len);
                link.add(new Link(buffer, 0, off, 0));
            }
        }
    }

    public void clear() {
        link.clear();;
        topOffset = 0;
        topAvailable = 0;
        topBuffer = null;
    }
}

