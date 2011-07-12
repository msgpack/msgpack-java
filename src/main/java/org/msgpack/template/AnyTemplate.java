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
import org.msgpack.*;
import org.msgpack.packer.Packer;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;


public class AnyTemplate<T> implements Template<T> {
    private static AnyTemplate INSTANCE = null;

    public static AnyTemplate getInstance(TemplateRegistry registry) {
	if (INSTANCE == null) {
	    INSTANCE = new AnyTemplate(registry);
	}
	return INSTANCE;
    }

    private TemplateRegistry registry;

    private AnyTemplate(TemplateRegistry registry) {
	
    }

    public void write(Packer pk, T target) throws IOException {
	if(target instanceof Value) {
	    pk.write((Value) target);
	} else if(target == null) {
	    pk.writeNil();
	} else {
	    registry.lookup(target.getClass()).write(pk, target);
	}
    }

    public T read(Unpacker u, T to) throws IOException, MessageTypeException {
	return u.read(to);
    }
}
