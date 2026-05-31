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
package org.msgpack.jackson.dataformat;

import tools.jackson.core.SerializableString;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MessagePackSerializedString
        implements SerializableString
{
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private final Object value;

    public MessagePackSerializedString(Object value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value == null ? null : value.toString();
    }

    @Override
    public int charLength()
    {
        String v = getValue();
        return v == null ? 0 : v.length();
    }

    @Override
    public char[] asQuotedChars()
    {
        String v = getValue();
        return v == null ? new char[0] : v.toCharArray();
    }

    @Override
    public byte[] asUnquotedUTF8()
    {
        String v = getValue();
        return v == null ? new byte[0] : v.getBytes(UTF8);
    }

    @Override
    public byte[] asQuotedUTF8()
    {
        return asUnquotedUTF8();
    }

    @Override
    public int appendQuotedUTF8(byte[] bytes, int i)
    {
        return appendUnquotedUTF8(bytes, i);
    }

    @Override
    public int appendQuoted(char[] chars, int i)
    {
        return appendUnquoted(chars, i);
    }

    @Override
    public int appendUnquotedUTF8(byte[] bytes, int i)
    {
        byte[] utf8 = asUnquotedUTF8();
        if (utf8.length > bytes.length - i) {
            return -1;
        }
        System.arraycopy(utf8, 0, bytes, i, utf8.length);
        return utf8.length;
    }

    @Override
    public int appendUnquoted(char[] chars, int i)
    {
        String v = getValue();
        if (v == null) {
            return 0;
        }
        if (v.length() > chars.length - i) {
            return -1;
        }
        v.getChars(0, v.length(), chars, i);
        return v.length();
    }

    @Override
    public int writeQuotedUTF8(OutputStream outputStream) throws IOException
    {
        return writeUnquotedUTF8(outputStream);
    }

    @Override
    public int writeUnquotedUTF8(OutputStream outputStream) throws IOException
    {
        byte[] utf8 = asUnquotedUTF8();
        outputStream.write(utf8);
        return utf8.length;
    }

    @Override
    public int putQuotedUTF8(ByteBuffer byteBuffer)
    {
        return putUnquotedUTF8(byteBuffer);
    }

    @Override
    public int putUnquotedUTF8(ByteBuffer byteBuffer)
    {
        byte[] utf8 = asUnquotedUTF8();
        if (utf8.length > byteBuffer.remaining()) {
            return -1;
        }
        byteBuffer.put(utf8);
        return utf8.length;
    }

    public Object getRawValue()
    {
        return value;
    }
}
