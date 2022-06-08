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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;
import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferInput;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MessagePackGeneratorTest
        extends MessagePackDataformatTestBase
{
    @Test
    public void testGeneratorShouldWriteObject()
            throws IOException
    {
        Map<String, Object> hashMap = new HashMap<String, Object>();
        // #1
        hashMap.put("str", "komamitsu");
        // #2
        hashMap.put("boolean", true);
        // #3
        hashMap.put("int", Integer.MAX_VALUE);
        // #4
        hashMap.put("long", Long.MIN_VALUE);
        // #5
        hashMap.put("float", 3.14159f);
        // #6
        hashMap.put("double", 3.14159d);
        // #7
        hashMap.put("bin", new byte[] {0x00, 0x01, (byte) 0xFE, (byte) 0xFF});
        // #8
        Map<String, Object> childObj = new HashMap<String, Object>();
        childObj.put("co_str", "child#0");
        childObj.put("co_int", 12345);
        hashMap.put("childObj", childObj);
        // #9
        List<Object> childArray = new ArrayList<Object>();
        childArray.add("child#1");
        childArray.add(1.23f);
        hashMap.put("childArray", childArray);
        // #10
        byte[] hello = "hello".getBytes("UTF-8");
        hashMap.put("ext", new MessagePackExtensionType((byte) 17, hello));

        long bitmap = 0;
        byte[] bytes = objectMapper.writeValueAsBytes(hashMap);
        MessageUnpacker messageUnpacker = MessagePack.newDefaultUnpacker(new ArrayBufferInput(bytes));
        assertEquals(hashMap.size(), messageUnpacker.unpackMapHeader());
        for (int i = 0; i < hashMap.size(); i++) {
            String key = messageUnpacker.unpackString();
            if (key.equals("str")) {
                // #1
                assertEquals("komamitsu", messageUnpacker.unpackString());
                bitmap |= 0x1 << 0;
            }
            else if (key.equals("boolean")) {
                // #2
                assertTrue(messageUnpacker.unpackBoolean());
                bitmap |= 0x1 << 1;
            }
            else if (key.equals("int")) {
                // #3
                assertEquals(Integer.MAX_VALUE, messageUnpacker.unpackInt());
                bitmap |= 0x1 << 2;
            }
            else if (key.equals("long")) {
                // #4
                assertEquals(Long.MIN_VALUE, messageUnpacker.unpackLong());
                bitmap |= 0x1 << 3;
            }
            else if (key.equals("float")) {
                // #5
                assertEquals(3.14159f, messageUnpacker.unpackFloat(), 0.01f);
                bitmap |= 0x1 << 4;
            }
            else if (key.equals("double")) {
                // #6
                assertEquals(3.14159d, messageUnpacker.unpackDouble(), 0.01f);
                bitmap |= 0x1 << 5;
            }
            else if (key.equals("bin")) {
                // #7
                assertEquals(4, messageUnpacker.unpackBinaryHeader());
                assertEquals((byte) 0x00, messageUnpacker.unpackByte());
                assertEquals((byte) 0x01, messageUnpacker.unpackByte());
                assertEquals((byte) 0xFE, messageUnpacker.unpackByte());
                assertEquals((byte) 0xFF, messageUnpacker.unpackByte());
                bitmap |= 0x1 << 6;
            }
            else if (key.equals("childObj")) {
                // #8
                assertEquals(2, messageUnpacker.unpackMapHeader());
                for (int j = 0; j < 2; j++) {
                    String childKey = messageUnpacker.unpackString();
                    if (childKey.equals("co_str")) {
                        assertEquals("child#0", messageUnpacker.unpackString());
                        bitmap |= 0x1 << 7;
                    }
                    else if (childKey.equals("co_int")) {
                        assertEquals(12345, messageUnpacker.unpackInt());
                        bitmap |= 0x1 << 8;
                    }
                    else {
                        assertTrue(false);
                    }
                }
            }
            else if (key.equals("childArray")) {
                // #9
                assertEquals(2, messageUnpacker.unpackArrayHeader());
                assertEquals("child#1", messageUnpacker.unpackString());
                assertEquals(1.23f, messageUnpacker.unpackFloat(), 0.01f);
                bitmap |= 0x1 << 9;
            }
            else if (key.equals("ext")) {
                // #10
                ExtensionTypeHeader header = messageUnpacker.unpackExtensionTypeHeader();
                assertEquals(17, header.getType());
                assertEquals(5, header.getLength());
                ByteBuffer payload = ByteBuffer.allocate(header.getLength());
                payload.flip();
                payload.limit(payload.capacity());
                messageUnpacker.readPayload(payload);
                payload.flip();
                assertArrayEquals("hello".getBytes(), payload.array());
                bitmap |= 0x1 << 10;
            }
            else {
                assertTrue(false);
            }
        }
        assertEquals(0x07FF, bitmap);
    }

    @Test
    public void testGeneratorShouldWriteArray()
            throws IOException
    {
        List<Object> array = new ArrayList<Object>();
        // #1
        array.add("komamitsu");
        // #2
        array.add(Integer.MAX_VALUE);
        // #3
        array.add(Long.MIN_VALUE);
        // #4
        array.add(3.14159f);
        // #5
        array.add(3.14159d);
        // #6
        Map<String, Object> childObject = new HashMap<String, Object>();
        childObject.put("str", "foobar");
        childObject.put("num", 123456);
        array.add(childObject);
        // #7
        array.add(false);

        long bitmap = 0;
        byte[] bytes = objectMapper.writeValueAsBytes(array);
        MessageUnpacker messageUnpacker = MessagePack.newDefaultUnpacker(new ArrayBufferInput(bytes));
        assertEquals(array.size(), messageUnpacker.unpackArrayHeader());
        // #1
        assertEquals("komamitsu", messageUnpacker.unpackString());
        // #2
        assertEquals(Integer.MAX_VALUE, messageUnpacker.unpackInt());
        // #3
        assertEquals(Long.MIN_VALUE, messageUnpacker.unpackLong());
        // #4
        assertEquals(3.14159f, messageUnpacker.unpackFloat(), 0.01f);
        // #5
        assertEquals(3.14159d, messageUnpacker.unpackDouble(), 0.01f);
        // #6
        assertEquals(2, messageUnpacker.unpackMapHeader());
        for (int i = 0; i < childObject.size(); i++) {
            String key = messageUnpacker.unpackString();
            if (key.equals("str")) {
                assertEquals("foobar", messageUnpacker.unpackString());
                bitmap |= 0x1 << 0;
            }
            else if (key.equals("num")) {
                assertEquals(123456, messageUnpacker.unpackInt());
                bitmap |= 0x1 << 1;
            }
            else {
                assertTrue(false);
            }
        }
        assertEquals(0x3, bitmap);
        // #7
        assertEquals(false, messageUnpacker.unpackBoolean());
    }

    @Test
    public void testMessagePackGeneratorDirectly()
            throws Exception
    {
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        File tempFile = createTempFile();

        JsonGenerator generator = messagePackFactory.createGenerator(tempFile, JsonEncoding.UTF8);
        assertTrue(generator instanceof MessagePackGenerator);
        generator.writeStartArray();
        generator.writeNumber(0);
        generator.writeString("one");
        generator.writeNumber(2.0f);
        generator.writeEndArray();
        generator.flush();
        generator.flush();      // intentional
        generator.close();

        FileInputStream fileInputStream = new FileInputStream(tempFile);
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(fileInputStream);
        assertEquals(3, unpacker.unpackArrayHeader());
        assertEquals(0, unpacker.unpackInt());
        assertEquals("one", unpacker.unpackString());
        assertEquals(2.0f, unpacker.unpackFloat(), 0.001f);
        assertFalse(unpacker.hasNext());
    }

    @Test
    public void testWritePrimitives()
            throws Exception
    {
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        File tempFile = createTempFile();

        JsonGenerator generator = messagePackFactory.createGenerator(tempFile, JsonEncoding.UTF8);
        assertTrue(generator instanceof MessagePackGenerator);
        generator.writeNumber(0);
        generator.writeString("one");
        generator.writeNumber(2.0f);
        generator.flush();
        generator.close();

        FileInputStream fileInputStream = new FileInputStream(tempFile);
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(fileInputStream);
        assertEquals(0, unpacker.unpackInt());
        assertEquals("one", unpacker.unpackString());
        assertEquals(2.0f, unpacker.unpackFloat(), 0.001f);
        assertFalse(unpacker.hasNext());
    }

    @Test
    public void testBigDecimal()
            throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

        {
            double d0 = 1.23456789;
            double d1 = 1.23450000000000000000006789;
            String d2 = "12.30";
            String d3 = "0.00001";
            List<BigDecimal> bigDecimals = Arrays.asList(
                    BigDecimal.valueOf(d0),
                    BigDecimal.valueOf(d1),
                    new BigDecimal(d2),
                    new BigDecimal(d3),
                    BigDecimal.valueOf(Double.MIN_VALUE),
                    BigDecimal.valueOf(Double.MAX_VALUE),
                    BigDecimal.valueOf(Double.MIN_NORMAL)
            );

            byte[] bytes = mapper.writeValueAsBytes(bigDecimals);
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);

            assertEquals(bigDecimals.size(), unpacker.unpackArrayHeader());
            assertEquals(d0, unpacker.unpackDouble(), 0.000000000000001);
            assertEquals(d1, unpacker.unpackDouble(), 0.000000000000001);
            assertEquals(Double.valueOf(d2), unpacker.unpackDouble(), 0.000000000000001);
            assertEquals(Double.valueOf(d3), unpacker.unpackDouble(), 0.000000000000001);
            assertEquals(Double.MIN_VALUE, unpacker.unpackDouble(), 0.000000000000001);
            assertEquals(Double.MAX_VALUE, unpacker.unpackDouble(), 0.000000000000001);
            assertEquals(Double.MIN_NORMAL, unpacker.unpackDouble(), 0.000000000000001);
        }

        {
            BigDecimal decimal = new BigDecimal("1234.567890123456789012345678901234567890");
            List<BigDecimal> bigDecimals = Arrays.asList(
                    decimal
            );

            try {
                mapper.writeValueAsBytes(bigDecimals);
                assertTrue(false);
            }
            catch (IllegalArgumentException e) {
                assertTrue(true);
            }
        }
    }

    @Test(expected = IOException.class)
    public void testEnableFeatureAutoCloseTarget()
            throws IOException
    {
        OutputStream out = createTempFileOutputStream();
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        ObjectMapper objectMapper = new ObjectMapper(messagePackFactory);
        List<Integer> integers = Arrays.asList(1);
        objectMapper.writeValue(out, integers);
        objectMapper.writeValue(out, integers);
    }

    @Test
    public void testDisableFeatureAutoCloseTarget()
            throws Exception
    {
        File tempFile = createTempFile();
        OutputStream out = new FileOutputStream(tempFile);
        MessagePackFactory messagePackFactory = new MessagePackFactory();
        ObjectMapper objectMapper = new ObjectMapper(messagePackFactory);
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        List<Integer> integers = Arrays.asList(1);
        objectMapper.writeValue(out, integers);
        objectMapper.writeValue(out, integers);
        out.close();

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(tempFile));
        assertEquals(1, unpacker.unpackArrayHeader());
        assertEquals(1, unpacker.unpackInt());
        assertEquals(1, unpacker.unpackArrayHeader());
        assertEquals(1, unpacker.unpackInt());
    }

    @Test
    public void testWritePrimitiveObjectViaObjectMapper()
            throws Exception
    {
        File tempFile = createTempFile();
        OutputStream out = new FileOutputStream(tempFile);

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        objectMapper.writeValue(out, 1);
        objectMapper.writeValue(out, "two");
        objectMapper.writeValue(out, 3.14);
        objectMapper.writeValue(out, Arrays.asList(4));
        objectMapper.writeValue(out, 5L);

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(new FileInputStream(tempFile));
        assertEquals(1, unpacker.unpackInt());
        assertEquals("two", unpacker.unpackString());
        assertEquals(3.14, unpacker.unpackFloat(), 0.0001);
        assertEquals(1, unpacker.unpackArrayHeader());
        assertEquals(4, unpacker.unpackInt());
        assertEquals(5, unpacker.unpackLong());
    }

    @Test
    public void testInMultiThreads()
            throws Exception
    {
        int threadCount = 8;
        final int loopCount = 4000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        final List<ByteArrayOutputStream> buffers = new ArrayList<ByteArrayOutputStream>(threadCount);
        List<Future<Exception>> results = new ArrayList<Future<Exception>>();

        for (int ti = 0; ti < threadCount; ti++) {
            buffers.add(new ByteArrayOutputStream());
            final int threadIndex = ti;
            results.add(executorService.submit(new Callable<Exception>()
            {
                @Override
                public Exception call()
                        throws Exception
                {
                    try {
                        for (int i = 0; i < loopCount; i++) {
                            objectMapper.writeValue(buffers.get(threadIndex), threadIndex);
                        }
                        return null;
                    }
                    catch (IOException e) {
                        return e;
                    }
                }
            }));
        }

        for (int ti = 0; ti < threadCount; ti++) {
            Future<Exception> exceptionFuture = results.get(ti);
            Exception exception = exceptionFuture.get(20, TimeUnit.SECONDS);
            if (exception != null) {
                throw exception;
            }
            else {
                ByteArrayOutputStream outputStream = buffers.get(ti);
                MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(outputStream.toByteArray());
                for (int i = 0; i < loopCount; i++) {
                    assertEquals(ti, unpacker.unpackInt());
                }
            }
        }
    }

    @Test
    public void testDisableStr8Support()
      throws Exception
    {
        String str8LengthString = new String(new char[32]).replace("\0", "a");

        // Test that produced value having str8 format
        ObjectMapper defaultMapper = new ObjectMapper(new MessagePackFactory());
        byte[] resultWithStr8Format = defaultMapper.writeValueAsBytes(str8LengthString);
        assertEquals(resultWithStr8Format[0], MessagePack.Code.STR8);

        // Test that produced value does not having str8 format
        MessagePack.PackerConfig config = new MessagePack.PackerConfig().withStr8FormatSupport(false);
        ObjectMapper mapperWithConfig = new ObjectMapper(new MessagePackFactory(config));
        byte[] resultWithoutStr8Format = mapperWithConfig.writeValueAsBytes(str8LengthString);
        assertNotEquals(resultWithoutStr8Format[0], MessagePack.Code.STR8);
    }

    interface NonStringKeyMapHolder
    {
        Map<Integer, String> getIntMap();

        void setIntMap(Map<Integer, String> intMap);

        Map<Long, String> getLongMap();

        void setLongMap(Map<Long, String> longMap);

        Map<Float, String> getFloatMap();

        void setFloatMap(Map<Float, String> floatMap);

        Map<Double, String> getDoubleMap();

        void setDoubleMap(Map<Double, String> doubleMap);

        Map<BigInteger, String> getBigIntMap();

        void setBigIntMap(Map<BigInteger, String> doubleMap);
    }

    public static class NonStringKeyMapHolderWithAnnotation
            implements NonStringKeyMapHolder
    {
        @JsonSerialize(keyUsing = MessagePackKeySerializer.class)
        private Map<Integer, String> intMap = new HashMap<Integer, String>();

        @JsonSerialize(keyUsing = MessagePackKeySerializer.class)
        private Map<Long, String> longMap = new HashMap<Long, String>();

        @JsonSerialize(keyUsing = MessagePackKeySerializer.class)
        private Map<Float, String> floatMap = new HashMap<Float, String>();

        @JsonSerialize(keyUsing = MessagePackKeySerializer.class)
        private Map<Double, String> doubleMap = new HashMap<Double, String>();

        @JsonSerialize(keyUsing = MessagePackKeySerializer.class)
        private Map<BigInteger, String> bigIntMap = new HashMap<BigInteger, String>();

        @Override
        public Map<Integer, String> getIntMap()
        {
            return intMap;
        }

        @Override
        public void setIntMap(Map<Integer, String> intMap)
        {
            this.intMap = intMap;
        }

        @Override
        public Map<Long, String> getLongMap()
        {
            return longMap;
        }

        @Override
        public void setLongMap(Map<Long, String> longMap)
        {
            this.longMap = longMap;
        }

        @Override
        public Map<Float, String> getFloatMap()
        {
            return floatMap;
        }

        @Override
        public void setFloatMap(Map<Float, String> floatMap)
        {
            this.floatMap = floatMap;
        }

        @Override
        public Map<Double, String> getDoubleMap()
        {
            return doubleMap;
        }

        @Override
        public void setDoubleMap(Map<Double, String> doubleMap)
        {
            this.doubleMap = doubleMap;
        }

        @Override
        public Map<BigInteger, String> getBigIntMap()
        {
            return bigIntMap;
        }

        @Override
        public void setBigIntMap(Map<BigInteger, String> bigIntMap)
        {
            this.bigIntMap = bigIntMap;
        }
    }

    public static class NonStringKeyMapHolderWithoutAnnotation
            implements NonStringKeyMapHolder
    {
        private Map<Integer, String> intMap = new HashMap<Integer, String>();

        private Map<Long, String> longMap = new HashMap<Long, String>();

        private Map<Float, String> floatMap = new HashMap<Float, String>();

        private Map<Double, String> doubleMap = new HashMap<Double, String>();

        private Map<BigInteger, String> bigIntMap = new HashMap<BigInteger, String>();

        @Override
        public Map<Integer, String> getIntMap()
        {
            return intMap;
        }

        @Override
        public void setIntMap(Map<Integer, String> intMap)
        {
            this.intMap = intMap;
        }

        @Override
        public Map<Long, String> getLongMap()
        {
            return longMap;
        }

        @Override
        public void setLongMap(Map<Long, String> longMap)
        {
            this.longMap = longMap;
        }

        @Override
        public Map<Float, String> getFloatMap()
        {
            return floatMap;
        }

        @Override
        public void setFloatMap(Map<Float, String> floatMap)
        {
            this.floatMap = floatMap;
        }

        @Override
        public Map<Double, String> getDoubleMap()
        {
            return doubleMap;
        }

        @Override
        public void setDoubleMap(Map<Double, String> doubleMap)
        {
            this.doubleMap = doubleMap;
        }

        @Override
        public Map<BigInteger, String> getBigIntMap()
        {
            return bigIntMap;
        }

        @Override
        public void setBigIntMap(Map<BigInteger, String> bigIntMap)
        {
            this.bigIntMap = bigIntMap;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNonStringKey()
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        for (Class<? extends NonStringKeyMapHolder> clazz :
                Arrays.asList(
                        NonStringKeyMapHolderWithAnnotation.class,
                        NonStringKeyMapHolderWithoutAnnotation.class)) {
            NonStringKeyMapHolder mapHolder = clazz.getConstructor().newInstance();
            mapHolder.getIntMap().put(Integer.MAX_VALUE, "i");
            mapHolder.getLongMap().put(Long.MIN_VALUE, "l");
            mapHolder.getFloatMap().put(Float.MAX_VALUE, "f");
            mapHolder.getDoubleMap().put(Double.MIN_VALUE, "d");
            mapHolder.getBigIntMap().put(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE), "bi");

            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            if (mapHolder instanceof NonStringKeyMapHolderWithoutAnnotation) {
                SimpleModule mod = new SimpleModule("test");
                mod.addKeySerializer(Object.class, new MessagePackKeySerializer());
                objectMapper.registerModule(mod);
            }

            byte[] bytes = objectMapper.writeValueAsBytes(mapHolder);
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
            assertEquals(5, unpacker.unpackMapHeader());
            for (int i = 0; i < 5; i++) {
                String keyName = unpacker.unpackString();
                assertThat(unpacker.unpackMapHeader(), is(1));
                if (keyName.equals("intMap")) {
                    assertThat(unpacker.unpackInt(), is(Integer.MAX_VALUE));
                    assertThat(unpacker.unpackString(), is("i"));
                }
                else if (keyName.equals("longMap")) {
                    assertThat(unpacker.unpackLong(), is(Long.MIN_VALUE));
                    assertThat(unpacker.unpackString(), is("l"));
                }
                else if (keyName.equals("floatMap")) {
                    assertThat(unpacker.unpackFloat(), is(Float.MAX_VALUE));
                    assertThat(unpacker.unpackString(), is("f"));
                }
                else if (keyName.equals("doubleMap")) {
                    assertThat(unpacker.unpackDouble(), is(Double.MIN_VALUE));
                    assertThat(unpacker.unpackString(), is("d"));
                }
                else if (keyName.equals("bigIntMap")) {
                    assertThat(unpacker.unpackBigInteger(), is(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)));
                    assertThat(unpacker.unpackString(), is("bi"));
                }
                else {
                    fail("Unexpected key name: " + keyName);
                }
            }
        }
    }

    @Test
    public void testComplexTypeKey()
            throws IOException
    {
        HashMap<TinyPojo, Integer> map = new HashMap<TinyPojo, Integer>();
        TinyPojo pojo = new TinyPojo();
        pojo.t = "foo";
        map.put(pojo, 42);

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        SimpleModule mod = new SimpleModule("test");
        mod.addKeySerializer(TinyPojo.class, new MessagePackKeySerializer());
        objectMapper.registerModule(mod);
        byte[] bytes = objectMapper.writeValueAsBytes(map);

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
        assertThat(unpacker.unpackMapHeader(), is(1));
        assertThat(unpacker.unpackMapHeader(), is(1));
        assertThat(unpacker.unpackString(), is("t"));
        assertThat(unpacker.unpackString(), is("foo"));
        assertThat(unpacker.unpackInt(), is(42));
    }

    @Test
    public void testComplexTypeKeyWithV06Format()
            throws IOException
    {
        HashMap<TinyPojo, Integer> map = new HashMap<TinyPojo, Integer>();
        TinyPojo pojo = new TinyPojo();
        pojo.t = "foo";
        map.put(pojo, 42);

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.setAnnotationIntrospector(new JsonArrayFormat());
        SimpleModule mod = new SimpleModule("test");
        mod.addKeySerializer(TinyPojo.class, new MessagePackKeySerializer());
        objectMapper.registerModule(mod);
        byte[] bytes = objectMapper.writeValueAsBytes(map);

        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(bytes);
        assertThat(unpacker.unpackMapHeader(), is(1));
        assertThat(unpacker.unpackArrayHeader(), is(1));
        assertThat(unpacker.unpackString(), is("foo"));
        assertThat(unpacker.unpackInt(), is(42));
    }

    // Test serializers that store a string as a number

    public static class IntegerSerializerStoringAsString
            extends JsonSerializer<Integer>
    {
        @Override
        public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException
        {
            gen.writeNumber(String.valueOf(value));
        }
    }

    @Test
    public void serializeStringAsInteger()
            throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.registerModule(
                new SimpleModule().addSerializer(Integer.class, new IntegerSerializerStoringAsString()));

        assertThat(
            MessagePack.newDefaultUnpacker(objectMapper.writeValueAsBytes(Integer.MAX_VALUE)).unpackInt(),
                is(Integer.MAX_VALUE));
    }

    public static class LongSerializerStoringAsString
            extends JsonSerializer<Long>
    {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException
        {
            gen.writeNumber(String.valueOf(value));
        }
    }

    @Test
    public void serializeStringAsLong()
            throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.registerModule(
                new SimpleModule().addSerializer(Long.class, new LongSerializerStoringAsString()));

        assertThat(
            MessagePack.newDefaultUnpacker(objectMapper.writeValueAsBytes(Long.MIN_VALUE)).unpackLong(),
                is(Long.MIN_VALUE));
    }

    public static class FloatSerializerStoringAsString
            extends JsonSerializer<Float>
    {
        @Override
        public void serialize(Float value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException
        {
            gen.writeNumber(String.valueOf(value));
        }
    }

    @Test
    public void serializeStringAsFloat()
            throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.registerModule(
                new SimpleModule().addSerializer(Float.class, new FloatSerializerStoringAsString()));

        assertThat(
            MessagePack.newDefaultUnpacker(objectMapper.writeValueAsBytes(Float.MAX_VALUE)).unpackFloat(),
                is(Float.MAX_VALUE));
    }

    public static class DoubleSerializerStoringAsString
            extends JsonSerializer<Double>
    {
        @Override
        public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException
        {
            gen.writeNumber(String.valueOf(value));
        }
    }

    @Test
    public void serializeStringAsDouble()
            throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.registerModule(
                new SimpleModule().addSerializer(Double.class, new DoubleSerializerStoringAsString()));

        assertThat(
            MessagePack.newDefaultUnpacker(objectMapper.writeValueAsBytes(Double.MIN_VALUE)).unpackDouble(),
                is(Double.MIN_VALUE));
    }

   public static class BigDecimalSerializerStoringAsString
            extends JsonSerializer<BigDecimal>
    {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException
        {
            gen.writeNumber(String.valueOf(value));
        }
    }

    @Test
    public void serializeStringAsBigDecimal()
            throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.registerModule(
                new SimpleModule().addSerializer(BigDecimal.class, new BigDecimalSerializerStoringAsString()));

        BigDecimal bd = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE);
        assertThat(
            MessagePack.newDefaultUnpacker(objectMapper.writeValueAsBytes(bd)).unpackDouble(),
                is(bd.doubleValue()));
    }

    public static class BigIntegerSerializerStoringAsString
            extends JsonSerializer<BigInteger>
    {
        @Override
        public void serialize(BigInteger value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException
        {
            gen.writeNumber(String.valueOf(value));
        }
    }

    @Test
    public void serializeStringAsBigInteger()
            throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        objectMapper.registerModule(
                new SimpleModule().addSerializer(BigInteger.class, new BigIntegerSerializerStoringAsString()));

        BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        assertThat(
            MessagePack.newDefaultUnpacker(objectMapper.writeValueAsBytes(bi)).unpackDouble(),
                is(bi.doubleValue()));
    }

    @Test
    public void testNestedSerialization() throws Exception
    {
        // The purpose of this test is to confirm if MessagePackFactory.setReuseResourceInGenerator(false)
        // works as a workaround for https://github.com/msgpack/msgpack-java/issues/508
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory().setReuseResourceInGenerator(false));
        OuterClass outerClass = objectMapper.readValue(
                objectMapper.writeValueAsBytes(new OuterClass("Foo")),
                OuterClass.class);
        assertEquals("Foo", outerClass.getName());
    }

    static class OuterClass
    {
        private final String name;

        public OuterClass(@JsonProperty("name") String name)
        {
            this.name = name;
        }

        public String getName()
                throws IOException
        {
            // Serialize nested class object
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            InnerClass innerClass = objectMapper.readValue(
                    objectMapper.writeValueAsBytes(new InnerClass("Bar")),
                    InnerClass.class);
            assertEquals("Bar", innerClass.getName());

            return name;
        }
    }

    static class InnerClass
    {
        private final String name;

        public InnerClass(@JsonProperty("name") String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }
}
