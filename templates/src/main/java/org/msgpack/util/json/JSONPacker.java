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
package org.msgpack.util.json;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import org.msgpack.io.Output;
import org.msgpack.io.StreamOutput;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.packer.AbstractPacker;
import org.msgpack.packer.PackerStack;

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
    private static final byte BACKSLASH = 0x5c;
    private static final byte ZERO = 0x30;

    private static final int FLAG_FIRST_ELEMENT = 0x01;
    private static final int FLAG_MAP_KEY = 0x02;
    private static final int FLAG_MAP_VALUE = 0x04;
    // private static final int FLAG_MAP = FLAG_MAP_KEY | FLAG_MAP_VALUE;

    protected final Output out;
    private int[] flags;

    private PackerStack stack = new PackerStack();
    private CharsetDecoder decoder;

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
        this.decoder = Charset.forName("UTF-8").newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    @Override
    protected void writeBoolean(boolean v) throws IOException {
        beginElement();
        if (v) {
            out.write(TRUE, 0, TRUE.length);
        } else {
            out.write(FALSE, 0, FALSE.length);
        }
        endElement();
    }

    @Override
    protected void writeByte(byte v) throws IOException {
        beginElement();
        byte[] b = Byte.toString(v).getBytes(); // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    @Override
    protected void writeShort(short v) throws IOException {
        beginElement();
        byte[] b = Short.toString(v).getBytes(); // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    @Override
    protected void writeInt(int v) throws IOException {
        beginElement();
        byte[] b = Integer.toString(v).getBytes(); // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    @Override
    protected void writeLong(long v) throws IOException {
        beginElement();
        byte[] b = Long.toString(v).getBytes(); // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    @Override
    protected void writeBigInteger(BigInteger v) throws IOException {
        beginElement();
        byte[] b = v.toString().getBytes(); // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    @Override
    protected void writeFloat(float v) throws IOException {
        beginElement();
        Float r = v;
        if (r.isInfinite() || r.isNaN()) {
            throw new IOException(
                    "JSONPacker doesn't support NaN and infinite float value");
        }
        byte[] b = Float.toString(v).getBytes(); // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    @Override
    protected void writeDouble(double v) throws IOException {
        beginElement();
        Double r = v;
        if (r.isInfinite() || r.isNaN()) {
            throw new IOException(
                    "JSONPacker doesn't support NaN and infinite float value");
        }
        byte[] b = Double.toString(v).getBytes(); // TODO optimize
        out.write(b, 0, b.length);
        endElement();
    }

    @Override
    protected void writeByteArray(byte[] b, int off, int len) throws IOException {
        beginStringElement();
        out.writeByte(QUOTE);
        escape(out, b, off, len);
        out.writeByte(QUOTE);
        endElement();
    }

    @Override
    protected void writeByteBuffer(ByteBuffer bb) throws IOException {
        beginStringElement();
        out.writeByte(QUOTE);
        int pos = bb.position();
        try {
            escape(out, bb);
        } finally {
            bb.position(pos);
        }
        out.writeByte(QUOTE);
        endElement();
    }

    @Override
    protected void writeString(String s) throws IOException {
        beginStringElement();
        out.writeByte(QUOTE);
        escape(out, s);
        out.writeByte(QUOTE);
        endElement();
    }

    @Override
    public Packer writeNil() throws IOException {
        beginElement();
        out.write(NULL, 0, NULL.length);
        endElement();
        return this;
    }

    @Override
    public Packer writeArrayBegin(int size) throws IOException {
        beginElement();
        out.writeByte(LEFT_BR);
        endElement();
        stack.pushArray(size);
        flags[stack.getDepth()] = FLAG_FIRST_ELEMENT;
        return this;
    }

    @Override
    public Packer writeArrayEnd(boolean check) throws IOException {
        if (!stack.topIsArray()) {
            throw new MessageTypeException(
                    "writeArrayEnd() is called but writeArrayBegin() is not called");
        }

        int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException(
                        "writeArrayEnd(check=true) is called but the array is not end: " + remain);
            }
            for (int i = 0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();

        out.writeByte(RIGHT_BR);
        return this;
    }

    @Override
    public Packer writeMapBegin(int size) throws IOException {
        beginElement();
        out.writeByte(LEFT_WN);
        endElement();
        stack.pushMap(size);
        flags[stack.getDepth()] = FLAG_FIRST_ELEMENT | FLAG_MAP_KEY;
        return this;
    }

    @Override
    public Packer writeMapEnd(boolean check) throws IOException {
        if (!stack.topIsMap()) {
            throw new MessageTypeException(
                    "writeMapEnd() is called but writeMapBegin() is not called");
        }

        int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException(
                        "writeMapEnd(check=true) is called but the map is not end: " + remain);
            }
            for (int i = 0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();

        out.writeByte(RIGHT_WN);
        return this;
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public void reset() {
        stack.clear();
    }

    private void beginElement() throws IOException {
        int flag = flags[stack.getDepth()];
        if ((flag & FLAG_MAP_KEY) != 0) {
            throw new IOException("Key of a map must be a string in JSON");
        }
        beginStringElement();
    }

    private void beginStringElement() throws IOException {
        int flag = flags[stack.getDepth()];
        if ((flag & FLAG_MAP_VALUE) != 0) {
            out.writeByte(COLON);
        } else if (stack.getDepth() > 0 && (flag & FLAG_FIRST_ELEMENT) == 0) {
            out.writeByte(COMMA);
        }
    }

    private void endElement() throws IOException {
        int flag = flags[stack.getDepth()];
        if ((flag & FLAG_MAP_KEY) != 0) {
            flag &= ~FLAG_MAP_KEY;
            flag |= FLAG_MAP_VALUE;
        } else if ((flag & FLAG_MAP_VALUE) != 0) {
            flag &= ~FLAG_MAP_VALUE;
            flag |= FLAG_MAP_KEY;
        }
        flag &= ~FLAG_FIRST_ELEMENT;
        flags[stack.getDepth()] = flag;
        stack.reduceCount();
    }

    private void escape(Output out, byte[] b, int off, int len) throws IOException {
        escape(out, ByteBuffer.wrap(b, off, len));
    }

    private void escape(Output out, ByteBuffer bb) throws IOException {
        // TODO optimize
        String str = decoder.decode(bb).toString();
        escape(out, str);
    }

    private final static int[] ESCAPE_TABLE;
    private final static byte[] HEX_TABLE;

    static {
        ESCAPE_TABLE = new int[128];
        for (int i = 0; i < 0x20; ++i) {
            // control char
            ESCAPE_TABLE[i] = -1;
        }
        ESCAPE_TABLE['"'] = '"';
        ESCAPE_TABLE['\\'] = '\\';
        ESCAPE_TABLE[0x08] = 'b';
        ESCAPE_TABLE[0x09] = 't';
        ESCAPE_TABLE[0x0C] = 'f';
        ESCAPE_TABLE[0x0A] = 'n';
        ESCAPE_TABLE[0x0D] = 'r';

        char[] hex = "0123456789ABCDEF".toCharArray();
        HEX_TABLE = new byte[hex.length];
        for (int i = 0; i < hex.length; ++i) {
            HEX_TABLE[i] = (byte) hex[i];
        }
    }

    private static void escape(Output out, String s) throws IOException {
        byte[] tmp = new byte[] { (byte) '\\', (byte) 'u', 0, 0, 0, 0 };
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int ch = chars[i];
            if (ch <= 0x7f) {
                int e = ESCAPE_TABLE[ch];
                if (e == 0) {
                    // ascii char
                    tmp[2] = (byte) ch;
                    out.write(tmp, 2, 1);
                } else if (e > 0) {
                    // popular control char
                    tmp[2] = BACKSLASH;
                    tmp[3] = (byte) e;
                    out.write(tmp, 2, 2);
                } else {
                    // control char uXXXXXX
                    tmp[2] = ZERO;
                    tmp[3] = ZERO;
                    tmp[4] = HEX_TABLE[ch >> 4];
                    tmp[5] = HEX_TABLE[ch & 0x0f];
                    out.write(tmp, 0, 6);
                }
            } else if (ch <= 0x7ff) {
                // 2-bytes char
                tmp[2] = (byte) (0xc0 | (ch >> 6));
                tmp[3] = (byte) (0x80 | (ch & 0x3f));
                out.write(tmp, 2, 2);
            } else if (ch >= 0xd800 && ch <= 0xdfff) {
                // surrogates
                tmp[2] = HEX_TABLE[(ch >> 12) & 0x0f];
                tmp[3] = HEX_TABLE[(ch >> 8) & 0x0f];
                tmp[4] = HEX_TABLE[(ch >> 4) & 0x0f];
                tmp[5] = HEX_TABLE[ch & 0x0f];
                out.write(tmp, 0, 6);
            } else {
                // 3-bytes char
                tmp[2] = (byte) (0xe0 | (ch >> 12));
                tmp[3] = (byte) (0x80 | ((ch >> 6) & 0x3f));
                tmp[4] = (byte) (0x80 | (ch & 0x3f));
                out.write(tmp, 2, 3);
            }
        }
    }
}
