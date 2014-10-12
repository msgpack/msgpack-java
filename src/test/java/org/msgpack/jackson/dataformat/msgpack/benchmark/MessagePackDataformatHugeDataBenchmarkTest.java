package org.msgpack.jackson.dataformat.msgpack.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.msgpack.jackson.dataformat.msgpack.MessagePackDataformatTestBase;
import org.msgpack.jackson.dataformat.msgpack.MessagePackFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagePackDataformatHugeDataBenchmarkTest extends MessagePackDataformatTestBase {
    private static final int ELM_NUM = 1500000;
    private static final int SAMPLING_COUNT = 4;
    private final ObjectMapper origObjectMapper = new ObjectMapper();
    private final ObjectMapper msgpackObjectMapper = new ObjectMapper(new MessagePackFactory());
    private static final List<Object> value;
    private static final byte[] packedByOriginal;
    private static final byte[] packedByMsgPack;
    static {
        value = new ArrayList<Object>();
        for (int i = 0; i < ELM_NUM; i++) {
            value.add((long)i);
        }
        for (int i = 0; i < ELM_NUM; i++) {
            value.add((double)i);
        }
        for (int i = 0; i < ELM_NUM; i++) {
            value.add(String.valueOf(i));
        }

        byte[] bytes = null;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        packedByOriginal = bytes;

        try {
            bytes = new ObjectMapper(new MessagePackFactory()).writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        packedByMsgPack = bytes;
    }

    @Test
    public void testBenchmark() throws Exception {
        double durationOfSerializeWithJson[] = new double[SAMPLING_COUNT];
        double durationOfSerializeWithMsgPack[] = new double[SAMPLING_COUNT];
        double durationOfDeserializeWithJson[] = new double[SAMPLING_COUNT];
        double durationOfDeserializeWithMsgPack[] = new double[SAMPLING_COUNT];
        for (int si = 0; si < SAMPLING_COUNT; si++) {
            long currentTimeMillis = System.currentTimeMillis();
            origObjectMapper.writeValueAsBytes(value);
            durationOfSerializeWithJson[si] = System.currentTimeMillis() - currentTimeMillis;

            currentTimeMillis = System.currentTimeMillis();
            msgpackObjectMapper.writeValueAsBytes(value);
            durationOfSerializeWithMsgPack[si] = System.currentTimeMillis() - currentTimeMillis;

            currentTimeMillis = System.currentTimeMillis();
            origObjectMapper.readValue(packedByOriginal, new TypeReference<List<Object>>() {});
            durationOfDeserializeWithJson[si] = System.currentTimeMillis() - currentTimeMillis;

            currentTimeMillis = System.currentTimeMillis();
            msgpackObjectMapper.readValue(packedByMsgPack, new TypeReference<List<Object>>() {});
            durationOfDeserializeWithMsgPack[si] = System.currentTimeMillis() - currentTimeMillis;
        }
        printStat("serialize(huge) with JSON", durationOfSerializeWithJson);
        printStat("serialize(huge) with MessagePack", durationOfSerializeWithMsgPack);
        printStat("deserialize(huge) with JSON", durationOfDeserializeWithJson);
        printStat("deserialize(huge) with MessagePack", durationOfDeserializeWithMsgPack);
    }
}
