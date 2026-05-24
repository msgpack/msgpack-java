package org.msgpack.jackson.dataformat.benchmark;

import org.msgpack.jackson.dataformat.benchmark.model.MediaItem;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class MsgpackReadBenchmark
{
    private final BenchmarkState state = new BenchmarkState();

    @Benchmark
    public Object readPojoMsgpack() throws Exception
    {
        return state.msgpackMapper.readValue(state.msgpackBytes, MediaItem.class);
    }

    @Benchmark
    public Object readPojoJson() throws Exception
    {
        return state.jsonMapper.readValue(state.jsonBytes, MediaItem.class);
    }
}
