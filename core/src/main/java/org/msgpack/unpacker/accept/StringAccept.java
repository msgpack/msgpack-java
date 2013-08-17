//
// MessagePack for Java
//
// Copyright (C) 2009-2013 FURUHASHI Sadayuki
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
package org.msgpack.unpacker.accept;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import org.msgpack.MessageTypeException;

public class StringAccept extends AbstractAccept {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public void acceptByteArray(byte[] raw) {
        CharsetDecoder decoder = UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            this.value = decoder.decode(ByteBuffer.wrap(raw)).toString();
        } catch (CharacterCodingException ex) {
            throw new MessageTypeException(ex);
        }
    }

    @Override
    public void acceptEmptyByteArray() {
        this.value = "";
    }
}
