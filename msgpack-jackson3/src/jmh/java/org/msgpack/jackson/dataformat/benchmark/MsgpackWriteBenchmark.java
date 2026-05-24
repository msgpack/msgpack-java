package org.msgpack.jackson.dataformat.benchmark;

import org.msgpack.jackson.dataformat.benchmark.model.MediaItem;
import org.msgpack.jackson.dataformat.benchmark.model.MediaItems;
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
public class MsgpackWriteBenchmark
{
    private final BenchmarkState state = new BenchmarkState();
    private final MediaItem item = MediaItems.stdMediaItem();

    @Benchmark
    public int writePojoMsgpack() throws Exception
    {
        NopOutputStream out = new NopOutputStream();
        state.msgpackMapper.writeValue(out, item);
        return out.size();
    }

    @Benchmark
    public int writePojoJson() throws Exception
    {
        NopOutputStream out = new NopOutputStream();
        state.jsonMapper.writeValue(out, item);
        return out.size();
    }
}
