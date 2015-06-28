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
package org.msgpack.jackson.dataformat.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackDataformatTestBase;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MessagePackDataformatPojoBenchmarkTest
        extends MessagePackDataformatTestBase
{
    private static final int LOOP_MAX = 1000;
    private static final int LOOP_FACTOR = 50;
    private static final int SAMPLING_COUNT = 4;
    private static final List<NormalPojo> pojos = new ArrayList<NormalPojo>(LOOP_MAX);
    private static final List<byte[]> pojosSerWithOrig = new ArrayList<byte[]>(LOOP_MAX);
    private static final List<byte[]> pojosSerWithMsgPack = new ArrayList<byte[]>(LOOP_MAX);
    private final ObjectMapper origObjectMapper = new ObjectMapper();
    private final ObjectMapper msgpackObjectMapper = new ObjectMapper(new MessagePackFactory());

    static {
        final ObjectMapper origObjectMapper = new ObjectMapper();
        final ObjectMapper msgpackObjectMapper = new ObjectMapper(new MessagePackFactory());

        for (int i = 0; i < LOOP_MAX; i++) {
            NormalPojo pojo = new NormalPojo();
            pojo.i = i;
            pojo.l = i;
            pojo.f = Float.valueOf(i);
            pojo.d = Double.valueOf(i);
            pojo.setS(String.valueOf(i));
            pojo.bool = i % 2 == 0;
            pojo.bi = BigInteger.valueOf(i);
            switch (i % 4) {
                case 0:
                    pojo.suit = Suit.SPADE;
                    break;
                case 1:
                    pojo.suit = Suit.HEART;
                    break;
                case 2:
                    pojo.suit = Suit.DIAMOND;
                    break;
                case 3:
                    pojo.suit = Suit.CLUB;
                    break;
            }
            pojo.b = new byte[] {(byte) i};
            pojos.add(pojo);
        }

        for (int i = 0; i < LOOP_MAX; i++) {
            try {
                pojosSerWithOrig.add(origObjectMapper.writeValueAsBytes(pojos.get(i)));
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < LOOP_MAX; i++) {
            try {
                pojosSerWithMsgPack.add(msgpackObjectMapper.writeValueAsBytes(pojos.get(i)));
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBenchmark()
            throws Exception
    {
        double[] durationOfSerializeWithJson = new double[SAMPLING_COUNT];
        double[] durationOfSerializeWithMsgPack = new double[SAMPLING_COUNT];
        double[] durationOfDeserializeWithJson = new double[SAMPLING_COUNT];
        double[] durationOfDeserializeWithMsgPack = new double[SAMPLING_COUNT];
        for (int si = 0; si < SAMPLING_COUNT; si++) {
            long currentTimeMillis = System.currentTimeMillis();
            for (int j = 0; j < LOOP_FACTOR; j++) {
                for (int i = 0; i < LOOP_MAX; i++) {
                    origObjectMapper.writeValueAsBytes(pojos.get(i));
                }
            }
            durationOfSerializeWithJson[si] = System.currentTimeMillis() - currentTimeMillis;

            currentTimeMillis = System.currentTimeMillis();
            for (int j = 0; j < LOOP_FACTOR; j++) {
                for (int i = 0; i < LOOP_MAX; i++) {
                    msgpackObjectMapper.writeValueAsBytes(pojos.get(i));
                }
            }
            durationOfSerializeWithMsgPack[si] = System.currentTimeMillis() - currentTimeMillis;

            currentTimeMillis = System.currentTimeMillis();
            for (int j = 0; j < LOOP_FACTOR; j++) {
                for (int i = 0; i < LOOP_MAX; i++) {
                    origObjectMapper.readValue(pojosSerWithOrig.get(i), NormalPojo.class);
                }
            }
            durationOfDeserializeWithJson[si] = System.currentTimeMillis() - currentTimeMillis;

            currentTimeMillis = System.currentTimeMillis();
            for (int j = 0; j < LOOP_FACTOR; j++) {
                for (int i = 0; i < LOOP_MAX; i++) {
                    msgpackObjectMapper.readValue(pojosSerWithMsgPack.get(i), NormalPojo.class);
                }
            }
            durationOfDeserializeWithMsgPack[si] = System.currentTimeMillis() - currentTimeMillis;
        }
        printStat("serialize(pojo) with JSON", durationOfSerializeWithJson);
        printStat("serialize(pojo) with MessagePack", durationOfSerializeWithMsgPack);
        printStat("deserialize(pojo) with JSON", durationOfDeserializeWithJson);
        printStat("deserialize(pojo) with MessagePack", durationOfDeserializeWithMsgPack);
    }
}
