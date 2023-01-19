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

import org.msgpack.core.buffer.MessageBufferInput;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessageBufferInputRegistry implements MessageBufferInputLocator
{
    private final Map<Class, MessageBufferInput> messageBufferInputMap = new HashMap<>(1);

    @Override
    public MessageBufferInput get(Class clazz)
    {
        return messageBufferInputMap.get(clazz);
    }

    public boolean register(Class clazz, MessageBufferInputProvider provider)
    {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(provider, "provider");

        if (messageBufferInputMap.containsKey(clazz)) {
            return false;
        }

        messageBufferInputMap.put(clazz, provider.provide());
        return true;
    }
}
