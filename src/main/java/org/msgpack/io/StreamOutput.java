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
package org.msgpack.io;

import java.io.IOException;
import java.io.OutputStream;

public class StreamOutput extends BufferedOutput {
    private OutputStream out;

    public StreamOutput(OutputStream out) {
        this(out, 1024);  // TODO default buffer size
    }

    public StreamOutput(OutputStream out, int bufferSize) {
        super(bufferSize);
        this.out = out;
    }

    protected boolean flushBuffer(byte[] buffer, int off, int len) throws IOException {
        out.write(buffer, off, len);
        return true;
    }
}

