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
    private LinkedList<ByteBuffer> link;
    private int writable;
    private int nextAdvance;

    private byte[] tmpBuffer;
    private ByteBuffer tmpByteBuffer;

    private final int bufferSize;

    public LinkedBufferInput(int bufferSize) {
        this.link = new LinkedList<ByteBuffer>();
        this.writable = -1;
        this.tmpBuffer = new byte[8];
        this.tmpByteBuffer = ByteBuffer.wrap(tmpBuffer);
        this.bufferSize = bufferSize;
    }

    public int read(byte[] b, int off, int len) throws EOFException {
        int olen = len;
        while(true) {
            ByteBuffer bb = link.peekFirst();
            if(bb == null) {
                break;
            }
            if(len < bb.remaining()) {
                bb.get(b, off, len);
                if(bb.remaining() == 0) {
                    link.removeFirst();
                }
                return olen;
            }
            int rem = bb.remaining();
            bb.get(b, off, rem);
            len -= rem;
            off += rem;
            link.removeFirst();
        }
        return olen - len;
    }

    public byte readByte() throws EOFException {
        ByteBuffer bb = link.peekFirst();
        if(bb == null || bb.remaining() == 0) {
            throw new EndOfBufferException();
        }
        byte result = bb.get();
        if(bb.remaining() == 0) {
            if(link.size() == 1 && writable >= 0) {
                bb.position(0);
                bb.limit(0);
                writable = bb.capacity();
            } else {
                link.removeFirst();
            }
        }
        return result;
    }

    public void advance() {
        int len = nextAdvance;
        ByteBuffer bb;
        while(true) {
            bb = link.peekFirst();
            if(bb == null) {
                nextAdvance = 0;
                return;
            }
            if(len <= bb.remaining()) {
                bb.position(bb.position()+len);
                if(bb.remaining() == 0) {
                    link.removeFirst();
                }
                break;
            }
            len -= bb.remaining();
            link.removeFirst();
        }
        if(link.isEmpty() && writable >= 0) {
            bb.position(0);
            bb.limit(0);
            link.addLast(bb);
            writable = bb.capacity();
        }
        nextAdvance = 0;
    }

    private void requireMore(int n) throws EOFException {
        int off = 0;
        for(ByteBuffer bb : link) {
            if(n <= bb.remaining()) {
                int pos = bb.position();
                bb.get(tmpBuffer, off, n);
                bb.position(pos);
                return;
            }
            int rem = bb.remaining();
            int pos = bb.position();
            bb.get(tmpBuffer, off, rem);
            bb.position(pos);
            n -= rem;
            off += rem;
        }
        throw new EndOfBufferException();
    }

    private ByteBuffer require(int n) throws EOFException {
        ByteBuffer bb = link.peekFirst();
        if(bb == null) {
            throw new EndOfBufferException();
        }
        if(n <= bb.remaining()) {
            nextAdvance = n;
            return bb;
        } else {
            requireMore(n);
            nextAdvance = n;
            return tmpByteBuffer;
        }
    }

    public byte getByte() throws EOFException {
        ByteBuffer bb = require(1);
        return bb.get(bb.position());
    }

    public short getShort() throws EOFException {
        ByteBuffer bb = require(2);
        return bb.getShort(bb.position());
    }

    public int getInt() throws EOFException {
        ByteBuffer bb = require(4);
        return bb.getInt(bb.position());
    }

    public long getLong() throws EOFException {
        ByteBuffer bb = require(8);
        return bb.getLong(bb.position());
    }

    public float getFloat() throws EOFException {
        ByteBuffer bb = require(4);
        return bb.getFloat(bb.position());
    }

    public double getDouble() throws EOFException {
        ByteBuffer bb = require(8);
        return bb.getDouble(bb.position());
    }

    public void feed(byte[] b) {
        feed(b, 0, b.length, false);
    }

    public void feed(byte[] b, boolean nocopy) {
        feed(b, 0, b.length, nocopy);
    }

    public void feed(byte[] b, int off, int len) {
        feed(b, off, len, false);
    }

    public void feed(byte[] b, int off, int len, boolean nocopy) {
        if(nocopy) {
            if(writable > 0 && link.size() == 1 && link.peekFirst().remaining() == 0) {
                link.addFirst(ByteBuffer.wrap(b, off, len));
                return;
            }
            link.addLast(ByteBuffer.wrap(b, off, len));
            writable = -1;
            return;
        }

        ByteBuffer bb = link.peekLast();
        if(len <= writable) {
            int pos = bb.position();
            bb.position(bb.limit());
            bb.limit(bb.limit() + len);
            bb.put(b, off, len);
            bb.position(pos);
            writable = bb.capacity() - bb.limit();
            return;
        }

        if(writable > 0) {
            int pos = bb.position();
            bb.position(bb.limit());
            bb.limit(bb.limit() + writable);
            bb.put(b, off, writable);
            bb.position(pos);
            off += writable;
            len -= writable;
            writable = 0;
        }

        int sz = Math.max(len, bufferSize);
        ByteBuffer nb = ByteBuffer.allocate(sz);
        nb.put(b, off, len);
        nb.limit(len);
        nb.position(0);
        link.addLast(nb);
        writable = sz - len;
    }

    public void feed(ByteBuffer b) {
        feed(b, false);
    }

    public void feed(ByteBuffer buf, boolean nocopy) {
        if(nocopy) {
            if(writable > 0 && link.size() == 1 && link.peekFirst().remaining() == 0) {
                link.addFirst(buf);
                return;
            }
            link.addLast(buf);
            writable = -1;
            return;
        }

        int rem = buf.remaining();

        ByteBuffer bb = link.peekLast();
        if(rem <= writable) {
            int pos = bb.position();
            bb.position(bb.limit());
            bb.limit(bb.limit() + rem);
            bb.put(buf);
            bb.position(pos);
            writable = bb.capacity() - bb.limit();
            return;
        }

        if(writable > 0) {
            int pos = bb.position();
            bb.position(bb.limit());
            bb.limit(bb.limit() + writable);
            buf.limit(writable);
            bb.put(buf);
            bb.position(pos);
            rem -= writable;
            buf.limit(buf.limit() + rem);
            writable = 0;
        }

        int sz = Math.max(rem, bufferSize);
        ByteBuffer nb = ByteBuffer.allocate(sz);
        nb.put(buf);
        nb.limit(rem);
        nb.position(0);
        link.addLast(nb);
        writable = sz - rem;
    }

    public void clear() {
        if(writable > 0) {
            ByteBuffer bb = link.getLast();
            link.clear();
            bb.position(0);
            bb.limit(0);
            link.addLast(bb);
            writable = bb.capacity();
        } else {
            link.clear();
        }
    }
}

