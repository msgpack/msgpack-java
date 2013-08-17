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

import org.msgpack.MessageTypeException;

public class IntAccept extends AbstractAccept {
    private int value;

    public int getValue() {
        return value;
    }

    @Override
    public void acceptInt(int v) {
        this.value = v;
    }

    @Override
    public void acceptLong(long v) {
        if(value < (long) Integer.MIN_VALUE || value > (long) Integer.MAX_VALUE) {
            throw new MessageTypeException("Expected int but got integer greater than "+Integer.MAX_VALUE+" or less than "+Integer.MIN_VALUE);
        }
        this.value = (int) v;
    }

    @Override
    public void acceptUnsignedLong(long v) {
        if(v < 0 || v > (long) Integer.MAX_VALUE) {
            throw new MessageTypeException("Expected int but got integer greater than "+Integer.MAX_VALUE+" or less than "+Integer.MIN_VALUE);
        }
        this.value = (int) v;
    }
}

