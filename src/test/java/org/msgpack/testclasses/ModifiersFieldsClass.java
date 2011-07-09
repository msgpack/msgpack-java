package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;


@Ignore @Message
public class ModifiersFieldsClass {
    public int f0;
    public final int f1 = 1;
    private int f2;
    protected int f3;
    int f4;

    public ModifiersFieldsClass() {}
}
