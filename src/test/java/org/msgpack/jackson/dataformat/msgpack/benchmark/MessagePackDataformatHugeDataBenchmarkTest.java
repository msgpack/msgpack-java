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
    private static final int ELM_NUM = 100000;
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
    public void testBenchmarkSerializeWithNormalObjectMapper() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String label = "normal object mapper: serialize(huge_data)";
        byte[] bytes = origObjectMapper.writeValueAsBytes(value);
        System.out.println(String.format("%s => %d", label, (System.currentTimeMillis() - currentTimeMillis)));
    }

    @Test
    public void testBenchmarkSerializeWithMessagePackObjectMapper() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String label = "msgpack object mapper: serialize(huge_data)";
        byte[] bytes = msgpackObjectMapper.writeValueAsBytes(value);
        System.out.println(String.format("%s => %d", label, (System.currentTimeMillis() - currentTimeMillis)));
    }

    @Test
    public void testBenchmarkDeserializeWithNormalObjectMapper() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String label = "normal object mapper: deserialize(huge_data)";
        List<Object> v = origObjectMapper.readValue(packedByOriginal, new TypeReference<List<Object>>() {});
        System.out.println(String.format("%s => %d", label, (System.currentTimeMillis() - currentTimeMillis)));
    }

    @Test
    public void testBenchmarkDeserializeWithMessagePackObjectMapper() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String label = "msgpack object mapper: deserialize(huge_data)";
        List<Object> v = msgpackObjectMapper.readValue(packedByMsgPack, new TypeReference<List<Object>>() {});
        System.out.println(String.format("%s => %d", label, (System.currentTimeMillis() - currentTimeMillis)));
    }
}
