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

public enum ValueType {

    UNKNOWN(false, false),
    NIL(false, false),
    BOOLEAN(false, false),
    INTEGER(true, false),
    FLOAT(true, false),
    STRING(false, true),
    BINARY(false, true),
    ARRAY(false, false),
    MAP(false, false),
    EXTENDED(false, false);

    private boolean numberType;
    private boolean rawType;

    private ValueType(boolean numberType, boolean rawType) {
        this.numberType = numberType;
        this.rawType = rawType;
    }

    public boolean isNilType() {
        return this == NIL;
    }

    public boolean isBooleanType() {
        return this == BOOLEAN;
    }

    public boolean isNumberType() {
        return numberType;
    }

    public boolean isIntegerType() {
        return this == INTEGER;
    }

    public boolean isFloatType() {
        return this == FLOAT;
    }

    public boolean isRawType() {
        return rawType;
    }

    public boolean isStringType() {
        return this == STRING;
    }

    public boolean isBinaryType() {
        return this == BINARY;
    }

    public boolean isArrayType() {
        return this == ARRAY;
    }

    public boolean isMapType() {
        return this == MAP;
    }

    public boolean isExtendedType() {
        return this == EXTENDED;
    }


    static ValueType toValueType(final byte b) {
        if ((b & 0x80) == 0) { // positive fixint
            return ValueType.INTEGER;
        }
        if ((b & 0xe0) == 0xe0) { // negative fixint
            return ValueType.INTEGER;
        }
        if ((b & 0xe0) == 0xa0) { // fixstr
            return ValueType.STRING;
        }
        if ((b & 0xf0) == 0x90) { // fixarray
            return ValueType.ARRAY;
        }
        if ((b & 0xf0) == 0x80) { // fixmap
            return ValueType.MAP;
        }
        switch (b & 0xff) {
            case 0xc0: // nil
                return ValueType.NIL;
            case 0xc2: // false
            case 0xc3: // true
                return ValueType.BOOLEAN;
            case 0xc4: // bin 8
            case 0xc5: // bin 16
            case 0xc6: // bin 32
                return ValueType.BINARY;
            case 0xc7: // ext 8
            case 0xc8: // ext 16
            case 0xc9: // ext 32
                return ValueType.EXTENDED;
            case 0xca: // float 32
            case 0xcb: // float 64
                return ValueType.FLOAT;
            case 0xcc: // unsigned int 8
            case 0xcd: // unsigned int 16
            case 0xce: // unsigned int 32
            case 0xcf: // unsigned int 64
            case 0xd0: // signed int 8
            case 0xd1: // signed int 16
            case 0xd2: // signed int 32
            case 0xd3: // signed int 64
                return ValueType.INTEGER;
            case 0xd4: // fixext 1
            case 0xd5: // fixext 2
            case 0xd6: // fixext 4
            case 0xd7: // fixext 8
            case 0xd8: // fixext 16
                return ValueType.EXTENDED;
            case 0xd9: // str 8
            case 0xda: // str 16
            case 0xdb: // str 32
                return ValueType.STRING;
            case 0xdc: // array 16
            case 0xdd: // array 32
                return ValueType.ARRAY;
            case 0xde: // map 16
            case 0xdf: // map 32
                return ValueType.MAP;
            default:
                return ValueType.UNKNOWN;
        }
    }

    private static byte[] table = new byte[256];
    private static ValueType[] symbolTable = ValueType.values();
    static {
        // Preparing symbol table (byte value -> ValueType ordinal)
        for(byte b = -127; b < 127; ++b) {
            table[b & 0xFF] = (byte) toValueType(b).ordinal();
        }
    }

    public static ValueType lookUp(final byte b) {
        return symbolTable[table[b & 0xFF]];
    }

}
