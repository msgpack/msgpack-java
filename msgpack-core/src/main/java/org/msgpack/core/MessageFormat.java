package org.msgpack.core;

import org.msgpack.core.MessagePack.Code;
import org.msgpack.core.annotations.VisibleForTesting;
import org.msgpack.value.ValueType;


/**
 * Describes the list of the message format types defined in the MessagePack specification.
 */
public enum MessageFormat {

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
    EXT8(ValueType.EXTENDED),
    EXT16(ValueType.EXTENDED),
    EXT32(ValueType.EXTENDED),
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
    FIXEXT1(ValueType.EXTENDED),
    FIXEXT2(ValueType.EXTENDED),
    FIXEXT4(ValueType.EXTENDED),
    FIXEXT8(ValueType.EXTENDED),
    FIXEXT16(ValueType.EXTENDED),
    STR8(ValueType.STRING),
    STR16(ValueType.STRING),
    STR32(ValueType.STRING),
    ARRAY16(ValueType.ARRAY),
    ARRAY32(ValueType.ARRAY),
    MAP16(ValueType.MAP),
    MAP32(ValueType.MAP),
    NEGFIXINT(ValueType.INTEGER)
    ;

    private final ValueType valueType;

    private MessageFormat(ValueType valueType) {
        this.valueType = valueType;
    }

    /**
     * Retruns the ValueType corresponding to this MessageFormat
     * @return
     * @throws MessageFormatException if this == NEVER_USED type
     */
    public ValueType getValueType() throws MessageFormatException {
        if(this == NEVER_USED)
            throw new MessageFormatException("Cannot convert NEVER_USED to ValueType");
        return valueType;
    }

    private final static MessageFormat[] formatTable = new MessageFormat[256];

    static {
        // Preparing a look up table for converting byte values into MessageFormat types
        for(int b = 0; b <= 0xFF; ++b) {
            MessageFormat mf = toMessageFormat((byte) b);
            formatTable[b] = mf;
        }
    }

    /**
     * Returns a MessageFormat type of the specified byte value
     * @param b
     * @return
     */
    public static MessageFormat valueOf(final byte b) {
        return formatTable[b & 0xFF];
    }

    /**
     * Converting a byte value into MessageFormat. For faster performance, use {@link #valueOf}
     * @param b
     * @return
     */
    @VisibleForTesting
    public static MessageFormat toMessageFormat(final byte b) {
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
