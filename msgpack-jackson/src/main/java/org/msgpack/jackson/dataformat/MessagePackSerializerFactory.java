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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;

public class MessagePackSerializerFactory
        extends BeanSerializerFactory
{
    /**
     * Constructor for creating instances without configuration.
     */
    public MessagePackSerializerFactory()
    {
        super(null);
    }

    /**
     * Constructor for creating instances with specified configuration.
     *
     * @param config
     */
    public MessagePackSerializerFactory(SerializerFactoryConfig config)
    {
        super(config);
    }

    @Override
    public JsonSerializer<Object> createKeySerializer(SerializationConfig config, JavaType keyType, JsonSerializer<Object> defaultImpl)
    {
        return new MessagePackKeySerializer();
    }
}
