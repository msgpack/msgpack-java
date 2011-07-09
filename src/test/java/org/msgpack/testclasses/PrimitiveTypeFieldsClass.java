package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class PrimitiveTypeFieldsClass {
    public byte f0;
    public short f1;
    public int f2;
    public long f3;
    public float f4;
    public double f5;
    public boolean f6;

    public PrimitiveTypeFieldsClass() {}
}
