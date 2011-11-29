package org.msgpack.simple;

import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.ArrayValue;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;


public class TestSimpleDynamicTyping {
    @Test
    @SuppressWarnings("unused")
    public void testTypes() throws IOException {
        MessagePack msgpack = new MessagePack();

        byte[] raw = msgpack.write(new int[] {1,2,3});
        Value v = msgpack.read(raw);

        if(v.isArrayValue()) {
            // ArrayValue extends List<Value>
            ArrayValue array = v.asArrayValue();
            int n0 = array.get(0).asIntegerValue().intValue();
            assertEquals(1, n0);
            int n1 = array.get(1).asIntegerValue().intValue();
            assertEquals(2, n1);
            int n2 = array.get(2).asIntegerValue().intValue();
            assertEquals(3, n2);

        } else if(v.isIntegerValue()) {
            // IntegerValue extends Number
            int num = v.asIntegerValue().intValue();

        } else if(v.isRawValue()) {
            // getString() or getByteArray()
            String str = v.asRawValue().getString();
        }
        // other types:
        //   NilValue asNilValue() / isNilValue()
        //   BooleanValue asBooleanValue() / isBooleanValue()
        //   IntegerValue asIntegerValue() / isIntegerValue()
        //   FloatValue asFloatValue() / isFloatValue()
        //   ArrayValue asArrayValue() / isArrayValue()
        //   MapValue asMapValue() / isMapValue()
        //   RawValue asRawValue() / isRawValue
    }

    @Test
    public void testSimpleConvert() throws IOException {
        MessagePack msgpack = new MessagePack();

        byte[] raw = msgpack.write(new int[] {1,2,3});
        Value v = msgpack.read(raw);

        // convert from dynamic type (Value) to static type (int[])
        int[] array = msgpack.convert(v, new int[3]);
        assertArrayEquals(new int[] {1,2,3}, array);

        // unconvert from static type (int[]) to dynamic type (Value)
        Value v2 = msgpack.unconvert(array);
        assertEquals(v, v2);
    }
}

