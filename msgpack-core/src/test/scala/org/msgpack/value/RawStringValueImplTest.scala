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
package org.msgpack.value

import org.msgpack.core.MessagePackSpec

class RawStringValueImplTest extends MessagePackSpec {

  "StringValue" should {
    "return the same hash code if they are equal" in {
      val str = "a"
      val a1  = ValueFactory.newString(str.getBytes("UTF-8"))
      val a2  = ValueFactory.newString(str)

      a1.shouldEqual(a2)
      a1.hashCode.shouldEqual(a2.hashCode)
      a2.shouldEqual(a1)
      a2.hashCode.shouldEqual(a1.hashCode)
    }
  }
}
