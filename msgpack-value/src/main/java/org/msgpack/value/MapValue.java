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

import java.util.Map;

public interface MapValue extends Value, BasicMapValue, Map<Value, Value> {
    public static interface Entry
            extends Map.Entry<Value, Value>, BasicMapValue.Entry {
    }

    public static interface EntryIterator
            extends java.util.Iterator<Entry>, BasicMapValue.EntryIterator {
    }

    public static interface EntrySequence
            extends java.lang.Iterable<Entry>, BasicMapValue.EntrySequence {
        @Override
        public EntryIterator iterator();
    }

    @Override
    public EntrySequence entrySequence();

    public Value[] getKeyValueArray();
}
