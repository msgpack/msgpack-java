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
import java.math.BigDecimal;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.msgpack.MessageTypeException;

public class BigDecimalTemplate extends AbstractTemplate<BigDecimal> {
    private BigDecimalTemplate() {
    }

    public void write(Packer pk, BigDecimal target, boolean required)
            throws IOException {
        if (target == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            pk.writeNil();
            return;
        }
        pk.write(target.toString());
    }

    public BigDecimal read(Unpacker u, BigDecimal to, boolean required)
            throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        String temp = u.readString();
        return new BigDecimal(temp);
    }

    static public BigDecimalTemplate getInstance() {
        return instance;
    }

    static final BigDecimalTemplate instance = new BigDecimalTemplate();
}
