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
package org.msgpack.unpacker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Date;

import org.msgpack.MessageTypeException;
import org.msgpack.type.RubySymbol;

final class ExtAccept extends Accept {
    Object value;
    private CharsetDecoder decoder;

    public ExtAccept() {
        super("ext value");
        this.decoder = Charset.forName("UTF-8").newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
    }
    
    @Override
    void acceptExt(int type, byte[] raw) throws IOException {
    	if (type == 0x13) {
    	  	if (raw.length != 8) {
        		throw new MessageTypeException("Expected 8 bytes");
        	}
            int secs = ((0xFF & raw[0]) << 24) | ((0xFF & raw[1]) << 16) |
                       ((0xFF & raw[2]) << 8) | (0xFF & raw[3]);
            int nsecs = ((0xFF & raw[4]) << 24) | ((0xFF & raw[5]) << 16) |
            		    ((0xFF & raw[6]) << 8) | (0xFF & raw[7]);
            long msecs = secs * 1000 + nsecs / 1000000;
            this.value = new Date(msecs);
    	} else if (type == 0x14) {
    		try {
    			this.value = new RubySymbol(decoder.decode(ByteBuffer.wrap(raw)).toString());
    		} catch (CharacterCodingException ex) {
    			throw new MessageTypeException(ex);
    		}
    	} else {
    		throw new MessageTypeException("Unrecognized ext type code");
    	}
    }	
}
