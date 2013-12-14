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
package org.msgpack.unpacker;

public class MessageUnpacker /*implements Unpacker */{
    public static class Options {
        // upcast raw type  // default:true : getNextType returns RAW?
        // allow readByteArray and readByteBuffer to read str types  // default:true
        // string decode malformed input action  // default:report
        // string decode unmappable character action  // default:report
        // byte buffer bulk allocation cache size
        // byte buffer allocator
        // allow byte buffer reference : advanced option
        // raw size limit
    }

    // TODO
}

