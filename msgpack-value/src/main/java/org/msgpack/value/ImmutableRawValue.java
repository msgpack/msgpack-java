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

/**
 * Immutable base interface of {@link ImmutableStringValue} and {@link ImmutableBinaryValue} interfaces.
 * <p/>
 * MessagePack's Raw type can represent a byte array at most 2<sup>64</sup>-1 bytes.
 *
 * @see org.msgpack.value.ImmutableStringValue
 * @see org.msgpack.value.ImmutableBinaryValue
 */
public interface ImmutableRawValue
        extends RawValue, ImmutableValue
{
}
