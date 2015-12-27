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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import static org.msgpack.jackson.dataformat.MessagePackDataformatTestBase.NormalPojo;
import static org.msgpack.jackson.dataformat.MessagePackDataformatTestBase.Suit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MessagePackDataformatPojoBenchmarkTest
{
    private static final int LOOP_MAX = 200;
    private static final int LOOP_FACTOR_SER = 40;
    private static final int LOOP_FACTOR_DESER = 200;
    private static final int COUNT = 6;
    private static final int WARMUP_COUNT = 4;
    private final List<NormalPojo> pojos = new ArrayList<NormalPojo>(LOOP_MAX);
    private final List<byte[]> pojosSerWithOrig = new ArrayList<byte[]>(LOOP_MAX);
    private final List<byte[]> pojosSerWithMsgPack = new ArrayList<byte[]>(LOOP_MAX);
    private final ObjectMapper origObjectMapper = new ObjectMapper();
    private final ObjectMapper msgpackObjectMapper = new ObjectMapper(new MessagePackFactory());

    public MessagePackDataformatPojoBenchmarkTest()
    {
        origObjectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        msgpackObjectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        for (int i = 0; i < LOOP_MAX; i++) {
            NormalPojo pojo = new NormalPojo();
            pojo.i = i;
            pojo.l = i;
            pojo.f = Float.valueOf(i);
            pojo.d = Double.valueOf(i);
            StringBuilder sb = new StringBuilder();
            for (int sbi = 0; sbi < i * 50; sbi++) {
                sb.append("x");
            }
            pojo.setS(sb.toString());
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
                throw new RuntimeException("Failed to create test data");
            }
        }

        for (int i = 0; i < LOOP_MAX; i++) {
            try {
                pojosSerWithMsgPack.add(msgpackObjectMapper.writeValueAsBytes(pojos.get(i)));
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to create test data");
            }
        }
    }

    @Test
    public void testBenchmark()
            throws Exception
    {
        Benchmarker benchmarker = new Benchmarker();

        File tempFileJackson = File.createTempFile("msgpack-jackson-", "-huge-jackson");
        tempFileJackson.deleteOnExit();
        final OutputStream outputStreamJackson = new FileOutputStream(tempFileJackson);

        File tempFileMsgpack = File.createTempFile("msgpack-jackson-", "-huge-msgpack");
        tempFileMsgpack.deleteOnExit();
        final OutputStream outputStreamMsgpack = new FileOutputStream(tempFileMsgpack);

        benchmarker.addBenchmark(new Benchmarker.Benchmarkable("serialize(pojo) with JSON") {
            @Override
            public void run()
                    throws Exception
            {
                for (int j = 0; j < LOOP_FACTOR_SER; j++) {
                    for (int i = 0; i < LOOP_MAX; i++) {
                        origObjectMapper.writeValue(outputStreamJackson, pojos.get(i));
                    }
                }
            }
        });

        benchmarker.addBenchmark(new Benchmarker.Benchmarkable("serialize(pojo) with MessagePack") {
            @Override
            public void run()
                    throws Exception
            {
                for (int j = 0; j < LOOP_FACTOR_SER; j++) {
                    for (int i = 0; i < LOOP_MAX; i++) {
                        msgpackObjectMapper.writeValue(outputStreamMsgpack, pojos.get(i));
                    }
                }
            }
        });

       benchmarker.addBenchmark(new Benchmarker.Benchmarkable("deserialize(pojo) with JSON") {
            @Override
            public void run()
                    throws Exception
            {
                for (int j = 0; j < LOOP_FACTOR_DESER; j++) {
                    for (int i = 0; i < LOOP_MAX; i++) {
                        origObjectMapper.readValue(pojosSerWithOrig.get(i), NormalPojo.class);
                    }
                }
            }
        });

        benchmarker.addBenchmark(new Benchmarker.Benchmarkable("deserialize(pojo) with MessagePack") {
            @Override
            public void run()
                    throws Exception
            {
                for (int j = 0; j < LOOP_FACTOR_DESER; j++) {
                    for (int i = 0; i < LOOP_MAX; i++) {
                        msgpackObjectMapper.readValue(pojosSerWithMsgPack.get(i), NormalPojo.class);
                    }
                }
            }
        });

        try {
            benchmarker.run(COUNT, WARMUP_COUNT);
        }
        finally {
            outputStreamJackson.close();
            outputStreamMsgpack.close();
        }
    }
}
