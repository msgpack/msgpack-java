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
package org.msgpack.packer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import org.msgpack.io.Output;
import org.msgpack.MessageTypeException;

abstract class AbstractMessagePackPacker extends Packer {
    protected final Output out;

    private PackerStack stack = new PackerStack();

    protected AbstractMessagePackPacker(Output out) {
        this.out = out;
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void writeByte(byte d) throws IOException {
        if(d < -(1<<5)) {
            out.writeByteAndByte((byte)0xd0, d);
        } else {
            out.writeByte(d);
        }
        stack.reduceCount();
    }

    @Override
    public void writeShort(short d) throws IOException {
        if(d < -(1<<5)) {
            if(d < -(1<<7)) {
                // signed 16
                out.writeByteAndShort((byte)0xd1, d);
            } else {
                // signed 8
                out.writeByteAndByte((byte)0xd0, (byte)d);
            }
        } else if(d < (1<<7)) {
            // fixnum
            out.writeByte((byte)d);
        } else {
            if(d < (1<<8)) {
                // unsigned 8
                out.writeByteAndByte((byte)0xcc, (byte)d);
            } else {
                // unsigned 16
                out.writeByteAndShort((byte)0xcd, d);
            }
        }
        stack.reduceCount();
    }

    @Override
    public void writeInt(int d) throws IOException {
        if(d < -(1<<5)) {
            if(d < -(1<<15)) {
                // signed 32
                out.writeByteAndInt((byte)0xd2, d);
            } else if(d < -(1<<7)) {
                // signed 16
                out.writeByteAndShort((byte)0xd1, (short)d);
            } else {
                // signed 8
                out.writeByteAndByte((byte)0xd0, (byte)d);
            }
        } else if(d < (1<<7)) {
            // fixnum
            out.writeByte((byte)d);
        } else {
            if(d < (1<<8)) {
                // unsigned 8
                out.writeByteAndByte((byte)0xcc, (byte)d);
            } else if(d < (1<<16)) {
                // unsigned 16
                out.writeByteAndShort((byte)0xcd, (short)d);
            } else {
                // unsigned 32
                out.writeByteAndInt((byte)0xce, d);
            }
        }
        stack.reduceCount();
    }

    @Override
    public void writeLong(long d) throws IOException {
        if(d < -(1L<<5)) {
            if(d < -(1L<<15)) {
                if(d < -(1L<<31)) {
                    // signed 64
                    out.writeByteAndLong((byte)0xd3, d);
                } else {
                    // signed 32
                    out.writeByteAndInt((byte)0xd2, (int)d);
                }
            } else {
                if(d < -(1<<7)) {
                    // signed 16
                    out.writeByteAndShort((byte)0xd1, (short)d);
                } else {
                    // signed 8
                    out.writeByteAndByte((byte)0xd0, (byte)d);
                }
            }
        } else if(d < (1<<7)) {
            // fixnum
            out.writeByte((byte)d);
        } else {
            if(d < (1L<<16)) {
                if(d < (1<<8)) {
                    // unsigned 8
                    out.writeByteAndByte((byte)0xcc, (byte)d);
                } else {
                    // unsigned 16
                    out.writeByteAndShort((byte)0xcd, (short)d);
                }
            } else {
                if(d < (1L<<32)) {
                    // unsigned 32
                    out.writeByteAndInt((byte)0xce, (int)d);
                } else {
                    // unsigned 64
                    out.writeByteAndLong((byte)0xcf, d);
                }
            }
        }
        stack.reduceCount();
    }

    @Override
    public void writeBigInteger(BigInteger d) throws IOException {
        if(d.bitLength() <= 64) {
            writeLong(d.longValue());
            stack.reduceCount();
        } else {
            throw new MessageTypeException("MessagePack can't serialize BigInteger larger than (2^64)-1");
        }
    }

    @Override
    public void writeFloat(float d) throws IOException {
        out.writeByteAndFloat((byte)0xca, d);
        stack.reduceCount();
    }

    @Override
    public void writeDouble(double d) throws IOException {
        out.writeByteAndDouble((byte)0xcb, d);
        stack.reduceCount();
    }

    @Override
    public void writeNil() throws IOException {
        out.writeByte((byte)0xc0);
        stack.reduceCount();
    }

    @Override
    public void writeBoolean(boolean d) throws IOException {
        if(d) {
            // true
            out.writeByte((byte)0xc3);
        } else {
            // false
            out.writeByte((byte)0xc2);
        }
        stack.reduceCount();
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        if(len < 32) {
            out.writeByte((byte)(0xa0 | len));
        } else if(len < 65536) {
            out.writeByteAndShort((byte)0xda, (short)len);
        } else {
            out.writeByteAndInt((byte)0xdb, len);
        }
        out.write(b, off, len);
    }

    @Override
    public void writeString(String s) throws IOException {
        // TODO encoding error
        byte[] b;
        try {
            b = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new MessageTypeException();
        }
        writeBytes(b, 0, b.length);
        stack.reduceCount();
    }

    @Override
    public void writeArrayBegin(int size) throws IOException {
        // TODO check size < 0?
        if(size == 0) {
            stack.reduceCount();
            out.writeByte((byte)0x90);
            stack.reduceCount();
            return;
        }
        if(size < 16) {
            // FixArray
            out.writeByte((byte)(0x90 | size));
        } else if(size < 65536) {
            out.writeByteAndShort((byte)0xdc, (short)size);
        } else {
            out.writeByteAndInt((byte)0xdd, size);
        }
        stack.pushArray(size);
    }

    @Override
    public void writeArrayEnd(boolean check) throws IOException {
        if(!stack.topIsArray()) {
            throw new MessageTypeException("writeArrayEnd() is called but writeArrayBegin() is not called");
        }

        int remain = stack.getTopCount();
        if(remain > 0) {
            if(check) {
                throw new MessageTypeException("writeArrayEnd(check=true) is called but the array is not end");
            }
            for(int i=0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();
        stack.reduceCount();
    }

    @Override
    public void writeMapBegin(int size) throws IOException {
        // TODO check size < 0?
        if(size == 0) {
            stack.reduceCount();
            out.writeByte((byte)0x80);
            stack.reduceCount();
            return;
        }
        if(size < 16) {
            // FixMap
            out.writeByte((byte)(0x80 | size));
        } else if(size < 65536) {
            out.writeByteAndShort((byte)0xde, (short)size);
        } else {
            out.writeByteAndInt((byte)0xdf, size);
        }
        stack.pushMap(size);
    }

    @Override
    public void writeMapEnd(boolean check) throws IOException {
        if(!stack.topIsMap()) {
            throw new MessageTypeException("writeArrayEnd() is called but writeArrayBegin() is not called");
        }

        int remain = stack.getTopCount();
        if(remain > 0) {
            if(check) {
                throw new MessageTypeException("writeArrayEnd(check=true) is called but the array is not end");
            }
            for(int i=0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();
        stack.reduceCount();
    }
}

