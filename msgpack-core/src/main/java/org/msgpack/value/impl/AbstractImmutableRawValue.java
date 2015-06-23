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
package org.msgpack.value.impl;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageStringCodingException;
import org.msgpack.value.ImmutableRawValue;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

public abstract class AbstractImmutableRawValue
        extends AbstractImmutableValue
        implements ImmutableRawValue
{
    protected final byte[] data;
    private volatile String decodedStringCache;
    private volatile CharacterCodingException codingException;

    public AbstractImmutableRawValue(byte[] data)
    {
        this.data = data;
    }

    public AbstractImmutableRawValue(String string)
    {
        this.decodedStringCache = string;
        this.data = string.getBytes(MessagePack.UTF8);  // TODO
    }

    @Override
    public ImmutableRawValue asRawValue()
    {
        return this;
    }

    @Override
    public byte[] getByteArray()
    {
        return Arrays.copyOf(data, data.length);
    }

    @Override
    public ByteBuffer getByteBuffer()
    {
        return ByteBuffer.wrap(data).asReadOnlyBuffer();
    }

    @Override
    public String getString()
    {
        if (decodedStringCache == null) {
            decodeString();
        }
        if (codingException != null) {
            throw new MessageStringCodingException(codingException);
        }
        else {
            return decodedStringCache;
        }
    }

    @Override
    public String stringValue()
    {
        if (decodedStringCache == null) {
            decodeString();
        }
        return decodedStringCache;
    }

    private void decodeString()
    {
        synchronized (data) {
            if (decodedStringCache != null) {
                return;
            }
            try {
                CharsetDecoder reportDecoder = MessagePack.UTF8.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT);
                this.decodedStringCache = reportDecoder.decode(getByteBuffer()).toString();
            }
            catch (CharacterCodingException ex) {
                try {
                    CharsetDecoder replaceDecoder = MessagePack.UTF8.newDecoder()
                            .onMalformedInput(CodingErrorAction.REPLACE)
                            .onUnmappableCharacter(CodingErrorAction.REPLACE);
                    this.decodedStringCache = replaceDecoder.decode(getByteBuffer()).toString();
                }
                catch (CharacterCodingException neverThrown) {
                    throw new MessageStringCodingException(neverThrown);
                }
                this.codingException = ex;
            }
        }
    }

    @Override
    public String toString()
    {
        return toString(new StringBuilder()).toString();
    }

    private StringBuilder toString(StringBuilder sb)
    {
        String s = stringValue();
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
            }
            else if (ch <= 0x7f) {
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
            }
            else if (ch >= 0xd800 && ch <= 0xdfff) {
                // surrogates
                escapeChar(sb, ch);
            }
            else {
                sb.append(ch);
            }
        }
        sb.append("\"");

        return sb;
    }

    private static final char[] HEX_TABLE = "0123456789ABCDEF".toCharArray();

    private void escapeChar(StringBuilder sb, int ch)
    {
        sb.append("\\u");
        sb.append(HEX_TABLE[(ch >> 12) & 0x0f]);
        sb.append(HEX_TABLE[(ch >> 8) & 0x0f]);
        sb.append(HEX_TABLE[(ch >> 4) & 0x0f]);
        sb.append(HEX_TABLE[ch & 0x0f]);
    }
}
