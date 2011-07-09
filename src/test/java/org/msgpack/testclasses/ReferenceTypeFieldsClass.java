package org.msgpack.testclasses;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class ReferenceTypeFieldsClass {
    public Byte f0;
    public Short f1;
    public Integer f2;
    public Long f3;
    public Float f4;
    public Double f5;
    public Boolean f6;
    public BigInteger f7;
    public String f8;
    public byte[] f9;
    public ByteBuffer f10;

    public ReferenceTypeFieldsClass() {}
}
