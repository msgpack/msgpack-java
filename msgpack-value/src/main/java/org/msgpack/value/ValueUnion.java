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
import java.util.Map;
import java.math.BigInteger;
import java.nio.ByteBuffer;

final class ValueUnion {
    public static enum Type {
        BOOLEAN,
        LONG,
        BIG_INTEGER,
        DOUBLE,
        BYTE_BUFFER,
        STRING,
        LIST,
        MAP;
    }

    private Type type;

    private long longValue;
    private double doubleValue;
    private Object objectValue;

    public void reset() {
        this.type = null;
    }

    public boolean isSet() {
        return type != null;
    }

    public Type getType() {
        return type;
    }

    public void setBoolean(boolean v) {
        this.type = Type.BOOLEAN;
        this.longValue = (v ? 1L : 0L);
    }

    public boolean getBoolean() {
        return longValue != 0L;
    }

    public void setLong(long v) {
        this.type = Type.LONG;
        this.longValue = v;
    }

    public long getLong() {
        return longValue;
    }

    public void setBigInteger(BigInteger v) {
        this.type = Type.BIG_INTEGER;
        this.objectValue = v;
    }

    public BigInteger getBigInteger() {
        return (BigInteger) objectValue;
    }

    public void setDouble(double v) {
        this.type = Type.DOUBLE;
        this.doubleValue = v;
    }

    public double getDouble() {
        return doubleValue;
    }

    public void setByteBuffer(ByteBuffer v) {
        this.type = Type.BYTE_BUFFER;
        this.objectValue = v;
    }

    public ByteBuffer getByteBuffer() {
        return (ByteBuffer) objectValue;
    }

    public void setString(String v) {
        this.type = Type.STRING;
        this.objectValue = v;
    }

    public String getString() {
        return (String) objectValue;
    }

    public void setList(List<Value> v) {
        this.type = Type.LIST;
        this.objectValue = v;
    }

    @SuppressWarnings("unchecked")
    public List<Value> getList() {
        return (List<Value>) objectValue;
    }

    public void setMap(Map<Value, Value> v) {
        this.type = Type.MAP;
        this.objectValue = v;
    }

    @SuppressWarnings("unchecked")
    public Map<Value, Value> getMap() {
        return (Map<Value, Value>) objectValue;
    }
}
