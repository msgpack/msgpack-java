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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MessagePackMapperTest
{
    static class PojoWithBigInteger
    {
        public BigInteger value;
    }

    static class PojoWithBigDecimal
    {
        public BigDecimal value;
    }

    private void shouldFailToHandleBigInteger(MessagePackMapper messagePackMapper) throws JsonProcessingException
    {
        PojoWithBigInteger obj = new PojoWithBigInteger();
        obj.value = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(10));

        try {
            messagePackMapper.writeValueAsBytes(obj);
            fail();
        }
        catch (IllegalArgumentException e) {
            // Expected
        }
    }

    private void shouldSuccessToHandleBigInteger(MessagePackMapper messagePackMapper) throws IOException
    {
        PojoWithBigInteger obj = new PojoWithBigInteger();
        obj.value = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(10));

        byte[] converted = messagePackMapper.writeValueAsBytes(obj);

        PojoWithBigInteger deserialized = messagePackMapper.readValue(converted, PojoWithBigInteger.class);
        assertEquals(obj.value, deserialized.value);
    }

    private void shouldFailToHandleBigDecimal(MessagePackMapper messagePackMapper) throws JsonProcessingException
    {
        PojoWithBigDecimal obj = new PojoWithBigDecimal();
        obj.value = new BigDecimal("1234567890.98765432100");

        try {
            messagePackMapper.writeValueAsBytes(obj);
            fail();
        }
        catch (IllegalArgumentException e) {
            // Expected
        }
    }

    private void shouldSuccessToHandleBigDecimal(MessagePackMapper messagePackMapper) throws IOException
    {
        PojoWithBigDecimal obj = new PojoWithBigDecimal();
        obj.value = new BigDecimal("1234567890.98765432100");

        byte[] converted = messagePackMapper.writeValueAsBytes(obj);

        PojoWithBigDecimal deserialized = messagePackMapper.readValue(converted, PojoWithBigDecimal.class);
        assertEquals(obj.value, deserialized.value);
    }

    @Test
    public void handleBigIntegerAsString() throws IOException
    {
        shouldFailToHandleBigInteger(new MessagePackMapper());
        shouldSuccessToHandleBigInteger(new MessagePackMapper().handleBigIntegerAsString());
    }

    @Test
    public void handleBigDecimalAsString() throws IOException
    {
        shouldFailToHandleBigDecimal(new MessagePackMapper());
        shouldSuccessToHandleBigDecimal(new MessagePackMapper().handleBigDecimalAsString());
    }

    @Test
    public void handleBigIntegerAndBigDecimalAsString() throws IOException
    {
        MessagePackMapper messagePackMapper = new MessagePackMapper().handleBigIntegerAndBigDecimalAsString();
        shouldSuccessToHandleBigInteger(messagePackMapper);
        shouldSuccessToHandleBigDecimal(messagePackMapper);
    }
}
