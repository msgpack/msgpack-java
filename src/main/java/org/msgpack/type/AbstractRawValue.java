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
package org.msgpack.type;

import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

abstract class AbstractRawValue extends AbstractValue implements RawValue {
    static final String UTF8 = "UTF-8";

    @Override
    public ValueType getType() {
        return ValueType.RAW;
    }

    @Override
    public boolean isRawValue() {
        return true;
    }

    @Override
    public RawValue asRawValue() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if (!v.isRawValue()) {
            return false;
        }

        return Arrays.equals(getByteArray(), v.asRawValue().getByteArray());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getByteArray());
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        String s;
        if (getClass() == StringRawValueImpl.class) {
            // StringRawValueImpl.getString never throws exception
            s = getString();
        } else {
            // don't throw encoding error exception
            // ignore malformed bytes
            CharsetDecoder decoder = Charset.forName(UTF8).newDecoder()
                    .onMalformedInput(CodingErrorAction.IGNORE)
                    .onUnmappableCharacter(CodingErrorAction.IGNORE);
            try {
                s = decoder.decode(ByteBuffer.wrap(getByteArray())).toString();
            } catch (CharacterCodingException ex) {
                // never comes here
                s = new String(getByteArray());
            }
        }

        sb.append("\"");
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch < 0x20) {
                switch (ch) {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                default:
                    // control chars
                    escapeChar(sb, ch);
                    break;
                }
            } else if (ch <= 0x7f) {
                switch (ch) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                default:
                    sb.append(ch);
                    break;
                }
            } else if (ch >= 0xd800 && ch <= 0xdfff) {
                // surrogates
                escapeChar(sb, ch);
            } else {
                sb.append(ch);
            }
        }
        sb.append("\"");

        return sb;
    }

    private final static char[] HEX_TABLE = "0123456789ABCDEF".toCharArray();

    private void escapeChar(StringBuilder sb, int ch) {
        sb.append("\\u");
        sb.append(HEX_TABLE[(ch >> 12) & 0x0f]);
        sb.append(HEX_TABLE[(ch >> 8) & 0x0f]);
        sb.append(HEX_TABLE[(ch >> 4) & 0x0f]);
        sb.append(HEX_TABLE[ch & 0x0f]);
    }
}
