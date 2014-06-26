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

import java.math.BigInteger;

public interface BasicIntegerValue extends BasicNumberValue {
    public boolean isInByteRange();

    public boolean isInShortRange();

    public boolean isInIntRange();

    public boolean isInLongRange();

    public byte getByte() throws MessageTypeIntegerOverflowException;

    public short getShort() throws MessageTypeIntegerOverflowException;

    public int getInt() throws MessageTypeIntegerOverflowException;

    public long getLong() throws MessageTypeIntegerOverflowException;

    public BigInteger getBigInteger() throws MessageTypeIntegerOverflowException;
}
