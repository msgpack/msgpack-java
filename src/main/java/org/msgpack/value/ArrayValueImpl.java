//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
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
import java.util.ListIterator;

class ArrayValueImpl extends AbstractArrayValue {
    private static ArrayValueImpl emptyInstance = new ArrayValueImpl(new Value[0], true);

    public static ArrayValue getEmptyInstance() {
        return emptyInstance;
    }

    private Value[] array;

    ArrayValueImpl(Value[] array, boolean gift) {
        if(gift) {
            this.array = array;
        } else {
            this.array = new Value[array.length];
            System.arraycopy(array, 0, this.array, 0, array.length);
        }
    }

    public int size() {
        return array.length;
    }

    public boolean isEmpty() {
        return array.length == 0;
    }

    public Value get(int index) {
        if(index < 0 || array.length <= index) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    public int indexOf(Object o) {
        if(o == null) {
            return -1;  // FIXME NullPointerException?
        }
        for(int i=0; i < array.length; i++) {
            if(array[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        if(o == null) {
            return -1;  // FIXME NullPointerException?
        }
        for(int i=array.length-1; i >= 0; i--) {
            if(array[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof ArrayValue)) {
            return false;
        }

        if(o.getClass() == ArrayValueImpl.class) {
            return equals((ArrayValueImpl) o);
        }

        ListIterator oi = ((List) o).listIterator();
        int i = 0;
        while(i < array.length) {
            if(!oi.hasNext() || !array[i].equals(oi.next())) {
                return false;
            }
        }
        return !oi.hasNext();
    }

    private boolean equals(ArrayValueImpl o) {
        if(array.length != o.array.length) {
            return false;
        }
        for(int i=0; i < array.length; i++) {
            if(!array[i].equals(o.array[i])) {
                return false;
            }
        }
        return true;
    }

    // TODO compareTo?

    public int hashCode() {
        int h = 1;
        for(int i=0; i < array.length; i++) {
            Value obj = array[i];
            h = 31*h + obj.hashCode();
        }
        return h;
    }

    public String toString() {
        if(array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(array[0]);
        for(int i=1; i < array.length; i++) {
            sb.append(",");
            sb.append(array[i]);
        }
        sb.append("]");
    }
}

