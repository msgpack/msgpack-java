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

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.ValueType;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;
import org.msgpack.value.ImmutableValue;
import org.msgpack.value.MutableNilValue;
import org.msgpack.value.MutableBooleanValue;
import org.msgpack.value.MutableIntegerValue;
import org.msgpack.value.MutableFloatValue;
import org.msgpack.value.MutableBinaryValue;
import org.msgpack.value.MutableStringValue;
import org.msgpack.value.MessageTypeCastException;
import org.msgpack.value.MessageTypeIntegerOverflowException;
import org.msgpack.value.MessageTypeStringCodingException;

class AbstractUnionMutableValue
        extends AbstractMutableValue
        implements MutableNilValue, MutableBooleanValue,
                   MutableIntegerValue, MutableFloatValue,
                   MutableBinaryValue, MutableStringValue
        // TODO implementation not completed yet
        //    MutableArrayValue, MutableMapValue {
        {

    protected ValueType type;
    protected ValueUnion union;

    private String decodedStringCache;
    private MessageTypeStringCodingException codingException;

    public AbstractUnionMutableValue() {
        this.type = ValueType.NIL;
        this.union = new ValueUnion();
    }

    protected void reset() {
        union.reset();
    }

    protected boolean isSet() {
        return union.isSet();
    }

    protected void resetToNilValue() {
        type = ValueType.NIL;
        union.reset();
    }

    protected void resetToBooleanValue(boolean value) {
        type = ValueType.BOOLEAN;
        union.setBoolean(value);
    }

    protected void resetToIntegerValue(long value) {
        type = ValueType.INTEGER;
        union.setLong(value);
    }

    protected void resetToIntegerValue(BigInteger value) {
        type = ValueType.INTEGER;
        union.setBigInteger(value);
    }

    protected void resetToFloatValue(double value) {
        type = ValueType.FLOAT;
        union.setDouble(value);
    }

    protected void resetToBinaryValue(ByteBuffer value) {
        type = ValueType.BINARY;
        union.setByteBuffer(value);
        decodedStringCache = null;
        codingException = null;
    }

    protected void resetToStringValue(String value) {
        type = ValueType.STRING;
        union.setString(value);
        decodedStringCache = value;
        codingException = null;
    }

    protected void resetToStringValue(ByteBuffer value) {
        type = ValueType.STRING;
        union.setByteBuffer(value);
        decodedStringCache = null;
        codingException = null;
    }

    protected void resetToArrayValue(List<Value> value) {
        type = ValueType.ARRAY;
        union.setList(value);
    }

    protected void resetToMapValue(Map<Value, Value> value) {
        type = ValueType.MAP;
        union.setMap(value);
    }

    @Override
    public ValueType getType() {
        return type;
    }

    @Override
    public ImmutableValue immutableValue() {
        return immutableCopy(true);
    }

    protected ImmutableValue immutableCopy(boolean deepCopy) {
        switch (getType()) {
        case NIL:
            return ValueFactory.createNilValue();
        case BOOLEAN:
            return ValueFactory.createBooleanValue(union.getBoolean());
        case INTEGER:
            if (union.getType() == ValueUnion.Type.LONG) {
                return ValueFactory.createIntegerValue(union.getLong());
            } else {
                return ValueFactory.createIntegerValue(union.getBigInteger());
            }
        case FLOAT:
            return ValueFactory.createFloatValue(union.getDouble());
        case STRING:
            synchronized (this) {
                if (union.getType() == ValueUnion.Type.STRING) {
                    return ValueFactory.createStringValue(union.getString());
                } else if (decodedStringCache != null && codingException == null) {
                    if (deepCopy) {
                        return ValueFactory.createStringValue(decodedStringCache);
                    } else {
                        return new ImmutableStringValueImpl(decodedStringCache, union.getByteBuffer());
                    }
                }
            }
            if (deepCopy) {
                return new ImmutableRawStringValueImpl(copyByteBuffer());
            } else {
                return new ImmutableRawStringValueImpl(union.getByteBuffer());
            }
        case BINARY:
            if (deepCopy) {
                return new ImmutableBinaryValueImpl(copyByteBuffer());
            } else {
                return new ImmutableRawStringValueImpl(union.getByteBuffer());
            }
        case ARRAY:
            if (deepCopy) {
                List<Value> list = union.getList();
                Value[] array = new Value[list.size()];
                Iterator<Value> ite = list.iterator();
                for(int i=0; ite.hasNext(); i++) {
                    array[i] = ite.next().immutableValue();
                }
                return new ImmutableArrayValueImpl(array);
            } else {
                ValueFactory.createArrayValue(union.getList());
            }
        case MAP:
            if (deepCopy) {
                Map<Value, Value> map = union.getMap();
                Value[] kvs = new Value[map.size() * 2];
                Iterator<Map.Entry<Value, Value>> ite = map.entrySet().iterator();
                int index = 0;
                while (ite.hasNext()) {
                    Map.Entry<Value, Value> pair = ite.next();
                    kvs[index] = pair.getKey().immutableValue();
                    index++;
                    kvs[index] = pair.getValue().immutableValue();
                    index++;
                }
                return new ImmutableArrayMapValueImpl(kvs);
            } else {
                ValueFactory.createMapValue(union.getMap());
            }
        case EXTENDED:
            // TODO
        }
        return null;
    }

    private ByteBuffer copyByteBuffer() {
        ByteBuffer copy = ByteBuffer.allocate(union.getByteBuffer().remaining());
        copy.put(union.getByteBuffer().duplicate());
        copy.rewind();
        return copy;
    }

    @Override
    public void writeTo(MessagePacker pk) throws IOException {
        // TODO optimize
        immutableCopy(false).writeTo(pk);
    }

    @Override
    public boolean equals(Object o) {
        // TODO optimize
        return immutableCopy(false).equals(o);
    }

    @Override
    public int hashCode() {
        // TODO optimize
        return immutableCopy(false).hashCode();
    }

    @Override
    public String toString() {
        // TODO optimize
        return immutableCopy(false).toString();
    }

    private void assertType(boolean condition) {
        if (!condition) {
            throw new MessageTypeCastException();
        }
    }

    //
    // BooleanValue
    // resetToBooleanValue() uses union.setBoolean
    //

    @Override
    public boolean booleanValue() {
        assertType(getType() == ValueType.BOOLEAN);
        return union.getBoolean();
    }

    //
    // NumberValue is IntegerValue or FloatValue
    // resetToIntegerValue() uses union.setLong or setBigInteger
    // resetToFloatValue() uses union.setDouble
    //

    @Override
    public byte byteValue() {
        assertType(getType().isNumberType());
        switch (union.getType()) {
        case LONG:
            return (byte) union.getLong();
        case DOUBLE:
            return (byte) union.getDouble();
        case BIG_INTEGER:
        default:
            return union.getBigInteger().byteValue();
        }
    }

    @Override
    public short shortValue() {
        assertType(getType().isNumberType());
        switch (union.getType()) {
        case LONG:
            return (short) union.getLong();
        case DOUBLE:
            return (short) union.getDouble();
        case BIG_INTEGER:
        default:
            return union.getBigInteger().shortValue();
        }
    }

    @Override
    public int intValue() {
        assertType(getType().isNumberType());
        switch (union.getType()) {
        case LONG:
            return (int) union.getLong();
        case DOUBLE:
            return (int) union.getDouble();
        case BIG_INTEGER:
        default:
            return union.getBigInteger().intValue();
        }
    }

    @Override
    public long longValue() {
        assertType(getType().isNumberType());
        switch (union.getType()) {
        case LONG:
            return union.getLong();
        case DOUBLE:
            return (long) union.getDouble();
        case BIG_INTEGER:
        default:
            return union.getBigInteger().longValue();
        }
    }

    @Override
    public BigInteger bigIntegerValue() {
        assertType(getType().isNumberType());
        switch (union.getType()) {
        case LONG:
            return BigInteger.valueOf(union.getLong());
        case DOUBLE:
            return new BigDecimal(union.getDouble()).toBigInteger();
        case BIG_INTEGER:
        default:
            return union.getBigInteger();
        }
    }

    @Override
    public float floatValue() {
        assertType(getType().isNumberType());
        switch (union.getType()) {
        case LONG:
            return (float) union.getLong();
        case DOUBLE:
            return (float) union.getDouble();
        case BIG_INTEGER:
        default:
            return union.getBigInteger().floatValue();
        }
    }

    @Override
    public double doubleValue() {
        assertType(getType().isNumberType());
        switch (union.getType()) {
        case LONG:
            return (double) union.getLong();
        case DOUBLE:
            return union.getDouble();
        case BIG_INTEGER:
        default:
            return union.getBigInteger().doubleValue();
        }
    }

    //
    // IntegerValue
    // resetToIntegerValue() uses union.setLong or setBigInteger
    //

    private static final long L_BYTE_MIN = (long) Byte.MIN_VALUE;
    private static final long L_BYTE_MAX = (long) Byte.MAX_VALUE;
    private static final long L_SHORT_MIN = (long) Short.MIN_VALUE;
    private static final long L_SHORT_MAX = (long) Short.MAX_VALUE;
    private static final long L_INT_MIN = (long) Integer.MIN_VALUE;
    private static final long L_INT_MAX = (long) Integer.MAX_VALUE;

    private static final BigInteger BI_BYTE_MIN = BigInteger.valueOf((long) Byte.MIN_VALUE);
    private static final BigInteger BI_BYTE_MAX = BigInteger.valueOf((long) Byte.MAX_VALUE);
    private static final BigInteger BI_SHORT_MIN = BigInteger.valueOf((long) Short.MIN_VALUE);
    private static final BigInteger BI_SHORT_MAX = BigInteger.valueOf((long) Short.MAX_VALUE);
    private static final BigInteger BI_INT_MIN = BigInteger.valueOf((long) Integer.MIN_VALUE);
    private static final BigInteger BI_INT_MAX = BigInteger.valueOf((long) Integer.MAX_VALUE);
    private static final BigInteger BI_LONG_MIN = BigInteger.valueOf((long) Long.MIN_VALUE);
    private static final BigInteger BI_LONG_MAX = BigInteger.valueOf((long) Long.MAX_VALUE);

    @Override
    public boolean isInByteRange() {
        assertType(getType() == ValueType.INTEGER);
        if (union.getType() == ValueUnion.Type.LONG) {
            long value = union.getLong();
            return L_BYTE_MIN <= value && value <= L_BYTE_MAX;
        } else {
            BigInteger value = union.getBigInteger();
            return 0 <= value.compareTo(BI_BYTE_MIN) && value.compareTo(BI_BYTE_MAX) <= 0;
        }
    }

    @Override
    public boolean isInShortRange() {
        assertType(getType() == ValueType.INTEGER);
        if (union.getType() == ValueUnion.Type.LONG) {
            long value = union.getLong();
            return L_SHORT_MIN <= value && value <= L_SHORT_MAX;
        } else {
            BigInteger value = union.getBigInteger();
            return 0 <= value.compareTo(BI_SHORT_MIN) && value.compareTo(BI_SHORT_MAX) <= 0;
        }
    }

    @Override
    public boolean isInIntRange() {
        assertType(getType() == ValueType.INTEGER);
        if (union.getType() == ValueUnion.Type.LONG) {
            long value = union.getLong();
            return L_INT_MIN <= value && value <= L_INT_MAX;
        } else {
            BigInteger value = union.getBigInteger();
            return 0 <= value.compareTo(BI_INT_MIN) && value.compareTo(BI_INT_MAX) <= 0;
        }
    }

    @Override
    public boolean isInLongRange() {
        assertType(getType() == ValueType.INTEGER);
        if (union.getType() == ValueUnion.Type.LONG) {
            long value = union.getLong();
            return true;
        } else {
            BigInteger value = union.getBigInteger();
            return 0 <= value.compareTo(BI_LONG_MIN) && value.compareTo(BI_LONG_MAX) <= 0;
        }
    }

    @Override
    public byte getByte() throws MessageTypeIntegerOverflowException {
        if (!isInByteRange()) {
            throw new MessageTypeIntegerOverflowException(isInLongRange());
        }
        if (union.getType() == ValueUnion.Type.LONG) {
            return (byte) union.getLong();
        } else {
            return union.getBigInteger().byteValue();
        }
    }

    @Override
    public short getShort() throws MessageTypeIntegerOverflowException {
        if (!isInShortRange()) {
            throw new MessageTypeIntegerOverflowException(isInLongRange());
        }
        if (union.getType() == ValueUnion.Type.LONG) {
            return (short) union.getLong();
        } else {
            return union.getBigInteger().shortValue();
        }
    }

    @Override
    public int getInt() throws MessageTypeIntegerOverflowException {
        if (!isInIntRange()) {
            throw new MessageTypeIntegerOverflowException(isInLongRange());
        }
        if (union.getType() == ValueUnion.Type.LONG) {
            return (int) union.getLong();
        } else {
            return union.getBigInteger().intValue();
        }
    }

    @Override
    public long getLong() throws MessageTypeIntegerOverflowException {
        if (!isInLongRange()) {
            throw new MessageTypeIntegerOverflowException(false);
        }
        if (union.getType() == ValueUnion.Type.LONG) {
            return (long) union.getLong();
        } else {
            return union.getBigInteger().longValue();
        }
    }

    @Override
    public BigInteger getBigInteger() throws MessageTypeIntegerOverflowException {
        assertType(getType() == ValueType.INTEGER);
        if (union.getType() == ValueUnion.Type.LONG) {
            return BigInteger.valueOf(union.getLong());
        } else {
            return union.getBigInteger();
        }
    }

    //
    // RawValue is StringValue or BinaryValue
    // resetToStringValue() uses union.setString or setByteBuffer
    // resetToBinaryValue() uses union.setByteBuffer
    // encodeString() uses union.setByteBuffer
    //
    // union.getTypeFamily() == STRING && decodedStringCache != null never happens
    // because it sets decodedStringCache when it calls union.setString
    //

    @Override
    public byte[] getByteArray() {
        assertType(getType().isRawType());
        if (union.getType() == ValueUnion.Type.STRING) {
            encodeString();
        }

        ByteBuffer byteBuffer = union.getByteBuffer();
        byte[] byteArray = new byte[byteBuffer.remaining()];
        byteBuffer.slice().get(byteArray);
        return byteArray;
    }

    @Override
    public ByteBuffer byteBufferValue() {
        assertType(getType().isRawType());
        if (union.getType() == ValueUnion.Type.STRING) {
            encodeString();
        }

        ByteBuffer byteBuffer = union.getByteBuffer();
        return byteBuffer.slice();
    }

    @Override
    public String stringValue() {
        assertType(getType().isRawType());

        if (decodedStringCache == null) {
            decodeString();
        }
        return decodedStringCache;
    }

    @Override
    public String getString() throws MessageTypeStringCodingException {
        if (decodedStringCache == null) {
            decodeString();
        }
        if (codingException != null) {
            throw codingException;
        }
        return decodedStringCache;
    }

    private synchronized void encodeString() {
        if (union.getType() != ValueUnion.Type.STRING) {
            return;
        }

        String string = union.getString();

        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(string.getBytes("UTF-8"));
            union.setByteBuffer(byteBuffer);
        } catch (UnsupportedEncodingException neverThrown) {
            throw new AssertionError(neverThrown);
        }
    }

    private synchronized void decodeString() {
        if (decodedStringCache != null) {
            return;
        }

        ByteBuffer byteBuffer = union.getByteBuffer();

        try {
            CharsetDecoder reportDecoder = Charset.forName("UTF-8").newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
            decodedStringCache = reportDecoder.decode(byteBuffer.asReadOnlyBuffer()).toString();
        } catch (UnsupportedCharsetException neverThrown) {
            throw new AssertionError(neverThrown);
        } catch (CharacterCodingException ex) {
            codingException = new MessageTypeStringCodingException(ex);
            try {
                CharsetDecoder replaceDecoder = Charset.forName("UTF-8").newDecoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
                decodedStringCache = replaceDecoder.decode(byteBuffer.asReadOnlyBuffer()).toString();
            } catch (UnsupportedCharsetException neverThrown) {
                throw new AssertionError(neverThrown);
            } catch (CharacterCodingException neverThrown) {
                throw new AssertionError(neverThrown);
            }
        }
    }

    // TODO delegate List methods to union.getList()
    // TODO delegate Map methods to union.getMap()
}
