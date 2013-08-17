package org.msgpack.template;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;

import static org.msgpack.template.Templates.*;

import org.junit.Test;


public class TestTemplates {
    public static enum MyEnum {
        A, B, C;
    }

    @SuppressWarnings("unused")
    @Test
    public void testGenericsTypesCompliable() throws IOException {
        Template<Byte> tbyte = TByte;
        Template<Short> tshort = TShort;
        Template<Integer> tinteger = TInteger;
        Template<Long> tlong = TLong;
        Template<Character> tcharacter = TCharacter;
        Template<BigInteger> tbiginteger = TBigInteger;
        Template<BigDecimal> tbigdecimail = TBigDecimal;
        Template<Float> tfloat = TFloat;
        Template<Double> tdouble = TDouble;
        Template<Boolean> tboolean = TBoolean;
        Template<String> tstring = TString;
        Template<byte[]> tbytearray = TByteArray;
        Template<ByteBuffer> tbytebuffer = TByteBuffer;
        Template<Date> tdate = TDate;

        Template<List<String>> tlist = tList(TString);
        Template<Map<String,Integer>> tmap = tMap(TString, TInteger);
        Template<Collection<Long>> tcollection = tCollection(TLong);
        Template<MyEnum> tordinalenum = tOrdinalEnum(MyEnum.class);
    }

    @Test
    public void testList() throws IOException {
        MessagePack msgpack = new MessagePack();

        BufferPacker pk = msgpack.createBufferPacker();

        Template<List<String>> t = tList(TString);
        List<String> list1 = new ArrayList<String>();
        list1.add("a");
        list1.add("b");
        t.write(pk, list1);

        byte[] raw = pk.toByteArray();
        Unpacker u = msgpack.createBufferUnpacker(raw);
        List<String> list2 = t.read(u, new ArrayList<String>());

        assertEquals(list1, list2);
    }
}

