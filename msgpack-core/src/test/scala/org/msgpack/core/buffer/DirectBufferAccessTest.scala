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
package org.msgpack.core.buffer

import wvlet.airspec.AirSpec

import java.nio.ByteBuffer

class DirectBufferAccessTest extends AirSpec:

  test("instantiate DirectBufferAccess") {
    val bb   = ByteBuffer.allocateDirect(1)
    val addr = DirectBufferAccess.getAddress(bb)

  }
