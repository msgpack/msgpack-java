package org.msgpack.testclasses;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class ReferenceTypeFieldsClassNotNullable {
    @NotNullable
    public Byte f0;
    @NotNullable
    public Short f1;
    @NotNullable
    public Integer f2;
    @NotNullable
    public Long f3;
    @NotNullable
    public Float f4;
    @NotNullable
    public Double f5;
    @NotNullable
    public Boolean f6;
    @NotNullable
    public BigInteger f7;
    @NotNullable
    public String f8;
    @NotNullable
    public byte[] f9;
    @NotNullable
    public ByteBuffer f10;

    public ReferenceTypeFieldsClassNotNullable() {}
}
