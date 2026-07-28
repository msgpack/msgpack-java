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

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.Version;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.cfg.MapperBuilderState;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MessagePackMapper extends ObjectMapper
{
    private static final long serialVersionUID = 3L;

    public static class Builder extends MapperBuilder<MessagePackMapper, Builder>
    {
        public Builder(MessagePackFactory f)
        {
            super(f);
        }

        protected Builder(StateImpl state)
        {
            super(state);
        }

        @Override
        public MessagePackMapper build()
        {
            return new MessagePackMapper(this);
        }

        public Builder handleBigIntegerAsString()
        {
            return withConfigOverride(BigInteger.class,
                    o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING)));
        }

        public Builder handleBigDecimalAsString()
        {
            return withConfigOverride(BigDecimal.class,
                    o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING)));
        }

        public Builder handleBigIntegerAndBigDecimalAsString()
        {
            return handleBigIntegerAsString().handleBigDecimalAsString();
        }

        @Override
        protected MapperBuilderState _saveState()
        {
            return new StateImpl(this);
        }

        protected static class StateImpl extends MapperBuilderState
        {
            private static final long serialVersionUID = 3L;

            public StateImpl(Builder src)
            {
                super(src);
            }

            @Override
            protected Object readResolve()
            {
                return new Builder(this).build();
            }
        }
    }

    public MessagePackMapper()
    {
        this(new Builder(new MessagePackFactory()));
    }

    public MessagePackMapper(MessagePackFactory f)
    {
        this(new Builder(f));
    }

    protected MessagePackMapper(Builder builder)
    {
        super(builder);
    }

    public static Builder builder()
    {
        return new Builder(new MessagePackFactory());
    }

    public static Builder builder(MessagePackFactory f)
    {
        return new Builder(f);
    }

    @Override
    public Version version()
    {
        return PackageVersion.VERSION;
    }
}
