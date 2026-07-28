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

import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.SerializerFactoryConfig;
import tools.jackson.databind.ser.BeanSerializerFactory;

public class MessagePackSerializerFactory
        extends BeanSerializerFactory
{
    public MessagePackSerializerFactory()
    {
        super(null);
    }

    public MessagePackSerializerFactory(SerializerFactoryConfig config)
    {
        super(config);
    }

    private static final MessagePackKeySerializer KEY_SERIALIZER = new MessagePackKeySerializer();

    @Override
    public ValueSerializer<Object> createKeySerializer(SerializationContext ctxt, JavaType keyType)
    {
        return KEY_SERIALIZER;
    }
}
