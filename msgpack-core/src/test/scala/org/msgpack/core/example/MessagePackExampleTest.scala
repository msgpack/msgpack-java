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
package org.msgpack.core.example

import org.msgpack.core.MessagePackSpec

/**
  *
  */
class MessagePackExampleTest extends MessagePackSpec {

  "example" should {

    "have basic usage" in {
      MessagePackExample.basicUsage()
    }

    "have packer usage" in {
      MessagePackExample.packer()
    }

    "have file read/write example" in {
      MessagePackExample.readAndWriteFile();
    }

    "have configuration example" in {
      MessagePackExample.configuration();
    }
  }
}
