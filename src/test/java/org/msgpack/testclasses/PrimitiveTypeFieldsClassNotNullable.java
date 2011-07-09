package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class PrimitiveTypeFieldsClassNotNullable {
    @NotNullable
    public byte f0;
    @NotNullable
    public short f1;
    @NotNullable
    public int f2;
    @NotNullable
    public long f3;
    @NotNullable
    public float f4;
    @NotNullable
    public double f5;
    @NotNullable
    public boolean f6;

    public PrimitiveTypeFieldsClassNotNullable() {}
}
