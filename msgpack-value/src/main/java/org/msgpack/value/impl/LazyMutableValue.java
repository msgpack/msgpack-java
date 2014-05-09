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

public class LazyMutableValue {
}

/*
public class LazyMutableValue
        extends AbstractUnionMutableValue {
    private int nextSkip;
    private boolean eof;

    private ValueUnpacker getUnpacker() throws IOException {
        // TODO lazy open
        return null;
    }

    private ValueUnpacker seekToNextElement() {
        ValueUnpacker u = getUnpacker();
        for(; nextSkip > 0; nextSkip--) {
            u.skipValue();  // TODO not skipToken()
        }
        return u;
    }

    private void lazyRead() throws IOException {
        ValueUnpacker u = seekToNextElement();
        ValueType t = u.getNextType();
        switch(t) {
        case NIL:
            u.readNil();
            resetToNilValue();
            break;
        case BOOLEAN:
            resetToBooleanValue(u.readBoolean());
        case INTEGER:
            try {
                resetToIntegerValue(u.readLong());
            } catch (MessageTypeIntegerOverflowException ex) {
                resetToIntegerValue(u.readBigInteger());
            }
        case FLOAT:
                resetToFloatValue(u.readDouble());
        case STRING:
            resetToStringValue(u.readPayloadToBuferBuffer(u.readRawStringLength()));
        case BINARY:
            resetToStringValue(u.readPayloadToBuferBuffer(u.readBinaryLength()));
        // TODO
             // need readValue()
        }
    }

    @Override
    public ValueType getTypeFamily() {
        if (!isSet()) {
            try {
                lazyRead();
            } catch (IOException ex) {
                throw new RuntimeException(ex);  // TODO RuntimeIOException
            }
        }
        return super.getTypeFamily();
    }

    public void advance() {
        if (isSet()) {
            reset();
        } else {
            nextSkip++;
        }
    }

    public boolean isEof() {
        if (eof) {
            return true;
        } else if (isSet()) {
            return false;
        } else {
            try {
                seekToNextElement().getNextType();
                return true;
            } catch (EOFException ex) {
                return true;
            } catch (IOException ex) {
                return true;  // TODO
            }
        }
    }
}
*/
