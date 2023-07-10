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

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class MessagePackMapperTest
{
    static class Pojo
    {
        public BigDecimal value;
    }

    @Test
    public void handleBigDecimalAsString() throws IOException
    {
        MessagePackMapper mapper = new MessagePackMapper().handleBigDecimalAsString();
        Pojo obj = new Pojo();
        obj.value = new BigDecimal("1234567890.98765432100");

        byte[] converted = mapper.writeValueAsBytes(obj);

        Pojo deserialized = mapper.readValue(converted, Pojo.class);
        assertEquals(obj.value, deserialized.value);
    }
}
