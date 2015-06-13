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
package org.msgpack.value;

import java.util.List;
import java.util.Iterator;

/**
 * The interface {@code ArrayValue} represents MessagePack's Array type.
 *
 * MessagePack's Array type can represent sequence of values.
 */
public interface ArrayValue extends Value, Iterable<Value> {
    @Override
    public ImmutableArrayValue toImmutable();

    /**
     * Returns number of elements in this array.
     */
    public int size();

    /**
     * Returns the element at the specified position in this array.
     *
     * @throws IndexOutOfBoundsException
     *         If the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    public Value get(int index);

    /**
     * Returns the element at the specified position in this array.
     * This method returns an ImmutableNilValue if the index is out of range.
     */
    public Value getOrNilValue(int index);

    /**
     * Returns an iterator over elements.
     */
    public Iterator<Value> iterator();

    /**
     * Returns the value as {@code List}.
     */
    public List<Value> list();
}
