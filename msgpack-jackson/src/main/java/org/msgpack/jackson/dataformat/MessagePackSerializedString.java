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

import com.fasterxml.jackson.core.SerializableString;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class MessagePackSerializedString
        implements SerializableString
{
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final Object value;

    public MessagePackSerializedString(Object value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value.toString();
    }

    @Override
    public int charLength()
    {
        return getValue().length();
    }

    @Override
    public char[] asQuotedChars()
    {
        return getValue().toCharArray();
    }

    @Override
    public byte[] asUnquotedUTF8()
    {
        return getValue().getBytes(UTF8);
    }

    @Override
    public byte[] asQuotedUTF8()
    {
        return ("\"" + getValue() + "\"").getBytes(UTF8);
    }

    @Override
    public int appendQuotedUTF8(byte[] bytes, int i)
    {
        return 0;
    }

    @Override
    public int appendQuoted(char[] chars, int i)
    {
        return 0;
    }

    @Override
    public int appendUnquotedUTF8(byte[] bytes, int i)
    {
        return 0;
    }

    @Override
    public int appendUnquoted(char[] chars, int i)
    {
        return 0;
    }

    @Override
    public int writeQuotedUTF8(OutputStream outputStream)
            throws IOException
    {
        return 0;
    }

    @Override
    public int writeUnquotedUTF8(OutputStream outputStream)
            throws IOException
    {
        return 0;
    }

    @Override
    public int putQuotedUTF8(ByteBuffer byteBuffer)
            throws IOException
    {
        return 0;
    }

    @Override
    public int putUnquotedUTF8(ByteBuffer byteBuffer)
            throws IOException
    {
        return 0;
    }

    public Object getRawValue()
    {
        return value;
    }
}
