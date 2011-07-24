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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import org.msgpack.io.Output;
import org.msgpack.io.StreamOutput;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;


public class JSONPacker extends AbstractPacker {
    private static final byte[] NULL = new byte[] { 0x6e, 0x75, 0x6c, 0x6c };
    private static final byte[] TRUE = new byte[] { 0x74, 0x72, 0x75, 0x65 };
    private static final byte[] FALSE = new byte[] { 0x66, 0x61, 0x6c, 0x73, 0x65 };
    private static final byte COMMA = 0x2c;
    private static final byte COLON = 0x3a;
    private static final byte QUOTE = 0x22;
    private static final byte LEFT_BR = 0x5b;
    private static final byte RIGHT_BR = 0x5d;
    private static final byte LEFT_WN = 0x7b;
    private static final byte RIGHT_WN = 0x7d;

    private static final int FLAG_FIRST_ELEMENT = 0x01;
    private static final int FLAG_MAP_KEY       = 0x02;
    private static final int FLAG_MAP_VALUE     = 0x04;
    private static final int FLAG_MAP           = FLAG_MAP_KEY | FLAG_MAP_VALUE;

    protected final Output out;
    private int[] flags;

    private PackerStack stack = new PackerStack();

    public JSONPacker(OutputStream stream) {
        this(new MessagePack(), stream);
    }

    public JSONPacker(MessagePack msgpack, OutputStream stream) {
	this(msgpack, new StreamOutput(stream));
    }

    protected JSONPacker(MessagePack msgpack, Output out) {
        super(msgpack);
        this.out = out;
        this.stack = new PackerStack();
        this.flags = new int[PackerStack.MAX_STACK_SIZE];
    }

    public void writeNil() throws IOException {
        beginElement();
        out.write(NULL, 0, NULL.length);
        endElement();
    }

    public void writeBoolean(boolean v) throws IOException {
        beginElement();
        if(v) {
            out.write(TRUE, 0, TRUE.length);
        } else {
            out.write(FALSE, 0, FALSE.length);
        }
        endElement();
    }

    public void writeByte(byte v) throws IOException {
        beginElement();
        byte[] b = Byte.toString(v).getBytes();  // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    public void writeShort(short v) throws IOException {
        beginElement();
        byte[] b = Short.toString(v).getBytes();  // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    public void writeInt(int v) throws IOException {
        beginElement();
        byte[] b = Integer.toString(v).getBytes();  // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    public void writeLong(long v) throws IOException {
        beginElement();
        byte[] b = Long.toString(v).getBytes();  // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    public void writeBigInteger(BigInteger v) throws IOException {
        beginElement();
        byte[] b = v.toString().getBytes();  // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    public void writeFloat(float v) throws IOException {
        beginElement();
        byte[] b = Float.toString(v).getBytes();  // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    public void writeDouble(double v) throws IOException {
        beginElement();
        byte[] b = Double.toString(v).getBytes();  // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    public void writeByteArray(byte[] b, int off, int len) throws IOException {
        beginElement();
        out.writeByte(QUOTE);
        out.write(b, off, len);  // FIXME escape
        out.writeByte(QUOTE);
        endElement();
    }

    public void writeByteBuffer(ByteBuffer bb) throws IOException {
        beginElement();
        out.writeByte(QUOTE);
        int pos = bb.position();
        try {
            out.write(bb);  // FIXME escape
        } finally {
            bb.position(pos);
        }
        out.writeByte(QUOTE);
        endElement();
    }

    public void writeString(String s) throws IOException {
        beginElement();
        byte[] b = s.getBytes();
        out.writeByte(QUOTE);
        out.write(b, 0, b.length);  // FIXME escape
        out.writeByte(QUOTE);
        endElement();
    }

    public void writeArrayBegin(int size) throws IOException {
        beginElement();
        out.writeByte(LEFT_BR);
        endElement();
        stack.pushArray(size);
        flags[stack.getDepth()] = FLAG_FIRST_ELEMENT;
    }

    public void writeArrayEnd(boolean check) throws IOException {
        if(!stack.topIsArray()) {
            throw new MessageTypeException("writeArrayEnd() is called but writeArrayBegin() is not called");
        }

        int remain = stack.getTopCount();
        if(remain > 0) {
            if(check) {
                throw new MessageTypeException("writeArrayEnd(check=true) is called but the array is not end: "+remain);
            }
            for(int i=0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();

        out.writeByte(RIGHT_BR);
    }

    public void writeMapBegin(int size) throws IOException {
        beginElement();
        out.writeByte(LEFT_WN);
        endElement();
        stack.pushMap(size);
        flags[stack.getDepth()] = FLAG_FIRST_ELEMENT | FLAG_MAP_KEY;
    }

    public void writeMapEnd(boolean check) throws IOException {
        if(!stack.topIsMap()) {
            throw new MessageTypeException("writeMapEnd() is called but writeMapBegin() is not called");
        }

        int remain = stack.getTopCount();
        if(remain > 0) {
            if(check) {
                throw new MessageTypeException("writeMapEnd(check=true) is called but the map is not end: "+remain);
            }
            for(int i=0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();

        out.writeByte(RIGHT_WN);
    }

    private void beginElement() throws IOException {
        int flag = flags[stack.getDepth()];
        if((flag & FLAG_MAP_VALUE) != 0) {
            out.writeByte(COLON);
        } else if(stack.getDepth() > 0 && (flag & FLAG_FIRST_ELEMENT) == 0) {
            out.writeByte(COMMA);
        }
    }

    private void endElement() throws IOException {
        int flag = flags[stack.getDepth()];
        if((flag & FLAG_MAP_KEY) != 0) {
            flag &= ~FLAG_MAP_KEY;
            flag |= FLAG_MAP_VALUE;
        } else if((flag & FLAG_MAP_VALUE) != 0) {
            flag &= ~FLAG_MAP_VALUE;
            flag |= FLAG_MAP_KEY;
        }
        flag &= ~FLAG_FIRST_ELEMENT;
        flags[stack.getDepth()] = flag;
        stack.reduceCount();
    }

    public void close() throws IOException {
        out.close();
    }
}

