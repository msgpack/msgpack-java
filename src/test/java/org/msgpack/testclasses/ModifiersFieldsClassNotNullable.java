package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;


@Ignore @Message
public class ModifiersFieldsClassNotNullable {
    @NotNullable
    public int f0;
    @NotNullable
    public final int f1 = 1;
    @NotNullable
    private int f2;
    @NotNullable
    protected int f3;
    @NotNullable
    int f4;

    public ModifiersFieldsClassNotNullable() {}
}
