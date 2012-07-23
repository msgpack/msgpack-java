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
import org.msgpack.template.Templates;
import org.msgpack.unpacker.Unpacker;

import org.junit.Test;

public class TestReadTemplate {
    public static enum MyEnum {
        A, B, C;
    }

    @Test
    public void testReadTemplateNull() throws IOException {
        Byte tbyte = u().read(Templates.TByte);
        assertNull(tbyte);

        Short tshort = u().read(Templates.TShort);
        assertNull(tshort);

        Integer tinteger = u().read(Templates.TInteger);
        assertNull(tinteger);

        Long tlong = u().read(Templates.TLong);
        assertNull(tlong);

        Character tcharacter = u().read(Templates.TCharacter);
        assertNull(tcharacter);

        BigInteger tbiginteger = u().read(Templates.TBigInteger);
        assertNull(tbiginteger);

        BigDecimal tbigdecimail = u().read(Templates.TBigDecimal);
        assertNull(tbigdecimail);

        Float tfloat = u().read(Templates.TFloat);
        assertNull(tfloat);

        Double tdouble = u().read(Templates.TDouble);
        assertNull(tdouble);

        Boolean tboolean = u().read(Templates.TBoolean);
        assertNull(tboolean);

        String tstring = u().read(Templates.TString);
        assertNull(tstring);

        byte[] tbytearray = u().read(Templates.TByteArray);
        assertNull(tbytearray);

        ByteBuffer tbytebuffer = u().read(Templates.TByteBuffer);
        assertNull(tbytebuffer);

        Date tdate = u().read(Templates.TDate);
        assertNull(tdate);

        List<String> tlist = u().read(Templates.tList(Templates.TString));
        assertNull(tlist);

        Map<String, Integer> tmap = u().read(
                Templates.tMap(Templates.TString, Templates.TInteger));
        assertNull(tmap);

        Collection<Long> tcollection = u().read(
                Templates.tCollection(Templates.TLong));
        assertNull(tcollection);

        MyEnum tordinalenum = u().read(Templates.tOrdinalEnum(MyEnum.class));
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
