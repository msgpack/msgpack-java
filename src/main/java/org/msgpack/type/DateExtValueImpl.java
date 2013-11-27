//
// MessagePack for Java
//
// Copyright (C) 2009 - 2013 FURUHASHI Sadayuki
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

import java.io.IOException;
import java.util.Date;

import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;

class DateExtValueImpl extends AbstractExtValue {
    private Date _date;

    DateExtValueImpl(Date date) {
        _date = date;
    }
    
    DateExtValueImpl(byte[] data) {
	  	if (data.length != 8) {
    		throw new MessageTypeException("Expected 8 bytes");
    	}
        int secs = ((0xFF & data[0]) << 24) | ((0xFF & data[1]) << 16) |
                   ((0xFF & data[2]) << 8) | (0xFF & data[3]);
        int nsecs = ((0xFF & data[4]) << 24) | ((0xFF & data[5]) << 16) |
        		    ((0xFF & data[6]) << 8) | (0xFF & data[7]);
        long msecs = secs * 1000 + nsecs / 1000000;
        _date = new Date(msecs);
    }

    @Override
    public Object getObject() {
    	return _date;
    }
   
    @Override
    public void writeTo(Packer pk) throws IOException {
    	pk.write(_date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value v = (Value) o;
        if (v.isExtValue()) {
        	return _date.equals(v.asExtValue().getObject());
        } else {
        	return false;
        }
    }

    @Override
    public int hashCode() {
    	return _date.hashCode();
    }

    @Override
    public String toString() {
        return _date.toString();
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(toString());
    }
}
