package org.msgpack.core;

import org.msgpack.core.MessagePack.Code;
import org.msgpack.core.annotations.VisibleForTesting;

import java.io.IOException;

/**
 * Defines the list of the message format in the specification
 */
public enum MessageFormat {

    // End of file
    EOF(ValueType.EOF) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            // do nothing
            return 0;
        }
    },
    // INT7
    POSFIXINT(ValueType.INTEGER) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException {
            unpacker.consume();
            return 0;
        }
    },
    // MAP4
    FIXMAP(ValueType.MAP) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException {
            int mapLen = unpacker.consume() & 0x0f;
            return mapLen * 2;
        }
    },
    // ARRAY4
    FIXARRAY(ValueType.ARRAY) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException {
            int arrLen = unpacker.consume() & 0x0f;
            return arrLen;
        }
    },
    // STR5
    FIXSTR(ValueType.STRING) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException {
            int strLen = unpacker.consume() & 0x1f;
            unpacker.consume(strLen);
            return 0;
        }
    },
    NIL(ValueType.NIL) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            return 0;
        }
    },
    UNKNOWN(ValueType.UNKNOWN) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            throw new MessageFormatException(String.format("unknown code: %02x is found", unpacker.lookAhead()));
        }
    },
    BOOLEAN(ValueType.BOOLEAN) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            return 0;
        }
    },
    BIN8(ValueType.BINARY) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength8());
            return 0;
        }
    },
    BIN16(ValueType.BINARY) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength16());
            return 0;
        }
    },
    BIN32(ValueType.BINARY) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength32());
            return 0;
        }
    },
    EXT8(ValueType.EXTENDED) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength8() + 1);
            return 0;
        }
    },
    EXT16(ValueType.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength16() + 1);
            return 0;
        }
    },
    EXT32(ValueType.EXTENDED) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(unpacker.readNextLength32() + 1);
            return 0;
        }
    },

    FLOAT32(ValueType.FLOAT) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(5);
            return 0;
        }
    },
    FLOAT64(ValueType.FLOAT){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(9);
            return 0;
        }
    },

    UINT8(ValueType.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(2);
            return 0;
        }
    },
    UINT16(ValueType.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(3);
            return 0;
        }
    },
    UINT32(ValueType.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(5);
            return 0;
        }
    },
    UINT64(ValueType.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(9);
            return 0;
        }
    },

    INT8(ValueType.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(2);
            return 0;
        }
    },
    INT16(ValueType.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(3);
            return 0;
        }
    },
    INT32(ValueType.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(5);
            return 0;
        }
    },
    INT64(ValueType.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(9);
            return 0;
        }
    },
    FIXEXT1(ValueType.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(2);
            return 0;
        }
    },
    FIXEXT2(ValueType.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(3);
            return 0;
        }
    },
    FIXEXT4(ValueType.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(5);
            return 0;
        }
    },
    FIXEXT8(ValueType.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(9);
            return 0;
        }
    },
    FIXEXT16(ValueType.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(17);
            return 0;
        }
    },

    STR8(ValueType.STRING){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength8());
            return 0;
        }
    },
    STR16(ValueType.STRING){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength16());
            return 0;
        }
    },
    STR32(ValueType.STRING){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength32());
            return 0;
        }
    },
    ARRAY16(ValueType.ARRAY){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            int arrLen = unpacker.readNextLength16();
            return arrLen;
        }
    },
    ARRAY32(ValueType.ARRAY){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            int arrLen = unpacker.readNextLength32();
            return arrLen;
        }
    },
    MAP16(ValueType.MAP){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            int mapLen = unpacker.readNextLength16();
            return mapLen * 2;
        }
    },
    MAP32(ValueType.MAP){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            int mapLen = unpacker.readNextLength32();
            return mapLen * 2;
        }
    },
    NEGFIXINT(ValueType.INTEGER) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            return 0;
        }
    }
    ;

    private final ValueType valueType;

    private MessageFormat(ValueType family) {
        this.valueType = family;
    }

    public ValueType getValueType() {
        return valueType;
    }

    /**
     * Skip reading a value object of this MessageFormat type
     * @param unpacker
     * @return the number of value object that further need to be skipped
     * @throws IOException
     */
    abstract int skip(MessageUnpacker unpacker) throws IOException;

    private final static MessageFormat[] formatTable = MessageFormat.values();
    private final static byte[] table = new byte[256];

    static {
        for(int b = 0; b < 0xFF; ++b) {
            table[b] = (byte) toMessageFormat((byte) b).ordinal();
        }
    }

    public static MessageFormat valueOf(final byte b) {
        return formatTable[table[b & 0xFF]];
    }

    @VisibleForTesting
    static MessageFormat toMessageFormat(final byte b) {
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
                return UNKNOWN;
        }
    }

}
