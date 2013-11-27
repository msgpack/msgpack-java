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
import java.io.UnsupportedEncodingException;

import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;

class RubySymbolExtValueImpl extends AbstractExtValue {
    private RubySymbol _ruby_symbol;

    RubySymbolExtValueImpl(String string) {
    	_ruby_symbol = new RubySymbol(string);
    }
    
    RubySymbolExtValueImpl(byte[] data) {
    	try {
    		_ruby_symbol = new RubySymbol(new String(data, "UTF-8"));
    	} catch (UnsupportedEncodingException ex) {
    		throw new MessageTypeException();
    	}
    }
    
    RubySymbolExtValueImpl(RubySymbol ruby_symbol) {
    	_ruby_symbol = ruby_symbol;
    }
    
    @Override
    public Object getObject() {
    	return _ruby_symbol;
    }
   
    @Override
    public void writeTo(Packer pk) throws IOException {
    	pk.write(_ruby_symbol);
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
        	return _ruby_symbol.equals(v.asExtValue().getObject());
        } else {
        	return false;
        }
    }

    @Override
    public int hashCode() {
    	return _ruby_symbol.hashCode();
    }

    @Override
    public String toString() {
        return _ruby_symbol.toString();
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(toString());
    }
}
