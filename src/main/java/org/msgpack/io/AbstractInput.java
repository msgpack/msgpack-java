//
// MessagePack for Java
//
// Copyright (C) 2011 Muga Nishizawa
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
package org.msgpack.io;

abstract class AbstractInput implements Input {

    private int readByteCount = 0;

    public int getReadByteCount() {
        return readByteCount;
    }

    public void resetReadByteCount() {
        readByteCount = 0;
    }

    protected final void incrReadByteCount(int size) {
        readByteCount += size;
    }

    protected final void incrReadOneByteCount() {
        readByteCount += 1;
    }
}
