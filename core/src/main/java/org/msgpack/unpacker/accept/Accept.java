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

public interface Accept {
    public void acceptNil() throws IOException;

    public void acceptBoolean(boolean v) throws IOException;

    public void acceptInt(int v) throws IOException;

    public void acceptLong(long v) throws IOException;

    public void acceptUnsignedLong(long v) throws IOException;

    public void acceptByteArray(byte[] raw) throws IOException;

    public void acceptEmptyByteArray() throws IOException;

    public void acceptFloat(float v) throws IOException;

    public void acceptDouble(double v) throws IOException;

    public void acceptArrayHeader(int size) throws IOException;

    public void acceptMapHeader(int size) throws IOException;
}

