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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionTypeCustomDeserializers
{
    private Map<Byte, Deser> deserTable = new ConcurrentHashMap<>();

    public ExtensionTypeCustomDeserializers()
    {
    }

    public ExtensionTypeCustomDeserializers(ExtensionTypeCustomDeserializers src)
    {
        this();
        this.deserTable.putAll(src.deserTable);
    }

    public void addCustomDeser(byte type, final Deser deser)
    {
        deserTable.put(type, new Deser()
        {
            @Override
            public Object deserialize(byte[] data)
                    throws IOException
            {
                return deser.deserialize(data);
            }
        });
    }

    public Deser getDeser(byte type)
    {
        return deserTable.get(type);
    }

    public void clearEntries()
    {
        deserTable.clear();
    }

    public interface Deser
    {
        Object deserialize(byte[] data)
                throws IOException;
    }
}
