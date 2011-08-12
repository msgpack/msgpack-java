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
package org.msgpack.type;

import java.util.Arrays;

abstract class AbstractRawValue extends AbstractValue implements RawValue {
    public ValueType getType() {
        return ValueType.RAW;
    }

    public boolean isRaw() {
        return true;
    }

    public RawValue asRawValue() {
        return this;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if(!v.isRaw()) {
            return false;
        }

        return Arrays.equals(getByteArray(), v.asRawValue().getByteArray());
    }

    public int hashCode() {
        return Arrays.hashCode(getByteArray());
    }

    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    public StringBuilder toString(StringBuilder sb) {
        String s = getString();
        sb.append("\"");
        for(int i=0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if(ch < 0x20) {
                switch(ch) {
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                default:
                    escapeChar(sb, ch);
                    break;
                }
            } else {
                switch(ch) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                default:
                    sb.append(ch);
                    break;
                }
            }
        }
        sb.append("\"");
        return sb;
    }

    private void escapeChar(StringBuilder sb, char ch) {
        sb.append("\\u");
        sb.append(((int)ch >> 12) & 0xF);
        sb.append(((int)ch >> 8) & 0xF);
        sb.append(((int)ch >> 4) & 0xF);
        sb.append((int)ch & 0xF);
    }
}

