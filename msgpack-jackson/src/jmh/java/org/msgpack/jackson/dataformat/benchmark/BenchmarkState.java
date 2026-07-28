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
