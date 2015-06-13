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

import org.msgpack.core.MessageFormat;

/**
 * MessageTypeFamily is a group of {@link org.msgpack.core.MessageFormat}s
 */
public enum ValueType {

    NIL(false, false),
    BOOLEAN(false, false),
    INTEGER(true, false),
    FLOAT(true, false),
    STRING(false, true),
    BINARY(false, true),
    ARRAY(false, false),
    MAP(false, false),
    EXTENSION(false, true);

    private final boolean numberType;
    private final boolean rawType;
    private final int bitMask;

    private ValueType(boolean numberType, boolean rawType) {
        this.numberType = numberType;
        this.rawType = rawType;
        this.bitMask = 1 << this.ordinal();
    }

    /**
     * Returns a bit mask representing this value type for quickly cheking
     * this value type
     * @return bit mask representing this value type
     */
    public int getBitMask() {
        return bitMask;
    }

    /**
     * Check whether the given bit mask represents this value type
     * @param bitMask
     * @return
     */
    public boolean isTypeOf(int bitMask) {
        return (this.bitMask & bitMask) != 0;
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

    public boolean isExtensionType() {
        return this == EXTENSION;
    }

    public static ValueType valueOf(byte b) {
        return MessageFormat.valueOf(b).getValueType();
    }

    public String toTypeName() {
        return this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
    }
}
