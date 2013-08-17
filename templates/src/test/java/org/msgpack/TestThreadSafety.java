package org.msgpack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.unpacker.BufferUnpacker;

public class TestThreadSafety {
    private List<String> list = createList(1000);
    private Map<String, String> map = createMap(1000);
    private static final String EXAMPLE_STRING;
    static {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            buf.append("0000000000111111111122222222223333333333444444444455555555556666666666777777777788888888889999999999");
        }
        EXAMPLE_STRING = buf.toString();
    }
    
    private List<String> createList(int n) {
        List<String> src = new ArrayList<String>();
        for (int i = 0; i < n; i++) {
            src.add(EXAMPLE_STRING);
        }
        return src;
    }

    private Map<String, String> createMap(int n) {
        Map<String, String> src = new HashMap<String, String>();
        for (int i = 0; i < n; i++) {
            src.put(String.valueOf(i), EXAMPLE_STRING);
        }
        return src;
    }

    private void testMsgpackDynamicString(int n) throws IOException {
        MessagePack msgpack = new MessagePack();
        BufferPacker packer = msgpack.createBufferPacker();
        for (int i = 0; i < n; i++) {
            packer.write(EXAMPLE_STRING);
        }

        byte[] raw = packer.toByteArray();
        BufferUnpacker unpacker = msgpack.createBufferUnpacker(raw);

        for (int i = 0; i < n; i++) {
            String dst = unpacker.read(String.class);
            if (!dst.equals(EXAMPLE_STRING)) {
                throw new AssertionError();
            }
        }
    }

    private void testMsgpackDynamicArray() throws IOException {
        List<String> src = list;

        MessagePack msgpack = new MessagePack();
        byte[] raw;
        raw = msgpack.write(src);

        List<String> dst = new LinkedList<String>();
        ArrayValue arrayValue = msgpack.read(raw).asArrayValue();
        for (Value v : arrayValue) {
            dst.add(v.asRawValue().getString());

            if (!v.asRawValue().getString().equals(EXAMPLE_STRING)) {
                throw new AssertionError();
            }
        }
    }

    private void testMsgpackDynamicMap() throws IOException {
        Map<String, String> src = map;

        MessagePack msgpack = new MessagePack();
        byte[] raw;
        raw = msgpack.write(src);

        MapValue mv = msgpack.read(raw).asMapValue();
        for (Entry<Value, Value> kv : mv.entrySet()) {
            if (!kv.getValue().asRawValue().getString().equals(EXAMPLE_STRING)) {
                throw new AssertionError();
            }
        }
    }

    static class TestRunner implements Callable<Void> {
        private final TestThreadSafety main;

        public TestRunner(TestThreadSafety main) {
            this.main = main;
        }

        @Override
        public Void call() throws Exception {
            try {
                main.testMsgpackDynamicString(1000);
                main.testMsgpackDynamicArray();
                main.testMsgpackDynamicMap();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    @Test
    public void testWithBulkData() throws InterruptedException, ExecutionException, TimeoutException {
        final TestThreadSafety main = new TestThreadSafety();
        List<Future<Void>> futures = new LinkedList<Future<Void>>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            futures.add(executorService.submit(new TestRunner(main)));
        }
        
        for (Future<Void> future : futures) {
            future.get(30, TimeUnit.SECONDS);
        }
    }
}
