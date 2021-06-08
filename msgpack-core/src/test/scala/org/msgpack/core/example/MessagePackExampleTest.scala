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

import wvlet.airspec.AirSpec

/**
  */
class MessagePackExampleTest extends AirSpec {

  test("example") {

    test("have basic usage") {
      MessagePackExample.basicUsage()
    }

    test("have packer usage") {
      MessagePackExample.packer()
    }

    test("have file read/write example") {
      MessagePackExample.readAndWriteFile();
    }

    test("have configuration example") {
      MessagePackExample.configuration();
    }
  }
}
