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

public interface BasicMapValue extends BasicValue {
    public static interface Entry {
        public BasicValue getKey();

        public BasicValue getValue();
    }

    public static interface EntryIterator {
        public boolean hasNext();

        public Entry next();
    }

    // note: BasicMapValue.EntrySequence is not iterable,
    //       unlike MapValue.EntrySequence
    public static interface EntrySequence {
        public EntryIterator iterator();
    }

    /**
     * Returns immutable sequence of key-value pairs.
     */
    public EntrySequence entrySequence();

    public boolean isEmpty();

    public int size();
}
