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
package org.msgpack.value;

/**
 * The prefix code set of MessagePack format. See also https://github.com/msgpack/msgpack/blob/master/spec.md for details.
 */
public final class MessagePackCode
{
    private MessagePackCode()
    {
        // No construction.
    }

    public static final boolean isFixInt(byte b)
    {
        int v = b & 0xFF;
        return v <= 0x7f || v >= 0xe0;
    }

    public static final boolean isPosFixInt(byte b)
    {
        return (b & POSFIXINT_MASK) == 0;
    }

    public static final boolean isNegFixInt(byte b)
    {
        return (b & NEGFIXINT_PREFIX) == NEGFIXINT_PREFIX;
    }

    public static final boolean isFixStr(byte b)
    {
        return (b & (byte) 0xe0) == MessagePackCode.FIXSTR_PREFIX;
    }

    public static final boolean isFixedArray(byte b)
    {
        return (b & (byte) 0xf0) == MessagePackCode.FIXARRAY_PREFIX;
    }

    public static final boolean isFixedMap(byte b)
    {
        return (b & (byte) 0xf0) == MessagePackCode.FIXMAP_PREFIX;
    }

    public static final boolean isFixedRaw(byte b)
    {
        return (b & (byte) 0xe0) == MessagePackCode.FIXSTR_PREFIX;
    }

    public static final byte POSFIXINT_MASK = (byte) 0x80;

    public static final byte FIXMAP_PREFIX = (byte) 0x80;
    public static final byte FIXARRAY_PREFIX = (byte) 0x90;
    public static final byte FIXSTR_PREFIX = (byte) 0xa0;

    public static final byte NIL = (byte) 0xc0;
    public static final byte NEVER_USED = (byte) 0xc1;
    public static final byte FALSE = (byte) 0xc2;
    public static final byte TRUE = (byte) 0xc3;
    public static final byte BIN8 = (byte) 0xc4;
    public static final byte BIN16 = (byte) 0xc5;
    public static final byte BIN32 = (byte) 0xc6;
    public static final byte EXT8 = (byte) 0xc7;
    public static final byte EXT16 = (byte) 0xc8;
    public static final byte EXT32 = (byte) 0xc9;
    public static final byte FLOAT32 = (byte) 0xca;
    public static final byte FLOAT64 = (byte) 0xcb;
    public static final byte UINT8 = (byte) 0xcc;
    public static final byte UINT16 = (byte) 0xcd;
    public static final byte UINT32 = (byte) 0xce;
    public static final byte UINT64 = (byte) 0xcf;

    public static final byte INT8 = (byte) 0xd0;
    public static final byte INT16 = (byte) 0xd1;
    public static final byte INT32 = (byte) 0xd2;
    public static final byte INT64 = (byte) 0xd3;

    public static final byte FIXEXT1 = (byte) 0xd4;
    public static final byte FIXEXT2 = (byte) 0xd5;
    public static final byte FIXEXT4 = (byte) 0xd6;
    public static final byte FIXEXT8 = (byte) 0xd7;
    public static final byte FIXEXT16 = (byte) 0xd8;

    public static final byte STR8 = (byte) 0xd9;
    public static final byte STR16 = (byte) 0xda;
    public static final byte STR32 = (byte) 0xdb;

    public static final byte ARRAY16 = (byte) 0xdc;
    public static final byte ARRAY32 = (byte) 0xdd;

    public static final byte MAP16 = (byte) 0xde;
    public static final byte MAP32 = (byte) 0xdf;

    public static final byte NEGFIXINT_PREFIX = (byte) 0xe0;

    public static final byte EXT_TIMESTAMP = (byte) -1;
}
