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
package org.msgpack.util.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.Converter;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

public class JSONUnpacker extends Converter {
    protected Reader in;
    private JSONParser parser;

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
    protected Value nextValue() throws IOException {
        try {
            Object obj = parser.parse(in);
            return objectToValue(obj);
        } catch (ParseException e) {
            throw new IOException(e); // TODO error FormatException
        } catch (IOException e) {
            throw new IOException(e); // TODO error FormatException
        }
    }

    @SuppressWarnings("rawtypes")
    private Value objectToValue(Object obj) {
        if (obj instanceof String) {
            return ValueFactory.createRawValue((String) obj);
        } else if (obj instanceof Integer) {
            return ValueFactory.createIntegerValue((Integer) obj);
        } else if (obj instanceof Long) {
            return ValueFactory.createIntegerValue((Long) obj);
        } else if (obj instanceof Map) {
            return mapToValue((Map) obj);
        } else if (obj instanceof List) {
            return listToValue((List) obj);
        } else if (obj instanceof Boolean) {
            return ValueFactory.createBooleanValue((Boolean) obj);
        } else if (obj instanceof Double) {
            return ValueFactory.createFloatValue((Double) obj);
        } else {
            return ValueFactory.createNilValue();
        }
    }

    @SuppressWarnings("rawtypes")
    private Value listToValue(List list) {
        Value[] array = new Value[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = objectToValue(list.get(i));
        }
        return ValueFactory.createArrayValue(array, true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Value mapToValue(Map map) {
        Value[] kvs = new Value[map.size() * 2];
        Iterator<Map.Entry> it = map.entrySet().iterator();
        for (int i = 0; i < kvs.length; i += 2) {
            Map.Entry pair = it.next();
            kvs[i] = objectToValue(pair.getKey());
            kvs[i + 1] = objectToValue(pair.getValue());
        }
        return ValueFactory.createMapValue(kvs, true);
    }

    @Override
    public int getReadByteCount() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void resetReadByteCount() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void close() throws IOException {
        in.close();
        super.close();
    }
}
