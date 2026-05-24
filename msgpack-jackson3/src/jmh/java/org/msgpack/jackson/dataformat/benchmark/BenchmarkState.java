package org.msgpack.jackson.dataformat.benchmark;

import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.msgpack.jackson.dataformat.MessagePackMapper;
import org.msgpack.jackson.dataformat.benchmark.model.MediaItem;
import org.msgpack.jackson.dataformat.benchmark.model.MediaItems;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@State(Scope.Thread)
public class BenchmarkState
{
    public final ObjectMapper msgpackMapper = MessagePackMapper.builder(new MessagePackFactory()).build();
    public final ObjectMapper jsonMapper = JsonMapper.builder().build();

    public final byte[] msgpackBytes;
    public final byte[] jsonBytes;

    public BenchmarkState()
    {
        try {
            MediaItem item = MediaItems.stdMediaItem();
            msgpackBytes = msgpackMapper.writeValueAsBytes(item);
            jsonBytes = jsonMapper.writeValueAsBytes(item);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
