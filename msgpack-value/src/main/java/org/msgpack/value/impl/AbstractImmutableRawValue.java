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
package org.msgpack.value.impl;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnsupportedCharsetException;

import org.msgpack.value.ImmutableRawValue;
import org.msgpack.value.MessageTypeStringCodingException;

abstract class AbstractImmutableRawValue
        extends AbstractImmutableValue implements ImmutableRawValue {

    // See also ResettableMutableValue

    protected final ByteBuffer byteBuffer;
    private transient String decodedStringCache;
    private transient MessageTypeStringCodingException codingException;

    public AbstractImmutableRawValue(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer.slice();
    }

    @Override
    public byte[] getByteArray() {
        byte[] byteArray = new byte[byteBuffer.remaining()];
        byteBuffer.slice().get(byteArray);
        return byteArray;
    }

    @Override
    public String getString() throws MessageTypeStringCodingException {
        if (decodedStringCache == null) {
            decodeString();
        }
        if (codingException != null) {
            throw codingException;
        }
        return decodedStringCache;
    }

    @Override
    public String stringValue() {
        if (decodedStringCache == null) {
            decodeString();
        }
        return decodedStringCache;
    }

    @Override
    public ByteBuffer byteBufferValue() {
        return byteBuffer.asReadOnlyBuffer();
    }

    private synchronized void decodeString() {
        if (decodedStringCache != null) {
            return;
        }

        try {
            CharsetDecoder reportDecoder = Charset.forName("UTF-8").newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
            decodedStringCache = reportDecoder.decode(byteBuffer.asReadOnlyBuffer()).toString();
        } catch (UnsupportedCharsetException neverThrown) {
            throw new AssertionError(neverThrown);
        } catch (CharacterCodingException ex) {
            codingException = new MessageTypeStringCodingException(ex);
            try {
                CharsetDecoder replaceDecoder = Charset.forName("UTF-8").newDecoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
                decodedStringCache = replaceDecoder.decode(byteBuffer.asReadOnlyBuffer()).toString();
            } catch (UnsupportedCharsetException neverThrown) {
                throw new AssertionError(neverThrown);
            } catch (CharacterCodingException neverThrown) {
                throw new AssertionError(neverThrown);
            }
        }
    }
}
