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
package org.msgpack.packer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;

public class Unconverter extends AbstractPacker {
    private PackerStack stack;
    private Object[] values;
    private Value result;

    // private Value topContainer;

    public Unconverter() {
        this(new MessagePack());
    }

    public Unconverter(MessagePack msgpack) {
        super(msgpack);
        this.stack = new PackerStack();
        this.values = new Object[PackerStack.MAX_STACK_SIZE];
    }

    public Value getResult() {
        return result;
    }

    public void resetResult() {
        this.result = null;
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        put(ValueFactory.createBooleanValue(v));
    }

    @Override
    public void writeByte(byte v) throws IOException {
        put(ValueFactory.createIntegerValue(v));
    }

    @Override
    public void writeShort(short v) throws IOException {
        put(ValueFactory.createIntegerValue(v));
    }

    @Override
    public void writeInt(int v) throws IOException {
        put(ValueFactory.createIntegerValue(v));
    }

    @Override
    public void writeBigInteger(BigInteger v) throws IOException {
        put(ValueFactory.createIntegerValue(v));
    }

    @Override
    public void writeLong(long v) throws IOException {
        put(ValueFactory.createIntegerValue(v));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        put(ValueFactory.createFloatValue(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        put(ValueFactory.createFloatValue(v));
    }

    @Override
    public void writeByteArray(byte[] b, int off, int len) throws IOException {
        put(ValueFactory.createRawValue(b, off, len));
    }

    @Override
    public void writeByteBuffer(ByteBuffer bb) throws IOException {
        put(ValueFactory.createRawValue(bb));
    }

    @Override
    public void writeString(String s) throws IOException {
        put(ValueFactory.createRawValue(s));
    }

    @Override
    public Packer writeNil() throws IOException {
        put(ValueFactory.createNilValue());
        return this;
    }

    @Override
    public Packer writeArrayBegin(int size) throws IOException {
        if (size == 0) {
            // Value[] array = new Value[size];
            putContainer(ValueFactory.createArrayValue());
            stack.pushArray(0);
            values[stack.getDepth()] = null;
        } else {
            Value[] array = new Value[size];
            putContainer(ValueFactory.createArrayValue(array, true));
            stack.pushArray(size);
            values[stack.getDepth()] = array;
        }
        return this;
    }

    @Override
    public Packer writeArrayEnd(boolean check) throws IOException {
        if (!stack.topIsArray()) {
            throw new MessageTypeException(
                    "writeArrayEnd() is called but writeArrayBegin() is not called");
        }

        int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException(
                        "writeArrayEnd(check=true) is called but the array is not end");
            }
            for (int i = 0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();
        if (stack.getDepth() <= 0) {
            this.result = (Value) values[0];
        }
        return this;
    }

    @Override
    public Packer writeMapBegin(int size) throws IOException {
        stack.checkCount();
        if (size == 0) {
            putContainer(ValueFactory.createMapValue());
            stack.pushMap(0);
            values[stack.getDepth()] = null;
        } else {
            Value[] array = new Value[size * 2];
            putContainer(ValueFactory.createMapValue(array, true));
            stack.pushMap(size);
            values[stack.getDepth()] = array;
        }
        return this;
    }

    @Override
    public Packer writeMapEnd(boolean check) throws IOException {
        if (!stack.topIsMap()) {
            throw new MessageTypeException(
                    "writeMapEnd() is called but writeMapBegin() is not called");
        }

        int remain = stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException(
                        "writeMapEnd(check=true) is called but the map is not end");
            }
            for (int i = 0; i < remain; i++) {
                writeNil();
            }
        }
        stack.pop();
        if (stack.getDepth() <= 0) {
            this.result = (Value) values[0];
        }
        return this;
    }

    @Override
    public Packer write(Value v) throws IOException {
        put(v);
        return this;
    }

    private void put(Value v) {
        if (stack.getDepth() <= 0) {
            this.result = v;
        } else {
            stack.checkCount();
            Value[] array = (Value[]) values[stack.getDepth()];
            array[array.length - stack.getTopCount()] = v;
            stack.reduceCount();
        }
    }

    private void putContainer(Value v) {
        if (stack.getDepth() <= 0) {
            values[0] = (Object) v;
        } else {
            stack.checkCount();
            Value[] array = (Value[]) values[stack.getDepth()];
            array[array.length - stack.getTopCount()] = v;
            stack.reduceCount();
        }
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
