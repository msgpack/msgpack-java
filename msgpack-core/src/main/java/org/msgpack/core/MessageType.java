package org.msgpack.core;

import org.msgpack.core.MessagePack.Code;

import java.io.IOException;

/**
 * Detailed type information of message pack values
 */
public enum MessageType {

    // End of file
    EOF(MessageTypeFamily.EOF) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            // do nothing
            return 0;
        }
    },
    // INT7
    POSFIXINT(MessageTypeFamily.INTEGER) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException {
            unpacker.consume();
            return 0;
        }
    },
    // MAP4
    FIXMAP(MessageTypeFamily.MAP) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException {
            int mapLen = unpacker.consume() & 0x0f;
            return mapLen * 2;
        }
    },
    // ARRAY4
    FIXARRAY(MessageTypeFamily.ARRAY) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException {
            int arrLen = unpacker.consume() & 0x0f;
            return arrLen;
        }
    },
    // STR5
    FIXSTR(MessageTypeFamily.STRING) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException {
            int strLen = unpacker.consume() & 0x1f;
            unpacker.consume(strLen);
            return 0;
        }
    },
    NIL(MessageTypeFamily.NIL) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            return 0;
        }
    },
    UNKNOWN(MessageTypeFamily.UNKNOWN) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            throw new MessageFormatException(String.format("unknown code: %02x is found", unpacker.lookAhead()));
        }
    },
    BOOLEAN(MessageTypeFamily.BOOLEAN) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            return 0;
        }
    },
    BIN8(MessageTypeFamily.BINARY) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength8());
            return 0;
        }
    },
    BIN16(MessageTypeFamily.BINARY) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength16());
            return 0;
        }
    },
    BIN32(MessageTypeFamily.BINARY) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength32());
            return 0;
        }
    },
    EXT8(MessageTypeFamily.EXTENDED) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength8() + 1);
            return 0;
        }
    },
    EXT16(MessageTypeFamily.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength16() + 1);
            return 0;
        }
    },
    EXT32(MessageTypeFamily.EXTENDED) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(unpacker.readNextLength32() + 1);
            return 0;
        }
    },

    FLOAT32(MessageTypeFamily.FLOAT) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(5);
            return 0;
        }
    },
    FLOAT64(MessageTypeFamily.FLOAT){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(9);
            return 0;
        }
    },

    UINT8(MessageTypeFamily.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(2);
            return 0;
        }
    },
    UINT16(MessageTypeFamily.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(3);
            return 0;
        }
    },
    UINT32(MessageTypeFamily.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(5);
            return 0;
        }
    },
    UINT64(MessageTypeFamily.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(9);
            return 0;
        }
    },

    INT8(MessageTypeFamily.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(2);
            return 0;
        }
    },
    INT16(MessageTypeFamily.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(3);
            return 0;
        }
    },
    INT32(MessageTypeFamily.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(5);
            return 0;
        }
    },
    INT64(MessageTypeFamily.INTEGER){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(9);
            return 0;
        }
    },
    FIXEXT1(MessageTypeFamily.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(2);
            return 0;
        }
    },
    FIXEXT2(MessageTypeFamily.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(3);
            return 0;
        }
    },
    FIXEXT4(MessageTypeFamily.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(5);
            return 0;
        }
    },
    FIXEXT8(MessageTypeFamily.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(9);
            return 0;
        }
    },
    FIXEXT16(MessageTypeFamily.EXTENDED){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume(17);
            return 0;
        }
    },

    STR8(MessageTypeFamily.STRING){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength8());
            return 0;
        }
    },
    STR16(MessageTypeFamily.STRING){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength16());
            return 0;
        }
    },
    STR32(MessageTypeFamily.STRING){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            unpacker.consume(unpacker.readNextLength32());
            return 0;
        }
    },
    ARRAY16(MessageTypeFamily.ARRAY){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            int arrLen = unpacker.readNextLength16();
            return arrLen;
        }
    },
    ARRAY32(MessageTypeFamily.ARRAY){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            int arrLen = unpacker.readNextLength32();
            return arrLen;
        }
    },
    MAP16(MessageTypeFamily.MAP){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            int mapLen = unpacker.readNextLength16();
            return mapLen * 2;
        }
    },
    MAP32(MessageTypeFamily.MAP){
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            int mapLen = unpacker.readNextLength32();
            return mapLen * 2;
        }
    },
    NEGFIXINT(MessageTypeFamily.INTEGER) {
        @Override
        int skip(MessageUnpacker unpacker) throws IOException{
            unpacker.consume();
            return 0;
        }
    }
    ;

    private final MessageTypeFamily family;

    private MessageType(MessageTypeFamily family) {
        this.family = family;
    }

    public MessageTypeFamily getTypeFamily() {
        return family;
    }

    /**
     * Skip reading a value object of this MessageFormat type
     * @param unpacker
     * @return the number of value object that further need to be skipped
     * @throws IOException
     */
    abstract int skip(MessageUnpacker unpacker) throws IOException;

    private final static MessageType[] formatTable = MessageType.values();
    private final static byte[] table = new byte[256];

    static {
        for(int b = 0; b < 0xFF; ++b) {
            table[b] = (byte) toMessageFormat((byte) b).ordinal();
        }
    }

    public static MessageType lookUp(final byte b) {
        return formatTable[table[b & 0xFF]];
    }

    static MessageType toMessageFormat(final byte b) {
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
