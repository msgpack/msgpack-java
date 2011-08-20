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

import java.io.EOFException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Unconverter;
import org.msgpack.type.Value;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;


public class Converter extends AbstractUnpacker {
    private final UnpackerStack stack;
    private Object[] values;
    protected Value value;

    public Converter(Value value) {
        this(new MessagePack(), value);
    }

    public Converter(MessagePack msgpack, Value value) {
        super(msgpack);
        this.stack = new UnpackerStack();
        this.values = new Object[UnpackerStack.MAX_STACK_SIZE];
        this.value = value;
    }

    Value getSourceValue() {
        return value;
    }

    void getSourceValue(Value value) {
        this.value = value;
    }

    // FIXME throws IOException?
    protected Value nextValue() {
        // FIXME EOFError?
        throw new NullPointerException("Value is not set");
    }

    private void ensureValue() {
        if(value == null) {
            value = nextValue();
        }
    }

    @Override
    public boolean tryReadNil() {
        stack.checkCount();
        if(getTop().isNil()) {
            stack.reduceCount();
            if(stack.getDepth() == 0) {
                value = null;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean trySkipNil() {
        ensureValue();

        if(stack.getDepth() > 0 && stack.getTopCount() <= 0) {
            // end of array or map
            return true;
        }

        if(getTop().isNil()) {
            stack.reduceCount();
            if(stack.getDepth() == 0) {
                value = null;
            }
            return true;
        }
        return false;
    }

    @Override
    public void readNil() {
        if(!getTop().isNil()) {
            throw new MessageTypeException("Expected nil but got not nil value");
        }
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
    }

    @Override
    public boolean readBoolean() {
        boolean v = getTop().asBooleanValue().getBoolean();
        stack.reduceCount();
        return v;
    }

    @Override
    public byte readByte() {
        byte v = getTop().asIntegerValue().getByte();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return v;
    }

    @Override
    public short readShort() {
        short v = getTop().asIntegerValue().getShort();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return v;
    }

    @Override
    public int readInt() {
        int v = getTop().asIntegerValue().getInt();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return v;
    }

    @Override
    public long readLong() {
        long v = getTop().asIntegerValue().getLong();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return v;
    }

    @Override
    public BigInteger readBigInteger() {
        BigInteger v = getTop().asIntegerValue().getBigInteger();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return v;
    }

    @Override
    public float readFloat() {
        float v = getTop().asFloatValue().getFloat();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return v;
    }

    @Override
    public double readDouble() {
        double v = getTop().asFloatValue().getDouble();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return v;
    }

    @Override
    public ByteBuffer readByteBuffer() {
        return ByteBuffer.wrap(readByteArray());
    }

    @Override
    public byte[] readByteArray() {
        byte[] raw = getTop().asRawValue().getByteArray();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return raw;
    }

    @Override
    public String readString() {
        String str = getTop().asRawValue().getString();
        stack.reduceCount();
        if(stack.getDepth() == 0) {
            value = null;
        }
        return str;
    }

    @Override
    public int readArrayBegin() {
        Value v = getTop();
        if(!v.isArray()) {
            throw new MessageTypeException("Expected array but got not array value");
        }
        ArrayValue a = v.asArrayValue();
        stack.reduceCount();
        stack.pushArray(a.size());
        values[stack.getDepth()] = a.getElementArray();
        return a.size();
    }

    @Override
    public void readArrayEnd() {
        readArrayEnd(false);
    }

    @Override
    public void readArrayEnd(boolean check) {
        if(!stack.topIsArray()) {
            throw new MessageTypeException("readArrayEnd() is called but readArrayBegin() is not called");
        }

        int remain = stack.getTopCount();
        if(remain > 0) {
            if(check) {
                throw new MessageTypeException("readArrayEnd(check=true) is called but the array is not end");
            }
            for(int i=0; i < remain; i++) {
                skip();
            }
        }
        stack.pop();

        if(stack.getDepth() == 0) {
            value = null;
        }
    }

    @Override
    public int readMapBegin() {
        Value v = getTop();
        if(!v.isMap()) {
            throw new MessageTypeException("Expected map but got not map value");
        }
        MapValue m = v.asMapValue();
        stack.reduceCount();
        stack.pushMap(m.size());
        values[stack.getDepth()] = m.getKeyValueArray();
        return m.size();
    }

    @Override
    public void readMapEnd() {
        readMapEnd(false);
    }

    @Override
    public void readMapEnd(boolean check) {
        if(!stack.topIsMap()) {
            throw new MessageTypeException("readMapEnd() is called but readMapBegin() is not called");
        }

        int remain = stack.getTopCount();
        if(remain > 0) {
            if(check) {
                throw new MessageTypeException("readMapEnd(check=true) is called but the map is not end");
            }
            for(int i=0; i < remain; i++) {
                skip();
            }
        }
        stack.pop();

        if(stack.getDepth() == 0) {
            value = null;
        }
    }

    private Value getTop() {
        ensureValue();

        stack.checkCount();
        if(stack.getDepth() == 0) {
            //if(stack.getTopCount() < 0) {
            //    //throw new EOFException();  // TODO
            //    throw new RuntimeException(new EOFException());
            //}
            return value;
        }
        Value[] array = (Value[]) values[stack.getDepth()];
        return array[array.length - stack.getTopCount()];
    }

    @Override
    public Value readValue() {
        if(stack.getDepth() == 0) {
            if(value == null) {
                return nextValue();
            } else {
                Value v = value;
                value = null;
                return v;
            }
        }
        Unconverter uc = new Unconverter(msgpack);
        readValue(uc);
        return uc.getResult();
    }

    @Override
    protected void readValue(Unconverter uc) {
        if(uc.getResult() != null) {
            uc.resetResult();
        }

        stack.checkCount();
        Value v = getTop();
        if(!v.isArray() && !v.isMap()) {
            uc.write(v);
            stack.reduceCount();
            if(stack.getDepth() == 0) {
                value = null;
            }
            if(uc.getResult() != null) {
                return;
            }
        }

        while(true) {
            while(stack.getDepth() != 0 && stack.getTopCount() == 0) {
                if(stack.topIsArray()) {
                    uc.writeArrayEnd(true);
                    stack.pop();
                } else if(stack.topIsMap()) {
                    uc.writeMapEnd(true);
                    stack.pop();
                } else {
                    throw new RuntimeException("invalid stack"); // FIXME error?
                }
                if(stack.getDepth() == 0) {
                    value = null;
                }
                if(uc.getResult() != null) {
                    return;
                }
            }

            stack.checkCount();
            v = getTop();
            if(v.isArray()) {
                ArrayValue a = v.asArrayValue();
                uc.writeArrayBegin(a.size());
                stack.reduceCount();
                stack.pushArray(a.size());
                values[stack.getDepth()] = a.getElementArray();

            } else if(v.isMap()) {
                MapValue m = v.asMapValue();
                uc.writeMapBegin(m.size());
                stack.reduceCount();
                stack.pushMap(m.size());
                values[stack.getDepth()] = m.getKeyValueArray();

            } else {
                uc.write(v);
                stack.reduceCount();
            }
        }
    }

    @Override
    public void skip() {
        stack.checkCount();
        Value v = getTop();
        if(!v.isArray() && !v.isMap()) {
            stack.reduceCount();
            if(stack.getDepth() == 0) {
                value = null;
            }
            return;
        }
        int targetDepth = stack.getDepth();
        while(true) {
            while(stack.getTopCount() == 0) {
                stack.pop();
                if(stack.getDepth() == 0) {
                    value = null;
                }
                if(stack.getDepth() <= targetDepth) {
                    return;
                }
            }

            stack.checkCount();
            v = getTop();
            if(v.isArray()) {
                ArrayValue a = v.asArrayValue();
                stack.reduceCount();
                stack.pushArray(a.size());
                values[stack.getDepth()] = a.getElementArray();

            } else if(v.isMap()) {
                MapValue m = v.asMapValue();
                stack.reduceCount();
                stack.pushMap(m.size());
                values[stack.getDepth()] = m.getKeyValueArray();

            } else {
                stack.reduceCount();
            }
        }
    }

    @Override
    public void close() {
    }

    public void reset() {
        stack.reset();
        value = null;
    }
}

