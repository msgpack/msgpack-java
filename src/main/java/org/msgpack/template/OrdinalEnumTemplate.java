//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
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

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;


public class OrdinalEnumTemplate<T> extends AbstractTemplate<T> {
    private T[] entries;

    public OrdinalEnumTemplate(Class<T> targetClass) {
	entries = targetClass.getEnumConstants();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void write(Packer pk, T target, boolean required) throws IOException {
	pk.writeInt(((Enum) target).ordinal());
    }

    @Override
    public T read(Unpacker pac, T to, boolean required) throws IOException, MessageTypeException {
	int ordinal = pac.readInt();
	if (ordinal >= entries.length) {
	    throw new MessageTypeException("illegal ordinal");
	}
	return entries[ordinal];
    }
}
