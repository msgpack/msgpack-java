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
package org.msgpack.unpacker;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.math.BigInteger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//import org.msgpack.io.Input;
//import org.msgpack.io.StreamInput;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;


public class JSONUnpacker extends Converter {
    protected Reader in;
    protected JSONParser parser;

    public JSONUnpacker(InputStream in) {
        this(new MessagePack(), in);
    }

    public JSONUnpacker(MessagePack msgpack, InputStream in) {
        this(msgpack, new InputStreamReader(in));
    }

    JSONUnpacker(MessagePack msgpack, Reader in) {
        super(msgpack, null);
        this.in = in;
        this.parser = new JSONParser();
    }

    @Override
    // FIXME throws IOException?
    protected Value nextValue() {
        try {
            Object obj = parser.parse(in);
            return objectToValue(obj);
        } catch (ParseException e) { // FIXME exception
            throw new MessageTypeException(e);
        } catch (IOException e) { // FIXME exception
            throw new MessageTypeException(e);
        }
    }

    private Value objectToValue(Object obj) {
        if(obj instanceof String) {
            return ValueFactory.rawValue((String)obj);
        } else if(obj instanceof Integer) {
            return ValueFactory.integerValue((Integer)obj);
        } else if(obj instanceof Long) {
            return ValueFactory.integerValue((Long)obj);
        } else if(obj instanceof Map) {
            return mapToValue((Map)obj);
        } else if(obj instanceof List) {
            return listToValue((List)obj);
        } else if(obj instanceof Boolean) {
            return ValueFactory.booleanValue((Boolean)obj);
        } else if(obj instanceof Double) {
            return ValueFactory.floatValue((Double)obj);
        } else {
            return ValueFactory.nilValue();
        }
    }

    private Value listToValue(List list) {
        Value[] array = new Value[list.size()];
        for(int i=0; i < array.length; i++) {
            array[i] = objectToValue(list.get(i));
        }
        return ValueFactory.arrayValue(array, true);
    }

    private Value mapToValue(Map map) {
        Value[] kvs = new Value[map.size()*2];
        Iterator<Map.Entry> it = map.entrySet().iterator();
        for(int i=0; i < kvs.length; i+=2) {
            Map.Entry pair = it.next();
            kvs[i] = objectToValue(pair.getKey());
            kvs[i+1] = objectToValue(pair.getValue());
        }
        return ValueFactory.mapValue(kvs, true);
    }

    public void close() throws IOException {
        in.close();
    }
}

