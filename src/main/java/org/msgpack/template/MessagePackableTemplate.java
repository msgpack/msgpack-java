//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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
package org.msgpack.template;

import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessagePackable;
import org.msgpack.MessageTypeException;

public class MessagePackableTemplate extends AbstractTemplate<MessagePackable> {
    private Class<?> targetClass;

    MessagePackableTemplate(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public void write(Packer pk, MessagePackable target, boolean required)
            throws IOException {
        if (target == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            pk.writeNil();
            return;
        }
        target.writeTo(pk);
    }

    public MessagePackable read(Unpacker u, MessagePackable to, boolean required)
            throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        if (to == null) {
            try {
                to = (MessagePackable) targetClass.newInstance();
            } catch (InstantiationException e) {
                throw new MessageTypeException(e);
            } catch (IllegalAccessException e) {
                throw new MessageTypeException(e);
            }
        }
        to.readFrom(u);
        return to;
    }
}
