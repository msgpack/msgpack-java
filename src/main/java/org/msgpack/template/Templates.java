//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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
package org.msgpack.template;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.msgpack.type.Value;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class Templates {
    public static final Template<Value> TValue = ValueTemplate.getInstance();

    public static final Template<Byte> TByte = ByteTemplate.getInstance();

    public static final Template<Short> TShort = ShortTemplate.getInstance();

    public static final Template<Integer> TInteger = IntegerTemplate.getInstance();

    public static final Template<Long> TLong = LongTemplate.getInstance();

    public static final Template<Character> TCharacter = CharacterTemplate.getInstance();

    public static final Template<BigInteger> TBigInteger = BigIntegerTemplate.getInstance();

    public static final Template<BigDecimal> TBigDecimal = BigDecimalTemplate.getInstance();

    public static final Template<Float> TFloat = FloatTemplate.getInstance();

    public static final Template<Double> TDouble = DoubleTemplate.getInstance();

    public static final Template<Boolean> TBoolean = BooleanTemplate.getInstance();

    public static final Template<String> TString = StringTemplate.getInstance();

    public static final Template<byte[]> TByteArray = ByteArrayTemplate.getInstance();

    public static final Template<ByteBuffer> TByteBuffer = ByteBufferTemplate.getInstance();

    public static final Template<Date> TDate = DateTemplate.getInstance();

    public static <T> Template<T> tNotNullable(Template<T> innerTemplate) {
        return new NotNullableTemplate(innerTemplate);
    }

    public static <E> Template<List<E>> tList(Template<E> elementTemplate) {
        return new ListTemplate(elementTemplate);
    }

    public static <K, V> Template<Map<K, V>> tMap(Template<K> keyTemplate, Template<V> valueTemplate) {
        return new MapTemplate(keyTemplate, valueTemplate);
    }

    public static <E> Template<Collection<E>> tCollection(Template<E> elementTemplate) {
        return new CollectionTemplate(elementTemplate);
    }

    public static <E extends Enum> Template<E> tOrdinalEnum(Class<E> enumClass) {
        return new OrdinalEnumTemplate(enumClass);
    }

    // public static Template<T> tClass(Class<T> target) {
    // // TODO
    // }

    @Deprecated
    public static Template tByte() {
        return TByte;
    }

    @Deprecated
    public static Template tShort() {
        return TShort;
    }

    @Deprecated
    public static Template tInteger() {
        return TInteger;
    }

    @Deprecated
    public static Template tLong() {
        return TLong;
    }

    @Deprecated
    public static Template tCharacter() {
        return TCharacter;
    }

    @Deprecated
    public static Template tBigInteger() {
        return TBigInteger;
    }

    @Deprecated
    public static Template tBigDecimal() {
        return TBigDecimal;
    }

    @Deprecated
    public static Template tFloat() {
        return TFloat;
    }

    @Deprecated
    public static Template tDouble() {
        return TDouble;
    }

    @Deprecated
    public static Template tBoolean() {
        return TBoolean;
    }

    @Deprecated
    public static Template tString() {
        return TString;
    }

    @Deprecated
    public static Template tByteArray() {
        return TByteArray;
    }

    @Deprecated
    public static Template tByteBuffer() {
        return TByteBuffer;
    }

    @Deprecated
    public static Template tDate() {
        return TDate;
    }
}
