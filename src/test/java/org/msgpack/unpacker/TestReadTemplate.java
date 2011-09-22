package org.msgpack.unpacker;

import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;

import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;

import static org.msgpack.template.Templates.*;

import org.junit.Test;


public class TestReadTemplate {
    public static enum MyEnum {
        A, B, C;
    }

    @Test
    public void testReadTemplateNull() throws IOException {
        Byte tbyte = u().read(TByte);
        assertNull(tbyte);

        Short tshort = u().read(TShort);
        assertNull(tshort);

        Integer tinteger = u().read(TInteger);
        assertNull(tinteger);

        Long tlong = u().read(TLong);
        assertNull(tlong);

        Character tcharacter = u().read(TCharacter);
        assertNull(tcharacter);

        BigInteger tbiginteger = u().read(TBigInteger);
        assertNull(tbiginteger);

        BigDecimal tbigdecimail = u().read(TBigDecimal);
        assertNull(tbigdecimail);

        Float tfloat = u().read(TFloat);
        assertNull(tfloat);

        Double tdouble = u().read(TDouble);
        assertNull(tdouble);

        Boolean tboolean = u().read(TBoolean);
        assertNull(tboolean);

        String tstring = u().read(TString);
        assertNull(tstring);

        byte[] tbytearray = u().read(TByteArray);
        assertNull(tbytearray);

        ByteBuffer tbytebuffer = u().read(TByteBuffer);
        assertNull(tbytebuffer);

        Date tdate = u().read(TDate);
        assertNull(tdate);

        List<String> tlist = u().read(tList(TString));
        assertNull(tlist);

        Map<String,Integer> tmap = u().read(tMap(TString, TInteger));
        assertNull(tmap);

        Collection<Long> tcollection = u().read(tCollection(TLong));
        assertNull(tcollection);

        MyEnum tordinalenum = u().read(tOrdinalEnum(MyEnum.class));
        assertNull(tordinalenum);
    }

    // return unpacker that can read a nil
    private Unpacker u() throws IOException {
        MessagePack msgpack = new MessagePack();
        BufferPacker pk = msgpack.createBufferPacker();
        pk.writeNil();
        Unpacker u = msgpack.createBufferUnpacker(pk.toByteArray());
        return u;
    }
}

