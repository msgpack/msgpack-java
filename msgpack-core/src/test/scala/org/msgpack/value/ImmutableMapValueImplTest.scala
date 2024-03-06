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

import wvlet.airspec.AirSpec

import scala.collection.mutable

class ImmutableMapValueImplTest extends AirSpec {
  private val map = ValueFactory.newMapBuilder()
    .put(ValueFactory.newString("one"), ValueFactory.newInteger(1))
    .put(ValueFactory.newString("two"), ValueFactory.newInteger(2))
    .put(ValueFactory.newString("three"), ValueFactory.newInteger(3))
    .build()
    .map()

  test("can access values by key") {
    map.get(ValueFactory.newString("one")) shouldBe ValueFactory.newInteger(1)
    map.get(ValueFactory.newString("two")) shouldBe ValueFactory.newInteger(2)
    map.get(ValueFactory.newString("three")) shouldBe ValueFactory.newInteger(3)
    map.get(ValueFactory.newString("other")) shouldBe null
    map.get("other") shouldBe null
    map.get(null) shouldBe null
  }

  test("can be iterated over with forEach") {

    val keys = mutable.Buffer[Value]()
    val values = mutable.Buffer[Value]()

    map.forEach((k, v) => { keys += k; values += v })

    keys.size shouldBe 3
    keys.head shouldBe ValueFactory.newString("one")
    keys(1) shouldBe ValueFactory.newString("two")
    keys(2) shouldBe ValueFactory.newString("three")

    values.size shouldBe 3
    values.head shouldBe ValueFactory.newInteger(1)
    values(1) shouldBe ValueFactory.newInteger(2)
    values(2) shouldBe ValueFactory.newInteger(3)
  }
}
