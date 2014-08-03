package org.msgpack.jackson.dataformat.msgpack.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.msgpack.jackson.dataformat.msgpack.MessagePackDataformatTestBase;
import org.msgpack.jackson.dataformat.msgpack.MessagePackFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MessagePackDataformatPojoBenchmarkTest extends MessagePackDataformatTestBase {
    private static final int LOOP_MAX = 50000;
    private static final int LOOP_FACTOR = 100;
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
            pojo.bi = BigInteger.valueOf(i);
            pojo.b = new byte[] {(byte) i};
            pojos.add(pojo);
        }

        for (int i = 0; i < LOOP_MAX; i++) {
            try {
                pojosSerWithOrig.add(origObjectMapper.writeValueAsBytes(pojos.get(i)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < LOOP_MAX; i++) {
            try {
                pojosSerWithMsgPack.add(msgpackObjectMapper.writeValueAsBytes(pojos.get(i)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBenchmarkSerializeWithNormalObjectMapper() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String label = "normal object mapper: serialize(pojo)";
        for (int j = 0; j < LOOP_FACTOR; j++)
            for (int i = 0; i < LOOP_MAX; i++) {
                origObjectMapper.writeValueAsBytes(pojos.get(i));
            }
        System.out.println(String.format("%s => %d", label, (System.currentTimeMillis() - currentTimeMillis)));
    }

    @Test
    public void testBenchmarkSerializeWithMessagePackObjectMapper() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String label = "msgpack object mapper: serialize(pojo)";
        for (int j = 0; j < LOOP_FACTOR; j++)
            for (int i = 0; i < LOOP_MAX; i++) {
                msgpackObjectMapper.writeValueAsBytes(pojos.get(i));
            }
        System.out.println(String.format("%s => %d", label, (System.currentTimeMillis() - currentTimeMillis)));
    }

    @Test
    public void testBenchmarkDeserializeWithNormalObjectMapper() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String label = "normal object mapper: deserialize(pojo)";
        for (int j = 0; j < LOOP_FACTOR; j++)
            for (int i = 0; i < LOOP_MAX; i++) {
                origObjectMapper.readValue(pojosSerWithOrig.get(i), NormalPojo.class);
            }
        System.out.println(String.format("%s => %d", label, (System.currentTimeMillis() - currentTimeMillis)));
    }

    @Test
    public void testBenchmarkDeserializeWithMessagePackObjectMapper() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String label = "msgpack object mapper: deserialize(pojo)";
        for (int j = 0; j < LOOP_FACTOR; j++)
            for (int i = 0; i < LOOP_MAX; i++) {
                msgpackObjectMapper.readValue(pojosSerWithMsgPack.get(i), NormalPojo.class);
            }
        System.out.println(String.format("%s => %d", label, (System.currentTimeMillis() - currentTimeMillis)));
    }
}
