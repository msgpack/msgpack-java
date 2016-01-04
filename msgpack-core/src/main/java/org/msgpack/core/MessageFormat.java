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
package org.msgpack.core;

import org.msgpack.core.annotations.VisibleForTesting;
import org.msgpack.value.ValueType;

/**
 * Describes the list of the message format types defined in the MessagePack specification.
 */
public enum MessageFormat
{
    // INT7
    POSFIXINT(ValueType.INTEGER),
    // MAP4
    FIXMAP(ValueType.MAP),
    // ARRAY4
    FIXARRAY(ValueType.ARRAY),
    // STR5
    FIXSTR(ValueType.STRING),
    NIL(ValueType.NIL),
    NEVER_USED(null),
    BOOLEAN(ValueType.BOOLEAN),
    BIN8(ValueType.BINARY),
    BIN16(ValueType.BINARY),
    BIN32(ValueType.BINARY),
    EXT8(ValueType.EXTENSION),
    EXT16(ValueType.EXTENSION),
    EXT32(ValueType.EXTENSION),
    FLOAT32(ValueType.FLOAT),
    FLOAT64(ValueType.FLOAT),
    UINT8(ValueType.INTEGER),
    UINT16(ValueType.INTEGER),
    UINT32(ValueType.INTEGER),
    UINT64(ValueType.INTEGER),

    INT8(ValueType.INTEGER),
    INT16(ValueType.INTEGER),
    INT32(ValueType.INTEGER),
    INT64(ValueType.INTEGER),
    FIXEXT1(ValueType.EXTENSION),
    FIXEXT2(ValueType.EXTENSION),
    FIXEXT4(ValueType.EXTENSION),
    FIXEXT8(ValueType.EXTENSION),
    FIXEXT16(ValueType.EXTENSION),
    STR8(ValueType.STRING),
    STR16(ValueType.STRING),
    STR32(ValueType.STRING),
    ARRAY16(ValueType.ARRAY),
    ARRAY32(ValueType.ARRAY),
    MAP16(ValueType.MAP),
    MAP32(ValueType.MAP),
    NEGFIXINT(ValueType.INTEGER);

    /**
     * The prefix code set of MessagePack. See also https://github.com/msgpack/msgpack/blob/master/spec.md for details.
     */
    public static final class Code
    {
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
            return (b & (byte) 0xe0) == Code.FIXSTR_PREFIX;
        }

        public static final boolean isFixedArray(byte b)
        {
            return (b & (byte) 0xf0) == Code.FIXARRAY_PREFIX;
        }

        public static final boolean isFixedMap(byte b)
        {
            return (b & (byte) 0xf0) == Code.FIXMAP_PREFIX;
        }

        public static final boolean isFixedRaw(byte b)
        {
            return (b & (byte) 0xe0) == Code.FIXSTR_PREFIX;
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
    }

    private static final MessageFormat[] formatTable = new MessageFormat[256];
    private final ValueType valueType;

    private MessageFormat(ValueType valueType)
    {
        this.valueType = valueType;
    }

    /**
     * Retruns the ValueType corresponding to this MessageFormat
     *
     * @return value type
     * @throws MessageFormatException if this == NEVER_USED type
     */
    public ValueType getValueType()
            throws MessageFormatException
    {
        if (this == NEVER_USED) {
            throw new MessageFormatException("Cannot convert NEVER_USED to ValueType");
        }
        return valueType;
    }

    static {
        // Preparing a look up table for converting byte values into MessageFormat types
        for (int b = 0; b <= 0xFF; ++b) {
            MessageFormat mf = toMessageFormat((byte) b);
            formatTable[b] = mf;
        }
    }

    /**
     * Returns a MessageFormat type of the specified byte value
     *
     * @param b MessageFormat of the given byte
     * @return
     */
    public static MessageFormat valueOf(final byte b)
    {
        return formatTable[b & 0xFF];
    }

    /**
     * Converting a byte value into MessageFormat. For faster performance, use {@link #valueOf}
     *
     * @param b MessageFormat of the given byte
     * @return
     */
    @VisibleForTesting
    static MessageFormat toMessageFormat(final byte b)
    {
        if (Code.isPosFixInt(b)) {
            return POSFIXINT;
        }
        if (Code.isNegFixInt(b)) {
            return NEGFIXINT;
        }
        if (Code.isFixStr(b)) {
            return FIXSTR;
        }
        if (Code.isFixedArray(b)) {
            return FIXARRAY;
        }
        if (Code.isFixedMap(b)) {
            return FIXMAP;
        }
        switch (b) {
            case Code.NIL:
                return NIL;
            case Code.FALSE:
            case Code.TRUE:
                return BOOLEAN;
            case Code.BIN8:
                return BIN8;
            case Code.BIN16:
                return BIN16;
            case Code.BIN32:
                return BIN32;
            case Code.EXT8:
                return EXT8;
            case Code.EXT16:
                return EXT16;
            case Code.EXT32:
                return EXT32;
            case Code.FLOAT32:
                return FLOAT32;
            case Code.FLOAT64:
                return FLOAT64;
            case Code.UINT8:
                return UINT8;
            case Code.UINT16:
                return UINT16;
            case Code.UINT32:
                return UINT32;
            case Code.UINT64:
                return UINT64;
            case Code.INT8:
                return INT8;
            case Code.INT16:
                return INT16;
            case Code.INT32:
                return INT32;
            case Code.INT64:
                return INT64;
            case Code.FIXEXT1:
                return FIXEXT1;
            case Code.FIXEXT2:
                return FIXEXT2;
            case Code.FIXEXT4:
                return FIXEXT4;
            case Code.FIXEXT8:
                return FIXEXT8;
            case Code.FIXEXT16:
                return FIXEXT16;
            case Code.STR8:
                return STR8;
            case Code.STR16:
                return STR16;
            case Code.STR32:
                return STR32;
            case Code.ARRAY16:
                return ARRAY16;
            case Code.ARRAY32:
                return ARRAY32;
            case Code.MAP16:
                return MAP16;
            case Code.MAP32:
                return MAP32;
            default:
                return NEVER_USED;
        }
    }
}
